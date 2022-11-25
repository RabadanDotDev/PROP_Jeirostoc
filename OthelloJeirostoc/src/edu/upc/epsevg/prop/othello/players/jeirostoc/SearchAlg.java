package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.Move;

/**
 * Base class to perform a search for the best movement.
 * 
 * @author raul
 * @author josep
 */
abstract class SearchAlg {
    protected boolean _searchIsOn;
    protected int _maxGlobalDepth;

    /**
     * Create a MiniMax search algorithm with a given max global length
     * 
     * @param maxGlobalDepth 
     */
    protected SearchAlg(int maxGlobalDepth) {
        _searchIsOn = true;
        _maxGlobalDepth = maxGlobalDepth;
    }
    
    /**
     * Get next move based on the current game status
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
