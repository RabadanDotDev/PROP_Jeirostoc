package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Transposition table of HeuristicStatus capable of giving a list of explorable
 * nodes.
 * 
 * @author raul
 * @author josep
 */
public class TT {
    /**
     * Mask to extract the "selected heuristic" from the 8 lower bytes of the
     * entry.
     */
    private static final long SELECTED_HEURISTIC_MASK  = 0xffffffffL;
    
    /**
     * Mask to extract the low byte.
     */
    private static final long BYTE_MASK = 0xffL;
    
    /**
     * Amount of bits to shift to the right to put the byte specifying the 
     * selected movement from the entry in the low position.
     */
    private static final int SELECTED_MOVEMENT_SHIFT = 32;
    
    /**
     * Amount of bits to shift to the right to put the byte specifying the 
     * depth below from the entry in the low position.
     */
    private static final int DEPTH_BELOW_SHIFT = 40;
    
    /**
     * Mask with a 1 on the position of the bit isExact from the flags field of 
     * the entry.
     */
    private static final long FLAG_IS_EXACT_MASK = 1L << (48);
    
    /**
     * Mask with a 1 on the position of the bit isAlpha from the flags field of 
     * the entry.
     */
    private static final long FLAG_IS_ALPHA_MASK = 1L << (49);
    
    /**
     * Mask with a 1 on the position of the bit isValidEntry from the flags 
     * field of the entry.
     */
    private static final long FLAG_IS_VALID_ENTRY_MASK = 1L << (50);
    
    /**
     * Longs per entry.
     */
    private static final long LONGS_PER_ENTRY = 2;
    
    /**
     * Default number of entries in the transposition table.
     */
    public static final long DEF_NUM_ENTRIES = 134204621;
    
    /**
     * Number of entries in the transposition table.
     */
    private final long _numEntries;
    
    /**
     * Transposition table internal data.
     */
    private final long[] _table;
    
    /**
     * Counter for the number of write collisions.
     */
    private long _numColisions;

    /**
     * Default constructor.
     */
    public TT() {
        _numEntries = DEF_NUM_ENTRIES;
        _table = new long[(int)(_numEntries*LONGS_PER_ENTRY)];
        _numColisions = 0;
    }
    
    /**
     * Constructor with a custom table size.
     * 
     * @param numEntries The number of entries in the table
     */
    public TT(int numEntries) {
        _numEntries = numEntries;
        _table = new long[(int)(_numEntries*LONGS_PER_ENTRY)];
        _numColisions = 0;
    }
    
