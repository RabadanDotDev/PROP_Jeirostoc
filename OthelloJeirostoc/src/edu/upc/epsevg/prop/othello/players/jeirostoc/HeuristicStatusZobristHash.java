package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Helper class to maintain a canonic Zobrist Hash.
 * 
 * @author raul
 * @author josep
 */
class HeuristicStatusZobristHash {
    /**
     * Last recorded places of the board that were occupied.
     */
    private BitSet _lastBoardOccupied;
    
    /**
     * Last colors of the board if they were occupied.
     */
    private BitSet _lastBoardColor;
    
    /**
     * The last recorded player.
     */
    private CellType _lastTurn;
    
    /**
     * The Zobrist hash of each variation of the board.
     */
    private long[] _zobristHashes;
    
    /**
     * The smallest Zobrist hash.
     */
    private long _minZobristHash;

    /**
     * Constructor that makes a COPY of the status of the game
     * 
     * @param board_occupied The cells that are occupied
     * @param board_color The color of the cells that are occupied
     * @param current_player The current player
     */
    public HeuristicStatusZobristHash(BitSet board_occupied, BitSet board_color, CellType current_player) {
        // Copy status
        this._lastBoardOccupied = (BitSet) board_occupied.clone();
        this._lastBoardColor    = (BitSet) board_color.clone();
        this._lastTurn          = current_player;
        
        regenZobristHashes();
    }

    /**
     * Copy constructor.
     */
    public HeuristicStatusZobristHash(HeuristicStatusZobristHash other) {
        this._lastBoardOccupied = (BitSet) other._lastBoardOccupied.clone();
        this._lastBoardColor    = (BitSet) other._lastBoardColor.clone();
        this._lastTurn          = other._lastTurn;
        this._zobristHashes     = other._zobristHashes.clone();
    }
    
    /**
     * Swap the current player this entry currently has. This function should be
     * only called from HeuristicStatus.
     */
    public void swapPlayer() {
        // Swap player
        xorZobristHashesWithPlayer(_lastTurn);
        _lastTurn = CellType.opposite(_lastTurn);
        xorZobristHashesWithPlayer(_lastTurn);
        
        // Update smallest hash
        updateMin();
    }
    
    /**
     * Update the Zobrist keys with the new game state. This function should be
     * only called from HeuristicStatus and only when the difference of 
     * movements made is at most one.
     * 
     * @param newBoardOccupied
     * @param newBoardColor
     * @param newCurrentPlayer 
     */
    public void updateZobristHashes(BitSet newBoardOccupied, BitSet newBoardColor, CellType newCurrentPlayer) {
        // Update zobrist hash for new occupied points
        BitSet newOccupiedPoints = _lastBoardOccupied; // We are just copying the ref, NOT all the contents
        newOccupiedPoints.xor(newBoardOccupied);
        
        for (int i = newOccupiedPoints.nextSetBit(0); i >= 0; i = newOccupiedPoints.nextSetBit(i+1)) {            
            xorZobristHashesWith(i, newBoardColor.get(i) ? 1 : 0); // XOR IN
        }
        
        // Update zobrist hash for the flipped points
        BitSet colorFlippedPoints = _lastBoardColor; // We are just copying the ref, NOT all the contents
        colorFlippedPoints.xor(newBoardColor);
        colorFlippedPoints.andNot(newOccupiedPoints);
        
        for (int i = colorFlippedPoints.nextSetBit(0); i >= 0; i = colorFlippedPoints.nextSetBit(i+1)) {
            xorZobristHashesWith(i, newBoardColor.get(i) ? 0 : 1); // XOR OUT
            xorZobristHashesWith(i, newBoardColor.get(i) ? 1 : 0); // XOR IN
        }
        
        // Update player
        xorZobristHashesWithPlayer(_lastTurn);        // XOR OUT
        xorZobristHashesWithPlayer(newCurrentPlayer); // XOR IN
        
        // Update smallest hash
        updateMin();
        
        // Store a copy of the new game status
        this._lastBoardOccupied = (BitSet) newBoardOccupied.clone();
        this._lastBoardColor    = (BitSet) newBoardColor.clone();
        this._lastTurn          = newCurrentPlayer;
    }
    
    /**
     * Get the Zobrist hashcode
     * @return The Zobrist hashcode
     */
    public long zobristHashCode() {
        return _minZobristHash;
    }
    
    private void updateMin() {
        _minZobristHash = _zobristHashes[0];
        for (int i = 1; i < _zobristHashes.length; i++) {
            _minZobristHash = Math.min(_minZobristHash, _zobristHashes[i]);
        }
    }
    
    /**
     * Update the Zobrist key set by doing XORs with the values corresponding to
     * the position indexed by bitsetIndex, the cell state and their respective
     * rotations. The _minZobristHash will become invalid.
     * 
     * @param bitsetIndex The position indexed by BitsetIndex
     * @param cellState The state of the cell.
     */
    private void xorZobristHashesWith(int bitsetIndex, int cellState) {
        ZobristKeyGen.BoardVariation[] variations = ZobristKeyGen.BoardVariation.values();
        for (int i = 0; i < variations.length; i++) {
            long zobristValue = ZobristKeyGen.getZobristValue(bitsetIndex, cellState, variations[i]);
            _zobristHashes[i] ^= zobristValue;
        }
    }
    
    /**
     * Update the Zobrist key set by doing XORs with the value corresponding to
     * the player. The _minZobristHash will become invalid.
     * 
     * @param bitsetIndex The position indexed by BitsetIndex
     * @param cellState The state of the cell.
     */
    private void xorZobristHashesWithPlayer(CellType player) {
        if(player.equals(CellType.PLAYER2)) {
            long zobristValue = ZobristKeyGen.getZobristValueP2();
            for (int i = 0; i < ZobristKeyGen.BoardVariation.values().length; i++) {
                _zobristHashes[i] ^= zobristValue;
            }
        }
    }
    
    /**
     * Regenerate the Zobrist keys.
     */
    private void regenZobristHashes() {
        // Init hashes to 0
        this._zobristHashes = new long[8];
        
        // Board positions
        for (int i = _lastBoardOccupied.nextSetBit(0); i >= 0; i = _lastBoardOccupied.nextSetBit(i+1)) {
            xorZobristHashesWith(i, _lastBoardColor.get(i) ? 1 : 0);
        }
        
        // Current player
        xorZobristHashesWithPlayer(_lastTurn);
        
        // Update smallest hash
        updateMin();
    }
}
