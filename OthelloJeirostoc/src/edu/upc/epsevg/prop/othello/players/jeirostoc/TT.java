package edu.upc.epsevg.prop.othello.players.jeirostoc;


/**
 * Transposition table of HeuristicStatus capable of giving a list of explorable
 * nodes
 * 
 * @author raul
 */
class TT {
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
    private static final int  SELECTED_MOVEMENT_SHIFT = 32;
    
    /**
     * Amount of bits to shift to the right to put the byte specifying the 
     * depth below from the entry in the low position.
     */
    private static final int  DEPTH_BELOW_SHIFT = 40;
    
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
     * Default number of entries in the transposition table.
     */
    private static final long DEF_NUM_ENTRIES = 134204621;
    
    /**
     * Number of entries in the transposition table.
     */
    private final long _numEntries;
    
    /**
     * Transposition table internal data.
     */
    private final long[] _table;

    /**
     * Default constructor.
     */
    public TT() {
        _numEntries = DEF_NUM_ENTRIES;
        _table = new long[(int)_numEntries*2];
    }
    
    /**
     * Constructor with a custom size table size.
     * 
     * @param numEntries The number of entries in the table
     */
    public TT(int numEntries) {
        _numEntries = numEntries;
        _table = new long[(int)_numEntries*2];
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
     * (true) or a lower bound (false).
     */
    public void register(Status s, float selectedHeuristic, byte selectedMovementBitIndex, byte depthBelow, boolean isExact, boolean isAlpha) {
        int variationIndex = s.getMinZobristKeyVariationIndex();
        long key = s.getZobristKey(variationIndex);
        int index = (int) Long.remainderUnsigned(key, _numEntries)*2;
        
        long currentKey   = _table[index];
        long currentEntry = _table[index+1];
        
        if (!extractIsValidEntry(currentEntry) || currentKey != key || extractDepthBelow(currentEntry) <= depthBelow) {
            _table[index  ] = key;
            _table[index+1] = toEntry(
                    selectedHeuristic, 
                    BoardVariation.applyTransformation(selectedMovementBitIndex, variationIndex), 
                    depthBelow, 
                    isExact, 
                    isAlpha
            );
        }
    }
    
    /**
     * Read the entry in the bitpacked format from the transposition table. If a
     * registered entry can't be found, it returns 0.
     * 
     * @param s The status to register
     * @return The entry
     */
    public long readEntry(Status s) {
        long key = s.getMinZobristKey();
        int index = (int) Long.remainderUnsigned(key, _numEntries)*2;
        
        long currentKey   = _table[index];
        long currentEntry = _table[index+1];
        
        if(extractIsValidEntry(currentEntry) && currentKey == key) 
            return currentEntry;
        else
            return 0;
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
     * @return 
     */
    public static long toEntry(float selectedHeuristic, byte selectedMovementBitIndex, byte depthBelow, boolean isExact, boolean isAlpha) {
        return ((long)Float.floatToRawIntBits(selectedHeuristic))          |
               ((long)selectedMovementBitIndex) << SELECTED_MOVEMENT_SHIFT |
               ((long)depthBelow)               <<       DEPTH_BELOW_SHIFT |
               (isExact ? FLAG_IS_EXACT_MASK  : 0L)                        |
               (isAlpha ? FLAG_IS_ALPHA_MASK  : 0L)                        |
               (FLAG_IS_VALID_ENTRY_MASK);
    }
    
    /**
     * Extract the selected heuristic from the entry.
     * 
     * @param entry The entry to extract from
     * @return The selected heuristic
     */
    public static float extractSelectedHeuristic(long entry) {
        return Float.intBitsToFloat((int)(entry & SELECTED_HEURISTIC_MASK));
    }
    
    /**
     * Extract the selected movement from the entry
     * 
     * @param entry The entry to extract from
     * @return The selected movement
     */
    public static byte extractSelectedMovement(long entry) {
        return (byte)((entry >> SELECTED_MOVEMENT_SHIFT) & BYTE_MASK);
    }
    
    /**
     * Extract the selected movement from the entry with the Variation that 
     * corresponds to s
     * 
     * @param s The status 
     * @param entry The entry to extract from
     * @return The selected movement with the correct orientation
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
     * @param entry The entry to extract from
     * @return The depthBelow
     */
    public static byte extractDepthBelow(long entry) {
        return (byte)((entry >> DEPTH_BELOW_SHIFT) & BYTE_MASK);
    }
    
    /**
     * Extract the isExact flag from the entry.
     * 
     * @param entry The entry to extract from
     * @return The isExact flag
     */
    public static boolean extractIsExact(long entry) {
        return (entry & FLAG_IS_EXACT_MASK) != 0;
    }
    
    /**
     * Extract the isAlpha flag from the entry.
     * 
     * @param entry The entry to extract from
     * @return The isAlpha flag
     */
    public static boolean extractIsAlpha(long entry) {
        return (entry & FLAG_IS_ALPHA_MASK) != 0;
    }
    
    /**
     * Extract the isValid flag from the entry.
     * 
     * @param entry The entry to extract from
     * @return The isValid flag
     */
    public static boolean extractIsValidEntry(long entry) {
        return (entry & FLAG_IS_VALID_ENTRY_MASK) != 0;
    }
}
