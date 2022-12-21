package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Status based on edu.upc.epsevg.prop.othello.Status with custom implementation
 * and limited memory allocation.
 * 
 * @author raul
 * @author josep
 */
public class Status {    
    ////////////////////////////////////////////////////////////////////////////
    // GameStatusExtractor subclass                                           //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Class to extract the relevant information from a given GameStatus.
     */
    private class GameStatusExtractor extends GameStatus {
        /**
         * Copy constructor.
         * 
         * @param gs The Game status to copy.
         */
        public GameStatusExtractor(GameStatus gs) {
            super(gs);
        }

        /**
         * Get the internal board occupied state as a long.
         * 
         * @return The internal board occupied state as a long.
         */
        public long getBoard_occupied() {
            long[] la = board_occupied.toLongArray();
            return la.length == 0 ? 0 : la[0];
        }

        /**
         * Get the internal board color state as a long.
         * 
         * @return The internal board color state as a long.
         */
        public long getBoard_color() {
            long[] la = board_color.toLongArray();
            return la.length == 0 ? 0 : la[0];
        }

        /**
         * Get the number of pieces of P1.
         * 
         * @return The number of pieces of P1.
         */
        public int getPiecesCountP1() {
            return piecesCountP1;
        }

        /**
         * Get the number of pieces of P2.
         * 
         * @return The number of pieces of P2.
         */
        public int getPiecesCountP2() {
            return piecesCountP2;
        }

        /**
         * Get the current player color.
         * 
         * @return The current player color.
         */
        public boolean getCurrentPlayerBit() {
            return (currentPlayer == CellType.PLAYER1 ? P1_BIT : P2_BIT);
        }

        /**
         * Get the winner player color.
         * 
         * @return The winner player color.
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
    public static final float HEURISTIC_VER = 5.0f;
    
    /**
     * Rotation and flip independent disk weights values.
     */
    private final static float[] DWV = {
     5, 
     1, 1, 
     3, 2, 2, 
     3, 2, 2, 1};
    
    /**
     * Rotation and flip independent neighbors weights values.
     */
    private final static float[] NWV = {
     4, 
     2,    1, 
     1.5f, 1, 1, 
     1.5f, 1, 1, 0};
    
    /**
     * Disk scores for each coordinate.
     */
    private final static float[] DISK_SCORES = {
        DWV[0], DWV[1], DWV[3], DWV[6], DWV[6], DWV[3], DWV[1], DWV[0],
        DWV[1], DWV[2], DWV[4], DWV[7], DWV[7], DWV[4], DWV[2], DWV[1],
        DWV[3], DWV[4], DWV[5], DWV[8], DWV[8], DWV[5], DWV[4], DWV[3],
        DWV[6], DWV[7], DWV[8], DWV[9], DWV[9], DWV[8], DWV[7], DWV[6],
        DWV[6], DWV[7], DWV[8], DWV[9], DWV[9], DWV[8], DWV[7], DWV[6],
        DWV[3], DWV[4], DWV[5], DWV[8], DWV[8], DWV[5], DWV[4], DWV[3],
        DWV[1], DWV[2], DWV[4], DWV[7], DWV[7], DWV[4], DWV[2], DWV[1],
        DWV[0], DWV[1], DWV[3], DWV[6], DWV[6], DWV[3], DWV[1], DWV[0]
    };
    
    /**
     * Neighbor scores for each coordinate.
     */
    private final static float[] NEIGHBOR_SCORES = {
        NWV[0], NWV[1], NWV[3], NWV[6], NWV[6], NWV[3], NWV[1], NWV[0],
        NWV[1], NWV[2], NWV[4], NWV[7], NWV[7], NWV[4], NWV[2], NWV[1],
        NWV[3], NWV[4], NWV[5], NWV[8], NWV[8], NWV[5], NWV[4], NWV[3],
        NWV[6], NWV[7], NWV[8], NWV[9], NWV[9], NWV[8], NWV[7], NWV[6],
        NWV[6], NWV[7], NWV[8], NWV[9], NWV[9], NWV[8], NWV[7], NWV[6],
        NWV[3], NWV[4], NWV[5], NWV[8], NWV[8], NWV[5], NWV[4], NWV[3],
        NWV[1], NWV[2], NWV[4], NWV[7], NWV[7], NWV[4], NWV[2], NWV[1],
        NWV[0], NWV[1], NWV[3], NWV[6], NWV[6], NWV[3], NWV[1], NWV[0]
    };
    
    /**
     * Score to give for each registered stable position.
     */
    private final static float stableScore = 5.0f;
    
    ////////////////////////////////////////////////////////////////////////////
    // Static variables (game logic)                                          //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * The size of the board.
     */
    public static final byte SIZE = 8;
    
    /**
     * The color of P1.
     */
    public static final int P1_COLOR = 1;
    
    /**
     * The bit status for P1 in a boolean.
     */
    public static final boolean P1_BIT = true;
    
    /**
     * The bit status (0 or 1) for P1 in a long.
     */
    public static final long P1_LONG_BIT = 1;
    
