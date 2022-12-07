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

    @Override
    public void searchOFF() {
        super.searchOFF();
//        System.out.println(System.currentTimeMillis() + " Search off recieved");
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
    public Move nextMove(HeuristicStatus hs) {  
        // Init trackers
        _nodesWithComputedHeuristic = 0;
        _depthReached = 0;
        
        // Compute next point
        Point p = minimaxNextPoint(hs);
        
        // Return selected movement
        return new Move(p, _nodesWithComputedHeuristic, _depthReached, _searchType);
    }
    
    protected Point minimaxNextPoint(HeuristicStatus hs) {
        if (!_searchIsOn)
            return null;
        
        _tp.clear();
        
        // Init result
        Point pointToMove = null;
        double bestHeuristic = Double.NEGATIVE_INFINITY;
        
        // Get moves
        ArrayList<HeuristicStatus> nextNodes = _tp.getNextExplorableNodes(hs, true);
        
        // Analize skipped turn if there is no movements
        if(nextNodes.isEmpty() && _searchIsOn) {
            bestHeuristic = minimax(
                    hs.getCurrentPlayer(), 
                    hs.getNextStatus(null), 
                    1, 
                    Double.NEGATIVE_INFINITY, 
                    Double.POSITIVE_INFINITY, 
                    false
            );
        }
        
        // Analize moves if they exist
        for (HeuristicStatus nextNode : nextNodes) {
            // Check if search can continue
            if(!_searchIsOn)
                break;
            
            // Get next heuristic
            double nextHeuristic;
            var tv = _tp.get(nextNode, _maxGlobalDepth);
            if(tv == null) {
                nextHeuristic = minimax(
                    hs.getCurrentPlayer(), 
                    nextNode, 
                    1, 
                    Double.NEGATIVE_INFINITY, 
                    Double.POSITIVE_INFINITY, 
                    false
                );
                
                // Register exploration to the transposition table
                if(_searchIsOn)
                    _tp.register(nextNode, nextHeuristic,  _maxGlobalDepth);
            } else {
                nextHeuristic = tv.selectedHeuristic;
            }
            
            // Store the found heuristic if its better
            if(bestHeuristic < nextHeuristic || pointToMove == null) {
                bestHeuristic = nextHeuristic;
                pointToMove = nextNode.getLastMovement();
            }
        }
        
        // Return selected point
        _lastBestHeuristic = bestHeuristic;
        return pointToMove;
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
    protected double minimax(CellType player, HeuristicStatus hs, int currentDepth, double alpha, double beta, boolean isMax) {     
        if (!_searchIsOn)
            return 0;
        
        // Check if we got to a terminal state
        if(hs.checkGameOver() || _maxGlobalDepth <= currentDepth) {
            _nodesWithComputedHeuristic++;
            _depthReached = Math.max(_depthReached, currentDepth);
            return hs.getHeuristic(player);
        }
        
        // Get moves
        ArrayList<HeuristicStatus> nextNodes = _tp.getNextExplorableNodes(hs, false);
        
        // Analize skipped turn if there is no movements
        if(nextNodes.isEmpty() && _searchIsOn) {
            alpha = beta = minimax(player, hs.getNextStatus(null), currentDepth+1, alpha, beta, !isMax);
        }
        
        // Analize moves if they exist
        for (HeuristicStatus nextNode : nextNodes) {
            // Check if search can continue
            if(!_searchIsOn)
                break;
            
            // Get next heuristic
            double nextHeuristic;
            var tv = _tp.get(nextNode, _maxGlobalDepth-currentDepth);
            if(tv == null) {
                nextHeuristic = minimax(player, nextNode, currentDepth+1, alpha, beta, !isMax);
                
                // Register exploration to the transposition table
                if(_searchIsOn)
                    _tp.register(nextNode, nextHeuristic, _maxGlobalDepth-currentDepth);
            } else {
                nextHeuristic = tv.selectedHeuristic;
            }
            
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
