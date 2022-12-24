package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * MiniMax search algorithm that chooses a move based on a MiniMax exploration 
 * and is executable as runnable
 * 
 * @author raul
 * @author josep
 */
class RunnableFutureMiniMax implements RunnableFuture {
    /**
     * Result class to group all the execution information.
     */
    class Result {
        /**
         * The amount of nodes whose heuristic was computed.
         */
        final long nodesWithComputedHeuristic;
        
        /**
         * The depth reached.
         */
        final int depthReached;
        
        /**
         * The last selected heuristic in the search.
         */
        final float lastSelectedHeuristic;
        
        /**
         * The last selected movement in the search.
         */
        final byte lastSelectedMovement;

        /**
         * Default constructor
         * 
         * @param nodesWithComputedHeuristic The number of nodes whose heuristic
         * was computed
         * @param depthReached The depth reached
         * @param lastSelectedHeuristic The last selected heuristic in the 
         * search
         * @param lastSelectedMovement The last selected movement in the search
         */
        Result(long nodesWithComputedHeuristic, int depthReached, float lastSelectedHeuristic, byte lastSelectedMovement) {
            this.nodesWithComputedHeuristic = nodesWithComputedHeuristic;
            this.depthReached = depthReached;
            this.lastSelectedHeuristic = lastSelectedHeuristic;
            this.lastSelectedMovement = lastSelectedMovement;
        }
    }
    
    /**
     * Indicates if the search is active or not. The change of this value to 
     * false has to be honored as soon as it is possible.
     */
    private boolean _searchIsOn;
    
    /**
     * Indicates if the search has completed successfully.
     */
    private boolean _finished;
    
    /**
     * The maximum depth the algorithm is allowed to go.
     */
    private final int _maxDepth;
    
    /**
     * The number of nodes which the search has computed their heuristic.
     */
    private long _nodesWithComputedHeuristic;
    
    /**
     * The maximum depth the current search has computed an heuristic.
     */
    private int _depthReached;
    
    /**
     * The player's current color.
     */
    private final int _playerColor;
    
    /**
     * The computed heuristic of the last returned movement.
     */
    private float _lastSelectedHeuristic;
    
    /**
     * The last movement selected in the last level of the search.
     */
    private byte _lastSelectedMovement;
    
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
     * Reference to the root node.
     */
    private final Status _s;
    
    /**
     * Toggle to select between the regular move ordering or the inverted move 
     * ordering.
     */
    private final boolean _regularOrder;

    /**
     * Constructor of the search.
     * 
     * @param maxDepth The max depth the search is allowed to go.
     * @param playerColor The color of the player.
     * @param tt A reference to the transposition table to use
     * @param s A reference of the status of the root node
     * @param regularOrder True if a regular move ordering has to be used, false
     * in the opposite case
     */
    public RunnableFutureMiniMax(int maxDepth, int playerColor, TT tt, Status s, boolean regularOrder) {
        int remainingMoves = (Status.SIZE*Status.SIZE - 4) - s.getNumMovements();
        
        this._searchIsOn = true;
        this._finished = false;
        this._maxDepth = Math.min(maxDepth, remainingMoves);
        this._nodesWithComputedHeuristic = 0;
        this._depthReached = 0;
        this._playerColor = playerColor;
        this._lastSelectedHeuristic = 0;
        this._lastSelectedMovement = -1;
        this._tt = tt;
        this._isExact = new boolean[Status.SIZE*Status.SIZE];
        this._s = s;
        this._regularOrder = regularOrder;
    }

    /**
     * Copy constructor with a depth increase
     * 
     * @param r The instance to make the copy from.
     * @param extraDepth The extra depth to add.
     */
    public RunnableFutureMiniMax(RunnableFutureMiniMax r, int extraDepth) {
        int remainingMoves = (Status.SIZE*Status.SIZE - 4) - r._s.getNumMovements();
            
        this._searchIsOn = true;
        this._finished = false;
        this._maxDepth = Math.min(r._maxDepth+extraDepth, remainingMoves);
        this._nodesWithComputedHeuristic = 0;
        this._depthReached = 0;
        this._playerColor = r._playerColor;
        this._lastSelectedHeuristic = 0;
        this._lastSelectedMovement = -1;
        this._tt = r._tt;
        this._isExact = new boolean[Status.SIZE*Status.SIZE];
        this._s = r._s;
        this._regularOrder = r._regularOrder;
    }
    
