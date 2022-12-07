package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Transposition table of HeuristicStatus capable of giving a list of explorable
 * nodes
 * 
 * @author raul
 */
class TranspositionTable {
    private class TTKey {
        long zobristHash;

        public TTKey(HeuristicStatus hs) {
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
            final TTKey other = (TTKey) obj;
            return this.zobristHash == other.zobristHash;
        }
    }
    
    class TTValue {
        double selectedHeuristic;

        public TTValue(double selectedHeuristic) {
            this.selectedHeuristic = selectedHeuristic;
        }
    }
    
    private HashMap<TTKey, TTValue> _table;

    public TranspositionTable() {
        this._table = new HashMap<>();
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
        TTKey t = new TTKey(hs);
        return _table.containsKey(t);
    }
    
    /**
     * Get the last registered value if hs has already been seen
     * 
     * @param hs The HeuristicStatus
     * @return The entry if it exists, null if it does not
     */
    public TTValue get(HeuristicStatus hs) {
        TTKey t = new TTKey(hs);
        return _table.get(t);
    }

    /**
     * Register exploration of node hs
     * 
     * @param hs The HeuristicStatus
     * @param selectedHeuristic The selected heuristic
     */
    public void register(HeuristicStatus hs, double selectedHeuristic) {
        _table.put(new TTKey(hs), new TTValue(selectedHeuristic));
    }
}
