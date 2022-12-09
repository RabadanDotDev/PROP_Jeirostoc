package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author raul
 */
public class StatusTest {
    Point[] sampleMovements = {
        null,
        new Point(2, 4),
        new Point(4, 5),
        new Point(5, 4),
        new Point(2, 5),
        new Point(3, 2),
        new Point(5, 2),
        new Point(4, 2),
        new Point(6, 3),
        new Point(6, 2),
        new Point(4, 1),
        new Point(5, 3),
        new Point(7, 2)
    };

    int[][][] sampleBoards = {
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1, -1,  0,  0,  0},
            { 0,  0,  0, -1,  1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1, -1,  0,  0,  0},
            { 0,  0,  1,  1,  1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1, -1,  0,  0,  0},
            { 0,  0,  1,  1, -1,  0,  0,  0},
            { 0,  0,  0,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1, -1,  0,  0,  0},
            { 0,  0,  1,  1,  1,  1,  0,  0},
            { 0,  0,  0,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1, -1,  0,  0,  0},
            { 0,  0,  1, -1,  1,  1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1,  0,  0,  0,  0},
            { 0,  0,  0,  1,  1,  0,  0,  0},
            { 0,  0,  1, -1,  1,  1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1,  0, -1,  0,  0},
            { 0,  0,  0,  1, -1,  0,  0,  0},
            { 0,  0,  1, -1,  1,  1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1,  1, -1,  0,  0},
            { 0,  0,  0,  1,  1,  0,  0,  0},
            { 0,  0,  1, -1,  1,  1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1,  1, -1,  0,  0},
            { 0,  0,  0,  1,  1,  0, -1,  0},
            { 0,  0,  1, -1,  1, -1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  1,  1,  1,  1,  0},
            { 0,  0,  0,  1,  1,  0, -1,  0},
            { 0,  0,  1, -1,  1, -1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0, -1,  0,  0,  0},
            { 0,  0,  0,  1, -1, -1,  1,  0},
            { 0,  0,  0,  1, -1,  0, -1,  0},
            { 0,  0,  1, -1, -1, -1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0, -1,  0,  0,  0},
            { 0,  0,  0,  1, -1, -1,  1,  0},
            { 0,  0,  0,  1,  1,  1, -1,  0},
            { 0,  0,  1, -1, -1, -1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        },
        {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0, -1,  0,  0,  0},
            { 0,  0,  0,  1, -1, -1, -1, -1},
            { 0,  0,  0,  1,  1,  1, -1,  0},
            { 0,  0,  1, -1, -1, -1,  0,  0},
            { 0,  0, -1,  0, -1,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        }
    };
    
    /**
     * Test of status.
     */
    @Test
    public void testSameResultsGuided() {        
        // Init
        GameStatus reference   = new GameStatus(sampleBoards[0]);
        Status     direct      = new Status(sampleBoards[0], Status.P1_COLOR);
        Status     copied      = new Status(reference);
        Status     incremental = new Status(sampleBoards[0], Status.P1_COLOR);
        
        ArrayList<Point> referenceMoves;
        ArrayList<Point> directMoves = new ArrayList<>();
        ArrayList<Point> copiedMoves = new ArrayList<>();
        ArrayList<Point> incrementalMoves = new ArrayList<>();
        
        // Get next moves
        referenceMoves = reference.getMoves();
        
        directMoves.clear();
        direct.getNextMoves(directMoves);
        
        copiedMoves.clear();
        copied.getNextMoves(copiedMoves);
        
        incrementalMoves.clear();
        incremental.getNextMoves(incrementalMoves);
        
        // Check
        assertEquals(reference.toString(), direct.toString());
        assertEquals(reference.toString(), copied.toString());
        assertEquals(reference.toString(), incremental.toString());
        assertEquals(direct.toString(true), copied.toString(true));
        assertEquals(direct.toString(true), incremental.toString(true));
        assertEquals(reference.getScore(CellType.PLAYER1), direct.getNumDiscs(true));
        assertEquals(reference.getScore(CellType.PLAYER1), copied.getNumDiscs(true));
        assertEquals(reference.getScore(CellType.PLAYER1), incremental.getNumDiscs(true));
        assertEquals(referenceMoves, directMoves);
        assertEquals(referenceMoves, copiedMoves);
        assertEquals(referenceMoves, incrementalMoves);
        
        for (int i = 1; i < sampleMovements.length; i++) {
            // Do movement
            System.out.println(i + ": Moving " + sampleMovements[i]);
            reference.movePiece(sampleMovements[i]);
            direct = new Status(sampleBoards[i], (i%2==0 ? Status.P1_COLOR : Status.P2_COLOR));
            copied = new Status(reference);
            incremental = new Status(incremental);
            incremental.movePiece(sampleMovements[i]);
            
            // Get next moves
            referenceMoves = reference.getMoves();

            directMoves.clear();
            direct.getNextMoves(directMoves);

            copiedMoves.clear();
            copied.getNextMoves(copiedMoves);

            incrementalMoves.clear();
            incremental.getNextMoves(incrementalMoves);
            
            // Check
            assertEquals(reference.toString(), direct.toString());
            assertEquals(reference.toString(), copied.toString());
            assertEquals(reference.toString(), incremental.toString());
            assertEquals(direct.toString(true), copied.toString(true));
            assertEquals(direct.toString(true), incremental.toString(true));
            assertEquals(reference.getScore(CellType.PLAYER1), direct.getNumDiscs(true));
            assertEquals(reference.getScore(CellType.PLAYER1), copied.getNumDiscs(true));
            assertEquals(reference.getScore(CellType.PLAYER1), incremental.getNumDiscs(true));
            assertEquals(referenceMoves, directMoves);
            assertEquals(referenceMoves, copiedMoves);
            assertEquals(referenceMoves, incrementalMoves);
        }
    }
    
    /**
     * Test of status.
     */
    @Test
    public void testSameResultsRandom() {
        for (int game = 0; game < 10000; game++) {
            System.out.println("Game " + game);
            // Init
            Random r = new Random();
            GameStatus reference   = new GameStatus();
            Status     copied      = new Status(reference);
            Status     incremental = new Status();
            
            ArrayList<Point> referenceMoves;
            ArrayList<Point> copiedMoves = new ArrayList<>();
            ArrayList<Point> incrementalMoves = new ArrayList<>();

            // Get next moves
            referenceMoves = reference.getMoves();
            
            copiedMoves.clear();
            copied.getNextMoves(copiedMoves);

            incrementalMoves.clear();
            incremental.getNextMoves(incrementalMoves);
            
            // Check
            assertEquals(reference.toString(), copied.toString());
            assertEquals(reference.toString(), incremental.toString());
            assertEquals(copied.toString(true), incremental.toString(true));
            assertEquals(reference.getScore(CellType.PLAYER1), copied.getNumDiscs(true));
            assertEquals(reference.getScore(CellType.PLAYER1), incremental.getNumDiscs(true));
            assertEquals(referenceMoves, copiedMoves);
            assertEquals(referenceMoves, incrementalMoves);
            
            for (int i = 1; i < 64; i++) {
                // Do movement
                if(referenceMoves.isEmpty()) {
                    reference.skipTurn();
                    copied = new Status(reference);
                    incremental = new Status(incremental);
                    incremental.skipTurn();
                } else {
                    Point p = referenceMoves.get(r.nextInt(referenceMoves.size()));
                    reference.movePiece(p);
                    copied = new Status(reference);
                    incremental = new Status(incremental);
                    incremental.movePiece(p);
                }

                // Get next moves
                referenceMoves = reference.getMoves();

                copiedMoves.clear();
                copied.getNextMoves(copiedMoves);

                incrementalMoves.clear();
                incremental.getNextMoves(incrementalMoves);

                // Check
                assertEquals(reference.toString(), copied.toString());
                assertEquals(reference.toString(), incremental.toString());
                assertEquals(copied.toString(true), incremental.toString(true));
                assertEquals(reference.getScore(CellType.PLAYER1), copied.getNumDiscs(true));
                assertEquals(reference.getScore(CellType.PLAYER1), incremental.getNumDiscs(true));
                assertEquals(referenceMoves, copiedMoves);
                assertEquals(referenceMoves, incrementalMoves);
            }
        }
    }
}
