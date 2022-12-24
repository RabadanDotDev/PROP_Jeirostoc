package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.io.BufferedReader;

/**
 * Transposition table wrapper that restricts the registered entries that can be
 * dumped to be at most n movements deep.
 * 
 * @author raul
 * @author josep
 */
public class TTRestricted extends TT {
    /**
     * The maximum number of movements the table accepts entries of.
     */
    final int _maxMoves;
    
    /**
     * The wrapped TT that will accept unlimited movements deep.
     */
    final TT _unrestricted;
    
    /**
     * Constructor with a custom table size and maximum number of movements.
     * 
     * @param numEntries The number of entries in the table.
     * @param maxMoves The maximum number of movements the table will accept
     * entries of.
     */
    public TTRestricted(int numEntries, int maxMoves) {
        super(numEntries);
        _maxMoves = maxMoves;
        _unrestricted = new TT(numEntries);
    }
    
    /**
     * Register an entry to the TranspositionTable. It will be added if there is
     * no collision, if the collision has a different min Zobrist hash or the 
     * depthBelow is lower or equal than the provided.
     * 
     * @param s The status to register.
     * @param selectedHeuristic The selected heuristic for this status.
     * @param selectedMovementBitIndex The selected movement expressed in x*SIZE
     * + y format.
     * @param depthBelow The depth explored below s.
     * @param isExact The flag to indicate if the heuristic is exact (true) or 
     * was calculated with pruning (false).
     * @param isAlpha The flag to indicate if the heuristic is a lower bound 
     * (true) or an upper bound (false).
     */
    @Override
    public void register(Status s, float selectedHeuristic, byte selectedMovementBitIndex, byte depthBelow, boolean isExact, boolean isAlpha) {
        _unrestricted.register(s, selectedHeuristic, selectedMovementBitIndex, depthBelow, isExact, isAlpha);
        if(s.getNumMovements() <= _maxMoves) {
            super.register(s, selectedHeuristic, selectedMovementBitIndex, depthBelow, isExact, isAlpha);
        }
    }
    
    /**
     * Get the number of collisions recorded.
     * 
     * @return The number of collisions recorded.
     */
    @Override
    public long getNumCollisions() {
        return _unrestricted.getNumCollisions();
    }
    
    /**
     * Read the entry in the bitpacked format from the transposition table. If a
     * registered entry with a valid movement or -1 as the movement can't be 
     * found, it returns 0.
     * 
     * @param s The status to register.
     * @return The entry.
     */
    @Override
    public long readEntry(Status s) {
        return _unrestricted.readEntry(s);
    }
    
    /**
     * Reads the stored transposition table from the opening book.
     * 
     * @param br The opening book to read.
     */
    @Override
    public void fill(BufferedReader br) {
        _unrestricted.fill(br);
        System.arraycopy(_unrestricted._table, 0, _table, 0, _unrestricted._table.length);
    }
}
