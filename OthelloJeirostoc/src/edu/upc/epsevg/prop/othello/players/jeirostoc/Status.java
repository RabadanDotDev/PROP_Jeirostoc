package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
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
        public long getBoard_occupied() {
            long[] la = board_occupied.toLongArray();
            return la.length == 0 ? 0 : la[0];
        }

        /**
         * Get a reference to the internal board_occupied
         * @return The reference to the internal board_occupied 
         */
        public long getBoard_color() {
            long[] la = board_color.toLongArray();
            return la.length == 0 ? 0 : la[0];
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
        public boolean getCurrentPlayerBit() {
            return (currentPlayer == CellType.PLAYER1 ? P1_BIT : P2_BIT);
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
    // Static variables (heuristic logic)                                     //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Heuristic version for debugging purposes.
     */
    public static final float HEURISTIC_VER = 2.1f;
    
    /**
     * Rotation and flip independent disk weights values.
     */
    private final static float[] dwv = {
    100, 
    -30, -40, 
     20,  5,  10, 
     20,  5,  0, 10};
    
    /**
     * Disk weights for each coordinate.
     */
    private final static float[] diskWeights = {
        dwv[0], dwv[1], dwv[3], dwv[6], dwv[6], dwv[3], dwv[1], dwv[0],
        dwv[1], dwv[2], dwv[4], dwv[7], dwv[7], dwv[4], dwv[2], dwv[1],
        dwv[3], dwv[4], dwv[5], dwv[8], dwv[8], dwv[5], dwv[4], dwv[3],
        dwv[6], dwv[7], dwv[8], dwv[9], dwv[9], dwv[8], dwv[7], dwv[6],
        dwv[6], dwv[7], dwv[8], dwv[9], dwv[9], dwv[8], dwv[7], dwv[6],
        dwv[3], dwv[4], dwv[5], dwv[8], dwv[8], dwv[5], dwv[4], dwv[3],
        dwv[1], dwv[2], dwv[4], dwv[7], dwv[7], dwv[4], dwv[2], dwv[1],
        dwv[0], dwv[1], dwv[3], dwv[6], dwv[6], dwv[3], dwv[1], dwv[0]
    };
    
    ////////////////////////////////////////////////////////////////////////////
    // Static variables (game logic)                                          //
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
     * The bit status for P1
     */
    public static final boolean P1_BIT = true;
    
    /**
     * The bit status for P1 expressed in a long
     */
    public static final long P1_LONG_BIT = 1;
    
    /**
     * The color of P2
     */
    public static final int P2_COLOR = -1;
    
    /**
     * The bit status for P2
     */
    public static final boolean P2_BIT = false;
    
    /**
     * The bit status for P2 expressed in a long
     */
    public static final long P2_LONG_BIT = 0;
    
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
     * the form x*SIZE + y.
     */
    private long _boardOccupied;
    
    /**
     * The color of the disc of each occupied position. The bits with 1 
     * correspond to the P1 or black discs.
     */
    private long _boardColor;
    
    /**
     * Unoccupied positions of the game with an adjacent occupied position.
     */
    private long _boardNeighbours;
    
    /**
     * The last movement made in the game, expressed in the form SIZE*x + y
     */
    private byte _lastMovement;
    
    /**
     * Boolean indicating if the game is in a terminal state.
     */ 
    private boolean _isTerminalState;
    
    /**
     * Current player bit.
     */
    private boolean _currentPlayerBit;
    
    /**
     * The number of pieces of the P1.
     */
    private int _piecesCountP1;
    
    /**
     * The number of pieces of the P2.
     */
    private int _piecesCountP2;
    
    /**
     * The zobrist hashes for each rotation of the board.
     */
    private long[] _zobristKeyChain;
    
    /**
     * The cached heuristic value from the disk weights sum.
     */
    private float _diskWeightsSum;
    
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
        _boardOccupied   = 0;
        _boardColor      = 0;
        _boardNeighbours = 0;
        
        // Init zobrist keychain
        _zobristKeyChain = new long[BoardVariation.NUM_VARIATIONS];
        
        // Init Metadata 1
        _piecesCountP1 = 0;
        _piecesCountP2 = 0;
        
        // Set default pieces
        claimPosition(3, 3, P1_BIT);
        claimPosition(4, 3, P2_BIT);
        claimPosition(3, 4, P2_BIT);
        claimPosition(4, 4, P1_BIT);
        
        // Init Heuristics caching
        _diskWeightsSum = computeDiskWeights();
        
        // Init game status
        _isTerminalState  = false;
        _currentPlayerBit = P1_BIT;
        _lastMovement     = -1;
    }
    
    /**
     * Constructor with a given board. Starts a game with the given board and 
     * starting player.
     * 
     * @param board The board. 1 means a disc of P1, -1 means a disc of P2 and 
     * 0 an empty space
     * @param startingPlayerBit The bit of the starting player
     */
    public Status(int[][] board, boolean startingPlayerBit) {
        // Init board
        _boardOccupied   = 0;
        _boardColor      = 0;
        _boardNeighbours = 0;
        
        // Init zobrist keychain
        _zobristKeyChain = new long[BoardVariation.NUM_VARIATIONS];
        if(startingPlayerBit == P2_BIT)
            ZobristKeyGen.updateKeyChainPlayerSwapped(_zobristKeyChain);
        
        // Init Metadata 1
        _piecesCountP1 = 0;
        _piecesCountP2 = 0;
        
        // Set pieces
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (board[y][x] == P1_COLOR) {
                    claimPosition(x, y, P1_BIT);
                } else if (board[y][x] == P2_COLOR) {
                    claimPosition(x, y, P2_BIT);
                }
            }
        }
        
        // Init Heuristics caching
        _diskWeightsSum = computeDiskWeights();
        
        // Init game status
        _isTerminalState  = computeIsTerminal();
        _currentPlayerBit = startingPlayerBit;
        _lastMovement     = -1;
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
        _boardNeighbours = 0;
        regenAvailableNeighbors();
        
        // Init game status
        _isTerminalState  = gse.isGameOver();
        _currentPlayerBit = gse.getCurrentPlayerBit();
        _lastMovement     = -1;
        
        // Init zobrist keychain
        _zobristKeyChain = new long[BoardVariation.NUM_VARIATIONS];
        regenZobristKeyChain();
        
        // Init Metadata
        _piecesCountP1 = gse.getPiecesCountP1();
        _piecesCountP2 = gse.getPiecesCountP2();
        
        // Init Heuristics caching
        _diskWeightsSum = computeDiskWeights();
    }
    
    /**
     * Copy constructor.
     * 
     * @param other The Status to copy
     */
    public Status(Status other) {
        // Copy board
        _boardOccupied   = other._boardOccupied;
        _boardColor      = other._boardColor;
        _boardNeighbours = other._boardNeighbours;
        
        // Copy zobrist keychain
        _zobristKeyChain = other._zobristKeyChain.clone();
        
        // Copy game status
        _isTerminalState  = other._isTerminalState;
        _currentPlayerBit = other._currentPlayerBit;
        _lastMovement     = other._lastMovement;
        
        // Copy metadata
        _piecesCountP1 = other._piecesCountP1;
        _piecesCountP2 = other._piecesCountP2;
        
        // Copy Heuristics caching
        _diskWeightsSum = other._diskWeightsSum;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Public interface                                                       //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Make a movement using the current player at the given point. Point is 
     * assumed not to be null
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
        
        return canMovePiece(x, y, _currentPlayerBit);
    }
    
    /**
     * Make the current player skip their turn.
     */
    public void skipTurn() {
        _currentPlayerBit = !_currentPlayerBit;
        ZobristKeyGen.updateKeyChainPlayerSwapped(_zobristKeyChain);
    }
    
    /**
     * Get the current player's color. 1 for P1 and -1 for P2.
     * 
     * @return The player's color
     */
    public int getCurrentPlayerColor() {
        return _currentPlayerBit == P1_BIT ? P1_COLOR : P2_COLOR;
    }
    
    /**
     * Get a reference to the last movement.
     * 
     * @return A reference to the last movement, expressed in the form SIZE*x+y
     */
    public byte getLastMovement() {
        return _lastMovement;
    }
    
    /**
     * Get the number of discs a player has.
     * 
     * @param playerBit The player bit
     * @return The number of movements a player has made.
     */
    public int getNumDiscs(boolean playerBit) {
        return playerBit == P1_BIT ? _piecesCountP1 : _piecesCountP2;
    }
    
    /**
     * Get the number of total discs
     * 
     * @return The number of total discs
     */
    public int getNumDiscs() {
        return _piecesCountP1 + _piecesCountP2;
    }
    
    /**
     * Get the number of total movements made in the game
     * 
     * @return The number of total movements made in the game
     */
    public int getNumMovements() {
        return _piecesCountP1 + _piecesCountP2 - 4;
    }
    
    /**
     * Get a list of the next possible statuses starting from this position.
     * 
     * @param result The array to deposit the new statuses objects at the end of
     * the list.
     */
    public void getNextMoves(List<Point> result) {        
        for (int bitIndex = 0; bitIndex < SIZE*SIZE; bitIndex++) {
            if (((_boardNeighbours >> bitIndex) & 1) == 1) {
                int x = bitIndex/SIZE;
                int y = bitIndex%SIZE;
                if(canMovePiece(x, y, _currentPlayerBit)) {
                    result.add(new Point(x, y));
                }
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
        getNextStatuses(result, -1);
    }
    
    /**
     * Get a list of the next possible statuses starting from this position.
     * 
     * @param result The array to deposit the new statuses objects at the end of
     * the list
     * @param bitIndexFirst The movement's BitIndex of the form SIZE*x + y that
     * should be added first to the list. It should be a correct position or -1
     */
    public void getNextStatuses(List<Status> result, int bitIndexFirst) {
        // Check first the given movement
        if(bitIndexFirst != -1 && canMovePiece(bitIndexFirst/SIZE, bitIndexFirst%SIZE)) {
            Status s = new Status(this);
            s.movePiece(bitIndexFirst/SIZE, bitIndexFirst%SIZE);
            result.add(s);
        }
        
        // Get the next statuses
        for (int bitIndex = 0; bitIndex < SIZE*SIZE; bitIndex++) {
            if (((_boardNeighbours >> bitIndex) & 1) == 1 && bitIndex != bitIndexFirst) {
                int x = bitIndex/SIZE;
                int y = bitIndex%SIZE;
                if(canMovePiece(x, y, _currentPlayerBit)) {
                    Status s = new Status(this);
                    s.movePiece(x, y);
                    result.add(s);
                }
            }
        }
    }

    @Override
    public String toString() {
        return toString(false);
    }
    
    public String toString(boolean extendedInfo) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < SIZE; y++) {
            sb.append('\t');
            for (int x = 0; x < SIZE; x++) {
                int bitIndex = toIndex(x, y);
                
                sb.append(' ');
                if (((_boardOccupied >> bitIndex) & 1) == 1) {
                    if(((_boardColor >> bitIndex) & 1) == P1_LONG_BIT)
                        sb.append('O');
                    else
                        sb.append('@');
                } else if(extendedInfo && ((_boardNeighbours >> bitIndex) & 1) == 1){
                    sb.append('N');
                } else {
                    sb.append('·');
                }
                sb.append(' ');
            }
            sb.append('\n');
        }
        if(_isTerminalState) {
            sb.append("YES");
        } else {
            sb.append("NO");
        }
        if(extendedInfo) {
            sb.append("\n");
            ArrayList<Long> result = new ArrayList<>(getZobristKeyChain().length);
            for (long item : getZobristKeyChain())
                result.add(item);
            Collections.sort(result);
            sb.append(result);
        }
        return sb.toString();
    }
    
    /**
     * Check if the game is in a terminal state.
     * @return True if the game is in a terminal state
     */
    public boolean isTerminal() {
        return _isTerminalState;
    }
    
    /**
     * Get the heuristic of the game with the point of view of player
     * 
     * @param playerColor The player color to use as point of view
     * @return The heuristic
     */
    public float getHeuristic(int playerColor) {
        if(_isTerminalState) {
            if(_piecesCountP2 < _piecesCountP1)
                return playerColor*Float.POSITIVE_INFINITY;
            else if(_piecesCountP1 < _piecesCountP2)
                return playerColor*Float.NEGATIVE_INFINITY;
            else
                return 0;
        }
        
        return _diskWeightsSum*playerColor;
    }
    
    /**
     * Find the lowest value Zobrist key in the keychain and return it.
     * @return The minimum Zobrist key
     */
    public long getMinZobristKey() {
        long min = _zobristKeyChain[0];
        for (int i = 1; i < _zobristKeyChain.length; i++) {
            if(min < _zobristKeyChain[i])
                min = _zobristKeyChain[i];
        }
        return min;
    }
    
    /**
     * Find the variation index of the lowest value Zobrist key in the keychain 
     * and return it.
     * @return The minimum Zobrist key's variation index
     */
    public int getMinZobristKeyVariationIndex() {
        long min = _zobristKeyChain[0];
        int minI = 0;
        for (int i = 1; i < _zobristKeyChain.length; i++) {
            if(min < _zobristKeyChain[i]) {
                min = _zobristKeyChain[i];
                minI = i;
            }
        }
        return minI;
    }
    
    /**
     * Get the zobrist key corresponding to 
     * BoardVariation.valueof(variationIndex)
     * 
     * @param variationIndex The index of the variation
     * @return The Zobrist key
     */
    public long getZobristKey(int variationIndex) {
        return _zobristKeyChain[variationIndex];
    }
    
    /**
     * Get a reference to the Zobrist keychain.
     * 
     * @return The Zobrist keychain
     */
    public long[] getZobristKeyChain() {
        return _zobristKeyChain;
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
     * Convert (x, y) to bit index with the form x*SIZE + y
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The bit index
     */
    private static int toIndex(int x, int y) {
        return x*SIZE + y;
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
               ((_boardOccupied >> toIndex(x, y)) & 1) == 1;
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
               ((_boardOccupied >> toIndex(x, y)) & 1) == 0;
    }
    
    /**
     * Checks if a disc is a neighbor from _boardNeighbours.
     * 
     * @param x The x coordinate, not necessarily valid
     * @param y The y coordinate, not necessarily valid
     * @return True if there is a valid neighbor at (x, y)
     */
    private boolean isNeighbor(int x, int y) {
        return inBounds(x, y) &&
               ((_boardNeighbours >> toIndex(x, y)) & 1) == 1;
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
                _boardNeighbours |= 1L << toIndex(x, y);
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
                _boardNeighbours |= 1L << toIndex(x2, y2);
            }
        }
    }
    
    /**
     * Regenerate availableNeighbors BitSet.
     */
    private void regenAvailableNeighbors() {
        _boardNeighbours = 0;
        
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                setIfItsNeighbor(x, y);
            }
        }
    }
    
    /**
     * Regenerate Zobrist keychain.
     */
    private void regenZobristKeyChain() {        
        // Board positions
        for (int bitIndex = 0; bitIndex < SIZE*SIZE; bitIndex++) {
            if (((_boardOccupied >> bitIndex) & 1) == 1) {
                ZobristKeyGen.updateKeyChainPositionClaim(_zobristKeyChain, bitIndex, ((_boardColor >> bitIndex) & 1) == 1);
            }
        }
        
        // Current player
        if(_currentPlayerBit == P2_BIT)
            ZobristKeyGen.updateKeyChainPlayerSwapped(_zobristKeyChain);
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
        // Update board
        long playerLongBit = (playerBit == P1_BIT ? P1_LONG_BIT : P2_LONG_BIT);
        _boardOccupied   |= 1L << toIndex(x, y);
        _boardColor      |= playerLongBit << toIndex(x, y);
        _boardNeighbours &= ~(1L << toIndex(x, y));
        updateAdjacentNeighbors(x, y);
        
        // Update zobrist keychain
        ZobristKeyGen.updateKeyChainPositionClaim(_zobristKeyChain, toIndex(x, y), playerBit);
        
        // Update meta
        if(playerBit == P1_BIT) {
            _piecesCountP1++;
            _diskWeightsSum += Status.diskWeights[toIndex(x, y)];
        } else {
            _piecesCountP2++;
            _diskWeightsSum -= Status.diskWeights[toIndex(x, y)];
        }
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
        // Flip position
        _boardColor ^= 1L << toIndex(x, y);
        
        // Update zobrist keychain
        ZobristKeyGen.updateKeyChainPositionFlip(_zobristKeyChain, toIndex(x, y));
        
        // Update meta
        if(playerBit == P1_BIT) {
            _piecesCountP1++;
            _piecesCountP2--;
            _diskWeightsSum += Status.diskWeights[toIndex(x, y)]*2;
        } else {
            _piecesCountP1--;
            _piecesCountP2++;
            _diskWeightsSum -= Status.diskWeights[toIndex(x, y)]*2;
        }
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
        long playerLongBit = (playerBit == P1_BIT ? P1_LONG_BIT : P2_LONG_BIT);
        
        // Go to the specified direction until out of bounds or finding a free 
        // position or a player's disc
        int positionsSeen = 0;
        do {
            x+=dx;
            y+=dy;
            positionsSeen++;
        } while (
            inBounds(x, y)                                       && 
            ((_boardOccupied >> toIndex(x, y)) & 1) == 1         &&
            ((_boardColor    >> toIndex(x, y)) & 1) != playerLongBit
        );
        
        // Return true if an envelop is possible
        return 
            1 < positionsSeen                                    && 
            inBounds(x, y)                                       && 
            ((_boardOccupied >> toIndex(x, y)) & 1) == 1         &&
            ((_boardColor    >> toIndex(x, y)) & 1) == playerLongBit;
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
        long playerLongBit = (playerBit == P1_BIT ? P1_LONG_BIT : P2_LONG_BIT);
        
        // Align to the first disc to flip
        x+=dx;
        y+=dy;
        
        // Flip discs until getting to the other same-color bit
        do {
            flipPosition(x, y, playerBit);
            x+=dx;
            y+=dy;
        } while (
            ((_boardColor >> toIndex(x, y)) & 1) != playerLongBit
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
        // Claim position
        claimPosition(x, y, _currentPlayerBit);
        
        // Flip pieces
        for (int dir = 0; dir < XINCR.length; dir++) {
            if(envelops(x, y, XINCR[dir], YINCR[dir], _currentPlayerBit))
                flipEnveloped(x, y, XINCR[dir], YINCR[dir], _currentPlayerBit);
        }
        
        // Invert player
        _currentPlayerBit = !_currentPlayerBit;
        ZobristKeyGen.updateKeyChainPlayerSwapped(_zobristKeyChain);
        
        // Store movement
        _lastMovement = (byte)toIndex(x, y);
        
        // Update terminal state
        _isTerminalState = computeIsTerminal();
    }
    
    /**
     * Check if the current game is in terminal state
     * @return True if it is terminal, false if it is not.
     */
    private boolean computeIsTerminal() {
        for (int bitIndex = 0; bitIndex < SIZE*SIZE; bitIndex++) {
            if (((_boardNeighbours >> bitIndex) & 1) == 1) {
                int x = bitIndex/SIZE;
                int y = bitIndex%SIZE;
                if(canMovePiece(x, y, P1_BIT) || canMovePiece(x, y, P2_BIT)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Compute the disk weight value from the current status.
     */
    private float computeDiskWeights() {
        float dw = 0;
        
        for (int bitIndex = 0; bitIndex < SIZE*SIZE; bitIndex++) {
            if (((_boardOccupied >> bitIndex) & 1) == 1) {
                if (((_boardColor >> bitIndex) & 1) == P1_LONG_BIT) {
                    dw += Status.diskWeights[bitIndex];
                } else {
                    dw -= Status.diskWeights[bitIndex];
                }
            }
        }
        
        return dw;
    }
}
