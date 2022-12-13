package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;

/**
 * Search algorithm that chooses a move based on MiniMax with iterative 
 * deepening.
 * 
 * @author raul
 * @author josep
 */
class SearchAlgMiniMaxIDS extends SearchAlgMiniMax {
    /**
     * Create a MiniMax search algorithm with iterative deepening.
     */
    public SearchAlgMiniMaxIDS() {
        super(1, SearchType.MINIMAX_IDS);
    }
    
    /**
    * Create a MiniMax search algorithm with iterative deepening with a 
    * different number of entries in the TT from the default.
     */
    public SearchAlgMiniMaxIDS(int numEntries) {
        super(1, SearchType.MINIMAX_IDS, numEntries);
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
        float lastHeuristicSoFar = 0.0f;
        byte lastMovementSoFar = -1;
        _maxGlobalDepth = 1;
        
        while (_searchIsOn) {
            // Do search at the current depth
            float heuristic = minimax(
                s,
                0,
                Float.NEGATIVE_INFINITY, 
                Float.POSITIVE_INFINITY, 
                true
            );
            
            if(_searchIsOn) {
                // Store results
                lastHeuristicSoFar = heuristic;
                lastMovementSoFar = _lastSelectedMovement;
                
                // Next depth
                _maxGlobalDepth++;
            }
        }
        
        // Store the last complete values
        _lastSelectedHeuristic = lastHeuristicSoFar;
        _lastSelectedMovement = lastMovementSoFar;
    }
}
