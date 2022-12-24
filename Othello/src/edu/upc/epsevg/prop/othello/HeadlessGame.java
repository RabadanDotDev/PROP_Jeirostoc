/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.othello;

import edu.upc.epsevg.prop.othello.players.DesdemonaPlayer;
import edu.upc.epsevg.prop.othello.players.RandomPlayer;
import edu.upc.epsevg.prop.othello.players.jeirostoc.PlayerBase;
import edu.upc.epsevg.prop.othello.players.jeirostoc.PlayerID;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    
    static FileWriter currentGameLog;

    public static void main(String[] args) {
        genTimingDifferencesID();
//        FileWriter fw = null;
//        try {
//            long time = System.currentTimeMillis();
//            fw = new FileWriter(time + "_actions.csv");
//            currentGameLog = new FileWriter(time + "_gameLog.log");
//        } catch (IOException ex) {
//            Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        IPlayer player = new PlayerID(fw);
//        //Player player2 = new RandomPlayer("Desdesmonasia");
//        IPlayer desdemona = new DesdemonaPlayer(2);//GB
//
//        
//        HeadlessGame game1 = new HeadlessGame(player, desdemona, 2, 5);
//        GameResult gr1 = game1.start();
//        
//        HeadlessGame game2 = new HeadlessGame(desdemona, player, 2, 5);
//        GameResult gr2 = game2.start();
//
//        reportUpdate("-------------------------------------------------------------");
//        reportUpdate(gr1.toString());
//        reportUpdate(gr2.toString());
//        reportUpdate("-------------------------------------------------------------");
    }
    
    private static void genTimingDifferencesID() {
        int timeouts[] = {1, 2, 3, 4, 5};
        
        for(int timeout : timeouts) {
            FileWriter fw = null;
            long time = System.currentTimeMillis();
            try {
                fw = new FileWriter(time + "_timeout_" + timeout + "_actions.csv");
                currentGameLog = new FileWriter(time + "_timeout_" + timeout + "_gameLog.log");
                
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            IPlayer player = new PlayerID(fw);
            IPlayer desdemona = new DesdemonaPlayer(2);//GB

            HeadlessGame game = new HeadlessGame(player, desdemona, timeout, 1);
            GameResult gr = game.start();
            reportUpdate(gr.toString());
        }
    }
    
    private static void genOpeningBook() {
        FileWriter fw1    = null;
        FileWriter fw2    = null;
        long time = System.currentTimeMillis();
        try {
            fw1 = new FileWriter(time + "_actions1.csv");
            fw2 = new FileWriter(time + "_actions2.csv");
            currentGameLog = new FileWriter(time + "_gameLog.log");
        } catch (IOException ex) {
            Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        PlayerBase.setCreateRestrictedTable(true);
        PlayerBase playerGenOpeningBook = new PlayerID(fw1);
        PlayerBase.setCreateRestrictedTable(false);
        
        PlayerBase playerAgainstItself = new PlayerID(fw2);
        
        IPlayer desdemona = new DesdemonaPlayer(2);//GB

        {
            BufferedWriter ttdump = null;
            try {
                ttdump = new BufferedWriter(new FileWriter(time + "tt1.data"));
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            reportUpdate("------------------PLAYER AGAINST ITSELF---------------------");
            HeadlessGame game1 = new HeadlessGame(playerGenOpeningBook, playerAgainstItself, 2, 5);
            GameResult gr1 = game1.start();
            HeadlessGame game2 = new HeadlessGame(playerAgainstItself, playerGenOpeningBook, 2, 5);
            GameResult gr2 = game2.start();
            reportUpdate("-------------------------------------------------------------");
            reportUpdate(gr1.toString());
            reportUpdate(gr2.toString());
            reportUpdate("-------------------------------------------------------------");
            
            playerGenOpeningBook.dumpTT(ttdump);
            try {
                ttdump.close();
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        {
            BufferedWriter ttdump = null;
            try {
                ttdump = new BufferedWriter(new FileWriter(time + "tt2.data"));
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            reportUpdate("------------------PLAYER AGAISNT DESDEMONA-------------------");
            HeadlessGame game1 = new HeadlessGame(playerGenOpeningBook, desdemona, 2, 5);
            GameResult gr1 = game1.start();
            HeadlessGame game2 = new HeadlessGame(desdemona, playerGenOpeningBook, 2, 5);
            GameResult gr2 = game2.start();
            reportUpdate("-------------------------------------------------------------");
            reportUpdate(gr1.toString());
            reportUpdate(gr2.toString());
            reportUpdate("-------------------------------------------------------------");
            
            playerGenOpeningBook.dumpTT(ttdump);
            try {
                ttdump.close();
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        {
            BufferedWriter ttdump = null;
            try {
                ttdump = new BufferedWriter(new FileWriter(time + "tt3.data"));
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            reportUpdate("------------------PLAYER MOVE FROM ROOT-------------------");
            computeMovement(playerGenOpeningBook, 60*60);
            
            playerGenOpeningBook.dumpTT(ttdump);
            try {
                ttdump.close();
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void computeMovement(IPlayer p, int timeout) {
        final Semaphore semaphore = new Semaphore(1);
        semaphore.tryAcquire();
        
        Thread t1 = new Thread(() -> {
                Move m = null;
                m = p.move(new GameStatus());
                
                if (m != null) {
                    reportUpdate("Moves " + m.getTo());
                }
                
                semaphore.release();
            });

            Thread t2 = new Thread(() -> {
                try {
                    Thread.sleep(timeout * 1000);
                } catch (InterruptedException ex) {
                }
                
                p.timeout();
            });

            t1.start();
            t2.start();
            long WAIT_EXTRA_TIME = 2000;
            try {
                if (!semaphore.tryAcquire(1, timeout * 1000 + WAIT_EXTRA_TIME, TimeUnit.MILLISECONDS)) {
                    reportUpdate("Player not finishing properlly");
                    semaphore.acquire();
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    private static void reportUpdate(String str) {
        System.out.println(str);
        try {
            currentGameLog.append(str);
            currentGameLog.append('\n');
            currentGameLog.flush();
        } catch (IOException ex) {
            Logger.getLogger(HeadlessGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //=====================================================================================0
    public HeadlessGame(IPlayer p1, IPlayer p2, int timeout, int gameCount) {

        this.players = new IPlayer[2];
        players[0] = p1;
        players[1] = p2;
        this.gameCount = gameCount;
        this.timeout = timeout;
    }
    
    public TwoSidesGameResult doGamesTwoSides() {
        GameResult gr1 = new GameResult();
        for (int i = 0; i < gameCount/2; i++) {
            reportUpdate("-------------------------------------------------------------");
            reportUpdate("Playing game nº " + i);
            gr1.update(play());
        }
        
        reportUpdate("Flipping players...");
        IPlayer tmp = players[0];
        players[0] = players[1];
        players[1] = tmp;
        
        GameResult gr2 = new GameResult();
        for (int i = gameCount/2; i < gameCount; i++) {
            reportUpdate("-------------------------------------------------------------");
            reportUpdate("Playing game nº " + i);
            gr2.update(play());
        }
        
        reportUpdate("-------------------------------------------------------------");
        reportUpdate(gr1.toString());
        reportUpdate(gr2.toString());
        reportUpdate("-------------------------------------------------------------");
        
        
        TwoSidesGameResult tsgr = new TwoSidesGameResult();
        tsgr.ties = gr1.ties + gr2.ties;
        tsgr.wins1 = gr1.wins1 + gr2.wins2;
        tsgr.wins2 = gr1.wins2 + gr2.wins1;
        
        return tsgr;
    }

    public GameResult start() {
        GameResult gr = new GameResult();
        for (int i = 0; i < gameCount; i++) {
            reportUpdate("-------------------------------------------------------------");
            reportUpdate("Playing game nº " + i);
            gr.update(play());
        }
        return gr;
    }

    private class Result {
        public boolean ok;
    }

    private CellType play() {
        this.status = new GameStatus();
        reportUpdate(status.toString());

        while (!this.status.isGameOver()) {
            if (!status.currentPlayerCanMove()) {
                reportUpdate(players[status.getCurrentPlayer() == CellType.PLAYER1 ? 0 : 1].getName() + " skips turn");
                status.skipTurn();
                reportUpdate(status.toString());
            } else {
                final Semaphore semaphore = new Semaphore(1);
                semaphore.tryAcquire();
                //System.out.println("." + new Date());
                final Result r = new Result();
                CellType cp = status.getCurrentPlayer();
                
                Thread t1 = new Thread(() -> {
                    Move m = null;
                    try {
                        m = players[cp == CellType.PLAYER1 ? 0 : 1].move(new GameStatus(status));
                    } catch(Exception ex) {
                        reportUpdate("Excepció descontrolada al player:"+cp.name());
                        ex.printStackTrace();
                    }
                    if (m != null) {
                        reportUpdate(players[cp == CellType.PLAYER1 ? 0 : 1].getName() + " moves " + m.getTo() + " ("  + cp + ")");
                        status.movePiece(m.getTo());
                        reportUpdate(status.toString());
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

                        reportUpdate("Espera il·legal ! Player trampós:"+cp.name());
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
            reportUpdate("Tie.");
        } else switch (status.winnerPlayer) {
            case PLAYER1:
                reportUpdate(players[0].getName() + " ("  + status.winnerPlayer + ") has won!");
                break;
            case PLAYER2:
                reportUpdate(players[1].getName() + " ("  + status.winnerPlayer + ") has won!");
                break;
            default:
                reportUpdate("Tie.");
                break;

        }
        return status.winnerPlayer;
    }

    private class GameResult {
        private int wins1;
        private int wins2;
        private int ties;

        public GameResult() {
            wins1 = wins2 = ties = 0;
        }

        public void update(CellType res) {
            switch (res) {
                case EMPTY:     ties++; break;
                case PLAYER1:  wins1++; break;
                default:       wins2++; break;
            }
        }

        @Override
        public String toString() {
            String res = "";
            res += "PLAYER 1 (" + pad(players[0].getName(), 40) + "):\t wins " + wins1 + "\t ties:" + ties + "\t looses:" + wins2 + "\n";
            res += "PLAYER 2 (" + pad(players[1].getName(), 40) + "):\t wins " + wins2 + "\t ties:" + ties + "\t looses:" + wins1 + "\n";
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

        public int getWins1() {
            return wins1;
        }

        public int getWins2() {
            return wins2;
        }

        public int getTies() {
            return ties;
        }
    }
    
    public class TwoSidesGameResult {
        int wins1;
        int wins2;
        int ties;
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
    