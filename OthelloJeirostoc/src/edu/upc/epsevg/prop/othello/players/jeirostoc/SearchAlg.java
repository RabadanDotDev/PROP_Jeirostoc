package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;

/**
 * Base class to perform a search for the best movement.
 * 
 * @author raul
 * @author josep
 */
abstract class SearchAlg {
    /**
     * Indicates if the search is active or not. This value has to be honored 
     * as soon as it is detected
     */
    protected boolean _searchIsOn;
    
    /**
     * The current maximum depth the algorithm is allowed to go.
     */
    protected int _maxGlobalDepth;
    
    /**
     * The specialized class search type.
     */
    protected final SearchType _searchType;

    /**
     * Create a MiniMax search algorithm with a given max global length
     * 
     * @param maxGlobalDepth 
     */
    protected SearchAlg(int maxGlobalDepth, SearchType searchType) {
        _searchIsOn = true;
        _maxGlobalDepth = maxGlobalDepth;
        _searchType = searchType;
    }
    
    /**
     * Get next move based on the current game status.
     * 
     * @param hs The current game status
     * @return The selected move
     */
    public abstract Move nextMove(HeuristicStatus hs);
    
    /**
     * Turn on the search.
     */
    public void searchOFF() { _searchIsOn = false; }
    
    /**
     * Turn off the search.
     */
    public void searchON() { _searchIsOn = true; }
}