    /**
     * The color of P2.
     */
    public static final int P2_COLOR = -1;
    
    /**
     * The bit status for P2 in a boolean.
     */
    public static final boolean P2_BIT = false;
    
    /**
     * The bit status (0 or 1) for P2 in a long.
     */
    public static final long P2_LONG_BIT = 0;
    
    /**
     * The color of an empty position.
     */
    public static final int NONE_COLOR = 0;
    
    /**
     * X increment to go to directions UP_LEFT, UP, UP_RIGHT, LEFT, RIGHT, 
     * BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT.
     */
    private static final int[] XINCR = {
        -1,  0,  1, 
        -1,      1, 
        -1,  0,  1
    };
    
    /**
     * Y increment to go to directions UP_LEFT, UP, UP_RIGHT, LEFT, RIGHT, 
     * BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT.
     */
    private static final int[] YINCR = {
        -1, -1, -1, 
         0,      0,
         1,  1,  1
    };
    
    /**
     * X increment with directions with opposite way followed by each other: 
     * UP_LEFT, BOTTOM_RIGHT, UP, BOTTOM, UP_RIGHT, BOTTOM_LEFT LEFT, RIGHT.
     */
    private static int[] XINCR2 = {
        XINCR[0], XINCR[7], XINCR[1], XINCR[6],
        XINCR[2], XINCR[5], XINCR[3], XINCR[4]
    };
    
    /**
     * Y increment with directions with opposite way followed by each other: 
     * UP_LEFT, BOTTOM_RIGHT, UP, BOTTOM, UP_RIGHT, BOTTOM_LEFT LEFT, RIGHT.
     */
    private static int[] YINCR2 = {
        YINCR[0], YINCR[7], YINCR[1], YINCR[6],
        YINCR[2], YINCR[5], YINCR[3], YINCR[4]
    };
    
    ////////////////////////////////////////////////////////////////////////////
    // Internal structure                                                     //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Positions of the game with a disc of any player. The bits are ordered in 
     * the form x*SIZE + y.
     */
    private long _boardOccupied;
    
    /**
     * The color of the disc of each occupied position. The bits are ordered in 
     * the form x*SIZE + y.
     */
    private long _boardColor;
    
    /**
     * Occupied positions that were determined to be stable.
     */
    private long _boardStable;
    
    /**
     * Unoccupied positions of the game with an adjacent occupied position of 
     * P1.
     */
    private long _boardNeighborsP1;
    
    /**
     * Unoccupied positions of the game with an adjacent occupied position of 
     * P2.
     */
    private long _boardNeighborsP2;
    
    /**
     * The number of empty neighboring positions to pieces of P1. If an empty 
     * position is adjacent to more than one discs of P1, it is counted as many 
     * times as adjacent discs there are.
     */
    private int _neighborsCountP1;
    
    /**
     * The number of empty neighboring positions to pieces of P2. If an empty 
     * position is adjacent to more than one discs of P2, it is counted as many 
     * times as adjacent discs there are.
     */
    private int _neighborsCountP2;
    
    /**
     * The number of pieces of P1.
     */
    private int _piecesCountP1;
    
    /**
     * The number of pieces of P2.
     */
    private int _piecesCountP2;
    
    /**
     * The Zobrist hashes for each rotation of the board.
     */
    private final long[] _zobristKeyChain;
    
    /**
     * The cached heuristic value from the disk weights sum.
     */
    private float _diskWeightsSum;
    
    /**
     * The cached heuristic value from the neighbor weights sum.
     */
    private float _neighborWeightsSum;
    
    /**
     * The cached heuristic value from the stable disc scores.
     */
    private float _stableDiscScoreSum;
    
    /**
     * The last recorded movement made in the game, expressed in the form SIZE*x
     * + y.
     */
    private byte _lastMovement;
    
    /**
     * Boolean indicating if the game is in a terminal state.
     */ 
    private boolean _isTerminalState;
    
    /**
     * The current player bit.
     */
    private boolean _currentPlayerBit;
    
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
        _boardOccupied    = 0;
        _boardColor       = 0;
        _boardStable      = 0;
        _boardNeighborsP1 = 0;
        _boardNeighborsP2 = 0;
        
        // Init Metadata
        _piecesCountP1 = 0;
        _piecesCountP2 = 0;
        _neighborsCountP1   = 0;
        _neighborsCountP2   = 0;
        _neighborWeightsSum = 0;
        _stableDiscScoreSum = 0;
        
        // Init zobrist keychain
        _zobristKeyChain = new long[BoardVariation.NUMBER];
        
        // Set default pieces
        claimPosition(3, 3, P1_BIT);
        claimPosition(4, 3, P2_BIT);
        claimPosition(3, 4, P2_BIT);
        claimPosition(4, 4, P1_BIT);
        
        // Init Heuristics cache
        _diskWeightsSum = computeDiskWeights();
        
