package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.BitSet;
import java.util.List;

/**
 * Status based on edu.upc.epsevg.prop.othello.Status with custom implementation
 * and limited memory allocation
 * 
 * @author raul
 * @author josep
 */
public class Status {
    ////////////////////////////////////////////////////////////////////////////
    // Subclasses                                                             //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Class to extract the relevant information from a given GameStatus.
     */
    private class GameStatusExtractor extends GameStatus {
        /**
         * Copy constructor
         * @param gs The Game status to copy
         */
        public GameStatusExtractor(GameStatus gs) {
            super(gs);
        }

        /**
         * Get a reference to the internal board_occupied
         * @return The reference to the internal board_occupied 
         */
        public BitSet getBoard_occupied() {
            return board_occupied;
        }

        /**
         * Get a reference to the internal board_occupied
         * @return The reference to the internal board_occupied 
         */
        public BitSet getBoard_color() {
            return board_color;
        }

        /**
         * Get the number of pieces of P1
         * @return The number of pieces of P1
         */
        public int getPiecesCountP1() {
            return piecesCountP1;
        }

        /**
         * Get the number of pieces of P2
         * @return The number of pieces of P2
         */
        public int getPiecesCountP2() {
            return piecesCountP2;
        }

        /**
         * Get the current player color
         * @return The current player color
         */
        public int getCurrentPlayerColor() {
            return (currentPlayer == CellType.PLAYER1 ? P1_COLOR : P2_COLOR);
        }

        /**
         * Get the winner player color
         * @return The winner player color
         */
        public int getWinnerPlayerColor() {
            return winnerPlayer == CellType.PLAYER1 ? P1_COLOR : winnerPlayer == CellType.PLAYER2 ? P2_COLOR : NONE_COLOR;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Static variables                                                       //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * The size of the board
     */
    public static final int SIZE = 8;
    
    /**
     * The color of P1
     */
    public static final int P1_COLOR = 1;
    
    /**
     * The bit status to use if the P1 claims a position
     */
    public static final boolean P1_BIT = true;
    
    /**
     * The color of P2
     */
    public static final int P2_COLOR = -1;
    
    /**
     * The bit status to use if the P2 claims a position
     */
    public static final boolean P2_BIT = false;
    
    /**
     * The color of an empty position
     */
    public static final int NONE_COLOR = 0;
    
    /**
     * X increment to go to directions UP_LEFT, UP, UP_RIGHT, LEFT, RIGHT, 
     * BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT (in that order)
     */
    private static int[] XINCR = {
        -1,  0,  1, 
        -1,      1, 
        -1,  0,  1
    };
    
    /**
     * Y increment to go to directions UP_LEFT, UP, UP_RIGHT, LEFT, RIGHT, 
     * BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT (in that order)
     */
    private static int[] YINCR = {
        -1, -1, -1, 
         0,      0,
         1,  1,  1
    };
    
    ////////////////////////////////////////////////////////////////////////////
    // Internal structure                                                     //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Positions of the game with a disc of a player. The bits are ordered in 
     * the form y*SIZE +x.
     */
    private final BitSet _boardOccupied;
    
    /**
     * The color of the disc of each occupied position. The bits with 1 
     * correspond to the P1 or black discs.
     */
    private final BitSet _boardColor;
    
    /**
     * Unoccupied positions of the game with an adjacent occupied position.
     */
    private final BitSet _boardNeighbours;
    
    /**
     * The last movement made in the game, expressed in the form {x, y}
     */
    private int[] _lastMovement;
    
    /**
     * Boolean indicating if the game is in a terminal state.
     */ 
    private boolean _isTerminalState;
    
    /**
     * Current player color.
     */
    private int _currentPlayerColor;
    
    /**
     * The number of pieces of the P1.
     */
    private int _piecesCountP1;
    
    /**
     * The number of pieces of the P2.
     */
    private int _piecesCountP2;
    
    ////////////////////////////////////////////////////////////////////////////
    // Constructors                                                           //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Default constructor. Starts a game with the board:
     * . . . . . . . .
     * . . . . . . . .
     * . . . . . . . .
     * . . . 1 2 . . .
     * . . . 2 1 . . .
     * . . . . . . . .
     * . . . . . . . .
     * . . . . . . . .
     * And with player 1 as the player to move.
     */
    public Status() {
        // Init board
        _boardOccupied   = new BitSet(64);
        _boardColor      = new BitSet(64);
        _boardNeighbours = new BitSet(64);
        
        // Set default pieces
        claimPosition(3, 3, P1_BIT);
        claimPosition(4, 3, P2_BIT);
        claimPosition(3, 4, P2_BIT);
        claimPosition(4, 4, P1_BIT);
        
        // Init game status
        _isTerminalState    = false;
        _currentPlayerColor = 1;
        _lastMovement       = new int[] {-1, -1};
        
        // Init Metadata
        _piecesCountP1 = 2;
        _piecesCountP2 = 2;
    }
    
    /**
     * Constructor with a given board. Starts a game with the given board and 
     * starting player.
     * 
     * @param board The board. 1 means a disc of P1, -1 means a disc of P2 and 
     * 0 an empty space
     * @param startingPlayerColor The color of the starting player
     */
    public Status(int[][] board, int startingPlayerColor) {
        // Init board
        _boardOccupied   = new BitSet(64);
        _boardColor      = new BitSet(64);
        _boardNeighbours = new BitSet(64);
        
        // Set pieces
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (board[y][x] == P1_COLOR) {
                    setCoord(_boardOccupied, x, y, true);
                    setCoord(_boardColor, x, y, P1_BIT);
                } else if (board[y][x] == P2_COLOR) {
                    setCoord(_boardOccupied, x, y, true);
                    setCoord(_boardColor, x, y, P2_BIT);
                }
            }
        }
        regenAvailableNeighbors();
        
