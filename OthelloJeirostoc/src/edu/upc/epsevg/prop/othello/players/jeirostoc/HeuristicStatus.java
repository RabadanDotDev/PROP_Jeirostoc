package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.Objects;

/**
 * Game status with capability of computing an heuristic
 * 
 * @author raul
 * @author josep
 */
class HeuristicStatus extends GameStatus {
    private final static double[] v = {
         70, 
        -30, -40, 
         20,  5,  10, 
         20,  5,  0, 10};
    private final static double[] diskWeights = {
        v[0], v[1], v[3], v[6], v[6], v[3], v[1], v[0],
        v[1], v[2], v[4], v[7], v[7], v[4], v[2], v[1],
        v[3], v[4], v[5], v[8], v[8], v[5], v[4], v[3],
        v[6], v[7], v[8], v[9], v[9], v[8], v[7], v[6],
        v[6], v[7], v[8], v[9], v[9], v[8], v[7], v[6],
        v[3], v[4], v[5], v[8], v[8], v[5], v[4], v[3],
        v[1], v[2], v[4], v[7], v[7], v[4], v[2], v[1],
        v[0], v[1], v[3], v[6], v[6], v[3], v[1], v[0]
    };
    
    /**
     * The version of the heuristic of class for debug purposes.
     */
    public final static double HEURISTIC_VER = 2.0;
    
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
     * Key with reduced size to use when inserting information to the 
     * Transposition Table
     */
    private TranspositionTable.TTKey _ttKey;
    
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
        _zh        = new HeuristicStatusZobristHash(hs._zh);
        _ttKey     = new TranspositionTable.TTKey(this);
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
        
        double heuristic = 0;
        
        for (int i = board_occupied.nextSetBit(0); i >= 0; i = board_occupied.nextSetBit(i+1)) {            
            double weight = diskWeights[i];
            if(board_color.get(i)) {
                heuristic += weight;
            } else {
                heuristic -= weight;
            }
        }
        
        if(p.equals(CellType.PLAYER1))
            return heuristic;
        else
            return -heuristic;
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
     * Get a reference to the TTKey
     * 
     * @return The reference to the TTKey
     */
    public TranspositionTable.TTKey getTTKey() {
        return _ttKey;
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
        _ttKey.update(this);
        _lastPoint = point;
    }

    /**
     * Make the current player skip a turn.
     */
    @Override
    public void skipTurn() {
        super.skipTurn();
        _zh.swapPlayer();
        _ttKey.update(this);
        _lastPoint = null;
    }

    Point getLastMovement() {
        return _lastPoint;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (_zh.zobristHashCode() ^ (_zh.zobristHashCode() >>> 32));
        return hash;
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
        return Objects.equals(this._zh.zobristHashCode(), other._zh.zobristHashCode());
    }
}
