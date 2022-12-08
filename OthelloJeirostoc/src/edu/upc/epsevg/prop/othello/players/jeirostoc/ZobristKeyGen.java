package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import java.awt.Point;
import java.util.Random;

/**
 * Helper class to generate Zobrist keys for the Othello game
 * 
 * @author raul
 * @author josep
 */
public class ZobristKeyGen {    
    /**
     * Enumeration to indicate the 8 possible variations of the board.
     */
    public static enum BoardVariation{
        BASE(0),
        ROT90(1),
        ROT180(2),
        ROT270(3),
        FLIP(4),
        FLIPROT90(5),
        FLIPROT180(6),
        FLIPROT270(7);
        
        public final int v;
        public final static int NUM_VARIATIONS = BoardVariation.values().length;
        
        private BoardVariation(int v) {
            this.v = v;
        }
        
        public static BoardVariation valueOf(int v) {
            return switch (v) {
                case 0  -> BASE;
                case 1  -> ROT90;
                case 2  -> ROT180;
                case 3  -> ROT270;
                case 4  -> FLIP;
                case 5  -> FLIPROT90;
                case 6  -> FLIPROT180;
                case 7  -> FLIPROT270;
                default -> BASE;
            };
        }
    }
    
    /**
     * The table containing all the Zobrist values in the 8 variations.
     */
    private static final long[] VALUES;
    
    /**
     * The size of the board.
     */
    private final static int BOARD_SIZE = 8;
    
    /**
     * The number of considered states that can a position have.
     */
    private final static int BOARD_STATE = 2;
    
    /**
     * The number of Zobrist values one variation of the map can have.
     */
    private final static int MAP_KEYS_COUNT = BOARD_SIZE*BOARD_SIZE*BOARD_STATE;
    
    static {
        // Init
        Random r = new Random();
        VALUES = new long[BoardVariation.values().length*MAP_KEYS_COUNT + 1];
        
        // Generate a zobrist value for each position and state of the board
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int s = 0; s < BOARD_STATE; s++) {
                    // Generate zobrist value for this position and state
                    long zv = r.nextLong();
                    
                    // Update all variations
                    VALUES[inversePosToIndex(x, y, s, BoardVariation.BASE)]       = zv;
                    VALUES[inversePosToIndex(x, y, s, BoardVariation.ROT90)]      = zv;
                    VALUES[inversePosToIndex(x, y, s, BoardVariation.ROT180)]     = zv;
                    VALUES[inversePosToIndex(x, y, s, BoardVariation.ROT270)]     = zv;
                    VALUES[inversePosToIndex(x, y, s, BoardVariation.FLIP)]       = zv;
                    VALUES[inversePosToIndex(x, y, s, BoardVariation.FLIPROT90)]  = zv;
                    VALUES[inversePosToIndex(x, y, s, BoardVariation.FLIPROT180)] = zv;
                    VALUES[inversePosToIndex(x, y, s, BoardVariation.FLIPROT270)] = zv;
                }
            }
        }
        
        // Generate the value to indicate that it is the turn of player 1
        long zv = r.nextLong();
        VALUES[BoardVariation.values().length*MAP_KEYS_COUNT] = zv;
    }
    
    /**
     * Get the Zobrist value corresponding to the position p with the 
     * CellType c and having applied the transformation indicated by bp.
     * 
     * @param p The position
     * @param c The cell type
     * @param bp The BoardVariation
     * @return The Zobrist value
     */
    public static long getZobristValue(Point p, CellType c, BoardVariation bp) {
        return VALUES[posToIndex(p.x, p.y, (c == CellType.PLAYER1 ? 1 : 0), bp.v)];
    }
    
    /**
     * Get the Zobrist value corresponding to the position indicated by 
     * bitsetIndex (with the form y*SIZE + x) cell state s (0 or 1) and having 
     * applied the transformation indicated by BoardVariation.valueOf(varNum).
     * 
     * @param bitsetIndex The BitSet index
     * @param s The cell state
     * @param varNum The BoardVariation
     * @return The Zobrist value
     */
    public static long getZobristValue(int bitsetIndex, int s, int varNum) {
        return VALUES[MAP_KEYS_COUNT*varNum + bitsetIndex*BOARD_STATE + s];
    }
    
    /**
     * Get the Zobrist value corresponding to the player 2 being the one who has
     * to make a move.
     * @return The Zobrist value
     */
    public static long getZobristValueP2() {
        return VALUES[VALUES.length-1];
    }
    
    /**
     * Get the index of the Zobrist value in VALUES corresponding to the board 
     * position (x, y) with cell state s after doing the transformation specified
     * by the variation varNum
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param s The cell state.
     * @param varNum The board variation number
     * @return The index
     */
    private static int posToIndex(int x, int y, int s, int varNum) {
        return posToIndex(y*BOARD_SIZE + x, s, varNum);
    }
    
    /**
     * Get the index of the Zobrist value in VALUES corresponding to the board 
     * position indicated by bitsetIndex with cell state s after doing the 
     * transformation specified by the variation varNum. The BitSet index is 
     * assumed to be equal to (y*board_size + x)
     * 
     * @param bitsetIndex The BitSet index
     * @param s The cell state.
     * @param varNum The board variation number
     * @return The index
     */
    private static int posToIndex(int bitsetIndex, int s, int varNum) {
        return MAP_KEYS_COUNT*varNum+ bitsetIndex*BOARD_STATE + s;
    }
    
    /**
     * Get the index of the Zobrist value in VALUES corresponding to the board 
     * position (x, y) with cell state s after doing the inverse transformation 
     * specified by the variation bp. The inverse transformation is understood 
     * to be the cell which x, y would fall in after doing bp
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param s The cell state.
     * @param bp The board variation
     * @return The index
     */
    private static int inversePosToIndex(int x, int y, int s, BoardVariation bp) {
        int invX, invY;
        switch (bp) {
            case BASE       -> {invX = x;              invY = y;              }
            case ROT90      -> {invX = y;              invY = BOARD_SIZE-x-1; }
            case ROT180     -> {invX = BOARD_SIZE-x-1; invY = BOARD_SIZE-y-1; }
            case ROT270     -> {invX = BOARD_SIZE-y-1; invY = x;              }
            case FLIP       -> {invX = x;              invY = BOARD_SIZE-y-1; }
            case FLIPROT90  -> {invX = y;              invY = x;              }
            case FLIPROT180 -> {invX = BOARD_SIZE-x-1; invY = y;              }
            case FLIPROT270 -> {invX = BOARD_SIZE-y-1; invY = BOARD_SIZE-x-1; }
            default         -> {invX = x;              invY = y;              }
        }
        
        return MAP_KEYS_COUNT*bp.v + (invY*BOARD_SIZE + invX)*BOARD_STATE + s;
    }
}
