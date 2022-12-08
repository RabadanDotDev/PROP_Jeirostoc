package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Transposition table of HeuristicStatus capable of giving a list of explorable
 * nodes
 * 
 * @author raul
 */
class TranspositionTable {
    static class TTKey {
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
        int distanceToBottom;

        public TTValue(double selectedHeuristic, int distanceToBottom) {
            this.selectedHeuristic = selectedHeuristic;
            this.distanceToBottom = distanceToBottom;
        }
    }
    
    private class SortMin implements Comparator<HeuristicStatus> {
        @Override
        public int compare(HeuristicStatus t1, HeuristicStatus t2) {
            return Double.compare(
                    getLastRegisteredHeuristic(t1), 
                    getLastRegisteredHeuristic(t2)
            );
        }
    }
    
    private class SortMax implements Comparator<HeuristicStatus> {
        @Override
        public int compare(HeuristicStatus t1, HeuristicStatus t2) {
            return Double.compare(
                    getLastRegisteredHeuristic(t2), 
                    getLastRegisteredHeuristic(t1)
            );
        }
    }
    
    private final HashMap<TTKey, TTValue>[] _table;
    private final SortMin _sMin = new SortMin();
    private final SortMax _sMax = new SortMax();
    
    public TranspositionTable() {
        this._table = new HashMap[8*8-4+1];
        for (int i = 0; i < _table.length; i++) {
            _table[i] = new HashMap<>();
        }
    }
    
    public void clear() {
    }
    
    /**
     * Get a list of nodes that can be explored from HeuristicStatus with the 
     * internal information from the transposition table.
     * 
     * @param hs The HeuristicStatus
     * @return The list of nodes
     */
    public ArrayList<HeuristicStatus> getNextExplorableNodes(HeuristicStatus hs, boolean isMax) {
        ArrayList<HeuristicStatus> hsl = new ArrayList<>();
        for (Point p : hs.getMoves()) {
            HeuristicStatus next = hs.getNextStatus(p);
            hsl.add(next);
        }
        hsl.sort(isMax ? _sMax : _sMin);
        return hsl;
    }

    /**
     * Check if the heuristicStatus has been already seen with at least the 
     * given distance to bottom
     * 
     * @param hs The HeuristicStatus
     * @param minDistanceToBottom The minimum distance to bottom
     * @return True if it has been registered the exploration of this node, 
     * false otherwise
     */
    public boolean seen(HeuristicStatus hs, int minDistanceToBottom) {
        return get(hs, minDistanceToBottom) != null;
    }
    
    /**
     * Get the last registered value if hs has already been seen with at least
     * the given distance to bottom
     * 
     * @param hs The HeuristicStatus
     * @param minDistanceToBottom The minimum distance to bottom
     * @return The entry if it exists, null if it does not
     */
    public TTValue get(HeuristicStatus hs, int minDistanceToBottom) {
        TTValue v = _table[hs.getMovementCount()].get(hs.getTTKey());
        if(v != null && minDistanceToBottom <= v.distanceToBottom ) 
            return v;
        else                                           
            return null;
    }
    
    /**
     * Get the last registered heuristic for hs or 0 in case it does not exist.
     * 
     * @param hs The HeuristicStatus
     * @return The heuristic or 0 if hs was not registered
     */
    private double getLastRegisteredHeuristic(HeuristicStatus hs) {
        TTValue v = _table[hs.getMovementCount()].get(hs.getTTKey());
        if(v == null) 
            return 0;
        else                                           
            return v.selectedHeuristic;
    }

    /**
     * Register exploration of node hs if distance to bottom is greater or equal 
     * to what we had before
     * 
     * @param hs The HeuristicStatus
     * @param selectedHeuristic The selected heuristic
     */
    public void register(HeuristicStatus hs, double selectedHeuristic, int distanceToBottom) {
        TTValue v = _table[hs.getMovementCount()].get(hs.getTTKey());
        if(v == null || v.distanceToBottom <= distanceToBottom) 
            _table[hs.getMovementCount()].put(hs.getTTKey(), new TTValue(selectedHeuristic, distanceToBottom));
    }
}
