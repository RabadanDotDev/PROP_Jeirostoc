package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;

/**
 * Player that conserves intermediate heuristic calculations and performs a 
 * search depending on how it is initialized.
 * 
 * @author raul
 * @author josep
 */
abstract class PlayerBase implements IAuto, IPlayer {
    /**
     * The search algorithm strategy the instance uses
     */
    protected final SearchAlg _searchAlg;
    
    /**
     * The last status from the game, used to cache heuristic computations 
     * between turns in case it is needed
     */
    private HeuristicStatus _lastStatus;
    
    /**
     * Protected constructor. It takes a SearchAlg strategy class to use.
     * @param searchAlg The SearchAlg to use
     */
    protected PlayerBase(SearchAlg searchAlg) {
        _searchAlg = searchAlg;
    }
    
    @Override
    public Move move(GameStatus gs) {
        // Update status
        HeuristicStatus nhs = new HeuristicStatus(gs, _lastStatus);
        
        // Do search
        _searchAlg.searchON();
        Move m = _searchAlg.nextMove(nhs);
        
        // Return result
        if(m.getTo() != null)
            _lastStatus = nhs.getNextStatus(m.getTo());
        
        return m;
    }
}
