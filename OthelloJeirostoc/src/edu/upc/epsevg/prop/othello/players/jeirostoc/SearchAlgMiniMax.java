package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    protected final TT _tt;
    
    /**
     * Create a new MiniMax search instance with a given max depth.
     * 
     * @param maxDepth The maximum depth the search is allowed to go.
     */
    public SearchAlgMiniMax(int maxDepth) {
        super(maxDepth, SearchType.MINIMAX);
        _tt = new TT();
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
        RunnableFutureMiniMax r = new RunnableFutureMiniMax(_maxGlobalDepth, _playerColor, _tt, s, true);
        r.run();
        RunnableFutureMiniMax.Result rr;
        try {
            rr = (RunnableFutureMiniMax.Result)r.get();
            _depthReached = rr.depthReached;
            _lastSelectedHeuristic = rr.lastSelectedHeuristic;
            _lastSelectedMovement = rr.lastSelectedMovement;
            _nodesWithComputedHeuristic = rr.nodesWithComputedHeuristic;
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(SearchAlgMiniMax.class.getName()).log(Level.SEVERE, null, ex);
        }
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
