package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import org.junit.Test;

/**
 * Test to profile the difference in efficiency between Status and GameStatus.
 * 
 * @author raul
 * @author josep
 */
public class StatusProfileTest {   
    /**
     * Test to profile the difference in efficiency between Status and GameStatus.
     */
    @Test
    public void testSameResultsRandomToProfile() {
        Random r = new Random();
        
        for (int game = 0; game < 50000; game++) {
            // Init
            GameStatus reference = new GameStatus();
            Status incremental = new Status();
            
            ArrayList<Point> referenceMoves;
            ArrayList<Point> incrementalMoves = new ArrayList<>();
            
            for (int i = 1; i < 64; i++) {
                // Get next moves
                referenceMoves = reference.getMoves();
                incrementalMoves.clear();
                incremental.getNextMoves(incrementalMoves);
            
                // Do movement
                if(referenceMoves.isEmpty()) {
                    reference = new GameStatus(reference);
                    reference.skipTurn();
                    
                    incremental = new Status(incremental);
                    incremental.skipTurn();
                } else {
                    Point p = referenceMoves.get(r.nextInt(referenceMoves.size()));
                    
                    reference = new GameStatus(reference);
                    reference.movePiece(p);
                    
                    incremental = new Status(incremental);
                    incremental.movePiece(p);
                }
            }
        }
    }
}