        // Init game status
        _isTerminalState    = computeIsTerminal();
        _currentPlayerColor = startingPlayerColor;
        _lastMovement       = new int[] {-1, -1};
        
        // Init Metadata
        _piecesCountP1 = 0; /*TODO*/
        _piecesCountP2 = 0; /*TODO*/
    }
    
    /**
     * Constructor from a Game status.
     * 
     * @param gs The game status to copy from
     */
    public Status(GameStatus gs) {
        // Create the extractor
        GameStatusExtractor gse = new GameStatusExtractor(gs);
        
        // Get a reference to the cloned board
        _boardOccupied   = gse.getBoard_occupied();
        _boardColor      = gse.getBoard_color();
        _boardNeighbours = new BitSet(64);
        regenAvailableNeighbors();
        
        // Init game status
        _isTerminalState    = gse.isGameOver();
        _currentPlayerColor = gse.getCurrentPlayerColor();
        _lastMovement       = new int[] {-1, -1};
        
        // Init Metadata
        _piecesCountP1 = gse.getPiecesCountP1();
        _piecesCountP2 = gse.getPiecesCountP2();
    }
    
    /**
     * Copy constructor.
     * 
     * @param other The Status to copy
     */
    public Status(Status other) {
        // Copy board
        _boardOccupied   = (BitSet) other._boardOccupied.clone();
        _boardColor      = (BitSet) other._boardColor.clone();
        _boardNeighbours = (BitSet) other._boardNeighbours.clone();
        
        // Copy game status
        _isTerminalState    = other._isTerminalState;
        _currentPlayerColor = other._currentPlayerColor;
        _lastMovement       = other._lastMovement.clone();
        
        // Copy metadata
        _piecesCountP1 = other._piecesCountP1;
        _piecesCountP2 = other._piecesCountP2;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Public interface                                                       //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Make a movement using the current player at the given point.
     * 
     * @param point The position to make a movement in
     */
    public void movePiece(Point point) {
        if(canMovePiece(point.x, point.y))
            movePiece(point.x, point.y);
        else
            throw new RuntimeException("Tried to make a movement in an invalid position!");
    }
    
    /**
     * Check if a movement at (x,y) can be made with currentPlayerColor
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @return True if only if a movement can be made at (x, y) with 
     * currentPlayerColor
     */
    public boolean canMovePiece(int x, int y) {
        // Check if the position is a valid neighbor
        if(!isNeighbor(x, y))
            return false;
        boolean playerBit = (_currentPlayerColor == P1_COLOR ? P1_BIT : P2_BIT);
        
        return canMovePiece(x, y, playerBit);
    }
    
    /**
     * Make the current player skip their turn.
     */
    public void skipTurn() {
        _currentPlayerColor = -_currentPlayerColor;
    }
    
    /**
     * Get the current player's color. 1 for P1 and -1 for P2.
     * 
     * @return The player's color
     */
    public int getCurrentPlayerColor() {
        return _currentPlayerColor;
    }
    
    /**
     * Get a reference to the last movement.
     * 
     * @return A reference to the last movement, expressed in the form int[]{x,y}
     */
    public int[] getLastMovement() {
        return _lastMovement;
    }
    
    /**
     * Get a list of the next possible statuses starting from this position.
     * 
     * @param result The array to deposit the new point objects. The Array
     * list should be empty.
     */
    public void getNextMoves(List<Point> result) {
        boolean playerBit = (_currentPlayerColor == P1_COLOR ? P1_BIT : P2_BIT);
        for (int i = _boardNeighbours.nextSetBit(0); i >= 0; i = _boardNeighbours.nextSetBit(i+1)) {
            int x = i/SIZE;
            int y = i%SIZE;
            if(canMovePiece(x, y, playerBit)) {
                result.add(new Point(x, y));
            }
        }
    }
    
    /**
     * Get a list of the next possible statuses starting from this position.
     * 
     * @param result The array to deposit the new statuses objects. The Array
     * list should be empty.
     */
    public void getNextStatuses(List<Status> result) {
        boolean playerBit = (_currentPlayerColor == P1_COLOR ? P1_BIT : P2_BIT);
        for (int i = _boardNeighbours.nextSetBit(0); i >= 0; i = _boardNeighbours.nextSetBit(i+1)) {
            int x = i/SIZE;
            int y = i%SIZE;
            if(canMovePiece(x, y, playerBit)) {
                Status s = new Status(this);
                s.movePiece(x, y);
                result.add(s);
            }
        }
    }

    @Override
    public String toString() {
        return toString(false);
    }
    
    public String toString(boolean withNeighbors) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < SIZE; y++) {
            sb.append('\t');
            for (int x = 0; x < SIZE; x++) {
                int bitIndex = toIndex(x, y);
                
                sb.append(' ');
                if(_boardOccupied.get(bitIndex) && _boardColor.get(bitIndex) == P1_BIT)
                    sb.append('O');
                else if(_boardOccupied.get(bitIndex))
                    sb.append('@');
                else if(withNeighbors && _boardNeighbours.get(bitIndex))
                    sb.append('N');
                else
                    sb.append('·');
                sb.append(' ');
            }
            sb.append('\n');
        }
        if(_isTerminalState) {
            sb.append("YES");
        } else {
            sb.append("NO");
        }
        return sb.toString();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Helpers                                                                //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Check if (x, y) is in bounds
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @return True if (x, y) is in bounds
     */
    private static boolean inBounds(int x, int y) {
        return -1 < x && x < SIZE &&
               -1 < y && y < SIZE;
    }
    
