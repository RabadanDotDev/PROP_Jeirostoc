package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.io.FileWriter;

/**
 * Player that does a search using MiniMax iteratively until it gets a timeout.
 * 
 * @author raul
 * @author josep
 */
public class PlayerID extends PlayerIDLazySMP {
    final String configName;
    
    ////////////////////////////////////////////////////////////////////////////
    // Constructor                                                            //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Default constructor.
     */
    public PlayerID() {
        super();
        configName = "";
    }
    
    /**
     * Constructor with custom transposition table size.
     * 
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public PlayerID(long numEntriesTT) {
        super(numEntriesTT);
        configName = "";
    }    
    
    /**
     * Constructor with logging activated.
     * 
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled.
     */
    public PlayerID(FileWriter fw) {
        super(fw);
        configName = "";
    }
    
    /**
     * Constructor with custom heuristic scores.
     * 
     * @param stableScoreConfig Configuration parameter value for Status: the 
     * score to evaluate the detected positions in with.
     * @param diskScoresConfig Configuration parameter value for Status: a list 
     * of the scores for having captured each position.
     * @param neighborScoresConfig Configuration parameter value for Status: a 
     * list of the scores for having each position as a neighbor.
     * @param fw File writer to write the logs in csv format to. If it is null, 
     * logging is disabled.
     * @param name Name of the configuration.
     * @param numEntriesTT The number of entries in the transposition table.
     */
    public PlayerID(float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig, FileWriter fw, String name, long numEntriesTT) {    
        super(stableScoreConfig, diskScoresConfig, neighborScoresConfig, fw, numEntriesTT);
        this.configName = name;
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
        return "JeiroMiniMaxID_" + configName;
    }
}
