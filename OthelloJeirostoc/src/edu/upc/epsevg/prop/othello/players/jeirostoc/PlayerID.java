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