    /**
     * Convert (x, y) to bit index with the form y*SIZE + x
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The bit index
     */
    private static int toIndex(int x, int y) {
        return x*SIZE + y;
    }
    
    /**
     * Set coordinate (x, y) from bs to value v
     * 
     * @param bs The BitSet to do the operation in
     * @param x The x coordinate
     * @param y The y coordinate
     * @param v The value to set
     */
    private static void setCoord(BitSet bs, int x, int y, boolean v) {
        bs.set(toIndex(x, y), v);
    }
    
    /**
     * Flip coordinate (x, y) from bs
     * 
     * @param bs The BitSet to do the operation in
     * @param x The x coordinate
     * @param y The y coordinate
     */
    private static void flipCoord(BitSet bs, int x, int y) {
        bs.flip(toIndex(x, y));
    }
    
    /**
     * Check if there is a disc at (x, y).
     * 
     * @param x The x coordinate, not necessarily valid
     * @param y The y coordinate, not necessarily valid
     * @return True if there is a valid disc at (x, y)
     */
    private boolean hasDisc(int x, int y) {
        return inBounds(x, y) &&
               _boardOccupied.get(toIndex(x, y));
    }
    
    /**
     * Check if there is a empty pos at (x, y).
     * 
     * @param x The x coordinate, not necessarily valid
     * @param y The y coordinate, not necessarily valid
     * @return True if there is a valid empty pos at (x, y)
     */
    private boolean isEmpty(int x, int y) {
        return inBounds(x, y) &&
               !_boardOccupied.get(toIndex(x, y));
    }
    
