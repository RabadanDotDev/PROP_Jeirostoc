package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;

/**
 * Search algorithm that chooses a move based on MiniMax with iterative 
 * deepening
 * 
 * @author raul
 * @author josep
 */
class SearchAlgMiniMaxIDS extends SearchAlgMiniMax {
    /**
     * Create a MiniMax search algorithm with iterative deepening.
     */
    public SearchAlgMiniMaxIDS() {
        super(0, SearchType.MINIMAX_IDS);
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
        
        // Do a minimax search incrementing the maxDepth until the search is
        // stopped
        Point bestSoFar = null;
        this._maxGlobalDepth = 1;
        while (_searchIsOn) {
            this._maxGlobalDepth++;
            Point p = minimaxNextPoint(hs);
            if(_searchIsOn)
                bestSoFar = p;
        }
        
        // Return selected movement
        return new Move(bestSoFar, _nodesWithComputedHeuristic, _depthReached, _searchType);
    }
}
