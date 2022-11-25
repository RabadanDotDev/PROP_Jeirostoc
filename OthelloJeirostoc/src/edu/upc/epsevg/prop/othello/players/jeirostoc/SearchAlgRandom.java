package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * Search algorithm that chooses a random move
 * 
 * @author raul
 * @author josep
 */
class SearchAlgRandom extends SearchAlg {

    public SearchAlgRandom() {
        super(-1);
    }
    
    @Override
    public Move nextMove(HeuristicStatus hs) {        
        ArrayList<Point> moves = hs.getMoves();
        if(moves.isEmpty()) {
            return new Move(null, 0L,0,  SearchType.RANDOM); 
        } else {
            Random rand = new Random();
            int q = rand.nextInt(moves.size());
            return new Move(moves.get(q), 0L, 0, SearchType.RANDOM);         
        }
    }
}