        // Init game status
        _lastMovement     = -1;
        _isTerminalState  = false;
        _currentPlayerBit = P1_BIT;
    }
    
    /**
     * Constructor with a given board. Starts a game with the given board and 
     * starting player.
     * 
     * @param board The board. 1 means a disc of P1, -1 means a disc of P2 and 
     * 0 an empty space.
     * @param startingPlayerBit The bit of the starting player.
     */
    public Status(int[][] board, boolean startingPlayerBit) {
        // Init board
        _boardOccupied    = 0;
        _boardColor       = 0;
        _boardStable      = 0;
        _boardNeighborsP1 = 0;
        _boardNeighborsP2 = 0;
        
        // Init neighbours count
        _neighborsCountP1 = 0;
        _neighborsCountP2 = 0;
        _neighborWeightsSum = 0;
        _stableDiscScoreSum = 0;
        
        // Init number of pieces
        _piecesCountP1 = 0;
        _piecesCountP2 = 0;
        
        // Init Zobrist keychain
        _zobristKeyChain = new long[BoardVariation.NUMBER];
        if(startingPlayerBit == P2_BIT)
            ZobristKeyGen.updateKeyChainPlayerSwapped(_zobristKeyChain);
        
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
        
        // Init heuristics caching
        _diskWeightsSum = computeDiskWeights();
        
        // Init game status
        _isTerminalState  = computeIsTerminal();
        _currentPlayerBit = startingPlayerBit;
        _lastMovement     = -1;
    }
    
    /**
     * Constructor from a Game status.
     * 
     * @param gs The game status to copy from.
     */
    public Status(GameStatus gs) {
        // Create the extractor
        GameStatusExtractor gse = new GameStatusExtractor(gs);
        
        // Copy board
        _boardOccupied = gse.getBoard_occupied();
        _boardColor    = gse.getBoard_color();
        regenStability();
        regenAvailableNeighbors();
        
        // Copy number of pieces
        _piecesCountP1 = gse.getPiecesCountP1();
        _piecesCountP2 = gse.getPiecesCountP2();
        
        // Init heuristics caching
        _diskWeightsSum = computeDiskWeights();
        
        // Init/Copy game status
        _isTerminalState  = gse.isGameOver();
        _currentPlayerBit = gse.getCurrentPlayerBit();
        _lastMovement     = -1;
        
        // Init Zobrist keychain
        _zobristKeyChain = new long[BoardVariation.NUMBER];
        regenZobristKeyChain();
    }
    
