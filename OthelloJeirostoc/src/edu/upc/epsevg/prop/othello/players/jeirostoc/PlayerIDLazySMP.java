package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Player that does a search using MiniMax iteratively and using a LazySMP
 * until it gets a timeout
 * 
 * @author raul
 * @author josep
 */
public class PlayerIDLazySMP extends PlayerBase {
    ////////////////////////////////////////////////////////////////////////////
    // Executor subclass                                                      //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * ThreadPoolExecutor specialization to execute tasks in a LazySMP way.
     */
    private class LazySMPExecutor extends ThreadPoolExecutor {
        /**
         * Lock to coordinate writing the results of the search to the outer 
         * class.
         */
        private final ReentrantLock storeResultsLock = new ReentrantLock();
        
        /**
         * The amount to increment the depth of each task.
         */
        private final int _depthTaskIncrement;
        
        /**
         * Constructor of the ThreadPool
         * 
         * @param nThreads The number of threads to use.
         */
        LazySMPExecutor(int nThreads) {
            super(nThreads, nThreads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            _depthTaskIncrement = Math.max(1, nThreads/2);
        }
        
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if(r instanceof RunnableFutureMiniMax rfm) {
                // Retrieve results from runnable
                RunnableFutureMiniMax.Result result;
                
                try {
                    result = (RunnableFutureMiniMax.Result)rfm.get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(PlayerIDLazySMP.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                
                if(result == null)
                    return;
                
                // Store results  
                storeResultsLock.lock();
                try {
                    _nodesWithComputedHeuristic += result.nodesWithComputedHeuristic;
                    if( _maxDepthCompleted <  rfm.getMaxDepth() || 
                       (_maxDepthCompleted == rfm.getMaxDepth() &&  _depthReached < result.depthReached)) {
                        _maxDepthCompleted = rfm.getMaxDepth();
                        _depthReached = result.depthReached;
                        _lastSelectedHeuristic = result.lastSelectedHeuristic;
                        _lastSelectedMovement = result.lastSelectedMovement;
                    }
                } finally {
                    storeResultsLock.unlock();
                }
                    
                // Generate next task if possible
                RunnableFutureMiniMax nextTask = new RunnableFutureMiniMax(rfm, _depthTaskIncrement);
                try {
                    this.execute(nextTask);
                } catch (RejectedExecutionException e) {}
            }
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Search variables                                                       //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * The maximum number of movements a search has completed with.
     */
    private int _maxDepthCompleted;
    
    /**
     * The executor of the searches.
     */
    private LazySMPExecutor _executor;
    
    ////////////////////////////////////////////////////////////////////////////
    // Constructor                                                            //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Default constructor.
     */
    public PlayerIDLazySMP() {
        super(SearchType.MINIMAX_IDS, null, TT.DEF_NUM_ENTRIES);
        _executor = new LazySMPExecutor(Runtime.getRuntime().availableProcessors());
    }
    
    /**
     * Constructor with logging activated.
     * 
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled.
     */
    public PlayerIDLazySMP(FileWriter fw) {
        super(SearchType.MINIMAX_IDS, fw, TT.DEF_NUM_ENTRIES);
        _executor = new LazySMPExecutor(Runtime.getRuntime().availableProcessors());
    }
    
    /**
     * Constructor with custom transposition table size.
     * 
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public PlayerIDLazySMP(long numEntriesTT) {
        super(SearchType.MINIMAX, null, numEntriesTT);
        _executor = new LazySMPExecutor(Runtime.getRuntime().availableProcessors());
    }
    
    /**
     * Constructor with custom heuristic scores.
     * 
     * @param stableScoreConfig Configuration parameter value for Status: the 
     * score to evaluate the detected positions in with
     * @param diskScoresConfig Configuration parameter value for Status: a list 
     * of the scores for having captured each position
     * @param neighborScoresConfig Configuration parameter value for Status: a 
     * list of the scores for having each position as a neighbor
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public PlayerIDLazySMP(float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig, FileWriter fw, long numEntriesTT) {
        super(SearchType.MINIMAX_IDS, stableScoreConfig, diskScoresConfig, neighborScoresConfig, fw, numEntriesTT);
        _executor = new LazySMPExecutor(Runtime.getRuntime().availableProcessors());
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Move                                                                   //
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void doSearch(Status s) {
        // Start thread execution
        _maxDepthCompleted = -1;
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            _executor.execute(new RunnableFutureMiniMax(
                1 + i/2,
                s.getCurrentPlayerColor(),
                _tt,
                s,
                i%2 == 0
            ));
        }
        
        // Wait for the search to complete
        try {
            _executor.awaitTermination(10, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlayerIDLazySMP.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Regenerate executor for the next search
            _executor = new LazySMPExecutor(Runtime.getRuntime().availableProcessors());
        }
    }

    @Override
    public void timeout() {
        _executor.shutdownNow();
    }
    
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
        return "JeiroMiniMaxIDLazySMP" ;
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
        sb.append("maxDepthCompleted").append(';');
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
        sb.append(_maxDepthCompleted).append(';');
        sb.append(_tt.getNumCollisions()).append(';');
        return sb.toString();
    }
}
