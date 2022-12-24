package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final float HEURISTIC_VER = 6.0f;
    
    /**
     * Default rotation and flip independent disk weights values.
     */
    public final static float[] DWV_DEF = {
        5, 
        1, 1, 
        3, 2, 2, 
        3, 2, 2, 1
    };
    
    /**
     * Default rotation and flip independent neighbors weights values.
     */
    public final static float[] NWV_DEF = {
        4, 
        2,    1, 
        1.5f, 1, 1, 
        1.5f, 1, 1, 0
    };
    
    /**
     * Default score to give for each registered stable position.
     */
    public final static float STABLE_SCORE_DEFAULT = 5.0f;
    
    /**
     * Default score to give for each captured position.
     */
    public final static float[] DISK_SCORES_DEFAULT = generateScoringTable(DWV_DEF); 
    
    /**
     * Default score to give for each neighboring position.
     */
    public final static float[] NEIGHBOR_SCORES_DEFAULT = generateScoringTable(NWV_DEF); 
    
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
     * Score to give for each registered stable position.
     */
    private final float _stableScore;
    
    /**
     * Score to give for each captured position.
     */
    private final float[] _diskScores;
    
    /**
     * Score to give for each neighboring position.
     */
    private final float[] _neighborScores;
    
    /**
     * Positions of the game with a disk of any player. The bits are ordered in 
     * the form x*SIZE + y.
     */
    private long _boardOccupied;
    
    /**
     * The color of the disk of each occupied position. The bits are ordered in 
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
     * position is adjacent to more than one disk of P1, it is counted as many 
     * times as adjacent disks there are.
     */
    private int _neighborsCountP1;
    
    /**
     * The number of empty neighboring positions to pieces of P2. If an empty 
     * position is adjacent to more than one disk of P2, it is counted as many 
     * times as adjacent disks there are.
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
     * The cached heuristic value from the stable disk scores.
     */
    private float _stableDiskScoreSum;
    
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
    // Public static utility functions                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Generate a table of SIZE*SIZE length of scores from a list of rotation 
     * and flip independent values. The expected format is as follows:
     * 0 . . . . . . .
     * 1 2 . . . . . .
     * 3 4 5 . . . . .
     * 6 7 8 9 . . . .
     * . . . . . . . .
     * . . . . . . . .
     * . . . . . . . .
     * . . . . . . . .
     * With each number from 0 to 9 indicating the position of the value on the 
     * array.
     * 
     * @param rfil The list of rotation and flip independent values.
     * @return The generated table.
     */
    public static float[] generateScoringTable(float[] rfil) {
        return new float[] {
            rfil[0], rfil[1], rfil[3], rfil[6], rfil[6], rfil[3], rfil[1], rfil[0],
            rfil[1], rfil[2], rfil[4], rfil[7], rfil[7], rfil[4], rfil[2], rfil[1],
            rfil[3], rfil[4], rfil[5], rfil[8], rfil[8], rfil[5], rfil[4], rfil[3],
            rfil[6], rfil[7], rfil[8], rfil[9], rfil[9], rfil[8], rfil[7], rfil[6],
            rfil[6], rfil[7], rfil[8], rfil[9], rfil[9], rfil[8], rfil[7], rfil[6],
            rfil[3], rfil[4], rfil[5], rfil[8], rfil[8], rfil[5], rfil[4], rfil[3],
            rfil[1], rfil[2], rfil[4], rfil[7], rfil[7], rfil[4], rfil[2], rfil[1],
            rfil[0], rfil[1], rfil[3], rfil[6], rfil[6], rfil[3], rfil[1], rfil[0]
        };
    }
    
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
        // Init scoring for heuristic
        _stableScore = STABLE_SCORE_DEFAULT;
        _diskScores = DISK_SCORES_DEFAULT;
        _neighborScores = NEIGHBOR_SCORES_DEFAULT;
        
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
        _stableDiskScoreSum = 0;
        
        // Init zobrist keychain
        _zobristKeyChain = new long[BoardVariation.NUMBER];
        
        // Set default pieces
        claimPosition(3, 3, P1_BIT);
        claimPosition(4, 3, P2_BIT);
        claimPosition(3, 4, P2_BIT);
        claimPosition(4, 4, P1_BIT);
        
        // Init Heuristics cache
        _diskWeightsSum = computeDiskScoresSum();
        
        // Init game status
        _lastMovement     = -1;
        _isTerminalState  = false;
        _currentPlayerBit = P1_BIT;
    }
    
    /**
     * Constructor with a given board. Starts a game with the given board and 
     * starting player.
     * 
     * @param board The board. 1 means a disk of P1, -1 means a disk of P2 and 
     * 0 an empty space.
     * @param startingPlayerBit The bit of the starting player.
     */
    public Status(int[][] board, boolean startingPlayerBit) {
        // Init scoring for heuristic
        _stableScore = STABLE_SCORE_DEFAULT;
        _diskScores = DISK_SCORES_DEFAULT;
        _neighborScores = NEIGHBOR_SCORES_DEFAULT;
        
        // Init board
        _boardOccupied    = 0;
        _boardColor       = 0;
        _boardStable      = 0;
        _boardNeighborsP1 = 0;
        _boardNeighborsP2 = 0;
        
        // Init neighbors count
        _neighborsCountP1 = 0;
        _neighborsCountP2 = 0;
        _neighborWeightsSum = 0;
        _stableDiskScoreSum = 0;
        
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
        _diskWeightsSum = computeDiskScoresSum();
        
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
        this(gs, STABLE_SCORE_DEFAULT, DISK_SCORES_DEFAULT, NEIGHBOR_SCORES_DEFAULT);
    }
    
    /**
     * Constructor from a Game status.
     * 
     * @param gs The game status to copy from.
     * @param stableScore The score to evaluate the detected positions with.
     * @param diskScores A list of the scores for having captured each position.
     * @param neighborScores A list of the scores for having each position as a 
     * neighbor.
     */
    public Status(GameStatus gs, float stableScore, float[] diskScores, float[] neighborScores) {
        // Init scoring for heuristic
        _stableScore = stableScore;
        _diskScores = diskScores;
        _neighborScores = neighborScores;
        
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
        _diskWeightsSum = computeDiskScoresSum();
        
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
        // Copy scoring for heuristic
        _stableScore    = other._stableScore;
        _diskScores     = other._diskScores;
        _neighborScores = other._neighborScores;
        
        // Copy board
        _boardOccupied    = other._boardOccupied;
        _boardColor       = other._boardColor;
        _boardStable      = other._boardStable;
        _boardNeighborsP1 = other._boardNeighborsP1;
        _boardNeighborsP2 = other._boardNeighborsP2;
        
        // Copy player neighbors' count
        _neighborsCountP1 = other._neighborsCountP1;
        _neighborsCountP2 = other._neighborsCountP2;
        _neighborWeightsSum = other._neighborWeightsSum;
        _stableDiskScoreSum = other._stableDiskScoreSum;
        
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
     * assumed not to be null and to canMovePiece(point.x, point.y).
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
     * Get the number of disks a player has.
     * 
     * @param playerBit The player bit.
     * @return The number of movements a player has made.
     */
    public int getNumDisks(boolean playerBit) {
        return playerBit == P1_BIT ? _piecesCountP1 : _piecesCountP2;
    }
    
    /**
     * Get the number of total disks.
     * 
     * @return The number of total disks.
     */
    public int getNumDisks() {
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
     * Get the number of neighbors player disks has.
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
     * Get a list of the next possible statuses starting from this position. The
     * moves except the bitIndexFirst are ordered in the opposite way as 
     * getNextMoves().
     * 
     * @param result The array to deposit the new statuses objects at the end of
     * the list.
     * @param bitIndexFirst The movement's BitIndex of the form SIZE*x + y that
     * should be added first to the list. It should be a correct position or -1.
     */
    public void getNextMovesInverse(List<Point> result, int bitIndexFirst) {
        // Add the given movement if possible
        if(bitIndexFirst != -1) {
            result.add(new Point(bitIndexFirst/SIZE, bitIndexFirst%SIZE));
        }
        
        for (int x = SIZE-1; 0 <= x; x--) {
            for (int y = SIZE-1; 0 <= y; y--) {
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
            
            // Disk weights
            sb.append("Disk weights: ")
              .append(Arrays.toString(_diskScores))
              .append("\n");
            
            sb.append("Disk weights sum: ")
              .append(_diskWeightsSum)
              .append("\n");
            
            // Disk neighbors
            sb.append("Disk neighbors: ")
              .append(Arrays.toString(_neighborScores))
              .append("\n");
            
            sb.append("Disk neighbors sum: ")
              .append(_neighborWeightsSum)
              .append("\n");
            
            // Stable score
            sb.append("Stable score: ")
              .append(_stableScore)
              .append("\n");
            
            sb.append("Stable score sum")
              .append(_stableDiskScoreSum)
              .append("\n");
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
        
        return playerColor*(_diskWeightsSum - _neighborWeightsSum + _stableDiskScoreSum);
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
     * @param bitSet The bitset to extract the bit from.
     * @param bitIndex The index of the bitset.
     * @return The bit value in a specific position of a bitset.
     */
    private static long getAt(long bitSet, int bitIndex) {
        return ((bitSet >> bitIndex) & 1L);
    }
    
    /**
     * Check if a bit at a specific index in long is in the same status as the
     * given.
     * 
     * @param bitSet The bitset to extract the bit from.
     * @param bitIndex The index of the bitset.
     * @param status The expected status.
     * @return True if the bitSet has a bit equal to status at bitsetIndex.
     */
    private static boolean hasAt(long bitSet, int bitIndex, long status) {
        return ((bitSet >> bitIndex) & 1L) == status;
    }
    
    /**
     * Check if a bit at a specific index in long is set.
     * 
     * @param bitSet The bitset to extract the bit from.
     * @param bitIndex The index of the bitset.
     * @return True if the bitSet has a set bit at bitsetIndex.
     */
    private static boolean isSetAt(long bitSet, int bitIndex) {
        return ((bitSet >> bitIndex) & 1L) == 1L;
    }
    
    
    /**
     * Check if a bit at a specific index in long is unset.
     * 
     * @param bitSet The bitset to extract the bit from.
     * @param bitIndex The index of the bitset.
     * @return True if the bitSet has an unset bit at bitsetIndex.
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
     * Check if there is a disk at (x, y).
     * 
     * @param x The x coordinate, not necessarily valid.
     * @param y The y coordinate, not necessarily valid.
     * @return True if there is a valid disk at (x, y).
     */
    private boolean hasDisk(int x, int y) {
        return inBounds(x, y) && 
               isSetAt(_boardOccupied, toIndex(x, y));
    }
    
    /**
     * Check if there is an empty pos at (x, y).
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
     * Checks if a disk is a neighbor from _boardNeighbours.
     * 
     * @param x The x coordinate, not necessarily valid.
     * @param y The y coordinate, not necessarily valid.
     * @param playerBit The bit of the player to check.
     * @return True if there is a valid neighbor at (x, y).
     */
    private boolean isNeighbor(int x, int y, boolean playerBit) {
        return inBounds(x, y) && 
               isSetAt(playerBit == P1_BIT ? _boardNeighborsP1 : _boardNeighborsP2, toIndex(x, y));
    }
    
    /**
     * Check if any of the bits surrounding a position have a specific status.
     * 
     * @param y The y coordinate, not necessarily valid.
     * @param x The x coordinate, not necessarily valid.
     * @param status The expected status.
     * @return True if any of the bits surrounding a position have a specific 
     * status.
     */
    private boolean hasAnyDiskSurroundingWithColor(int x, int y, long status) {
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
     * 
     * @param y The y coordinate, not necessarily valid.
     * @param x The x coordinate, not necessarily valid.
     */
    private boolean isStableAt(int x, int y) {
        return !inBounds(x, y) || isSetAt(_boardStable, toIndex(x, y));
    }
    
    /**
     * Check if the position is stable because it's out of bounds or is stable
     * with the given color.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param status The expected status if hasDisk(x, y).
     * @return True if the position is stable.
     */
    private boolean hasStableWithColor(int x, int y, long status) {
        return !inBounds(x, y) || (
            isSetAt(_boardOccupied, toIndex(x, y))    &&
            hasAt(_boardColor, toIndex(x, y), status) &&
            isSetAt(_boardStable, toIndex(x, y))
        );
    }
    
    /**
     * Set (x, y) if it is a neighbor of a occupied disk. It is assumed not to 
     * be set.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    private void setIfItsNeighbor(int x, int y) {
        if(!isEmpty(x, y))
            return;
        
        boolean p1Set = false;
        boolean p2Set = false;
        for (int dir = 0; dir < XINCR.length && (!p1Set || !p2Set); dir++) {
            int x2 = x + XINCR[dir], y2 = y + YINCR[dir];
            if(!hasDisk(x2, y2)) {
                continue;
            }
            
            if (hasAt(_boardColor, toIndex(x2, y2), P1_LONG_BIT)) {
                _boardNeighborsP1 |= 1L << toIndex(x, y);
                ++_neighborsCountP1;
                _neighborWeightsSum += _neighborScores[toIndex(x, y)];
                p1Set = true;
            } else {
                _boardNeighborsP2 |= 1L << toIndex(x, y);
                ++_neighborsCountP2;
                _neighborWeightsSum -= _neighborScores[toIndex(x, y)];
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
            _neighborWeightsSum -= _neighborScores[toIndex(x, y)];
        }
        
        if (hasAt(_boardNeighborsP2, bitsetIndex, 1L)) {
            _boardNeighborsP2 &= ~(1L << toIndex(x, y));
            _neighborsCountP2--;
            _neighborWeightsSum += _neighborScores[toIndex(x, y)];
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
                _neighborWeightsSum += _neighborScores[toIndex(x2, y2)];
            } else if(playerBit == P2_BIT && hasAt(_boardNeighborsP2, toIndex(x2, y2), 0L)) {
                _boardNeighborsP2 |= 1L << toIndex(x2, y2);
                ++_neighborsCountP2;
                _neighborWeightsSum -= _neighborScores[toIndex(x2, y2)];
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
                _neighborWeightsSum += _neighborScores[toIndex(x2, y2)];
            } else if(playerBit == P2_BIT && !hasAt(_boardNeighborsP2, toIndex(x2, y2), 1L)) {
                _boardNeighborsP2 |= 1L << toIndex(x2, y2);
                ++_neighborsCountP2;
                _neighborWeightsSum -= _neighborScores[toIndex(x2, y2)];
            }
            
            // Unset old
            if(playerBit == P2_BIT && !hasAnyDiskSurroundingWithColor(x2, y2, P1_LONG_BIT)) {
                _boardNeighborsP1 &= ~(1L << toIndex(x2, y2));
                _neighborsCountP1--;
                _neighborWeightsSum -= _neighborScores[toIndex(x2, y2)];
            } else if(playerBit == P1_BIT && !hasAnyDiskSurroundingWithColor(x2, y2, P2_LONG_BIT)) {
                _boardNeighborsP2 &= ~(1L << toIndex(x2, y2));
                _neighborsCountP2--;
                _neighborWeightsSum += _neighborScores[toIndex(x2, y2)];
            }
        }
    }
    
    /**
     * Update the stability status of (x, y).
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param playerBit The player bit.
     */
    private void updateStability(int x, int y) {
        if(isStableAt(x, y) || isUnsetAt(_boardOccupied, toIndex(x, y)) || _stableScore == 0)
            return;
        
        // Check surrounding stability with same color disks
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
                _stableDiskScoreSum += _stableScore;
            } else {
                _stableDiskScoreSum -= _stableScore;
            }
            
            // Update surrounding
            for (int i = 0; i < XINCR.length; i++) {
                updateStability(x + XINCR[i], y + YINCR[i]);
            }
        } else {
            // Check surrounding stability with different color disks
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
        _stableDiskScoreSum = 0;
        
        updateStability(0,      0);
        updateStability(0,      SIZE-1);
        updateStability(SIZE-1, 0);
        updateStability(SIZE-1, SIZE-1);
    }
    
    /**
     * Claim position (x, y) for player. The position is assumed to 
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
            _diskWeightsSum += _diskScores[toIndex(x, y)];
        } else {
            _piecesCountP2++;
            _diskWeightsSum -= _diskScores[toIndex(x, y)];
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
     * Flip position (x, y). The position is assumed to hasDisk(x, y) and to have
     * !playerBit.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param playerBit The player bit.
     */
    private void flipPosition(int x, int y, boolean playerBit) {
        // Update board
        _boardColor ^= 1L << toIndex(x, y);
        
        // Update pieces count
        if(playerBit == P1_BIT) {
            _piecesCountP1++;
            _piecesCountP2--;
            _diskWeightsSum += _diskScores[toIndex(x, y)]*2;
        } else {
            _piecesCountP1--;
            _piecesCountP2++;
            _diskWeightsSum -= _diskScores[toIndex(x, y)]*2;
        }
        
        // Update neighbors
        flipSurroundingNeighbors(x, y, playerBit);
        
        // Update stability
        updateStability(x, y);
        
        // Update zobrist keychain
        ZobristKeyGen.updateKeyChainPositionFlip(_zobristKeyChain, toIndex(x, y));
    }
      
    /**
     * Check if a movement at (x, y) would envelop enemies pieces at (dx, dy) 
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
        // position or a player's disk
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
     * Check if a movement at (x, y) can be made with the given playerBit.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate. 
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
     * Make a movement at (x, y) with direction (dx, dy). The position, bit and 
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
        
        // Align to the first disk to flip
        x+=dx;
        y+=dy;
        
        // Flip disks until getting to the other same-color bit
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
     * Check if the current game is in terminal state.
     * 
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
     * Compute the disk score value sum from the current status.
     */
    private float computeDiskScoresSum() {
        float dw = 0;
        
        for (int bitIndex = 0; bitIndex < SIZE*SIZE; bitIndex++) {
            if (isSetAt(_boardOccupied, bitIndex)) {
                if (hasAt(_boardColor, bitIndex, P1_LONG_BIT)) {
                    dw += _diskScores[bitIndex];
                } else {
                    dw -= _diskScores[bitIndex];
                }
            }
        }
        
        return dw;
    }
}