    /**
     * Copy constructor.
     * 
     * @param other The Status to copy.
     */
    public Status(Status other) {
        // Copy board
        _boardOccupied    = other._boardOccupied;
        _boardColor       = other._boardColor;
        _boardStable      = other._boardStable;
        _boardNeighborsP1 = other._boardNeighborsP1;
        _boardNeighborsP2 = other._boardNeighborsP2;
        
        // Copy player neighbors's count
        _neighborsCountP1 = other._neighborsCountP1;
        _neighborsCountP2 = other._neighborsCountP2;
        _neighborWeightsSum = other._neighborWeightsSum;
        _stableDiscScoreSum = other._stableDiscScoreSum;
        
        // Copy number of pieces
        _piecesCountP1 = other._piecesCountP1;
        _piecesCountP2 = other._piecesCountP2;
        
        // Copy Zobrist keychain
        _zobristKeyChain = other._zobristKeyChain.clone();
        
        // Copy heuristics caching
        _diskWeightsSum = other._diskWeightsSum;
        
        // Copy game status
        _isTerminalState  = other._isTerminalState;
        _currentPlayerBit = other._currentPlayerBit;
        _lastMovement     = other._lastMovement;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Public interface                                                       //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Make a movement using the current player at the given point. Point is 
     * assumed not to be null and to canMovePiece(point.x, point.y)
     * 
     * @param point The position to make a movement in.
     */
    public void movePiece(Point point) {
        movePiece(point.x, point.y);
    }
    
    /**
     * Check if a movement at (x,y) can be made with currentPlayerColor.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return True if only if a movement can be made at (x, y) with 
     * currentPlayerColor.
     */
    public boolean canMovePiece(int x, int y) {
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
     * Get the current player's color.
     * 
     * @return The player's color.
     */
    public int getCurrentPlayerColor() {
        return _currentPlayerBit == P1_BIT ? P1_COLOR : P2_COLOR;
    }
    
    /**
     * Get the last movement.
     * 
     * @return The last movement, expressed in the form SIZE*x+y.
     */
    public byte getLastMovement() {
        return _lastMovement;
    }
    
    /**
     * Get the number of discs a player has.
     * 
     * @param playerBit The player bit.
     * @return The number of movements a player has made.
     */
    public int getNumDiscs(boolean playerBit) {
        return playerBit == P1_BIT ? _piecesCountP1 : _piecesCountP2;
    }
    
    /**
     * Get the number of total discs.
     * 
     * @return The number of total discs.
     */
    public int getNumDiscs() {
        return _piecesCountP1 + _piecesCountP2;
    }
    
    /**
     * Get the number of total movements made in the game.
     * 
     * @return The number of total movements made in the game.
     */
    public int getNumMovements() {
        return _piecesCountP1 + _piecesCountP2 - 4;
    }
    
    /**
     * Get the number of neighbors a player disks has.
     * 
     * @param playerBit The player bit.
     * @return The number of movements a player has made.
     */
    public int getNumNeighbors(boolean playerBit) {
        return playerBit == P1_BIT ? _neighborsCountP1 : _neighborsCountP2;
    }
    
    /**
     * Get a list of the next possible statuses starting from this position.
     * 
     * @param result The array to deposit the new statuses objects at the end of
     * the list.
     */
    public void getNextMoves(List<Point> result) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if(canMovePiece(x, y, _currentPlayerBit)) {
                    result.add(new Point(x, y));
                }
            }
        }
    }
    
    /**
     * Get a list of the next possible statuses starting from this position.
     * 
     * @param result The array to deposit the new statuses objects at the end of
     * the list.
     * @param bitIndexFirst The movement's BitIndex of the form SIZE*x + y that
     * should be added first to the list. It should be a correct position or -1.
     */
    public void getNextMoves(List<Point> result, int bitIndexFirst) {
        // Add the given movement if possible
        if(bitIndexFirst != -1) {
            result.add(new Point(bitIndexFirst/SIZE, bitIndexFirst%SIZE));
        }
        
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (toIndex(x, y) != bitIndexFirst &&
                    canMovePiece(x, y, _currentPlayerBit)) {
                    result.add(new Point(x, y));
                }
            }
        }
    }
    
    /**
     * Get a list of the next possible statuses starting from this position.
     * 
     * @param result The array to deposit the new statuses objects. 
     */
    public void getNextStatuses(List<Status> result) {  
        getNextStatuses(result, -1);
    }
    
    /**
     * Get a list of the next possible statuses starting from this position with
     * one specific movement at the front if possible.
     * 
     * @param result The array to deposit the new statuses objects at the end of
     * the list.
     * @param bitIndexFirst The movement's BitIndex of the form SIZE*x + y that
     * should be added first to the list. It should be a correct position or -1.
     */
    public void getNextStatuses(List<Status> result, int bitIndexFirst) {
        // Generate the given movement if possible
        if(bitIndexFirst != -1) {
            Status s = new Status(this);
            s.movePiece(bitIndexFirst/SIZE, bitIndexFirst%SIZE);
            result.add(s);
        }
        
        // Get the next statuses
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (toIndex(x, y) != bitIndexFirst &&
                    canMovePiece(x, y, _currentPlayerBit)) {
                    Status s = new Status(this);
                    s.movePiece(x, y);
                    result.add(s);
                }
            }
        }
    }

    /**
     * Return a string representation of the class.
     * 
     * @return The string representation of the class.
     */
    @Override
    public String toString() {
        return toString(false);
    }
    
    /**
     * Return a string representation of the class.
     * 
     * @param extendedInfo Show extra information.
     * @return The string representation of the class.
     */
    public String toString(boolean extendedInfo) {
        StringBuilder sb = new StringBuilder();
        
        // Board
        if(extendedInfo) {
            sb.append("\t ");
            for (int x = 0; x < SIZE; x++) {
                sb.append(' ');
                sb.append(x);
                sb.append(' ');
            }
            sb.append('\n');
        }
        
        for (int y = 0; y < SIZE; y++) {
            sb.append('\t');
            if(extendedInfo)
                sb.append(y);
            
            for (int x = 0; x < SIZE; x++) {
                int bitIndex = toIndex(x, y);
                
                sb.append(' ');
                if (isSetAt(_boardStable, bitIndex) && extendedInfo) {
                    if(hasAt(_boardColor, bitIndex, P1_LONG_BIT))
                        sb.append('Ø');
                    else
                        sb.append('#');
                } else if (isSetAt(_boardOccupied, bitIndex)) {
                    if(hasAt(_boardColor, bitIndex, P1_LONG_BIT))
                        sb.append('O');
                    else
                        sb.append('@');
                } else if(extendedInfo && isSetAt(_boardNeighborsP1, bitIndex) && isSetAt(_boardNeighborsP2, bitIndex)){
                    sb.append('N');
                } else if(extendedInfo && isSetAt(_boardNeighborsP1, bitIndex)){
                    sb.append('n');
                } else if(extendedInfo && isSetAt(_boardNeighborsP2, bitIndex)){
                    sb.append('m');
                } else {
                    sb.append('·');
                }
                sb.append(' ');
            }
            sb.append('\n');
        }
        
        // Terminal state
        if(_isTerminalState) {
            sb.append("YES");
        } else {
            sb.append("NO");
        }
        
        if(extendedInfo) {
            sb.append("\n");
            
            // Zobrist keychain
            sb.append("KeyChain: ");
            if (_zobristKeyChain != null) {
                ArrayList<Long> result = new ArrayList<>(_zobristKeyChain.length);
                for (long item : _zobristKeyChain)
                    result.add(item);
                Collections.sort(result);
                sb.append(result);
            }
            sb.append("\n");
            
            // Disk weights sum
            sb.append("Disk Weights sum: ");
            sb.append(_diskWeightsSum);
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Check if the game is in a terminal state.
     * 
     * @return True if the game is in a terminal state.
     */
    public boolean isTerminal() {
        return _isTerminalState;
    }
    
    /**
     * Get the heuristic of the game with the point of view of player.
     * 
     * @param playerColor The player color to use as the point of view.
     * @return The heuristic.
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
        
        return playerColor*(_diskWeightsSum - _neighborWeightsSum + stableScore);
    }
    
    /**
     * Find the lowest value Zobrist key in the keychain and return it.
     * 
     * @return The minimum Zobrist key.
     */
    public long getMinZobristKey() {
        long min = _zobristKeyChain[0];
        for (int i = 1; i < _zobristKeyChain.length; i++) {
            if(_zobristKeyChain[i] < min)
                min = _zobristKeyChain[i];
        }
        return min;
    }
    
    /**
     * Find the variation index of the lowest value Zobrist key in the keychain 
     * and return it.
     * 
     * @return The minimum Zobrist key's variation index.
     */
    public int getMinZobristKeyVariationIndex() {
        long min = _zobristKeyChain[0];
        int minI = 0;
        for (int i = 1; i < _zobristKeyChain.length; i++) {
            if(_zobristKeyChain[i] < min) {
                min = _zobristKeyChain[i];
                minI = i;
            }
        }
        return minI;
    }
    
    /**
     * Get the zobrist key corresponding to BoardVariation.valueof(variationIndex).
     * 
     * @param variationIndex The index of the variation.
     * @return The Zobrist key.
     */
    public long getZobristKey(int variationIndex) {
        return _zobristKeyChain[variationIndex];
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Helpers                                                                //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Get the bit value in a specific position of a bitset.
     * 
     * @return The bit value in a specific position of a bitset.
     */
    private static long getAt(long bitSet, int bitIndex) {
        return ((bitSet >> bitIndex) & 1L);
    }
    
    /**
     * Check if bit at a specific index in long is in the same status as the
     * given.
     * 
     * @return True if the bitSet has a bit equal to status at bitsetIndex.
     */
    private static boolean hasAt(long bitSet, int bitIndex, long status) {
        return ((bitSet >> bitIndex) & 1L) == status;
    }
    
    /**
     * Check if bit at a specific index in long is set.
     * 
     * @return True if the bitSet has a set bit at bitsetIndex.
     */
    private static boolean isSetAt(long bitSet, int bitIndex) {
        return ((bitSet >> bitIndex) & 1L) == 1L;
    }
    
    
    /**
     * Check if bit at a specific index in long is unset.
     * 
     * @return True if the bitSet has a unset bit at bitsetIndex.
     */
    private static boolean isUnsetAt(long bitSet, int bitIndex) {
        return ((bitSet >> bitIndex) & 1L) == 0L;
    }
    
    /**
     * Check if (x, y) is in bounds.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return True if (x, y) is in bounds.
     */
    private static boolean inBounds(int x, int y) {
        return -1 < x && x < SIZE &&
               -1 < y && y < SIZE;
    }
    
    /**
     * Convert (x, y) to a bit index with the form x*SIZE + y. It is assumed 
     * that inBounds(x, y).
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The bit index.
     */
    private static int toIndex(int x, int y) {
        return x*SIZE + y;
    }
    
    /**
     * Check if there is a disc at (x, y).
     * 
     * @param x The x coordinate, not necessarily valid.
     * @param y The y coordinate, not necessarily valid.
     * @return True if there is a valid disc at (x, y).
     */
    private boolean hasDisc(int x, int y) {
        return inBounds(x, y) && 
               isSetAt(_boardOccupied, toIndex(x, y));
    }
    
    /**
     * Check if there is a empty pos at (x, y).
     * 
     * @param x The x coordinate, not necessarily valid.
     * @param y The y coordinate, not necessarily valid.
     * @return True if there is a valid empty pos at (x, y).
     */
    private boolean isEmpty(int x, int y) {
        return inBounds(x, y) && 
               isUnsetAt(_boardOccupied, toIndex(x, y));
    }
    
    /**
     * Checks if a disc is a neighbor from _boardNeighbours.
     * 
     * @param x The x coordinate, not necessarily valid.
     * @param y The y coordinate, not necessarily valid.
     * @param playerBit The bit of the player to check
     * @return True if there is a valid neighbor at (x, y).
     */
    private boolean isNeighbor(int x, int y, boolean playerBit) {
        return inBounds(x, y) && 
               isSetAt(playerBit == P1_BIT ? _boardNeighborsP1 : _boardNeighborsP2, toIndex(x, y));
    }
    
    /**
     * Check if any of the bits surrounding a position have a specific status
     * 
     * @return True if any of the bits surrounding a position have a specific 
     * status
     */
    private boolean hasAnyDiscSurroundingWithColor(int x, int y, long status) {
        for (int dir = 0; dir < XINCR.length; dir++) {
            int x2 = x + XINCR[dir]; int y2 = y + YINCR[dir];
            
            if(inBounds(x2, y2) && 
               isSetAt(_boardOccupied, toIndex(x2, y2)) &&
               hasAt(_boardColor, toIndex(x2, y2), status)
            ) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if the position is stable.
     */
    private boolean isStableAt(int x, int y) {
        return !inBounds(x, y) || isSetAt(_boardStable, toIndex(x, y));
    }
    
    /**
     * Check if the position is stable because its out of bounds or is stable
     * with the given color
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param status The expected status if hasDisc(x, y)
     * @return True if the position is stable
     */
    private boolean hasStableWithColor(int x, int y, long status) {
        return !inBounds(x, y) || (
            isSetAt(_boardOccupied, toIndex(x, y))    &&
            hasAt(_boardColor, toIndex(x, y), status) &&
            isSetAt(_boardStable, toIndex(x, y))
        );
    }
    
    /**
     * Set (x, y) if it is a neighbor of a occupied disc. It is assumed not to 
     * be set
     */
    private void setIfItsNeighbor(int x, int y) {
        if(!isEmpty(x, y))
            return;
        
        boolean p1Set = false;
        boolean p2Set = false;
        for (int dir = 0; dir < XINCR.length && (!p1Set || !p2Set); dir++) {
            int x2 = x + XINCR[dir], y2 = y + YINCR[dir];
            if(!hasDisc(x2, y2)) {
                continue;
            }
            
            if (hasAt(_boardColor, toIndex(x2, y2), P1_LONG_BIT)) {
                _boardNeighborsP1 |= 1L << toIndex(x, y);
                ++_neighborsCountP1;
                _neighborWeightsSum += NEIGHBOR_SCORES[toIndex(x, y)];
                p1Set = true;
            } else {
                _boardNeighborsP2 |= 1L << toIndex(x, y);
                ++_neighborsCountP2;
                _neighborWeightsSum -= NEIGHBOR_SCORES[toIndex(x, y)];
                p2Set = true;
            }
        }
    }
    
    /**
     * Regenerate availableNeighbors BitSet.
     */
    private void regenAvailableNeighbors() {
        _boardNeighborsP1 = 0;
        _boardNeighborsP2 = 0;
        _neighborsCountP1 = 0;
        _neighborsCountP2 = 0;
        _neighborWeightsSum = 0;
        
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
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
            if (isSetAt(_boardOccupied, bitIndex)) {
                ZobristKeyGen.updateKeyChainPositionClaim(
                        _zobristKeyChain, 
                        bitIndex, 
                        isSetAt(_boardColor, bitIndex)
                );
            }
        }
        
        // Current player
        if(_currentPlayerBit == P2_BIT)
            ZobristKeyGen.updateKeyChainPlayerSwapped(_zobristKeyChain);
    }
    
    /**
     * Remove the existence of a neighbor at (x, y). The position is assumed to
     * be valid.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    private void removeNeighbor(int x, int y) {
        int bitsetIndex = toIndex(x, y);
        if(hasAt(_boardNeighborsP1, bitsetIndex, 1L)) {
            _boardNeighborsP1 &= ~(1L << toIndex(x, y));
            _neighborsCountP1--;
            _neighborWeightsSum -= NEIGHBOR_SCORES[toIndex(x, y)];
        }
        
        if (hasAt(_boardNeighborsP2, bitsetIndex, 1L)) {
            _boardNeighborsP2 &= ~(1L << toIndex(x, y));
            _neighborsCountP2--;
            _neighborWeightsSum += NEIGHBOR_SCORES[toIndex(x, y)];
        }
    }
    
    /**
     * Set the surrounding unset neighbors of a claimed position (x, y) with 
     * playerBit. The position is assumed to be valid.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param playerBit The player bit.
     */
    private void setSurroundingNeighbors(int x, int y, boolean playerBit) {
        for (int dir = 0; dir < XINCR.length; dir++) {
            int x2 = x + XINCR[dir], y2 = y + YINCR[dir];
            if (!isEmpty(x2, y2))
                continue;
            
            // Set position if new
            if(playerBit == P1_BIT && hasAt(_boardNeighborsP1, toIndex(x2, y2), 0L)) {
                _boardNeighborsP1 |= 1L << toIndex(x2, y2);
                ++_neighborsCountP1;
                _neighborWeightsSum += NEIGHBOR_SCORES[toIndex(x2, y2)];
            } else if(playerBit == P2_BIT && hasAt(_boardNeighborsP2, toIndex(x2, y2), 0L)) {
                _boardNeighborsP2 |= 1L << toIndex(x2, y2);
                ++_neighborsCountP2;
                _neighborWeightsSum -= NEIGHBOR_SCORES[toIndex(x2, y2)];
            }
        }
    }
    
    /**
     * Flip the surrounding neighbors of (x, y) with playerBit as the new owner 
     * of the position. The position is assumed to be valid.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param playerBit The player bit.
     */
    private void flipSurroundingNeighbors(int x, int y, boolean playerBit) {
        for (int dir = 0; dir < XINCR.length; dir++) {
            int x2 = x + XINCR[dir], y2 = y + YINCR[dir];
            if (!isEmpty(x2, y2))
                continue;
            
            // Set position if new
            if(playerBit == P1_BIT && !hasAt(_boardNeighborsP1, toIndex(x2, y2), 1L)) {
                _boardNeighborsP1 |= 1L << toIndex(x2, y2);
                ++_neighborsCountP1;
                _neighborWeightsSum += NEIGHBOR_SCORES[toIndex(x2, y2)];
            } else if(playerBit == P2_BIT && !hasAt(_boardNeighborsP2, toIndex(x2, y2), 1L)) {
                _boardNeighborsP2 |= 1L << toIndex(x2, y2);
                ++_neighborsCountP2;
                _neighborWeightsSum -= NEIGHBOR_SCORES[toIndex(x2, y2)];
            }
            
            // Unset old
            if(playerBit == P2_BIT && !hasAnyDiscSurroundingWithColor(x2, y2, P1_LONG_BIT)) {
                _boardNeighborsP1 &= ~(1L << toIndex(x2, y2));
                _neighborsCountP1--;
                _neighborWeightsSum -= NEIGHBOR_SCORES[toIndex(x2, y2)];
            } else if(playerBit == P1_BIT && !hasAnyDiscSurroundingWithColor(x2, y2, P2_LONG_BIT)) {
                _boardNeighborsP2 &= ~(1L << toIndex(x2, y2));
                _neighborsCountP2--;
                _neighborWeightsSum += NEIGHBOR_SCORES[toIndex(x2, y2)];
            }
        }
    }
    
    /**
     * Update the stability status of (x,y).
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param playerBit The player bit.
     */
    private void updateStability(int x, int y) {
        if(isStableAt(x, y) || isUnsetAt(_boardOccupied, toIndex(x, y)))
            return;
        
        // Check surrounding stability with same color discs
        int sameColorDifDirectionCount = 0;
        long thisColor = getAt(_boardColor, toIndex(x, y));
        
        for (int i = 0; i < XINCR2.length; i+=2) {
            int x2 = x + XINCR2[i+0], y2 = y + YINCR2[i+0];
            int x3 = x + XINCR2[i+1], y3 = y + YINCR2[i+1];
            
            if (hasStableWithColor(x2, y2, thisColor) || hasStableWithColor(x3, y3, thisColor)) {
                sameColorDifDirectionCount++;
            }
        }
        
        if(4 <= sameColorDifDirectionCount) {
            // Set pos
            _boardStable |= 1L << toIndex(x, y);
            
            // Update stable score
            if (thisColor == P1_LONG_BIT) {
                _stableDiscScoreSum += stableScore;
            } else {
                _stableDiscScoreSum -= stableScore;
            }
            
            // Update surrounding
            for (int i = 0; i < XINCR.length; i++) {
                updateStability(x + XINCR[i], y + YINCR[i]);
            }
        } else {
            // Check surrounding stability with different color discs
            long otherColor = ~thisColor;
            
            for (int i = 0; i < XINCR.length; i+=2) {
                int x2 = x + XINCR[i+0], y2 = y + YINCR[i+0];

                if (!hasStableWithColor(x2, y2, otherColor))
                    return;
            }

            // Set Pos
            _boardStable |= 1L << toIndex(x, y);
        }
    }
    
    /**
     * Regenerate _boardStable values.
     */
    private void regenStability() {
        _boardStable = 0;
        _stableDiscScoreSum = 0;
        
        updateStability(0,      0);
        updateStability(0,      SIZE-1);
        updateStability(SIZE-1, 0);
        updateStability(SIZE-1, SIZE-1);
    }
    
    /**
     * Claim position (x,y) for player. The position is assumed to 
     * canMovePiece(x, y) or being called from the constructor.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param playerBit The player bit.
     */
    private void claimPosition(int x, int y, boolean playerBit) {
        // Update board
        long playerLongBit = (playerBit == P1_BIT ? P1_LONG_BIT : P2_LONG_BIT);
        _boardOccupied   |= 1L            << toIndex(x, y);
        _boardColor      |= playerLongBit << toIndex(x, y);
        
        // Update pieces count
        if(playerBit == P1_BIT) {
            _piecesCountP1++;
            _diskWeightsSum += Status.DISK_SCORES[toIndex(x, y)];
        } else {
            _piecesCountP2++;
            _diskWeightsSum -= Status.DISK_SCORES[toIndex(x, y)];
        }
        
        // Update neighbors
        removeNeighbor(x, y);
        setSurroundingNeighbors(x, y, playerBit);
        
        // Update stability
        updateStability(x, y);
        
        // Update zobrist keychain
        ZobristKeyGen.updateKeyChainPositionClaim(
                _zobristKeyChain, 
                toIndex(x, y), 
                playerBit
        );
    }

    /**
     * Flip position (x,y). The position is assumed to hasDisc(x, y) and to have
     * !playerBit.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param playerBit The player bit.
     */
    private void flipPosition(int x, int y, boolean playerBit) {
        // Update board
        _boardColor ^= 1L << toIndex(x, y);
        
        // Update neighbors
        flipSurroundingNeighbors(x, y, playerBit);
        
        // Update stability
        updateStability(x, y);
        
        // Update zobrist keychain
        ZobristKeyGen.updateKeyChainPositionFlip(_zobristKeyChain, toIndex(x, y));
        
        // Update pieces count
        if(playerBit == P1_BIT) {
            _piecesCountP1++;
            _piecesCountP2--;
            _diskWeightsSum += Status.DISK_SCORES[toIndex(x, y)]*2;
        } else {
            _piecesCountP1--;
            _piecesCountP2++;
            _diskWeightsSum -= Status.DISK_SCORES[toIndex(x, y)]*2;
        }
    }
      
    /**
     * Check if a movement at (x,y) would envelop enemies pieces at (dx, dy) 
     * direction. The position is assumed to isNeighbor(x, y).
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param dx The x coordinate increment.
     * @param dy The y coordinate increment.
     * @param playerBit The player bit.
     */
    private boolean envelops(int x, int y, int dx, int dy, boolean playerBit) {
        long playerLongBit = (playerBit == P1_BIT ? P1_LONG_BIT : P2_LONG_BIT);
        long otherLongBit  = (playerBit == P1_BIT ? P2_LONG_BIT : P1_LONG_BIT);
        
        // Go to the specified direction until out of bounds or finding a free 
        // position or a player's disc
        int positionsSeen = 0;
        do {
            x+=dx;
            y+=dy;
            positionsSeen++;
        } while (
            inBounds(x, y) && 
            isSetAt(_boardOccupied, toIndex(x, y)) && 
            hasAt(_boardColor, toIndex(x, y), otherLongBit) &&
            isUnsetAt(_boardStable, toIndex(x, y))
        );
        
        // Return true if an envelop is possible
        return 1 < positionsSeen && 
               inBounds(x, y) && 
               isSetAt(_boardOccupied, toIndex(x, y)) &&
               hasAt(_boardColor, toIndex(x, y), playerLongBit);
    }
    
    /**
     * Check if a movement at (x,y) can be made with the given playerBit.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate    . 
     * @param playerBit The playerBit coordinate.
     * @return True if only if a movement can be made at (x, y) with playerBit.
     */
    private boolean canMovePiece(int x, int y, boolean playerBit) {       
        return isNeighbor(x, y, !playerBit) && (
               envelops(x, y, XINCR[0], YINCR[0], playerBit) || // UP_LEFT
               envelops(x, y, XINCR[1], YINCR[1], playerBit) || // UP
               envelops(x, y, XINCR[2], YINCR[2], playerBit) || // UP_RIGHT
               envelops(x, y, XINCR[3], YINCR[3], playerBit) || // LEFT
               envelops(x, y, XINCR[4], YINCR[4], playerBit) || // RIGHT
               envelops(x, y, XINCR[5], YINCR[5], playerBit) || // BOTTOM_LEFT
               envelops(x, y, XINCR[6], YINCR[6], playerBit) || // BOTTOM
               envelops(x, y, XINCR[7], YINCR[7], playerBit)    // BOTTOM_RIGHT
        );
    }
    
    /**
     * Make a movement at (x,y) with direction (dx, dy). The position, bit and 
     * direction are assumed to be correct.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param dx The x coordinate increment.
     * @param dy The y coordinate increment.
     * @param playerBit The player bit.
     */
    private void flipEnveloped(int x, int y, int dx, int dy, boolean playerBit) {
        long otherLongBit  = (playerBit == P1_BIT ? P2_LONG_BIT : P1_LONG_BIT);
        
        // Align to the first disc to flip
        x+=dx;
        y+=dy;
        
        // Flip discs until getting to the other same-color bit
        do {
            flipPosition(x, y, playerBit);
            x+=dx;
            y+=dy;
        } while (
            hasAt(_boardColor, toIndex(x, y), otherLongBit)
        );
    }
    
    /**
     * Make a movement using the current player at the given point. The position
     * is assumed to canMovePiece(x, y).
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
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
            int x = bitIndex/SIZE, y = bitIndex%SIZE;

            if(canMovePiece(x, y, P1_BIT) || canMovePiece(x, y, P2_BIT)) {
                return false;
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
            if (isSetAt(_boardOccupied, bitIndex)) {
                if (hasAt(_boardColor, bitIndex, P1_LONG_BIT)) {
                    dw += Status.DISK_SCORES[bitIndex];
                } else {
                    dw -= Status.DISK_SCORES[bitIndex];
                }
            }
        }
        
        return dw;
    }
}
