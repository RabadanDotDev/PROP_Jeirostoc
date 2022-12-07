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
    /**
     * The version of the heuristic of class for debug purposes.
     */
    public final static double HEURISTIC_VER = 1.0;
    
    /**
     * Helper class instance to generate the Zobrist hash.
     */
    private final HeuristicStatusZobristHash _zh;
    
    /**
     * Last movement made in this heuristic status class. This value will only
     * be copied in the value constructor.
     */
    private Point _lastPoint;
    
    /**
     * Creates a HeuristicStatus based on a specific game status
     * 
     * @param status Game status to create HeuristicStatus from
     */
    public HeuristicStatus(int [][] status){
        super(status);
        _zh = new HeuristicStatusZobristHash(board_occupied, board_color, currentPlayer);
    }
    
    /**
     * Creates a HeuristicStatus based on an existing GameStatus
     * 
     * @param gs The game status to make a copy from
     */
    public HeuristicStatus(GameStatus gs) {
        super(gs);
        _zh = new HeuristicStatusZobristHash(board_occupied, board_color, currentPlayer);
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
        if(lastStatus == null) {
            _zh = new HeuristicStatusZobristHash(board_occupied, board_color, currentPlayer);
        } else {
            _zh = new HeuristicStatusZobristHash(lastStatus._zh);
            _zh.updateZobristHashes(board_occupied, board_color, currentPlayer);
        }
        _lastPoint = null;
    }
    
    /**
     * Copy constructor.
     * 
     * @param hs The heuristic status to make a copy from
     */
    public HeuristicStatus(HeuristicStatus hs) {
        super(hs);
        _zh = new HeuristicStatusZobristHash(hs._zh);
        _lastPoint = hs._lastPoint;
    }
    
    /**
     * Creates a new HeuristicStatus with a movement made in "to"
     * 
     * @param to The coordinate to make the next movement
     * @return The new heuristic status
     */
    public HeuristicStatus getNextStatus(java.awt.Point to) {
        HeuristicStatus hs = new HeuristicStatus(this);
        if(to == null)
            hs.skipTurn();
        else
            hs.movePiece(to);
        return hs;
    }
    
    /**
     * Get the heuristic of the current state of the game from the point of view
     * of the current player
     * 
     * @return The heuristic of the current state of the game from the point of
     * view of the current player
     */
    public double getHeuristic() {
        return getHeuristic(currentPlayer);
    }
    
    /**
     * Get the heuristic of the current state of the game
     * 
     * @param p The cell type to use to calculate the heuristic from
     * @return The heuristic of the current state of the game
     */
    public double getHeuristic(CellType p) {
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
    public long getZobristHash() {
        return _zh.zobristHashCode();
    }

    /**
     * Make a movement using the current player in point
     * 
     * @param point The position to make a movement in
     */
    @Override
    public void movePiece(Point point) {
        super.movePiece(point);
        _zh.updateZobristHashes(board_occupied, board_color, currentPlayer);
        _lastPoint = point;
    }

    /**
     * Make the current player skip a turn.
     */
    @Override
    public void skipTurn() {
        super.skipTurn();
        _zh.swapPlayer();
        _lastPoint = null;
    }

    Point getLastMovement() {
        return _lastPoint;
    }
}
