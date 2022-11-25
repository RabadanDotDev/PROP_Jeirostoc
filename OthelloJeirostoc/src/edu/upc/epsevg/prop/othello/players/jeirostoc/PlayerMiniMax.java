package edu.upc.epsevg.prop.othello.players.jeirostoc;

/**
 * Player that does a search using MiniMax and ignores timeouts
 * 
 * @author raul
 * @author josep
 */
public class PlayerMiniMax extends PlayerBase {

    public PlayerMiniMax(int maxDepth) {
        super(new SearchAlgMiniMax(maxDepth));
    }
    
    @Override
    public String getName() {
        return "Jeirostoc MiniMax" ;
    }

    @Override
    public void timeout() {}
}
