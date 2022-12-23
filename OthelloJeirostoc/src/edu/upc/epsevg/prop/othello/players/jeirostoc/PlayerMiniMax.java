package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;
import java.io.FileWriter;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Player that does a search using MiniMax with a given depth and ignores 
 * timeouts
 * 
 * @author raul
 * @author josep
 */
public class PlayerMiniMax extends PlayerBase {
    ////////////////////////////////////////////////////////////////////////////
    // Search variables                                                       //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * The maximum number of movements the player is allowed to explore.
     */
    private final int _maxDepth;
    
    ////////////////////////////////////////////////////////////////////////////
    // Constructor                                                            //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Default constructor.
     * 
     * @param maxDepth The maximum number of movements the player is allowed to 
     * explore
     */
    public PlayerMiniMax(int maxDepth) {
        super(SearchType.MINIMAX, null, TT.DEF_NUM_ENTRIES);
        _maxDepth = maxDepth;
    }    
    
    /**
     * Constructor with logging activated.
     * 
     * @param maxDepth The maximum number of movements the player is allowed to 
     * explore
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled.
     */
    public PlayerMiniMax(int maxDepth, FileWriter fw) {
        super(SearchType.MINIMAX, fw, TT.DEF_NUM_ENTRIES);
        _maxDepth = maxDepth;
    }
    
    /**
     * Constructor with custom transposition table size.
     * 
     * @param maxDepth The maximum number of movements the player is allowed to 
     * explore
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public PlayerMiniMax(int maxDepth, long numEntriesTT) {
        super(SearchType.MINIMAX, null, numEntriesTT);
        _maxDepth = maxDepth;
    }
    
    /**
     * Constructor with custom heuristic scores  and logging.
     * 
     * @param maxDepth The maximum number of movements the player is allowed to 
     * explore
     * @param stableScoreConfig Configuration parameter value for Status: the 
     * score to evaluate the detected positions in with
     * @param diskScoresConfig Configuration parameter value for Status: a list 
     * of the scores for having captured each position
     * @param neighborScoresConfig Configuration parameter value for Status: a 
     * list of the scores for having each position as a neighbor
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled.
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public PlayerMiniMax(int maxDepth, float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig, FileWriter fw, long numEntriesTT) {
        super(SearchType.MINIMAX, stableScoreConfig, diskScoresConfig, neighborScoresConfig, fw, numEntriesTT);
        _maxDepth = maxDepth;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Move                                                                   //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Do the search for a movement based on the status s and deposit the
     * selected movement in _lastMovementSelected, the heuristic of the movement
     * in _lastBestHeuristic, the depth reached in _depthReached and the nodes
     * whose heuristic has been obtained in _nodesWithComputedHeuristic. It
     * assumes that _nodesWithComputedHeuristic, _depthReached, _playerColor,
     * _lastMovementSelected have been correctly initialized.
     * 
     * @param s The status to base the search on.
     */
    @Override
    public void doSearch(Status s) {
        RunnableFutureMiniMax r = new RunnableFutureMiniMax(_maxDepth, _playerColor, _tt, s, true);
        r.run();
        RunnableFutureMiniMax.Result rr;
        try {
            rr = (RunnableFutureMiniMax.Result)r.get();
            _depthReached = rr.depthReached;
            _lastSelectedHeuristic = rr.lastSelectedHeuristic;
            _lastSelectedMovement = rr.lastSelectedMovement;
            _nodesWithComputedHeuristic = rr.nodesWithComputedHeuristic;
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(PlayerMiniMax.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Inform the player that it has run out of time. This will be ignored.
     */
    @Override
    public void timeout() {}

    ////////////////////////////////////////////////////////////////////////////
    // Logging                                                                //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Get the name of the player.
     * 
     * @return The name of the player
     */
    @Override
    public String getName() {
        return "Jeirostoc_MiniMax" ;
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
        sb.append("maxDepth").append(';');
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
        sb.append(_maxDepth).append(';');
        sb.append(_tt.getNumCollisions()).append(';');
        return sb.toString();
    }
}
