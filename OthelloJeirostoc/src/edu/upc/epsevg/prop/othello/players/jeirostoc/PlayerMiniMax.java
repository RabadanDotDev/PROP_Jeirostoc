package edu.upc.epsevg.prop.othello.players.jeirostoc;

/**
 * Player that does a search using MiniMax with a given depth and ignores 
 * timeouts
 * 
 * @author raul
 * @author josep
 */
public class PlayerMiniMax extends PlayerBase {
    /**
     * Default constructor.
     * 
     * @param maxDepth The maximum number of movements the player is allowed to 
     * explore
     */
    public PlayerMiniMax(int maxDepth) {
        super(new SearchAlgMiniMax(maxDepth));
    }
    
    /**
     * Constructor with custom heuristic scores.
     * 
     * @param maxDepth The maximum number of movements the player is allowed to 
     * explore
     * @param stableScoreConfig Configuration parameter value for Status: the 
     * score to evaluate the detected positions in with
     * @param diskScoresConfig Configuration parameter value for Status: a list 
     * of the scores for having captured each position
     * @param neighborScoresConfig Configuration parameter value for Status: a 
     * list of the scores for having each position as a neighbor
     */
    public PlayerMiniMax(int maxDepth, float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig) {
        super(new SearchAlgMiniMax(maxDepth), stableScoreConfig, diskScoresConfig, neighborScoresConfig);
    }
        
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
     * Inform the player that it has run out of time. This will be ignored.
     */
    @Override
    public void timeout() {}
}
