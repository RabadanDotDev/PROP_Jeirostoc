package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Player that conserves intermediate heuristic calculations and performs a 
 * search depending on how it is initialized.
 * 
 * @author raul
 * @author josep
 */
abstract class PlayerBase implements IAuto, IPlayer {
    ////////////////////////////////////////////////////////////////////////////
    // Logging config                                                         //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * End of the filename to log the actions to. 
     */
    private final static String CSV_BASE_FILE_NAME = "move_log.csv";
    
    /**
     * Toggle to determine if logging should be done.
     */
    private final static boolean DO_LOG = true;
    
    /**
     * FileWriter where to write the log of the actions of the player.
     */
    private FileWriter fw;
    
    ////////////////////////////////////////////////////////////////////////////
    // Status heuristic config                                                //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Configuration parameter value for Status: score to give for each 
     * registered stable position.
     */
    private final float _stableScoreConfig;
    
    /**
     * Configuration parameter value for Status: score to give for each captured 
     * position.
     */
    private final float[] _diskScoresConfig;
    
    /**
     * Configuration parameter value for Status: Score to give for each 
     * neighboring position.
     */
    private final float[] _neighborScoresConfig;
    
    ////////////////////////////////////////////////////////////////////////////
    // Search variables                                                       //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * The specialized class search type.
     */
    private final SearchType _searchType;
    
    /**
     * The number of movements of the last given Status to the player.
     */
    protected long _numLastMovements;
    
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
    
    ////////////////////////////////////////////////////////////////////////////
    // Constructor                                                            //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Protected constructor. It takes a SearchAlg strategy class to use.
     * @param searchAlg The SearchAlg to use
     */
    protected PlayerBase(SearchType searchType) {
        this(searchType, Status.STABLE_SCORE_DEFAULT, Status.DISK_SCORES_DEFAULT, Status.NEIGHBOR_SCORES_DEFAULT);
    }
    
    /**
     * Protected constructor. It takes a SearchAlg strategy class to use.
     * @param searchAlg The SearchAlg to use
     * @param stableScoreConfig Configuration parameter value for Status: the 
     * score to evaluate the detected positions in with
     * @param diskScoresConfig Configuration parameter value for Status: a list 
     * of the scores for having captured each position
     * @param neighborScoresConfig Configuration parameter value for Status: a 
     * list of the scores for having each position as a neighbor
     */
    protected PlayerBase(SearchType searchType, float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig) {
        // Search config
        _searchType = searchType;
        
        // Log config
        if(DO_LOG) { 
            try {
                fw = new FileWriter(
                        System.currentTimeMillis() + "_" +
                        getName() + "_" + 
                        CSV_BASE_FILE_NAME
                );
                
                fw.append(getLogLineHeader());
                fw.append("\n");
            } catch (IOException ex) {
                Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't open csv file", ex);
            } 
        }
        
        // Init score config
        _stableScoreConfig = stableScoreConfig;
        _diskScoresConfig = diskScoresConfig;
        _neighborScoresConfig = neighborScoresConfig;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Move                                                                   //
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Move move(GameStatus gs) {
        // Init search
        Status s = new Status(gs, _stableScoreConfig, _diskScoresConfig, _neighborScoresConfig);
        _numLastMovements = s.getNumMovements();
        _nodesWithComputedHeuristic = 0;
        _depthReached = 0;
        _playerColor = s.getCurrentPlayerColor();
        _lastSelectedHeuristic = 0;
        _lastSelectedMovement = -1;
        
        // Do the search
        doSearch(s);
        
        // Log selected movement
        if(DO_LOG)
            logLastSearch();
        
        // Return selected movement
        Point p = null;
        if (_lastSelectedMovement != -1) {
            p = new Point(_lastSelectedMovement/Status.SIZE, _lastSelectedMovement%Status.SIZE);
        }
        return new Move(p, _nodesWithComputedHeuristic, _depthReached, _searchType);
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
    
    ////////////////////////////////////////////////////////////////////////////
    // Logging                                                                //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Get a newline-terminated string with the header of the information 
     * retrieved from getLogLineLastSearch.
     * 
     * @return The newline-terminated string with the header of the information 
     * retrieved from getLogLineLastSearch.
     */
    protected String getLogLineHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("numMovements").append(';');
        sb.append("searchType").append(';');
        sb.append("heuristic_ver").append(';');
        sb.append("playerColor").append(';');
        sb.append("nodesWithComputedHeuristic").append(';');
        sb.append("lastSelectedMovement").append(';');
        sb.append("lastSelectedHeuristic").append(';');
        sb.append("depthReached").append(';');
        return sb.toString();
    }
    
    /**
     * Get a semicolon-separated string with all the captured information about 
     * the last search.
     * 
     * @return The newline-terminated string with all the captured information 
     * about the last search. 
     */
    protected String getLogLineLastSearch() {
        StringBuilder sb = new StringBuilder();
        sb.append(_numLastMovements).append(';');
        sb.append(_searchType).append(';');
        sb.append(Status.HEURISTIC_VER).append(';');
        sb.append(_playerColor).append(';');
        sb.append(_nodesWithComputedHeuristic).append(';');
        sb.append(_lastSelectedMovement).append(';');
        sb.append(_lastSelectedHeuristic).append(';');
        sb.append(_depthReached).append(';');
        return sb.toString();
    }
    
    private void logLastSearch() {
        try {
            fw.append(getLogLineLastSearch());
            fw.append('\n');
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't write to csv file", ex);
        }
    }
}
