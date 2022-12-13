package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.SearchType;
import java.util.ArrayList;
import java.util.Random;

/**
 * Search algorithm that chooses a random move
 * 
 * @author raul
 * @author josep
 */
class SearchAlgRandom extends SearchAlg {
    /**
     * The random instance.
     */
    private Random _rand;
    
    /**
     * Create a SearchAlg that just chooses a random move.
     */
    public SearchAlgRandom() {
        super(-1, SearchType.RANDOM);
        _rand = new Random();
    }
    
    /**
     * Do the search for a movement based on the status s and deposit the 
     * selected movement in _lastMovementSelected, the heuristic of the movement
     * in _lastBestHeuristic, the depth reached in _depthReached and the nodes 
     * whose heuristic has been obtained in _nodesWithComputedHeuristic . It 
     * assumes that _nodesWithComputedHeuristic, _depthReached, _playerColor, 
     * _lastMovementSelected have been correctly initialized.
     * 
     * @param s The status to base the search on
     */
    @Override
    public void doSearch(Status s) {
        _depthReached = -1;
        
        ArrayList<Status> statuses = new ArrayList<>();
        s.getNextStatuses(statuses);
        
        if(statuses.isEmpty()) {
            _lastSelectedMovement = -1;
        } else {
            int q = _rand.nextInt(statuses.size());
            _lastSelectedMovement = statuses.get(q).getLastMovement();
        }
    }
}
