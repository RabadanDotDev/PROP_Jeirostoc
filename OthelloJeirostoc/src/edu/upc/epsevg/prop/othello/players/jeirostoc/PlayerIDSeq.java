package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;
import java.io.FileWriter;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Player that does a search using MiniMax iteratively and sequentially until 
 * it gets a timeout.
 * 
 * @author raul
 * @author josep
 */
public class PlayerIDSeq extends PlayerBase {
    ////////////////////////////////////////////////////////////////////////////
    // Search variables                                                       //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * The maximum number of movements a search has started with.
     */
    private int _maxDepthStarted;
    
    /**
     * The current run.
     */
    RunnableFutureMiniMax _currentRun;
    
    ////////////////////////////////////////////////////////////////////////////
    // Constructor                                                            //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Default constructor.
     */
    public PlayerIDSeq() {
        super(SearchType.MINIMAX_IDS, null, TT.DEF_NUM_ENTRIES);
    }
    
    /**
     * Constructor with logging activated.
     * 
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled.
     */
    public PlayerIDSeq(FileWriter fw) {
        super(SearchType.MINIMAX_IDS, fw, TT.DEF_NUM_ENTRIES);
    }
    
    /**
     * Constructor with custom transposition table size.
     * 
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public PlayerIDSeq(long numEntriesTT) {
        super(SearchType.MINIMAX, null, numEntriesTT);
    }
    
    /**
     * Constructor with custom heuristic scores and logging.
     * 
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
    public PlayerIDSeq(float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig, FileWriter fw, long numEntriesTT) {
        super(SearchType.MINIMAX_IDS, stableScoreConfig, diskScoresConfig, neighborScoresConfig, fw, numEntriesTT);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Move                                                                   //
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void doSearch(Status s) {
        RunnableFutureMiniMax.Result currentResult;
        int remainingMoves = (Status.SIZE*Status.SIZE - 4) - s.getNumMovements();
        
        for (_maxDepthStarted = 1; _maxDepthStarted <= remainingMoves; _maxDepthStarted++) {
            // Search current depth
            _currentRun = new RunnableFutureMiniMax(_maxDepthStarted, _playerColor, _tt, s, true);
            _currentRun.run();
            try {
                currentResult = (RunnableFutureMiniMax.Result)_currentRun.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(PlayerIDSeq.class.getName()).log(Level.SEVERE, null, ex);
                currentResult = null;
            }
            
            if(currentResult == null) {
                // End search
                break;
            } else {
                // Store results
                _depthReached = currentResult.depthReached;
                _lastSelectedHeuristic = currentResult.lastSelectedHeuristic;
                _lastSelectedMovement = currentResult.lastSelectedMovement;
                _nodesWithComputedHeuristic += currentResult.nodesWithComputedHeuristic;
            }
        }
    }

    @Override
    public void timeout() {
        if(_currentRun != null)
            _currentRun.cancel(false);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Logging                                                                //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Get the name of the player.
     * 
     * @return The name of the player.
     */
    @Override
    public String getName() {
        return "JeiroMiniMaxIDSeq" ;
    }
        
    /**
     * Get a semicolon-separated string with the header of the information 
     * retrieved from getLogLineLastSearch.
     * 
     * @return The newline-terminated string with the header of the information 
     * retrieved from getLogLineLastSearch.
     */
    @Override
    public String getLogLineHeader() {
        StringBuilder sb = new StringBuilder(super.getLogLineHeader());
        sb.append("maxDepthStarted").append(';');
        sb.append("ttColissions").append(';');
        return sb.toString();
    }
    
    /**
     * Get a semicolon-separated string with all the captured information about 
     * the last search.
     * 
     * @return The newline-terminated string with all the captured information 
     * about the last search. 
     */
    @Override
    public String getLogLineLastSearch() {
        StringBuilder sb = new StringBuilder(super.getLogLineLastSearch());
        sb.append(_maxDepthStarted).append(';');
        sb.append(_tt.getNumCollisions()).append(';');
        return sb.toString();
    }
}
