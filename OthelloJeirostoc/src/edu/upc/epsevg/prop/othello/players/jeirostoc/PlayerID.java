package edu.upc.epsevg.prop.othello.players.jeirostoc;

/**
 * Player that does a search using MiniMax iteratively until it gets a timeout
 * 
 * @author raul
 * @author josep
 */
public class PlayerID extends PlayerBase {
    public PlayerID() {
        super(new SearchAlgMiniMaxIDS());
    }
    
    @Override
    public String getName() {
        return "Jeirostoc MiniMaxID";
    }

    @Override
    public void timeout() {
        _searchAlg.searchOFF();
    }
}
