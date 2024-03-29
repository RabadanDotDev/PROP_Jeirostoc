package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
abstract public class PlayerBase implements IAuto, IPlayer {
    ////////////////////////////////////////////////////////////////////////////
    // Logging variables                                                      //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * FileWriter where to write the log of the actions of the player.
     */
    private final FileWriter _fw;
    
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
    
    /**
     * Transposition table.
     */
    protected final TT _tt;
    
    ////////////////////////////////////////////////////////////////////////////
    // TT config, creation and dumping                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * The filename to store and read the transposition table from.
     */
    private static final String TT_FILENAME = "JeirostocTranspositionTable.data";
    
    /**
     * The max number of movements for the restricted table.
     */
    private static final int MAX_MOVES_RESTRICTED = 10;
    
    /**
     * The type of TT to create.
     */
    private static boolean createRestrictedTable = false;
    
    /**
     * Set if TT should be created with the base class or the restricted class.
     * 
     * @param b True if they should be restricted, false otherwise.
     */
    public static void setCreateRestrictedTable(boolean b) {
        createRestrictedTable = b;
    }
    
    /**
     * Create a TT with the given number entries.
     * 
     * @param numEntriesTT The number of entries.
     * @return The TT with the given number entries.
     */
    public static TT createTable(int numEntriesTT) {
        // Instantiate table
        TT tt;
        
        if (createRestrictedTable) {
            tt = new TTRestricted((int)numEntriesTT, MAX_MOVES_RESTRICTED);
        } else {
            tt = new TT((int)numEntriesTT);
        }
        
        // Try to fill the table from an already existing table from disk
        try {
            BufferedReader br = new BufferedReader(new FileReader(TT_FILENAME));
            tt.fill(br);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlayerBase.class.getName()).log(Level.WARNING, "Could not find the TT table!");
        }
        
        return tt;
    }
    
    /**
     * Write the contents of the TT into bw.
     * 
     * @param bw The buffered writer to write into.
     */
    public void dumpTT(BufferedWriter bw) {
        _tt.dump(bw);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Constructor                                                            //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Protected constructor.
     * 
     * @param searchType The SearchType of the specialization.
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled.
     * @param numEntriesTT The number of entries in the TT.
     */
    protected PlayerBase(SearchType searchType, FileWriter fw, long numEntriesTT) {
        this(searchType, Status.STABLE_SCORE_DEFAULT, Status.DISK_SCORES_DEFAULT, Status.NEIGHBOR_SCORES_DEFAULT, fw, numEntriesTT);
    }
    
    /**
     * Protected constructor.
     * @param searchType The SearchType of the specialization.
     * @param stableScoreConfig Configuration parameter value for Status: the 
     * score to evaluate the detected positions in with.
     * @param diskScoresConfig Configuration parameter value for Status: a list 
     * of the scores for having captured each position.
     * @param neighborScoresConfig Configuration parameter value for Status: a 
     * list of the scores for having each position as a neighbor.
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled.
     * @param numEntriesTT The number of entries in the transposition table.
     */
    protected PlayerBase(SearchType searchType, float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig, FileWriter fw, long numEntriesTT) {
        // Init search config
        _searchType = searchType;
        
        // TT
        _tt = createTable((int)numEntriesTT);
        
        // Log config
        _fw = fw;
        if(_fw != null) { 
            try {
                fw.append(getLogLineHeader());
                fw.append("\n");
            } catch (IOException ex) {
                Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't write csv header", ex);
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
        if(_fw != null)
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
     * Get a semicolon-separated string with the header of the information 
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
    
    /**
     * Write to the _fw the log of the last search.
     */
    private void logLastSearch() {
        try {
            _fw.append(getLogLineLastSearch());
            _fw.append('\n');
            _fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't write entry to csv file", ex);
        }
    }
}
