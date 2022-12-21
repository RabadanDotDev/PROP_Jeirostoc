package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Search algorithm that chooses a move based on a MiniMax exploration
 * 
 * @author raul
 * @author josep
 */
class SearchAlgMiniMax extends SearchAlg {    
    /**
     * Transposition table.
     */
    private final TT _tt;
    
    /**
     * The array to indicate between recursion levels if the current level has
     * been pruned or not.
     */
    private final boolean[] _isExact;
    
    /**
     * Create a new MiniMax search instance with a given max depth.
     * 
     * @param maxDepth The maximum depth the search is allowed to go.
     */
    public SearchAlgMiniMax(int maxDepth) {
        super(maxDepth, SearchType.MINIMAX);
        _tt = new TT();
        _isExact = new boolean[Status.SIZE*Status.SIZE];
    }
    
    /**
     * Create a new MiniMax search instance with a given max depth and a
     * different number of entries in the TT from the default.
     * 
     * @param maxDepth The maximum depth the search is allowed to go.
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public SearchAlgMiniMax(int maxDepth,  int numEntriesTT) {
        super(maxDepth, SearchType.MINIMAX);
        _tt = new TT(numEntriesTT);
        _isExact = new boolean[Status.SIZE*Status.SIZE];
    }
    
    /**
     * Create a MiniMax search algorithm with a given max global length and a
     * different SearchType from the default.
     * 
     * @param maxGlobalDepth The max global depth of the search.
     * @param searchType The search type.
     * @param numEntriesTT The number of entries in the transposition table.
     */
    protected SearchAlgMiniMax(int maxGlobalDepth, SearchType searchType) {
        super(maxGlobalDepth, searchType);
        _tt = new TT();
        _isExact = new boolean[Status.SIZE*Status.SIZE];
    }
    
    /**
     * Create a MiniMax search algorithm with a given max global length and a
     * different SearchType and number of entries from the default.
     * 
     * @param maxGlobalDepth The max global depth of the search.
     * @param searchType The search type.
     * @param numEntriesTT The number of entries in the transposition table.
     */
    protected SearchAlgMiniMax(int maxGlobalDepth, SearchType searchType, int numEntriesTT) {
        super(maxGlobalDepth, searchType);
        _tt = new TT(numEntriesTT);
        _isExact = new boolean[Status.SIZE*Status.SIZE];
    }
    
    /**
     * Do the search for a movement based on the status s and deposit the
     * selected movement in _lastMovementSelected, the heuristic of the movement
     * in _lastBestHeuristic, the depth reached in _depthReached and the nodes
     * whose heuristic has been obtained in _nodesWithComputedHeuristic. It
     * assumes that _nodesWithComputedHeuristic, _depthReached, _playerColor,
     * _lastMovementSelected have been correctly initialized.
     * 
     * @param s The status to base the search on.
     */
    @Override
    public void doSearch(Status s) {
        _lastSelectedHeuristic = minimax(
                s,
                0,
                Float.NEGATIVE_INFINITY, 
                Float.POSITIVE_INFINITY, 
                true
        );
    }
    
    /**
     * Maximize or minimize the heuristic from the perspective of player within
     * the bounds alpha and beta. _lastMovementSelected will be set to the last
     * selected movement or -1 if no movement was selected, _depthReached and
     * _nodesWithComputedHeuristic will be updated accordingly.
     * 
     * @param s The current game state.
     * @param currentDepth The depth of this call.
     * @param alpha The lower bound.
     * @param beta The upper bound.
     * @param isMax True if the heuristic has to be maximized and false if it 
     * has to be minimized.
     * @return The heuristic more favorable to the current player within the 
     * bounds alpha and beta.
     */
    protected float minimax(Status s, int currentDepth, float alpha, float beta, boolean isMax) {        
        // Mark this level as exact
        _isExact[currentDepth] = true;
        
        // Check if we are in a terminal state
        if(s.isTerminal() || _maxGlobalDepth <= currentDepth) {
            _nodesWithComputedHeuristic++;
            _depthReached = Math.max(_depthReached, currentDepth);
            _lastSelectedMovement = -1;
            return s.getHeuristic(_playerColor);
        }
        
        // Retrieve the entry from transposition table
        long entry = _tt.readEntry(s);
        byte selectedNextMove = TT.extractSelectedMovementIfValidEntry(s, entry);
        if(TT.canExtractHeuristic(entry, _maxGlobalDepth-currentDepth)) {
            float extractedHeuristic = TT.extractSelectedHeuristic(entry)*_playerColor;
            
            // Return if it is an exact heuristic
            if (TT.extractIsExact(entry)) {
                _lastSelectedMovement = selectedNextMove;
                return extractedHeuristic;
            }
            
            // Update bounds
            if(TT.extractIsAlpha(entry)) alpha = Math.max(alpha, extractedHeuristic);
            else                         beta = Math.min( beta, extractedHeuristic);
            
            // Prune if we exceeded lower or upper bound already
            if(beta <= alpha) {
                _lastSelectedMovement = selectedNextMove;
                _isExact[currentDepth] = false;
                return isMax ? alpha : beta;
            }
        }
        
        // Get next moves
        ArrayList<Point> nextMoves = new ArrayList<>();
        s.getNextMoves(nextMoves, selectedNextMove);
        
        // Analize moves if they exist
        for (Point nextMove : nextMoves) {
            // Check if the analisis can continue (interruption or pruning)
            if(!_searchIsOn || beta <= alpha) {
                _isExact[currentDepth] = false;
                break;
            }
            
            // Generate next node
            Status nextNode = new Status(s);
            nextNode.movePiece(nextMove);
            
            // Get the heuristic from the next level
            float nextHeuristic = minimax(nextNode, currentDepth+1, alpha, beta, !isMax);
            
            // Update bounds
            if(isMax && alpha < nextHeuristic) {
                alpha = nextHeuristic;
                selectedNextMove = nextNode.getLastMovement();
            } else if(!isMax && nextHeuristic < beta) {
                beta = nextHeuristic;
                selectedNextMove = nextNode.getLastMovement();
            } else if (selectedNextMove == -1) {
                selectedNextMove = nextNode.getLastMovement();
            }
            
            // Update this level's isExtact status
            _isExact[currentDepth] = _isExact[currentDepth] && _isExact[currentDepth+1];
        }
        
        // Analize skipped turn if there is no movements
        if(nextMoves.isEmpty() && _searchIsOn) {
            // Generate next node
            Status next = new Status(s);
            next.skipTurn();
            
            // Get the heuristic from the next level
            alpha = beta = minimax(next, currentDepth+1, alpha, beta, !isMax);
            
            // Update this level's isExtact status
            _isExact[currentDepth] = _isExact[currentDepth] && _isExact[currentDepth+1];
        }
        
        // Register result to the transposition table
        if(_searchIsOn) {
            _tt.register(
                    s,
                    (isMax ? alpha : beta)*_playerColor,
                    selectedNextMove, 
                    (byte)(_maxGlobalDepth-currentDepth), 
                    _isExact[currentDepth], 
                    isMax
            );
        }
        
        // Return the maxmimized or minimized bound
        _lastSelectedMovement = selectedNextMove;
        return isMax ? alpha : beta;
    }

    @Override
    public String getLogLineHeader() {
        StringBuilder sb = new StringBuilder(super.getLogLineHeader());
        sb.append("ttNumCollisions").append(';');
        return sb.toString();
    }
    
    @Override
    public String getLogLineLastSearch() {
        StringBuilder sb = new StringBuilder(super.getLogLineLastSearch());
        sb.append(_tt.getNumCollisions()).append(';');
        return sb.toString();
    }
}
