package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to generate Zobrist keys for the Othello game
 * 
 * @author raul
 * @author josep
 */
public class ZobristKeyGen {
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
     * The filename to store the zobrist values to.
     */
    private final static String filename = "ZobristValues.data";
    
    /**
     * Get the Zobrist value corresponding to the position p with the 
     * CellType c and having applied the transformation indicated by bp.
     * 
     * @param p The position.
     * @param c The cell type.
     * @param bp The BoardVariation.
     * @return The Zobrist value.
     */
    public static long getZobristValue(Point p, CellType c, BoardVariation bp) {
        return VALUES[posToIndex(p.x, p.y, (c == CellType.PLAYER1 ? (int)Status.P1_LONG_BIT : (int)Status.P2_LONG_BIT), bp.v)];
    }
    
    /**
     * Update a list of Zobrist hashes performing a XOR IN and XOR OUT of all 
     * the variations of a specific BitSet index (with the form x*SIZE + y) and 
     * a specific state.
     * 
     * @param keychain The list of Zobrist hashes to update.
     * @param bitsetIndex The BitSet index.
     */
    public static void updateKeyChainPositionFlip(long[] keychain, int bitsetIndex) {
        int basePos = bitsetIndex*BOARD_STATE*BoardVariation.NUMBER;
        
        for (int i = 0; i < BoardVariation.NUMBER; i++) {
            keychain[i] ^= VALUES[basePos + i];
            keychain[i] ^= VALUES[basePos + i + BoardVariation.NUMBER];
        }
    }
    
    /**
     * Update a list of Zobrist hashes performing a XOR IN of all the variations
     * of a specific BitSet index (with the form x*SIZE + y) and a specific status
     * 
     * @param keychain The list of Zobrist hashes to update.
     * @param bitsetIndex The BitSet index.
     * @param playerBit The player bit for the state.
     */
    public static void updateKeyChainPositionClaim(long[] keychain, int bitsetIndex, boolean playerBit) {
        int basePos = bitsetIndex*BOARD_STATE*BoardVariation.NUMBER +
                      (playerBit ? BoardVariation.NUMBER : 0);
        
        for (int i = 0; i < BoardVariation.NUMBER; i++) {
            keychain[i] ^= VALUES[basePos+i];
        }
    }
    
    /**
     * Update a list of Zobrist hashes performing a XOR IN/OUT the value of P2.
     * 
     * @param keychain The list of Zobrist hashes to update.
     */
    public static void updateKeyChainPlayerSwapped(long[] keychain) {
        int basePos = VALUES.length-1;
        
        for (int i = 0; i < BoardVariation.NUMBER; i++) {
            keychain[i] ^= VALUES[basePos];
        }
    }
    
    /**
     * Get the index of the Zobrist value in VALUES corresponding to the board 
     * position (x, y) with cell state s after doing the transformation specified
     * by the variation varNum.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param s The cell state.
     * @param varNum The board variation number.
     * @return The index.
     */
    private static int posToIndex(int x, int y, int s, int varNum) {
        return posToIndex(x*BOARD_SIZE + y, s, varNum);
    }
    
    /**
     * Get the index of the Zobrist value in VALUES corresponding to the board 
     * position indicated by bitsetIndex with cell state s after doing the 
     * transformation specified by the variation varNum. The BitSet index is 
     * assumed to be equal to (y*board_size + x)
     * 
     * @param bitsetIndex The BitSet index.
     * @param s The cell state.
     * @param varNum The board variation number.
     * @return The index.
     */
    private static int posToIndex(int bitsetIndex, int s, int varNum) {
        return bitsetIndex*BOARD_STATE*BoardVariation.NUMBER +
                                     s*BoardVariation.NUMBER +
                                                      varNum;
    }
    
    /**
     * Get the index of the Zobrist value in VALUES corresponding to the board 
     * position (x, y) with cell state s after doing the inverse transformation 
     * specified by the variation bp. The inverse transformation is understood 
     * to be the cell which x, y would fall in after doing bp.
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
        
        return (invX*BOARD_SIZE + invY)*BOARD_STATE*BoardVariation.NUMBER +
                                                  s*BoardVariation.NUMBER +
                                                                     bp.v;
    }
    
    /**
     * Dumps all zobrist keys form to the opening book
     * @param bw The opening book to write
     */
    public static void dumpValues(BufferedWriter bw) {
        for (int i = 0; i < VALUES.length; ++i) {
            try {
                bw.append(Long.toString(VALUES[i]));
                bw.append("\n");
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ZobristKeyGen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Reads the stored keys from the opening book
     * @param br The opening book to read
     * @return Returns how many lines were read
     */
    public static int fillValues(BufferedReader br) {
        int index = 0;
        try {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                VALUES[index] = Long.parseLong(line);
                index++;
            }
        } catch (IOException ex) {
            Logger.getLogger(ZobristKeyGen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return index;
    }
    
    /**
     * Generate the values of the Zobrist hashing.
     */
    private static void generateValues() {
        Random r = new Random();
        
        // Generate a zobrist value for each position and state of the board
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
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
        
        // Generate the value to indicate that it is the turn of player 2
        long zv = r.nextLong();
        VALUES[VALUES.length-1] = zv;
    }
    
    static {
        // Create vector
        VALUES = new long[
                BOARD_SIZE*BOARD_SIZE*           // all the positions
                BOARD_STATE*                     // 2 states by position
                BoardVariation.NUMBER +  // 8 variations by position
                1                                // 1 key to specify the player
        ];
        
        // Try to read already existing values
        int linesRead = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            linesRead = ZobristKeyGen.fillValues(br);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ZobristKeyGen.class.getName()).log(Level.WARNING, "Could not read already existing zobrist value file!");
        }
        
        // Generate new values in case they were not read sucessfully
        try {
            if (linesRead != BOARD_SIZE*BOARD_SIZE*BOARD_STATE*BoardVariation.NUMBER + 1) {
                Logger.getLogger(ZobristKeyGen.class.getName()).log(Level.INFO, "Generating new zobrist values...");
                
                // Gerate values and store them
                generateValues();
                
                // Add zobrist keys to the Opening Book
                BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
                ZobristKeyGen.dumpValues(bw);
            }
        } catch (IOException ex) {
            Logger.getLogger(ZobristKeyGen.class.getName()).log(Level.SEVERE, null, ex);
            generateValues();
        }
    }
}