    /**
     * Checks if a disc is a neighbor from _boardNeighbours.
     * 
     * @param x The x coordinate, not necessarily valid
     * @param y The y coordinate, not necessarily valid
     * @return True if there is a valid neighbor at (x, y)
     */
    private boolean isNeighbor(int x, int y) {
        return inBounds(x, y) && _boardNeighbours.get(toIndex(x, y));
    }
    
    /**
     * Set bitIndex if it is a neighbor of a occupied disc.
     */
    private void setIfItsNeighbor(int x, int y) {
        if(!isEmpty(x, y))
            return;
        
        for (int dir = 0; dir < XINCR.length; dir++) {
            int x2 = x + XINCR[dir];
            int y2 = y + YINCR[dir];
            if(hasDisc(x2, y2)) {
                setCoord(_boardNeighbours, x, y, true);
                return;
            }
        }
    }
    
    /**
     * Set neighbors of a occupied disc. The position is assumed to 
     * hasDisc(x, y)
     */
    private void updateAdjacentNeighbors(int x, int y) {        
        for (int dir = 0; dir < XINCR.length; dir++) {
            int x2 = x + XINCR[dir];
            int y2 = y + YINCR[dir];
            if(isEmpty(x2, y2)) {
                setCoord(_boardNeighbours, x2, y2, true);
            }
        }
    }
    
