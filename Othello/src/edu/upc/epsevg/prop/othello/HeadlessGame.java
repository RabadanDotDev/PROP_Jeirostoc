/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.othello;

import edu.upc.epsevg.prop.othello.players.DesdemonaPlayer;
import edu.upc.epsevg.prop.othello.players.RandomPlayer;
import edu.upc.epsevg.prop.othello.players.jeirostoc.PlayerID;
import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bernat
 */
public class HeadlessGame {

    private IPlayer players[];
    private GameStatus status;
    private int gameCount;
    private int timeout;

    public static void main(String[] args) {

        IPlayer playerID = new PlayerID();
        //Player player2 = new RandomPlayer("Desdesmonasia");
        IPlayer desdemona = new DesdemonaPlayer(2);//GB

        
        HeadlessGame game1 = new HeadlessGame(playerID, desdemona, 2, 5);
        GameResult gr1 = game1.start();
        
        HeadlessGame game2 = new HeadlessGame(desdemona, playerID, 2, 5);
        GameResult gr2 = game2.start();

        System.out.println("-------------------------------------------------------------");
        System.out.println(gr1);
        System.out.println(gr2);
        System.out.println("-------------------------------------------------------------");
    }

    //=====================================================================================0
    public HeadlessGame(IPlayer p1, IPlayer p2, int timeout, int gameCount) {

        this.players = new IPlayer[2];
        players[0] = p1;
        players[1] = p2;
        this.gameCount = gameCount;
        this.timeout = timeout;
    }

    public GameResult start() {
        GameResult gr = new GameResult();
        for (int i = 0; i < gameCount; i++) {
            System.out.println("-------------------------------------------------------------");
            System.out.println("Playing game nº " + i);
            gr.update(play(players[0], players[1]));
        }
        return gr;
    }

    private class Result {
        public boolean ok;
    }

    private CellType play(IPlayer player, IPlayer player0) {
        this.status = new GameStatus();

        while (!this.status.isGameOver()) {
            if (!status.currentPlayerCanMove()) {
                status.skipTurn();
            } else {
                final Semaphore semaphore = new Semaphore(1);
                semaphore.tryAcquire();
                //System.out.println("." + new Date());
                final Result r = new Result();
                CellType cp = status.getCurrentPlayer();
                System.out.println(status);
                
                Thread t1 = new Thread(() -> {
                    Move m = null;
                    try {
                        m = players[cp == CellType.PLAYER1 ? 0 : 1].move(new GameStatus(status));
                    } catch(Exception ex) {
                        System.out.println("Excepció descontrolada al player:"+cp.name());
                        ex.printStackTrace();
                    }
                    if (m != null) {
                        status.movePiece(m.getTo());
                        System.out.println(players[cp == CellType.PLAYER1 ? 0 : 1].getName() + " moves " + m.getTo() + " ("  + cp + ")");
                    } else {
                        status.forceLoser();
                    }
                    r.ok = true;
                    semaphore.release();
                });

                Thread t2 = new Thread(() -> {
                    try {
                        Thread.sleep(HeadlessGame.this.timeout * 1000);
                    } catch (InterruptedException ex) {
                    }
                    if (!r.ok) {
                        players[cp == CellType.PLAYER1 ? 0 : 1].timeout();
                    }
                });

                t1.start();
                t2.start();
                long WAIT_EXTRA_TIME = 2000;
                try {
                    if (!semaphore.tryAcquire(1, timeout * 1000 + WAIT_EXTRA_TIME, TimeUnit.MILLISECONDS)) {

                        System.out.println("Espera il·legal ! Player trampós:"+cp.name());
                        //throw new RuntimeException("Jugador trampós ! Espera il·legal !");
                        // Som millors persones deixant que el jugador il·legal continui jugant...
                        semaphore.acquire();
                    }
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Netegem la memòria (for free!)
                gc();
            }
        }
        
        if (null == status.winnerPlayer ) {
            System.out.println("Tie.");
        } else switch (status.winnerPlayer) {
            case PLAYER1:
                System.out.println(players[0].getName() + " ("  + status.winnerPlayer + ") has won!");
                break;
            case PLAYER2:
                System.out.println(players[1].getName() + " ("  + status.winnerPlayer + ") has won!");
                break;
            default:
                System.out.println("Tie.");
                break;

        }
        return status.winnerPlayer;
    }

    private class GameResult {

        java.util.List<CellType> results;

        public GameResult() {
            results = new ArrayList<CellType>();

        }

        public void update(CellType res) {
            results.add(res);
        }

        @Override
        public String toString() {
            String res = "";
            int wins1 = 0, ties1 = 0, loose1 = 0;
            for (CellType c : results) {
                if (null == c) {
                    loose1++;
                } else {
                    switch (c) {
                        case EMPTY:
                            ties1++;
                            break;
                        case PLAYER1:
                            wins1++;
                            break;
                        default:
                            loose1++;
                            break;
                    }
                }
            }

            res += "PLAYER 1 (" + pad(players[0].getName(), 40) + "):\t wins " + wins1 + "\t ties:" + ties1 + "\t looses:" + loose1 + "\n";
            res += "PLAYER 2 (" + pad(players[1].getName(), 40) + "):\t wins " + loose1 + "\t ties:" + ties1 + "\t looses:" + wins1 + "\n";
            return res;
        }

        public String pad(String inputString, int length) {
            if (inputString.length() >= length) {
                return inputString;
            }
            StringBuilder sb = new StringBuilder();
            while (sb.length() < length - inputString.length()) {
                sb.append(' ');
            }
            sb.append(inputString);

            return sb.toString();
        }
    }

    
    /**
     * This method guarantees that garbage collection is done unlike
     * <code>{@link System#gc()}</code>
     */
    public static void gc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<Object>(obj);
        obj = null;
        while (ref.get() != null) {
            System.gc();
        }
    }
}
    