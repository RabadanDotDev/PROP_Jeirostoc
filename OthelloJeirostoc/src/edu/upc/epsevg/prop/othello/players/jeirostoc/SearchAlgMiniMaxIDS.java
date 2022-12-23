package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Search algorithm that chooses a move based on MiniMax with iterative 
 * deepening.
 * 
 * @author raul
 * @author josep
 */
class SearchAlgMiniMaxIDS extends SearchAlgMiniMax {
    class LazySMPExecutor extends ThreadPoolExecutor {
        private final ReentrantLock storeResultsLock = new ReentrantLock();
        private final int _depthTaskIncrement;
        
        LazySMPExecutor(int nThreads) {
            super(nThreads, nThreads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            _depthTaskIncrement = Math.max(1, nThreads/2);
        }
        
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if(r instanceof RunnableFutureMiniMax rfm) {
                // Get results
                RunnableFutureMiniMax.Result result;
                
                try {
                    result = (RunnableFutureMiniMax.Result)rfm.get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(SearchAlgMiniMaxIDS.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                
                if(result == null)
                    return;
                
                // Store results  
                storeResultsLock.lock();
                try {
                    _nodesWithComputedHeuristic += result.nodesWithComputedHeuristic;
                    if(_depthReached < result.depthReached) {
                        _depthReached = result.depthReached;
                        _lastSelectedHeuristic = result.lastSelectedHeuristic;
                        _lastSelectedMovement = result.lastSelectedMovement;
                    }
                } finally {
                    storeResultsLock.unlock();
                }
                    
                // Generate next task
                RunnableFutureMiniMax nextTask = new RunnableFutureMiniMax(rfm, _depthTaskIncrement);
                try {
                    this.execute(nextTask);
                } catch (RejectedExecutionException e) {}
            }
        }
    }
    
    RunnableFutureMiniMax _currentRun;
    LazySMPExecutor _executor;
        
    /**
     * Create a MiniMax search algorithm with iterative deepening.
     */
    public SearchAlgMiniMaxIDS() {
        super(1, SearchType.MINIMAX_IDS);
        _currentRun = null;
        _executor = new LazySMPExecutor(Runtime.getRuntime().availableProcessors());
    }
    
    /**
    * Create a MiniMax search algorithm with iterative deepening with a 
    * different number of entries in the TT from the default.
     */
    public SearchAlgMiniMaxIDS(int numEntries) {
        super(1, SearchType.MINIMAX_IDS, numEntries);
        _currentRun = null;
        _executor = new LazySMPExecutor(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void searchOFF() {
        super.searchOFF();
        if(_currentRun != null)
            _currentRun.cancel(false);
        _executor.shutdownNow();
    }
    
    /**
     * Do the search for a movement based on the status s and deposit the 
     * selected movement in _lastMovementSelected, the heuristic of the movement
     * in _lastBestHeuristic, the depth reached in _depthReached and the nodes 
     * whose heuristic has been obtained in _nodesWithComputedHeuristic . It 
     * assumes that _nodesWithComputedHeuristic, _depthReached, _playerColor, 
     * _lastMovementSelected have been correctly initialized.
     * 
     * @param s The status to base the search on.
     */
    @Override
    public void doSearch(Status s) {
        doSearchLazySMP(s);
    }
    
    private void doSearchLazySMP(Status s) {
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            _executor.execute(new RunnableFutureMiniMax(
                1 + i%2,
                s.getCurrentPlayerColor(),
                _tt,
                s,
                i%2 == 0
            ));
        }
        
        try {
            _executor.awaitTermination(10, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchAlgMiniMaxIDS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        _executor = new LazySMPExecutor(Runtime.getRuntime().availableProcessors());
    }
    
    private void doSearchSeq(Status s) {
        RunnableFutureMiniMax.Result currentResult;
        int remainingMoves = (Status.SIZE*Status.SIZE - 4) - s.getNumMovements();
        for (_maxGlobalDepth = 1; _maxGlobalDepth <= remainingMoves; _maxGlobalDepth++) {
            _currentRun = new RunnableFutureMiniMax(_maxGlobalDepth, _playerColor, _tt, s, true);
            _currentRun.run();
            
            try {
                currentResult = (RunnableFutureMiniMax.Result)_currentRun.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(SearchAlgMiniMax.class.getName()).log(Level.SEVERE, null, ex);
                currentResult = null;
            }
            
            if(currentResult != null) {
                // Store results
                _depthReached = currentResult.depthReached;
                _lastSelectedHeuristic = currentResult.lastSelectedHeuristic;
                _lastSelectedMovement = currentResult.lastSelectedMovement;
                _nodesWithComputedHeuristic += currentResult.nodesWithComputedHeuristic;
            } else {
                break;
            }
        }
    }
}