    /**
     * Regenerate availableNeighbors BitSet.
     */
    private void regenAvailableNeighbors() {
        _boardNeighbours.clear();
        
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                setIfItsNeighbor(x, y);
            }
        }
    }
    
    /**
     * Claim position (x,y) for player. The position is assumed to 
     * canMovePiece(x, y) or being called from the constructor
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param playerBit The player bit
     */
    private void claimPosition(int x, int y, boolean playerBit) {
        setCoord(_boardOccupied, x, y, true);
        setCoord(_boardColor, x, y, playerBit);
        setCoord(_boardNeighbours, x, y, false);
        updateAdjacentNeighbors(x, y);
    }

    /**
     * Flip position (x,y). The position is assumed to hasDisc(x, y) and to have
     * !playerBit
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param playerBit The player bit
     */
    private void flipPosition(int x, int y, boolean playerBit) {
        flipCoord(_boardColor, x, y);
    }
    
    /**
     * Check if a movement at (x,y) would envelop enemies pieces at (dx, dy) 
     * direction. The position is assumed to isNeighbor(x, y)
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param dx The x coordinate increment
     * @param dy The y coordinate increment
     * @param playerBit The player bit
     */
    private boolean envelops(int x, int y, int dx, int dy, boolean playerBit) {
        // Go to the specified direction until out of bounds or finding a free 
        // position or a player's disc
        int positionsSeen = 0;
        do {
            x+=dx;
            y+=dy;
            positionsSeen++;
        } while (
            inBounds(x, y) && 
            _boardOccupied.get(toIndex(x, y)) &&
            _boardColor.get(toIndex(x, y)) != playerBit
        );
        
        // Return true if an envelop is possible
        return 1 < positionsSeen && 
               inBounds(x, y) && 
               _boardOccupied.get(toIndex(x, y)) &&
               _boardColor.get(toIndex(x, y)) == playerBit;
    }
    
    /**
     * Check if a movement at (x,y) can be made with the given playerBit. 
     * Assumes isNeighbor(x, y)
     * 
     * @param x The x coordinate
     * @param y The y coordinate     
     * @param playerBit The playerBit coordinate
     * @return True if only if a movement can be made at (x, y) with 
     * playerBit
     */
    private boolean canMovePiece(int x, int y, boolean playerBit) {       
        return envelops(x, y, XINCR[0], YINCR[0], playerBit) || // UP_LEFT
               envelops(x, y, XINCR[1], YINCR[1], playerBit) || // UP
               envelops(x, y, XINCR[2], YINCR[2], playerBit) || // UP_RIGHT
               envelops(x, y, XINCR[3], YINCR[3], playerBit) || // LEFT
               envelops(x, y, XINCR[4], YINCR[4], playerBit) || // RIGHT
               envelops(x, y, XINCR[5], YINCR[5], playerBit) || // BOTTOM_LEFT
               envelops(x, y, XINCR[6], YINCR[6], playerBit) || // BOTTOM
               envelops(x, y, XINCR[7], YINCR[7], playerBit);   // BOTTOM_RIGHT
    }
    
    /**
     * Make a movement at (x,y) with direction (dx, dy). The position, bit and 
     * direction are assumed to be correct
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param dx The x coordinate increment
     * @param dy The y coordinate increment
     * @param playerBit The player bit
     */
    private void flipEnveloped(int x, int y, int dx, int dy, boolean playerBit) {
        // Align to the first disc to flip
        x+=dx;
        y+=dy;
        
        // Flip discs until getting to the other same-color bit
        do {
            flipPosition(x, y, playerBit);
            x+=dx;
            y+=dy;
        } while (
            _boardColor.get(toIndex(x, y)) != playerBit
        );
    }
    
    /**
     * Make a movement using the current player at the given point. The position
     * is assumed to canMovePiece(x, y)
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     */
    private void movePiece(int x, int y) {        
        boolean playerBit = (_currentPlayerColor == P1_COLOR ? P1_BIT : P2_BIT);
        
        // Claim position
        claimPosition(x, y, playerBit);
        
        // Flip pieces
        for (int dir = 0; dir < XINCR.length; dir++) {
            if(envelops(x, y, XINCR[dir], YINCR[dir], playerBit))
                flipEnveloped(x, y, XINCR[dir], YINCR[dir], playerBit);
        }
        
        // Invert player
        _currentPlayerColor = -_currentPlayerColor;
        
        // Store movement
        _lastMovement[0] = x;
        _lastMovement[1] = y;
        
        // Update terminal state
        _isTerminalState = computeIsTerminal();
    }
    
    /**
     * Check if the current game is in terminal state
     * @return True if it is terminal, false if it is not.
     */
    private boolean computeIsTerminal() {
        for (int i = _boardNeighbours.nextSetBit(0); i >= 0; i = _boardNeighbours.nextSetBit(i+1)) {
            int x = i/SIZE;
            int y = i%SIZE;
            if(canMovePiece(x, y, true) || canMovePiece(x, y, false)) {
                return false;
            }
        }
        return true;
    }
}
