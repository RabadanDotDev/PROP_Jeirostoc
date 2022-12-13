package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
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
    private final static String  CSV_BASE_FILE_NAME = "move_log.csv";
    private final static boolean DO_LOG = true;
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
        
        if(DO_LOG) { 
            try {
                fw = new FileWriter(
                        System.currentTimeMillis() + "_" +
                        getName() + "_" + 
                        CSV_BASE_FILE_NAME
                );
                
                fw.append(_searchAlg.getLogLineHeader());
                fw.append("heuristic_ver\n");
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
        if(DO_LOG)
            logLastSearch();
        
        // Return result
        return m;
    }
    
    private void logLastSearch() {
        try {
            fw.append(_searchAlg.getLogLineLastSearch());
            fw.append(Float.toString(Status.HEURISTIC_VER));
            fw.append('\n');
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(PlayerBase.class.getName()).log(Level.SEVERE, "Couldn't write to csv file", ex);
        }
    }
}