    /**
     * Execute the task.
     */
    @Override
    public void run() {
        _lastSelectedHeuristic = minimax(
                _s,
                0,
                Float.NEGATIVE_INFINITY, 
                Float.POSITIVE_INFINITY, 
                true
        );
        
        if(_searchIsOn) {
            _finished = true;    
        }
    }

    /**
     * Cancel the execution of the task.
     * 
     * @param bln Ignored.
     * @return True.
     */
    @Override
    public boolean cancel(boolean bln) {
        _searchIsOn = false;
        return true;
    }

    /**
     * Check if the task has been canceled/interrupted.
     * 
     * @return True if it has been canceled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return !_searchIsOn;
    }

    /**
     * Check if the task has ended successfully.
     * 
     * @return True if it has ended successfully, false otherwise.
     */
    @Override
    public boolean isDone() {
        return !_searchIsOn || _finished;
    }

    /**
     * Get the result of the search if it has ended successfully
     * 
     * @return The result of the search if it has ended successfully, null 
     * otherwise
     * @throws InterruptedException if the containing thread is interrupted
     * @throws ExecutionException if the execution generated any errors.
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        while(!isDone()) {}
        
        if (_finished) {
            return new Result(
                    _nodesWithComputedHeuristic, 
                    _depthReached, 
                    _lastSelectedHeuristic, 
                    _lastSelectedMovement
            );
        } else {
            return null;
        }
    }

    /**
     * Get the result of the search if it has ended successfully in at least l 
     * [tu].
     * 
     * @param l The amount of time to wait
     * @param tu The units of the time
     * @return The result of the search if it has ended successfully, null 
     * otherwise
     * @throws InterruptedException if the containing thread is interrupted
     * @throws ExecutionException if the execution generated any errors.
     * @throws TimeoutException if the time has passed without completing the 
     * task
     */
    @Override
    public Object get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        
        while(!isDone() || (System.currentTimeMillis()-start) < tu.toMillis(l)) {}
        
        if(isDone()) {
            return get();
        } else {
            throw new TimeoutException();
        }
    }

    /**
     * Get the max depth the search is allowed to go.
     * 
     * @return The max depth the search is allowed to go.
     */
    public int getMaxDepth() {
        return _maxDepth;
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
    private float minimax(Status s, int currentDepth, float alpha, float beta, boolean isMax) {
        // Stop search if the thread recieved an interrupt
        if (Thread.currentThread().isInterrupted()) {
            _searchIsOn = false;
        }
        
        // Mark this level as exact
        _isExact[currentDepth] = true;
        
        // Check if we are in a terminal state
        if(s.isTerminal() || _maxDepth <= currentDepth) {
            _nodesWithComputedHeuristic++;
            _depthReached = Math.max(_depthReached, currentDepth);
            _lastSelectedMovement = -1;
            return s.getHeuristic(_playerColor);
        }
        
        // Retrieve the entry from transposition table
        long entry = _tt.readEntry(s);
        byte selectedNextMove = TT.extractSelectedMovementIfValidEntry(s, entry);
        if(TT.canExtractHeuristic(entry, _maxDepth-currentDepth)) {
            _depthReached = Math.max(_depthReached, currentDepth + TT.extractDepthBelow(entry));
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
        if(_regularOrder)
            s.getNextMoves(nextMoves, selectedNextMove);
        else
            s.getNextMovesInverse(nextMoves, selectedNextMove);
        
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
                    (byte)(_maxDepth-currentDepth), 
                    _isExact[currentDepth], 
                    isMax
            );
        }
        
        // Return the maxmimized or minimized bound
        _lastSelectedMovement = selectedNextMove;
        return isMax ? alpha : beta;
    }
}
