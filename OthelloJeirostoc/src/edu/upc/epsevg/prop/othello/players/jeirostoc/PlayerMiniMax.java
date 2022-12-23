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
    
    /**
     * Transposition table.
     */
    private final TT _tt;
    
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
        super(SearchType.MINIMAX, null);
        _maxDepth = maxDepth;
        _tt = new TT();
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
        super(SearchType.MINIMAX, fw);
        _maxDepth = maxDepth;
        _tt = new TT();
    }
    
    /**
     * Constructor with custom transposition table size.
     * 
     * @param maxDepth The maximum number of movements the player is allowed to 
     * explore
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public PlayerMiniMax(int maxDepth, int numEntriesTT) {
        super(SearchType.MINIMAX, null);
        _maxDepth = maxDepth;
        _tt = new TT(numEntriesTT);
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
     */
    public PlayerMiniMax(int maxDepth, float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig, FileWriter fw) {
        super(SearchType.MINIMAX, stableScoreConfig, diskScoresConfig, neighborScoresConfig, fw);
        _maxDepth = maxDepth;
        _tt = new TT();
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
    
    @Override
    public String getLogLineHeader() {
        StringBuilder sb = new StringBuilder(super.getLogLineHeader());
        sb.append("maxDepth").append(';');
        sb.append("ttColissions").append(';');
        return sb.toString();
    }
    
    @Override
    public String getLogLineLastSearch() {
        StringBuilder sb = new StringBuilder(super.getLogLineLastSearch());
        sb.append(_maxDepth).append(';');
        sb.append(_tt.getNumCollisions()).append(';');
        return sb.toString();
    }
}
