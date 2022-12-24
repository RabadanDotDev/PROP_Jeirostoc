package edu.upc.epsevg.prop.othello;

import static edu.upc.epsevg.prop.othello.HeadlessGame.currentGameLog;
import edu.upc.epsevg.prop.othello.players.DesdemonaPlayer;
import edu.upc.epsevg.prop.othello.players.jeirostoc.PlayerID;
import edu.upc.epsevg.prop.othello.players.jeirostoc.Status;
import edu.upc.epsevg.prop.othello.players.jeirostoc.TT;
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
        String name;
        
        float stableScoreConfig;
        float discScoresConfig[];
        float neighborScoresConfig[];
        
        float recordedWins;
        float recordedGames;

        public HeuristicSettings(String name, float stableScoreConfig, float[] discScoresConfig, float[] neighborScoresConfig, int defWins, int defGames) {
            this.name = name;            
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
    static int gamesAgainstDesdemona = 30;
    static int gamesAgainstEachOther = 6;
    
    public static void main(String[] args) {
        settings.add(new HeuristicSettings(
            "Ekaitz",
            Status.STABLE_SCORE_DEFAULT, 
            Status.DISK_SCORES_DEFAULT,
            Status.NEIGHBOR_SCORES_DEFAULT,
            0, 0
        ));
        settings.add(new HeuristicSettings(
            "Fionnghuala",
            0, 
            Status.DISK_SCORES_DEFAULT,
            Status.NEIGHBOR_SCORES_DEFAULT,
            0, 0
        ));
        float[] emptyScores = {
            0, 
            0, 0, 
            0, 0, 0, 
            0, 0, 0, 0
        };
        float[] diskScores = {
            5, 
            1, 1, 
            2, 1, 4, 
            2, 1, 3, 1
        };
        float[] neighborScores = {
            4, 
            1.5f, 1, 
            2   , 1, 1, 
            2   , 1, 1, 0
        };
        float[] emptyTable = Status.generateScoringTable(emptyScores);
        float[] diskScoresTable = Status.generateScoringTable(diskScores);
        float[] neighborScoresTable = Status.generateScoringTable(neighborScores);
        settings.add(new HeuristicSettings (
            "Rogelio",
            Status.STABLE_SCORE_DEFAULT,
            diskScoresTable,
            neighborScoresTable,
            0, 0
        ));
        settings.add(new HeuristicSettings (
            "Ataulfo",
            0,
            diskScoresTable,
            neighborScoresTable,
            0, 0
        ));
        settings.add(new HeuristicSettings (
            "Santiago",
            Status.STABLE_SCORE_DEFAULT,
            diskScoresTable,
            emptyTable,
            0, 0
        ));
        settings.add(new HeuristicSettings(
            "Magnolio",
            Status.STABLE_SCORE_DEFAULT,
            diskScoresTable,
            Status.NEIGHBOR_SCORES_DEFAULT,
            0, 0
        ));
        
        testsInterSettings(settings);
        testsDesdemona(settings);
    }
    
    static void testsDesdemona(ArrayList<HeuristicSettings> st) {
        FileWriter actions1 = null;
        FileWriter gameLog = null;
        
        // Tests against desdemona
        for(int i = 0; i < st.size(); i++) {
            HeuristicSettings s = st.get(i);
            
            // Create output files
            try {
                long time = System.currentTimeMillis();
                actions1 = new FileWriter(time + "_" + s.name + "_vs_desdemona_actions.csv");
                gameLog = new FileWriter(time + "_" + s.name + "_vs_desdemona_gameLog.log");
            } catch (IOException ex) {
                Logger.getLogger(HeadlessGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            
            // Do game
            IPlayer player;
            player = new PlayerID(
                    s.stableScoreConfig,
                    s.discScoresConfig, 
                    s.neighborScoresConfig,
                    actions1,
                    s.name,
                    TT.DEF_NUM_ENTRIES
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
                
                HeuristicSettings s1 = st.get(i);
                HeuristicSettings s2 = st.get(j);
                
                // Create output files
                try {
                    long time = System.currentTimeMillis();
                    actions1 = new FileWriter(time + "_" + s1.name + "_vs_" + s2.name + "_actions_" + s1.name + ".csv");
                    actions2 = new FileWriter(time + "_" + s1.name + "_vs_" + s2.name + "_actions_" + s2.name + ".csv");
                    gameLog = new FileWriter(time + "_" + s1.name + "_vs_" + s2.name + "_gameLog.log");
                } catch (IOException ex) {
                    Logger.getLogger(HeadlessGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            
                // Do game
                IPlayer player1 = new PlayerID(
                    s1.stableScoreConfig, 
                    s1.discScoresConfig, 
                    s1.neighborScoresConfig, 
                    actions1,
                    s1.name,
                    TT.DEF_NUM_ENTRIES
                );
                
                IPlayer player2 = new PlayerID(
                    s2.stableScoreConfig, 
                    s2.discScoresConfig, 
                    s2.neighborScoresConfig, 
                    actions2,
                    s2.name,
                    TT.DEF_NUM_ENTRIES
                );

                HeadlessGame.currentGameLog = gameLog;
                HeadlessGame game = new HeadlessGame(player1, player2, timeout, gamesAgainstEachOther);
                game.doGamesTwoSides();
            }
        }
    }
}
