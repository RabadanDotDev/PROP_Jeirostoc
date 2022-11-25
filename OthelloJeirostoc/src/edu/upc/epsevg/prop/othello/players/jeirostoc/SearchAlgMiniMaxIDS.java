package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;

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
        super(0);
        _searchType = SearchType.MINIMAX_IDS;
    }
    
    /**
     * Get next move based on the current game status
     * 
     * @param hs The current game status
     * @return The selected move
     */
    @Override
    public Move nextMove(HeuristicStatus hs) {
        Move bestSoFar = new Move(null, 0L,0,  SearchType.MINIMAX_IDS);
        
        while (_searchIsOn) {
            this._maxGlobalDepth++;
            bestSoFar = super.nextMove(hs);
        }
        
        return bestSoFar;
    }
}
