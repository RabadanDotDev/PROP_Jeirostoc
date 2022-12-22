package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Search algorithm that chooses a move based on MiniMax with iterative 
 * deepening.
 * 
 * @author raul
 * @author josep
 */
class SearchAlgMiniMaxIDS extends SearchAlgMiniMax {
    RunnableFutureMiniMax _currentRun;
        
    /**
     * Create a MiniMax search algorithm with iterative deepening.
     */
    public SearchAlgMiniMaxIDS() {
        super(1, SearchType.MINIMAX_IDS);
        _currentRun = null;
    }
    
    /**
    * Create a MiniMax search algorithm with iterative deepening with a 
    * different number of entries in the TT from the default.
     */
    public SearchAlgMiniMaxIDS(int numEntries) {
        super(1, SearchType.MINIMAX_IDS, numEntries);
        _currentRun = null;
    }

    @Override
    public void searchOFF() {
        super.searchOFF();
        if(_currentRun != null)
            _currentRun.cancel(false);
    }
    
    /**
     * Do the search for a movement based on the status s and deposit the 
     * selected movement in _lastMovementSelected, the heuristic of the movement
     * in _lastBestHeuristic, the depth reached in _depthReached and the nodes 
     * whose heuristic has been obtained in _nodesWithComputedHeuristic . It 
     * assumes that _nodesWithComputedHeuristic, _depthReached, _playerColor, 
     * _lastMovementSelected have been correctly initialized.
     * 
     * @param s The status to base the search on.
     */
    @Override
    public void doSearch(Status s) {
        // Init ID
        _maxGlobalDepth = 1;
        RunnableFutureMiniMax.Result currentResult = null;
        
        while (_searchIsOn) {
            _currentRun = new RunnableFutureMiniMax(_maxGlobalDepth, _playerColor, _tt, s);
            _currentRun.run();
            
            try {
                currentResult = (RunnableFutureMiniMax.Result)_currentRun.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(SearchAlgMiniMax.class.getName()).log(Level.SEVERE, null, ex);
                currentResult = null;
            }
            
            if(currentResult != null) {
                // Store results
                _depthReached = currentResult.depthReached;
                _lastSelectedHeuristic = currentResult.lastSelectedHeuristic;
                _lastSelectedMovement = currentResult.lastSelectedMovement;
                _nodesWithComputedHeuristic += currentResult.nodesWithComputedHeuristic;
                
                // Next depth
                _maxGlobalDepth++;
            }
        }
    }
}