    /**
     * Dumps all data form the transposition table to the opening book
     * @param bw The opening book to write
     */
    public void dump(BufferedWriter bw) {
        try {
            for (int entry = 0; entry < _numEntries; entry++) {
                int index = entry * (int)LONGS_PER_ENTRY;
                if(extractIsValidEntry(_table[index+1])) {
                    bw.append(Long.toString(_table[index  ])).append("\n");
                    bw.append(Long.toString(_table[index+1])).append("\n");
                }
            }
            
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(TT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Reads the stored transposition table from the opening book
     * @param br The opening book to read
     */
    public void fill(BufferedReader br) {
        try {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                // Extract xored_key+data
                long xored_key = Long.parseLong(line);
                long entry     = Long.parseLong(br.readLine());
                
                // Write to table
                long key       = xored_key ^ entry;
                int index = (int) (Long.remainderUnsigned(key, _numEntries)*LONGS_PER_ENTRY);
                
                _table[index]   = xored_key;
                _table[index+1] = entry;
            }
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(TT.class.getName()).log(Level.SEVERE, null, ex);
            
            // Clear table just in case
            for (int i = 0; i < _table.length; i++) {
                _table[i] = 0;
            }
        }
    }
    
    /**
     * Register an entry to the TranspositionTable. It will be added if there is
     * no collision, if the collision has a different min Zobrist hash or the 
     * depthBelow is lower or equal than the provided
     * 
     * @param s The status to register
     * @param selectedHeuristic The selected heuristic for this status
     * @param selectedMovementBitIndex The selected movement expressed in x*SIZE
     * + y format
     * @param depthBelow The depth explored below s
     * @param isExact The flag to indicate if the heuristic is exact (true) or 
     * was calculated with pruning (false)
     * @param isAlpha The flag to indicate if the heuristic is an lower bound 
     * (true) or a upper bound (false).
     */
    public void register(Status s, float selectedHeuristic, byte selectedMovementBitIndex, byte depthBelow, boolean isExact, boolean isAlpha) {
        // Compute index and key
        int variationIndex = s.getMinZobristKeyVariationIndex();
        long key = s.getZobristKey(variationIndex);
        int index = (int) (Long.remainderUnsigned(key, _numEntries)*LONGS_PER_ENTRY);
        
        // Get current
        long currentKey      = _table[index];
        long currentEntry    = _table[index+1];
        
        // Try write
        if (!extractIsValidEntry(currentEntry) ||
            (currentKey ^ currentEntry) != key || 
            extractDepthBelow(currentEntry) <= depthBelow) {
            
            long newEntry = toEntry(
                    selectedHeuristic, 
                    BoardVariation.applyTransformation(selectedMovementBitIndex, variationIndex), 
                    depthBelow, 
                    isExact, 
                    isAlpha
            );
            
            _table[index  ] = key ^ newEntry;
            _table[index+1] = newEntry;
            
            if(extractIsValidEntry(currentEntry) && (currentKey ^ currentEntry) != key)
                _numColisions++;
        }
    }
    
    /**
     * Read the entry in the bitpacked format from the transposition table. If a
     * registered entry with a valid movement or -1 as the movement can't be 
     * found, it returns 0.
     * 
     * @param s The status to register
     * @return The entry
     */
    public long readEntry(Status s) {
        // Compute index and key
        long key = s.getMinZobristKey();
        int index = (int) (Long.remainderUnsigned(key, _numEntries)*LONGS_PER_ENTRY);
        
        // Get current
        long currentKey      = _table[index];
        long currentEntry    = _table[index+1];
        byte extractedMove = extractSelectedMovement(s, currentEntry);
        
        // Check and return
        if(extractIsValidEntry(currentEntry) && (currentKey ^ currentEntry) == key) {
            if(extractedMove == -1 || s.canMovePiece(extractedMove/Status.SIZE, extractedMove%Status.SIZE)) {
                return currentEntry;
            } else {
                System.out.println("[JeiroWarning] Extracted invalid movement. Current entry:");
                System.out.println(entryToString(currentEntry));
                System.out.println(s.toString(true));
            }
        }
        
        return 0;
    }
    
    /**
     * Get the number of collisions recorded.
     */
    public long getNumCollisions() {
        return _numColisions;
    }
    
    /**
     * Express the component entries in the bitpacked format.
     * 
     * @param selectedHeuristic The selected heuristic
     * @param selectedMovementBitIndex The selected movement expressed in x*SIZE
     * + y format
     * @param depthBelow The depth explored
     * @param isExact The flag to indicate if the heuristic is exact (true) or 
     * was calculated with pruning (false)
     * @param isAlpha The flag to indicate if the heuristic is an upper bound 
     * (true) or a lower bound (false).
     * @return The entry in the bitpacked format.
     */
    public static long toEntry(float selectedHeuristic, byte selectedMovementBitIndex, byte depthBelow, boolean isExact, boolean isAlpha) {
        return ((long)Float.floatToRawIntBits(selectedHeuristic) & SELECTED_HEURISTIC_MASK) |
               ((long)selectedMovementBitIndex & BYTE_MASK) << SELECTED_MOVEMENT_SHIFT        |
               ((long)depthBelow               & BYTE_MASK) <<       DEPTH_BELOW_SHIFT        |
               (isExact ? FLAG_IS_EXACT_MASK : 0L)                                            |
               (isAlpha ? FLAG_IS_ALPHA_MASK : 0L)                                            |
               FLAG_IS_VALID_ENTRY_MASK;
    }
    
    /**
     * Extract the selected heuristic from the entry.
     * 
     * @param entry The entry to extract from.
     * @return The selected heuristic.
     */
    public static float extractSelectedHeuristic(long entry) {
        return Float.intBitsToFloat((int)(entry & SELECTED_HEURISTIC_MASK));
    }
    
    /**
     * Extract the selected movement from the entry.
     * 
     * @param entry The entry to extract from.
     * @return The selected movement.
     */
    public static byte extractSelectedMovement(long entry) {
        return (byte)((entry >> SELECTED_MOVEMENT_SHIFT) & BYTE_MASK);
    }
    
    /**
     * Extract the selected movement from the entry with the Variation that 
     * corresponds to s.
     * 
     * @param s The status.
     * @param entry The entry to extract from.
     * @return The selected movement with the correct orientation.
     */
    public static byte extractSelectedMovement(Status s, long entry) {
        return BoardVariation.applyInverseTransformation(
                (byte)((entry >> SELECTED_MOVEMENT_SHIFT) & BYTE_MASK),
                s.getMinZobristKeyVariationIndex()
        );
    }
    
    /**
     * Extract the depthBelow from the entry.
     * 
     * @param entry The entry to extract from.
     * @return The depthBelow.
     */
    public static byte extractDepthBelow(long entry) {
        return (byte)((entry >> DEPTH_BELOW_SHIFT) & BYTE_MASK);
    }
    
    /**
     * Extract the isExact flag from the entry.
     * 
     * @param entry The entry to extract from.
     * @return The isExact flag.
     */
    public static boolean extractIsExact(long entry) {
        return (entry & FLAG_IS_EXACT_MASK) != 0;
    }
    
    /**
     * Extract the isAlpha flag from the entry.
     * 
     * @param entry The entry to extract from.
     * @return The isAlpha flag.
     */
    public static boolean extractIsAlpha(long entry) {
        return (entry & FLAG_IS_ALPHA_MASK) != 0;
    }
    
    /**
     * Extract the isValid flag from the entry.
     * 
     * @param entry The entry to extract from.
     * @return The isValid flag.
     */
    public static boolean extractIsValidEntry(long entry) {
        return (entry & FLAG_IS_VALID_ENTRY_MASK) != 0;
    }
    
    /**
     * Extract the selected movement in entry if the entry is valid and return 
     * it. In case in is not valid, return -1.
     * 
     * @param s The status.
     * @param entry The entry to extract from.
     * @return The selected movement with the correct orientation or -1.
     */
    static byte extractSelectedMovementIfValidEntry(Status s, long entry) {
        if(extractIsValidEntry(entry))
            return extractSelectedMovement(s, entry);
        else
            return -1;
    }
    
    /**
     * Check if the entry is valid, the recorded depth is equal or greater than
     * minDepthBelow and the stored bound is the same as the given one.
     * 
     * @param entry The entry.
     * @param minDepthBelow The minimum depth below the entry has to have to be 
     * able to extract the heuristic from it.
     * @param isAlpha True to indicate if the expected heuristic is an upper 
     * bound (true) or a lower bound (false).
     * @return True if the heuristic can be used with the given params and false
     * if not
     */
    static boolean canExtractHeuristic(long entry, int minDepthBelow) {
        return extractIsValidEntry(entry) && 
               minDepthBelow <= extractDepthBelow(entry);
    }
    
    /**
     * Express the given entry as a string.
     * 
     * @param entry The entry to express as a string.
     * @return The string representation of the entry.
     */
    public static String entryToString(long entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("Raw: ");
        sb.append(String.format("%64s", Long.toBinaryString(entry)).replace(' ', '0'));
        sb.append('\n');
        sb.append("selectedHeuristic: "); 
        sb.append(extractSelectedHeuristic(entry));
        sb.append('\n');
        sb.append("selectedMovement:  ");
        sb.append(extractSelectedMovement(entry));
        sb.append('\n');
        sb.append("depthBelow:        ");
        sb.append(extractDepthBelow(entry));
        sb.append('\n');
        sb.append("isExact:           ");
        sb.append(extractIsExact(entry));
        sb.append('\n');
        sb.append("isAlpha:           ");
        sb.append(extractIsAlpha(entry));
        sb.append('\n');
        sb.append("isValidEntry:      ");
        sb.append(extractIsValidEntry(entry));
        return sb.toString();
    }
}
