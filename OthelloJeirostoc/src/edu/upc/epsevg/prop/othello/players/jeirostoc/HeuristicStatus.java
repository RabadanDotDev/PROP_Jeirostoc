package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;

/**
 * Game status with capability of computing an heuristic
 * 
 * @author raul
 * @author josep
 */
public class HeuristicStatus extends GameStatus {
    /**
     * Creates a HeuristicStatus based on a specific game status
     * 
     * @param status Game status to create HeuristicStatus from
     */
    public HeuristicStatus(int [][] status){
        super(status);
    }
    
    /**
     * Creates a HeuristicStatus based on an existing GameStatus
     * 
     * @param gs The game status to make a copy from
     */
    public HeuristicStatus(GameStatus gs) {
        super(gs);
    }
    
    /**
     * Creates a new HeuristicStatus with a movement made in "to"
     * 
     * @param to The coordinate to make the next movement
     * @return The new heuristic status
     */
    HeuristicStatus getNextStatus(java.awt.Point to) {
        HeuristicStatus hs = new HeuristicStatus(this);
        hs.movePiece(to);
        return hs;
    }
    
    double getHeuristic(CellType p){
        if(this.isGameOver() && this.GetWinner().equals(p))
            return Double.POSITIVE_INFINITY;
        else if (this.isGameOver())
            return Double.NEGATIVE_INFINITY;
        else if(p.equals(CellType.PLAYER1))
            return this.piecesCountP1 - this.piecesCountP2;
        else
            return this.piecesCountP2 - this.piecesCountP1;
    }
}
