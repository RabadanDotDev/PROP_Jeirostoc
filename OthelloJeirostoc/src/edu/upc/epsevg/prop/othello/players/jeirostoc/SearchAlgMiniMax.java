package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.Move;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Search algorithm that chooses a move based on MiniMax
 * 
 * @author raul
 * @author josep
 */
class SearchAlgMiniMax extends SearchAlg {
    
    /**
     * Create a new MiniMax search instance with a given max depth
     * 
     * @param maxDepth 
     */
    public SearchAlgMiniMax(int maxDepth) {
        super(maxDepth);
    }
    
    /**
     * Get next move based on the current game status
     * 
     * @param hs The current game status
     * @return The selected move
     */
    @Override
    public Move nextMove(HeuristicStatus hs) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * Return the heuristic more favorable to the current player within the 
     * bounds alpha and beta.
     * 
     * @param player The player to evaluate the game with
     * @param hs The current game state
     * @param maxDepth The max allowed recursive depth
     * @param alpha The upper bound
     * @param beta The lower bound
     * @param color 1 if its the player owner of the search, -1 if its not.
     * @return the heuristic more favorable to the current player within the 
     * bounds alpha and beta.
     */
    public double minimax(CellType player, HeuristicStatus hs, int maxDepth, double alpha, double beta, boolean isMax) {
        // Check if we got to a terminal state
        if(hs.checkGameOver() || maxDepth == 0) {
            return hs.getHeuristic(player);
        }
        
        // Get moves
        ArrayList<Point> points = hs.getMoves();
        
        // Analize moves
        for (Point p : points) {
            // Get next heuristic
            double nextHeuristic = minimax(player, hs, maxDepth-1, alpha, beta, !isMax);
            
            if(isMax) {
                // Update lower bound
                alpha = Math.max(alpha, nextHeuristic);
            } else {
                // Update upper bound
                beta = Math.min(beta, nextHeuristic);
            }
            
            // Prune if we exceeded lower or upper bound
            if(beta <= alpha)
                break;
        }
        
        // Return most favourable bound to current player
        return isMax ? alpha : beta;
    }
}
