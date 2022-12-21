package edu.upc.epsevg.prop.othello.players.jeirostoc;

/**
 * Player that does a search using MiniMax iteratively until it gets a timeout
 * 
 * @author raul
 * @author josep
 */
public class PlayerID extends PlayerBase {
    /**
     * Default constructor.
     */
    public PlayerID() {
        super(new SearchAlgMiniMaxIDS());
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
     */
    public PlayerID(float stableScoreConfig, float[] diskScoresConfig, float[] neighborScoresConfig) {
        super(new SearchAlgMiniMaxIDS(), stableScoreConfig, diskScoresConfig, neighborScoresConfig);
    }
    
    /**
     * Get the name of the player.
     * 
     * @return The name of the player
     */
    @Override
    public String getName() {
        return "Jeirostoc_MiniMaxID";
    }

    /**
     * Inform the player that it has run out of time.
     */
    @Override
    public void timeout() {
        _searchAlg.searchOFF();
    }
}
