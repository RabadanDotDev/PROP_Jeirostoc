package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Player that conserves intermediate heuristic calculations and performs a 
 * search depending on how it is initialized.
 * 
 * @author raul
 * @author josep
 */
abstract class PlayerBase implements IAuto, IPlayer {
    private final static String  CSV_FILE_NAME = "move_log.csv";
    private final static boolean LOG_MOVEMENTS = true;
    private static FileWriter fw = null;
    
    /**
     * The search algorithm strategy the instance uses
     */
    protected final SearchAlg _searchAlg;
    
    /**
     * Protected constructor. It takes a SearchAlg strategy class to use.
     * @param searchAlg The SearchAlg to use
     */
    protected PlayerBase(SearchAlg searchAlg) {
        _searchAlg = searchAlg;
        
        if(LOG_MOVEMENTS) { 
            try {
                fw = new FileWriter(
                        System.currentTimeMillis() + "_" +
                        getName() + "_" + 
                        CSV_FILE_NAME
                );
                
                fw.append("movement_count;point;nodes_computed_heuristic;depth_reached;search_type;heuristic;heuristic_ver\n");
            } catch (IOException ex) {
                Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't open csv file", ex);
            } 
        }
    }
    
    @Override
    public Move move(GameStatus gs) {
        // Update status
        Status s = new Status(gs);
        
        // Do search
        _searchAlg.searchON();
        Move m = _searchAlg.nextMove(s);
        
        // Log selected movement
        if(LOG_MOVEMENTS)
            logMovement(m, s.getNumMovements(), gs.getCurrentPlayer());
        
        // Return result
        return m;
    }
    
    private void logMovement(Move m, int moves, CellType p) {
        try {
            fw.append("" +
                    moves                             + ";" +
                    m.getTo()                         + ";" +
                    m.getNumerOfNodesExplored()       + ";" +
                    m.getMaxDepthReached()            + ";" +
                    m.getSearchType()                 + ";" +
                    _searchAlg.getLastBestHeuristic() + ";" +
                    HeuristicStatus.HEURISTIC_VER     + "\n"
            );
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't write to csv file", ex);
        }
    }
}
