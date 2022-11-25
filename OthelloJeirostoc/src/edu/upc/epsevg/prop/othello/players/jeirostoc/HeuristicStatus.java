package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;

/**
 * Game status with capability of computing an heuristic
 * 
 * @author raul
 * @author josep
 */
class HeuristicStatus extends GameStatus {
    private final StatusEntry entry;
    
    /**
     * Creates a HeuristicStatus based on a specific game status
     * 
     * @param status Game status to create HeuristicStatus from
     */
    public HeuristicStatus(int [][] status){
        super(status);
        entry = new StatusEntry(board_occupied, board_color, currentPlayer);
    }
    
    /**
     * Creates a HeuristicStatus based on an existing GameStatus
     * 
     * @param gs The game status to make a copy from
     */
    public HeuristicStatus(GameStatus gs) {
        this(gs, null);
    }

    /**
     * Creates a HeuristicStatus based on an existing GameStatus and copies all 
     * the relevant intermediary information from lastStatus
     * 
     * @param gs The game status to make a copy from
     * @param lastStatus The last heuristic status to get the relevant 
     * intermediary information from
     */
    public HeuristicStatus(GameStatus gs, HeuristicStatus lastStatus) {
        super(gs);
        entry = new StatusEntry(board_occupied, board_color, currentPlayer);
    }
    
    /**
     * Creates a new HeuristicStatus with a movement made in "to"
     * 
     * @param to The coordinate to make the next movement
     * @return The new heuristic status
     */
    public HeuristicStatus getNextStatus(java.awt.Point to) {
        HeuristicStatus hs = new HeuristicStatus(this);
        hs.movePiece(to);
        return hs;
    }
    
    /**
     * Get the heuristic of the current state of the game
     * 
     * @param p The cell type to use to calculate the heursitic from
     * @return The heuristic of the currrent state of the game
     */
    public double getHeuristic(CellType p){
        if(this.isGameOver() && this.GetWinner().equals(p))
            return Double.POSITIVE_INFINITY;
        else if (this.isGameOver())
            return Double.NEGATIVE_INFINITY;
        else if(p.equals(CellType.PLAYER1))
            return this.piecesCountP1 - this.piecesCountP2;
        else
            return this.piecesCountP2 - this.piecesCountP1;
    }
    
    /**
     * Get the number of movements (without counting skipped turns) the current
     * game has had
     * 
     * @return The number of movements this status the current game has had
     */
    public int getMovementCount() {
        return this.piecesCountP1 + this.piecesCountP2 - 4;
    }
    
    /**
     * Get a copy of the entry representing the current status of the game
     * 
     * @return The copy of the entry representing the current status of the game
     */
    public StatusEntry getAsEntry() {
        return entry.clone();
    }

    @Override
    public void movePiece(Point point) {
        super.movePiece(point);
        entry.swapPlayer();
    }

    @Override
    public int hashCode() {
        return entry.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HeuristicStatus other = (HeuristicStatus) obj;
        return this.entry.equals(other);
    }
}
