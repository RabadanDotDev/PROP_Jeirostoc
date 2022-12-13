package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;

/**
 * Base class to perform a search for the best movement.
 * 
 * @author raul
 * @author josep
 */
abstract class SearchAlg {
    /**
     * The specialized class search type.
     */
    private final SearchType _searchType;
    
    /**
     * The number of movements that the last Status instance given to search 
     * had.
     */
    private int _lastNumMovements;
    
    /**
     * Indicates if the search is active or not. The change of this value to 
     * false has to be honored as soon as it is possible.
     */
    protected boolean _searchIsOn;
    
    /**
     * The current maximum depth the algorithm is allowed to go currently.
     */
    protected int _maxGlobalDepth;
    
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
     * The player's current color.
     */
    protected int _playerColor;
    
    /**
     * The computed heuristic of the last returned movement.
     */
    protected float _lastSelectedHeuristic;
    
    /**
     * The last movement selected in the last level of the search.
     */
    protected byte _lastSelectedMovement;

    /**
     * Create a MiniMax search algorithm with a given max global length.
     * 
     * @param maxGlobalDepth The max initial global depth.
     */
    protected SearchAlg(int maxGlobalDepth, SearchType searchType) {
        _searchIsOn = true;
        _maxGlobalDepth = maxGlobalDepth;
        _searchType = searchType;
        _lastNumMovements = 0;
    }
    
    /**
     * Get next move based on the current game status.
     * 
     * @param s The current game status.
     * @return The selected move.
     */
    public final Move nextMove(Status s) {
        // Init
        _lastNumMovements = s.getNumMovements();
        _nodesWithComputedHeuristic = 0;
        _depthReached = 0;
        _playerColor = s.getCurrentPlayerColor();
        _lastSelectedHeuristic = 0;
        _lastSelectedMovement = -1;
        
        // Do the actual search
        doSearch(s);
        
        // Return selected movement
        Point p = null;
        if (_lastSelectedMovement != -1) {
            p = new Point(_lastSelectedMovement/Status.SIZE, _lastSelectedMovement%Status.SIZE);
        }
        return new Move(p, _nodesWithComputedHeuristic, _depthReached, _searchType);
    }
    
    /**
     * Turn off the search.
     */
    public void searchOFF() { _searchIsOn = false; }
    
    /**
     * Turn on the search.
     */
    public void searchON() { _searchIsOn = true; }

    /**
     * Get a newline-terminated string with the header of the information 
     * retrieved from getLogLineLastSearch.
     * 
     * @return The newline-terminated string with the header of the information 
     * retrieved from getLogLineLastSearch.
     */
    public String getLogLineHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("lastNumMovements").append(';');
        sb.append("searchType").append(';');
        sb.append("maxGlobalDepth").append(';');
        sb.append("nodesWithComputedHeuristic").append(';');
        sb.append("depthReached").append(';');
        sb.append("playerColor").append(';');
        sb.append("lastSelectedHeuristic").append(';');
        sb.append("lastSelectedMovement").append(';');
        return sb.toString();
    }
    
    /**
     * Get a semicolon-separated string with all the captured information about 
     * the last search.
     * 
     * @return The newline-terminated string with all the captured information 
     * about the last search. 
     */
    public String getLogLineLastSearch() {
        StringBuilder sb = new StringBuilder();
        sb.append(_lastNumMovements).append(';');
        sb.append(_searchType).append(';');
        sb.append(_maxGlobalDepth).append(';');
        sb.append(_nodesWithComputedHeuristic).append(';');
        sb.append(_depthReached).append(';');
        sb.append(_playerColor).append(';');
        sb.append(_lastSelectedHeuristic).append(';');
        sb.append(_lastSelectedMovement).append(';');
        return sb.toString();
    }
    
    /**
     * Do the search for a movement based on the status s and deposit the 
     * selected movement in _lastMovementSelected, the heuristic of the movement
     * in _lastBestHeuristic, the depth reached in _depthReached and the nodes 
     * whose heuristic has been obtained in _nodesWithComputedHeuristic. It 
     * assumes that _nodesWithComputedHeuristic, _depthReached, _playerColor, 
     * _lastMovementSelected have been correctly initialized.
     * 
     * @param s The status to base the search on
     */
    protected abstract void doSearch(Status s);
}
