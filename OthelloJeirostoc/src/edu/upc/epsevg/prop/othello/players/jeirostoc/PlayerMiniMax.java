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
     * Get the name of the player.
     * 
     * @return The name of the player
     */
    @Override
    public String getName() {
        return "Jeirostoc MiniMax" ;
    }
    
    /**
     * Inform the player that it has run out of time. This will be ignored.
     */
    @Override
    public void timeout() {}
}
