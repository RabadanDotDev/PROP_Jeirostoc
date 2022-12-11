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
     * Toggle to select to use the heuristic from the TT
     */
    private static final boolean USE_HEURISTIC_TT = true;
    
    /**
     * Toggle to select to cut when getting a isExact entry
     */
    private static final boolean CUT_IS_EXACT_TT = false;
    
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
    private final TT _tt;
    
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
        _tt = new TT();
    }
    
    /**
     * Create a MiniMax search algorithm with a given max global length and 
     * different SearchType. This is intended for specializations of this class
     * 
     * @param maxGlobalDepth 
     */
    protected SearchAlgMiniMax(int maxGlobalDepth, SearchType searchType) {
        super(maxGlobalDepth, searchType);
        _tt = new TT();
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
        byte bestNextMove = -1;
        float bestHeuristic = Float.NEGATIVE_INFINITY;
        
        // Retrieve entry from transposition table
        long entry = _tt.readEntry(s);
        if (TT.extractIsValidEntry(entry)) {
            // Extract last selected movement
            bestNextMove = TT.extractSelectedMovement(entry);
            
            // Extract last heuristic if its more deep
            if (USE_HEURISTIC_TT &&
                _maxGlobalDepth <= TT.extractDepthBelow(entry) &&
                TT.extractIsAlpha(entry)
            ) {
                
                bestHeuristic = TT.extractSelectedHeuristic(entry);
                
                if (TT.extractIsExact(entry) && CUT_IS_EXACT_TT) {
                    _lastBestHeuristic = bestHeuristic;
                    return new Point(bestNextMove/Status.SIZE, bestNextMove%Status.SIZE);
                }
            }
        }
        
        // Get moves
        ArrayList<Status> nextNodes = new ArrayList<>();
        s.getNextStatuses(nextNodes, bestNextMove);
        
        // Analize moves if they exist
        for (Status next : nextNodes) {
            // Check if search can continue
            if(!_searchIsOn)
                break;
            
            // Get next heuristic
            float nextHeuristic = minimax(
                next, 
                1, 
                bestHeuristic, 
                Float.POSITIVE_INFINITY, 
                false
            );
            
            // Store the found heuristic if its better
            if(bestHeuristic < nextHeuristic || bestNextMove == -1) {
                bestHeuristic = nextHeuristic;
                bestNextMove = next.getLastMovement();
            }
        }
        
        // Analize skipped turn if there is no movements
        if(nextNodes.isEmpty() && _searchIsOn) {
            Status next = new Status(s);
            next.skipTurn();
            
            bestHeuristic = minimax(
                    next, 
                    1, 
                    bestHeuristic, 
                    Float.POSITIVE_INFINITY, 
                    false
            );
        }
        
        // Register result to the transposition table
        if(_searchIsOn) {
            _tt.register(
                    s, 
                    bestHeuristic, 
                    bestNextMove, 
                    (byte)_maxGlobalDepth, 
                    false, 
                    true
            );
        }
        
        // Return selected point
        if(bestNextMove == -1) {
            return null;
        } else {
            _lastBestHeuristic = bestHeuristic;
            return new Point(bestNextMove/Status.SIZE, bestNextMove%Status.SIZE);
        }
    }
    
    /**
     * Maximize or minimize the heuristic from the perspective of player within 
     * the bounds alpha and beta.
     * 
     * @param player The player to evaluate the game with
     * @param s The current game state
     * @param currentDepth The depth of this call
     * @param alpha The lower bound
     * @param beta The upper bound
     * @param isMax True if the heuristic has to be maximized and false if it 
     * has to be minimized.
     * @return the heuristic more favorable to the current player within the 
     * bounds alpha and beta.
     */
    protected float minimax(Status s, int currentDepth, float alpha, float beta, boolean isMax) {        
        // Check if we got to a terminal state
        if(s.isTerminal()|| _maxGlobalDepth <= currentDepth) {
            _nodesWithComputedHeuristic++;
            _depthReached = Math.max(_depthReached, currentDepth);
            return s.getHeuristic(_playerColor);
        }
        
        // Retrieve entry from transposition table
        long entry = _tt.readEntry(s);
        byte selectedNextMove = -1;
        if (TT.extractIsValidEntry(entry)) {
            // Extract last selected movement
            selectedNextMove = TT.extractSelectedMovement(entry);
            
            // Extract last heuristic if its more deep
            if (USE_HEURISTIC_TT &&
                _maxGlobalDepth <= TT.extractDepthBelow(entry) &&
                TT.extractIsAlpha(entry) == isMax
            ) {
                float nextHeuristic = TT.extractSelectedHeuristic(entry);
                
                if(isMax) {
                    // Update lower bound
                    alpha = Math.max(alpha, nextHeuristic);
                } else {
                    // Update upper bound
                    beta = Math.min(beta, nextHeuristic);
                }
                
                if (TT.extractIsExact(entry) && CUT_IS_EXACT_TT) {
                    return isMax ? alpha : beta;
                }
            }
        }
        
        // Get moves
        ArrayList<Status> nextNodes = new ArrayList<>();
        s.getNextStatuses(nextNodes, selectedNextMove);
        
        // Analize moves if they exist
        for (Status nextNode : nextNodes) {
            // Check if search can continue
            if(!_searchIsOn)
                break;
            
            // Get next heuristic
            float nextHeuristic = minimax(nextNode, currentDepth+1, alpha, beta, !isMax);
            
            if(isMax && alpha < nextHeuristic) {
                // Update lower bound
                alpha = nextHeuristic;
                selectedNextMove = nextNode.getLastMovement();
            } else if(!isMax && nextHeuristic < beta) {
                // Update upper bound
                beta = nextHeuristic;
                selectedNextMove = nextNode.getLastMovement();
            }
            
            // Prune if we exceeded lower or upper bound
            if(beta <= alpha)
                break;
        }
        
        // Analize skipped turn if there is no movements
        if(nextNodes.isEmpty() && _searchIsOn) {
            Status next = new Status(s);
            next.skipTurn();
            return minimax(next, currentDepth+1, alpha, beta, !isMax);
        }
        
        // Register result to the transposition table
        if(_searchIsOn) {
            _tt.register(
                    s,
                    isMax ? alpha : beta,
                    selectedNextMove, 
                    (byte)(_maxGlobalDepth-currentDepth), 
                    false, 
                    isMax
            );
        }
        
        // Return the maxmimized or minimized bound
        return isMax ? alpha : beta;
    }
}
