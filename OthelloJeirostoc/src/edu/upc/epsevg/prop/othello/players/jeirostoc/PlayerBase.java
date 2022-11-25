package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * Base class for PlayerMinimax and PlayerID
 * 
 * @author raul
 * @author josep
 */
public abstract class PlayerBase implements IAuto, IPlayer {
    private HeuristicStatus _lastStatus;

    @Override
    public Move move(GameStatus gs) {
        HeuristicStatus nhs = new HeuristicStatus(gs, _lastStatus);
        Move m;
        
        ArrayList<Point> moves =  nhs.getMoves();
        if(moves.isEmpty()) {
            m = new Move(null, 0L,0,  SearchType.RANDOM); 
        } else {
            Random rand = new Random();
            int q = rand.nextInt(moves.size());
            m = new Move( moves.get(q), 0L, 0, SearchType.RANDOM);         
        }
        
        _lastStatus = nhs.getNextStatus(m.getTo());
        return m;
    }

    @Override
    public void timeout() {
        System.out.println("TODO timeout");
    }
    
}
