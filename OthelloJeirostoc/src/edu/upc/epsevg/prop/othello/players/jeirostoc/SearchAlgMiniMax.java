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
    protected long _nodesWithComputedHeuristic;
    
    /**
     * The maximum depth the current search has computed an heuristic.
     */
    protected int _depthReached;
    
    /**
     * Transposition table.
     */
    private TranspositionTable _tp;
    
    /**
     * The player's color
     */
    private int _playerColor;

    @Override
    public void searchOFF() {
        super.searchOFF();
    }
    
    /**
     * Create a new MiniMax search instance with a given max depth
     * 
     * @param maxDepth 
     */
    public SearchAlgMiniMax(int maxDepth) {
        super(maxDepth, SearchType.MINIMAX);
        _tp = new TranspositionTable();
    }
    
    /**
     * Create a MiniMax search algorithm with a given max global length and 
     * different SearchType. This is intended for specializations of this class
     * 
     * @param maxGlobalDepth 
     */
    protected SearchAlgMiniMax(int maxGlobalDepth, SearchType searchType) {
        super(maxGlobalDepth, searchType);
        _tp = new TranspositionTable();
    }
    
    /**
     * Get next move based on the current game status
     * 
     * @param hs The current game status
     * @return The selected move
     */
    @Override
    public Move nextMove(Status s) {  
        // Init trackers
        _nodesWithComputedHeuristic = 0;
        _depthReached = 0;
        
        // Compute next point
        Point p = minimaxNextPoint(s);
        
        // Return selected movement
        return new Move(p, _nodesWithComputedHeuristic, _depthReached, _searchType);
    }
    
    protected Point minimaxNextPoint(Status s) {
        // Init
        _playerColor = s.getCurrentPlayerColor();
        
        // Init result
        Status bestNext = null;
        double bestHeuristic = Double.NEGATIVE_INFINITY;
        
        // Get moves
        ArrayList<Status> nextNodes = new ArrayList<>();
        s.getNextStatuses(nextNodes);
        
        // Analize skipped turn if there is no movements
        if(nextNodes.isEmpty() && _searchIsOn) {
            bestNext = new Status(s);
            bestNext.skipTurn();
            
            bestHeuristic = minimax(
                    bestNext, 
                    1, 
                    Double.NEGATIVE_INFINITY, 
                    Double.POSITIVE_INFINITY, 
                    false
            );
        }
        
        // Analize moves if they exist
        for (Status next : nextNodes) {
            // Check if search can continue
            if(!_searchIsOn)
                break;
            
            // Get next heuristic
            double nextHeuristic;
            nextHeuristic = minimax(
                next, 
                1, 
                Double.NEGATIVE_INFINITY, 
                Double.POSITIVE_INFINITY, 
                false
            );
            
            // Store the found heuristic if its better
            if(bestHeuristic < nextHeuristic || bestNext == null) {
                bestHeuristic = nextHeuristic;
                bestNext = next;
            }
        }
        
        // Return selected point
        if(bestNext == null) {
            return null;
        } else {
            _lastBestHeuristic = bestHeuristic;
            return new Point(bestNext.getLastMovement()[0], bestNext.getLastMovement()[1]);
        }
    }
    
    /**
     * Maximize or minimize the heuristic from the perspective of player within 
     * the bounds alpha and beta.
     * 
     * @param player The player to evaluate the game with
     * @param s The current game state
     * @param currentDepth The depth of this call
     * @param alpha The upper bound
     * @param beta The lower bound
     * @param isMax True if the heuristic has to be maximized and false if it 
     * has to be minimized.
     * @return the heuristic more favorable to the current player within the 
     * bounds alpha and beta.
     */
    protected double minimax(Status s, int currentDepth, double alpha, double beta, boolean isMax) {        
        // Check if we got to a terminal state
        if(s.isTerminal()|| _maxGlobalDepth <= currentDepth) {
            _nodesWithComputedHeuristic++;
            _depthReached = Math.max(_depthReached, currentDepth);
            return s.getHeuristic(_playerColor);
        }
        
        // Get moves
        ArrayList<Status> nextNodes = new ArrayList<>();
        s.getNextStatuses(nextNodes);
        
        // Analize skipped turn if there is no movements
        if(nextNodes.isEmpty() && _searchIsOn) {
            Status next = new Status(s);
            next.skipTurn();
            return minimax(next, currentDepth+1, alpha, beta, !isMax);
        }
        
        // Analize moves if they exist
        for (Status nextNode : nextNodes) {
            // Check if search can continue
            if(!_searchIsOn)
                break;
            
            // Get next heuristic
            double nextHeuristic = minimax(nextNode, currentDepth+1, alpha, beta, !isMax);
            
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
