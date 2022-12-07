package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Transposition table of HeuristicStatus capable of giving a list of explorable
 * nodes
 * 
 * @author raul
 */
class TranspositionTable {
    private class TTEntry {
        long zobristHash;

        public TTEntry(HeuristicStatus hs) {
            this.zobristHash = hs.getZobristHash();
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + (int) (this.zobristHash ^ (this.zobristHash >>> 32));
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
            final TTEntry other = (TTEntry) obj;
            return this.zobristHash == other.zobristHash;
        }
    }
    
    private HashSet<TTEntry> _table;

    public TranspositionTable() {
        this._table = new HashSet<>();
    }
    
    public void clear() {
        this._table.clear();
    }
    
    /**
     * Get a list of nodes that can be explored from HeuristicStatus with the 
     * internal information from the transposition table.
     * 
     * @param hs The HeuristicStatus
     * @return The list of nodes
     */
    public ArrayList<HeuristicStatus> getNextExplorableNodes(HeuristicStatus hs) {
        ArrayList<HeuristicStatus> hsl = new ArrayList<>();
        for (Point p : hs.getMoves()) {
            HeuristicStatus next = hs.getNextStatus(p);
            if(!seen(next))
                hsl.add(next);
        }
        return hsl;
    }

    /**
     * Check if the heuristicStatus has been already seen
     * 
     * @param hs The HeuristicStatus
     * @return True if it has been registered the exploration of this node, 
     * false otherwise
     */
    public boolean seen(HeuristicStatus hs) {
        TTEntry t = new TTEntry(hs);
        return _table.contains(t);
    }    

    /**
     * Register exploration of node hs
     * 
     * @param hs The HeuristicStatus
     */
    public void register(HeuristicStatus hs) {
        _table.add(new TTEntry(hs));
    }
}
