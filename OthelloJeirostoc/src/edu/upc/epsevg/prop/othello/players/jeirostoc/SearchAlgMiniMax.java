package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
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
     * The number of nodes which the current search has computed their 
     * heuristic.
     */
    private long _nodesWithComputedHeuristic;
    
    /**
     * The maximum depth the current search has computed an heuristic.
     */
    private int _depthReached;
    
    /**
     * Create a new MiniMax search instance with a given max depth
     * 
     * @param maxDepth 
     */
    public SearchAlgMiniMax(int maxDepth) {
        super(maxDepth, SearchType.MINIMAX);
    }
    
    /**
     * Create a MiniMax search algorithm with a given max global length and 
     * different SearchType. This is intended for specializations of this class
     * 
     * @param maxGlobalDepth 
     */
    protected SearchAlgMiniMax(int maxGlobalDepth, SearchType searchType) {
        super(maxGlobalDepth, searchType);
    }
    
    /**
     * Get next move based on the current game status
     * 
     * @param hs The current game status
     * @return The selected move
     */
    @Override
    public Move nextMove(HeuristicStatus hs) {  
        // Init trackers
        _nodesWithComputedHeuristic = 0;
        _depthReached = 0;
        
        // Init result
        Point pointToMove = null;
        double bestHeuristic = Double.NEGATIVE_INFINITY;
        
        // Analize moves if they exist
        ArrayList<Point> points = hs.getMoves();
        for (Point p : points) {
            // Check if search can continue
            if(!_searchIsOn)
                break;
            
            // Get next heuristic
            double nextHeuristic = minimax(
                    hs.getCurrentPlayer(), 
                    hs.getNextStatus(p), 
                    1, 
                    Double.NEGATIVE_INFINITY, 
                    Double.POSITIVE_INFINITY, 
                    false
            );
            
            // Store the found heuristic if its better
            if(bestHeuristic < nextHeuristic || pointToMove == null) {
                bestHeuristic = nextHeuristic;
                pointToMove = p;
            }
        }
        
        // Return selected movement
        return new Move(pointToMove, _nodesWithComputedHeuristic, _depthReached, _searchType);
    }
    
    /**
     * Maximize or minimize the heuristic from the perspective of player within 
     * the bounds alpha and beta.
     * 
     * @param player The player to evaluate the game with
     * @param hs The current game state
     * @param currentDepth The depth of this call
     * @param alpha The upper bound
     * @param beta The lower bound
     * @param isMax True if the heuristic has to be maximized and false if it 
     * has to be minimized.
     * @return the heuristic more favorable to the current player within the 
     * bounds alpha and beta.
     */
    public double minimax(CellType player, HeuristicStatus hs, int currentDepth, double alpha, double beta, boolean isMax) {     
        // Check if we got to a terminal state
        if(hs.checkGameOver() || _maxGlobalDepth <= currentDepth) {
            _nodesWithComputedHeuristic++;
            _depthReached = Math.max(_depthReached, currentDepth);
            return hs.getHeuristic(player);
        }
        
        // Get moves
        ArrayList<Point> points = hs.getMoves();
        
        // Analize skipped turn if there is no movements
        if(points.isEmpty())
            return minimax(player, hs.getNextStatus(null), currentDepth+1, alpha, beta, !isMax);
        
        // Analize moves if they exist
        for (Point p : points) {
            // Check if search can continue
            if(!_searchIsOn)
                break;
            
            // Get next heuristic
            double nextHeuristic = minimax(player, hs.getNextStatus(p), currentDepth+1, alpha, beta, !isMax);
            
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
        
        // Return the maxmimized or minimized bound
        return isMax ? alpha : beta;
    }
}
