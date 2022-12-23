package edu.upc.epsevg.prop.othello;

import static edu.upc.epsevg.prop.othello.HeadlessGame.currentGameLog;
import edu.upc.epsevg.prop.othello.players.DesdemonaPlayer;
import edu.upc.epsevg.prop.othello.players.jeirostoc.PlayerID;
import edu.upc.epsevg.prop.othello.players.jeirostoc.Status;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class to automatically compare different heuristic configurations
 * 
 * @author raul
 * @author josep
 */
public class HeuristicComparisions {
    static class HeuristicSettings implements Comparable<HeuristicSettings> {
        float stableScoreConfig;
        float discScoresConfig[];
        float neighborScoresConfig[];
        
        float recordedWins;
        float recordedGames;

        public HeuristicSettings(float stableScoreConfig, float[] discScoresConfig, float[] neighborScoresConfig, int defWins, int defGames) {
            this.stableScoreConfig = stableScoreConfig;
            this.discScoresConfig = discScoresConfig;
            this.neighborScoresConfig = neighborScoresConfig;
            this.recordedWins = defWins;
            this.recordedGames = defGames;
        }

        @Override
        public int compareTo(HeuristicSettings t) {            
            return Double.compare(recordedWins/recordedGames,  t.recordedWins/t.recordedGames);
        }
    }
    
    static ArrayList<HeuristicSettings> settings = new ArrayList<>();
    
    static int timeout = 2;
    static int gamesAgainstDesdemona = 2;
    static int gamesAgainstEachOther = 2;
    
    public static void main(String[] args) {
        settings.add(new HeuristicSettings(
            Status.STABLE_SCORE_DEFAULT, 
            Status.DISK_SCORES_DEFAULT,
            Status.NEIGHBOR_SCORES_DEFAULT,
            0, 0
        ));
        settings.add(new HeuristicSettings(
            0, 
            Status.DISK_SCORES_DEFAULT,
            Status.NEIGHBOR_SCORES_DEFAULT,
            0, 0
        ));
        
        testsDesdemona(settings);
        testsInterSettings(settings);
    }
    
    static void testsDesdemona(ArrayList<HeuristicSettings> st) {
        FileWriter actions1 = null;
        FileWriter gameLog = null;
        
        // Tests against desdemona
        for(int i = 0; i < st.size(); i++) {
            // Create output files
            try {
                long time = System.currentTimeMillis();
                actions1 = new FileWriter(time + "_" + i + "_vs_desdemona_actions.csv");
                gameLog = new FileWriter(time + "_" + i + "_vs_desdemona_gameLog.log");
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            
            // Do game
            HeuristicSettings s = st.get(i);
            IPlayer player = new PlayerID(
                    s.stableScoreConfig, 
                    s.discScoresConfig, 
                    s.neighborScoresConfig, 
                    actions1
            );
            IPlayer desdemona = new DesdemonaPlayer(2);//GB
            
            HeadlessGame.currentGameLog = gameLog;
            HeadlessGame game = new HeadlessGame(player, desdemona, timeout, gamesAgainstDesdemona);
            game.doGamesTwoSides();
        }
    }
    
    static void testsInterSettings(ArrayList<HeuristicSettings> st) {
        FileWriter actions1 = null;
        FileWriter actions2 = null;
        FileWriter gameLog = null;
        // Tests against each other
        for(int i = 0; i < st.size(); i++) {
            for (int j = i+1; j < st.size(); j++) {
                if(i == j)
                    continue;
                
                // Create output files
                try {
                    long time = System.currentTimeMillis();
                    actions1 = new FileWriter(time + "_" + i + "_vs_ " + j +"_actions1.csv");
                    actions2 = new FileWriter(time + "_" + i + "_vs_ " + j +"_actions2.csv");
                    gameLog = new FileWriter(time + "_" + i + "_vs_ " + j +"_vs_gameLog.log");
                } catch (IOException ex) {
                    Logger.getLogger(HeadlessGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            
                // Do game
                HeuristicSettings s1 = st.get(i);
                HeuristicSettings s2 = st.get(j);
                IPlayer player1 = new PlayerID(
                    s1.stableScoreConfig, 
                    s1.discScoresConfig, 
                    s1.neighborScoresConfig, 
                    actions1
                );
                
                IPlayer player2 = new PlayerID(
                    s2.stableScoreConfig, 
                    s2.discScoresConfig, 
                    s2.neighborScoresConfig, 
                    actions2
                );

                HeadlessGame.currentGameLog = gameLog;
                HeadlessGame game = new HeadlessGame(player1, player2, timeout, gamesAgainstEachOther);
                game.doGamesTwoSides();
            }
        }
    }
}
