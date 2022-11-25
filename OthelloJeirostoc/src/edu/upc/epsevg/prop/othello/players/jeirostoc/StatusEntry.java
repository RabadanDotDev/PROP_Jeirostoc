package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import java.util.BitSet;
import java.util.Objects;

/**
 * Simplified HeuristicStatus to use it as a key/entry of a set or a map and 
 * retrieve their hashcode
 * 
 * @author raul
 * @author josep
 */
class StatusEntry {
    /**
     * BitSet from a GameStatus specifying the places of the board that are 
     * occupied.
     */
    private final BitSet _boardOccupied;
    
    /**
     * BitSet from a GameStatus specifying the color of places of the board if 
     * they are occupied.
     */
    private final BitSet _boardColor;
    
    /**
     * The current player of the GameStatus.
     */
    private CellType _turn;

    /**
     * Create a new status entry with a reference to board_occupied and 
     * board_color and with the current player as current_player.
     * 
     * @param board_occupied
     * @param board_color
     * @param current_player 
     */
    public StatusEntry(BitSet board_occupied, BitSet board_color, CellType current_player) {
        this._boardOccupied = board_occupied;
        this._boardColor = board_color;
        this._turn = current_player;
    }
    
    /**
     * Clone StatusEntry, becoming unlinked from the original HeuristicStatus if
     * it was.
     * 
     * @return The clone
     */
    @Override
    public StatusEntry clone() {
        return new StatusEntry(
                (BitSet)_boardOccupied.clone(), 
                (BitSet)_boardColor.clone(),
                _turn
        );
    }
    
    /**
     * Swap the current player this entry currently has. This function should be
     * only called from HeuristicStatus.
     */
    public void swapPlayer() {
        _turn = CellType.opposite(_turn);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this._boardOccupied);
        hash = 43 * hash + Objects.hashCode(this._boardColor);
        hash = 43 * hash + Objects.hashCode(this._turn);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StatusEntry other = (StatusEntry) obj;
        if (!Objects.equals(this._boardOccupied, other._boardOccupied)) {
            return false;
        }
        if (!Objects.equals(this._boardColor, other._boardColor)) {
            return false;
        }
        return this._turn == other._turn;
    }

    public BitSet getBoardOccupied() {
        return _boardOccupied;
    }

    public BitSet getBoardColor() {
        return _boardColor;
    }

    public CellType getTurn() {
        return _turn;
    }
}
