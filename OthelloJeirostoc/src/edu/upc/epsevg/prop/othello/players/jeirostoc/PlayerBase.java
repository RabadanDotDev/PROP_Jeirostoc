package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
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
    private final static String  CSV_BASE_FILE_NAME = "move_log.csv";
    private final static boolean DO_LOG = true;
    private static FileWriter fw = null;
    
    /**
     * The search algorithm strategy the instance uses
     */
    protected final SearchAlg _searchAlg;
    
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
    
    /**
     * Protected constructor. It takes a SearchAlg strategy class to use.
     * @param searchAlg The SearchAlg to use
     */
    protected PlayerBase(SearchAlg searchAlg) {
        this(searchAlg, Status.STABLE_SCORE_DEFAULT, Status.DISK_SCORES_DEFAULT, Status.NEIGHBOR_SCORES_DEFAULT);
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
    protected PlayerBase(SearchAlg searchAlg, float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig) {
        // Init search alg
        _searchAlg = searchAlg;
        
        // Init log
        if(DO_LOG) { 
            try {
                fw = new FileWriter(
                        System.currentTimeMillis() + "_" +
                        getName() + "_" + 
                        CSV_BASE_FILE_NAME
                );
                
                fw.append(_searchAlg.getLogLineHeader());
                fw.append("heuristic_ver\n");
            } catch (IOException ex) {
                Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't open csv file", ex);
            } 
        }
        
        // Init score config
        _stableScoreConfig = stableScoreConfig;
        _diskScoresConfig = diskScoresConfig;
        _neighborScoresConfig = neighborScoresConfig;
    }
    
    @Override
    public Move move(GameStatus gs) {
        // Update status
        Status s = new Status(gs, _stableScoreConfig, _diskScoresConfig, _neighborScoresConfig);
        
        // Do search
        _searchAlg.searchON();
        Move m = _searchAlg.nextMove(s);
        
        // Log selected movement
        if(DO_LOG)
            logLastSearch();
        
        // Return result
        return m;
    }
    
    private void logLastSearch() {
        try {
            fw.append(_searchAlg.getLogLineLastSearch());
            fw.append(Float.toString(Status.HEURISTIC_VER));
            fw.append('\n');
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't write to csv file", ex);
        }
    }
}
