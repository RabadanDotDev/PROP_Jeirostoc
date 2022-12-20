package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test status class.
 * 
 * @author raul
 * @author josep
 */
public class StatusTest {
    /**
     * Sample movements to have a guided test case.
     */
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

    /**
     * Sample boards to have a guided test case.
     */
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
     * Rotate a board by the variation bv.
     * 
     * @param board The board to rotate.
     * @param bv The variation.
     * @return The new rotated board.
     */
    private int[][] rotateBoard(int[][] board, BoardVariation bv) {
        int[][] rotatedBoard = new int[Status.SIZE][Status.SIZE];
        for (int x = 0; x < Status.SIZE; x++) {
            for (int y = 0; y < Status.SIZE; y++) {
                Point p = BoardVariation.applyTransformation(new Point(x, y), bv);
                rotatedBoard[p.x][p.y] = board[x][y];
            }
        }
        return rotatedBoard;
    }
    
    /**
     * Test that Status gets the same results as GameStatus independently how is
     * it initialized (guided version).
     */
    @Test
    public void testSameResultsGuided() {   
        // Init
        GameStatus reference   = new GameStatus(sampleBoards[0]);
        Status     direct      = new Status(sampleBoards[0], Status.P1_BIT);
        Status     copied      = new Status(reference);
        Status     incremental = new Status(sampleBoards[0], Status.P1_BIT);   
        
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
            reference.movePiece(sampleMovements[i]);
            direct = new Status(sampleBoards[i], i%2==0);
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
            assertEquals(sampleMovements[i].x, incremental.getLastMovement()/Status.SIZE);
            assertEquals(sampleMovements[i].y, incremental.getLastMovement()%Status.SIZE);
        }
    }
    
    /**
     * Test that Status gets the same results as GameStatus independently how is
     * it initialized (random movements version).
     */
    @Test
    public void testSameResultsRandom() {
        Random r = new Random();
        
        for (int game = 0; game < 50000; game++) {
            // Init
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
                Point p = null;
                if(referenceMoves.isEmpty()) {
                    reference.skipTurn();
                    copied = new Status(reference);
                    incremental = new Status(incremental);
                    incremental.skipTurn();
                } else {
                    p = referenceMoves.get(r.nextInt(referenceMoves.size()));
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
                if(p != null) {
                    assertEquals(p.x, incremental.getLastMovement()/Status.SIZE);
                    assertEquals(p.y, incremental.getLastMovement()%Status.SIZE);
                }
            }
        }
    }
    
    /**
     * Assert that all the status in hss have the same heuristic and Zobrist
     * keys.
     * 
     * @param hss The array of Status to check.
     */
    private void assertFullEquals(Status[] hss) {
        for (int i = 0; i < hss.length; i++) {
            for (int j = 0; j < hss.length; j++) {
                if(i != j) {
                    assertEquals(
                        hss[i].getHeuristic(Status.P1_COLOR), 
                        hss[j].getHeuristic(Status.P1_COLOR), 
                        0.001
                    );
                    
                    assertEquals(
                        hss[i].getMinZobristKey(), 
                        hss[j].getMinZobristKey()
                    );
                }
            }
        }
    }
    
    /**
     * Test that boards have the same zobrist hash and heuristic independently 
     * of their variation (guided version).
     */
    @Test
    public void testSimetricBoardGuided() {
        BoardVariation[] bvs = BoardVariation.values();
        
        // Init statues
        Status[] statuses = new Status[bvs.length*2];
        for (int i = 0; i < bvs.length; i++) {
            statuses[i] = new Status(rotateBoard(
                    sampleBoards[0], 
                    bvs[i]
            ), Status.P1_BIT);
            
            statuses[bvs.length+i] = new Status(rotateBoard(
                    sampleBoards[0], 
                    bvs[i]
            ), Status.P1_BIT);
        }
        assertFullEquals(statuses);
        
        // Reproduce movements
        for (int move = 1; move < sampleBoards.length; move++) {
            for (int i = 0; i < 8; i++) {
                // Given board
                statuses[i] = new Status(rotateBoard(
                        sampleBoards[move], 
                        bvs[i]
                ), (move%2 == 0));
                
                // Incremental movement
                statuses[bvs.length+i].movePiece(BoardVariation.applyTransformation(
                        sampleMovements[move],
                        bvs[i]
                ));
            }
            
            assertFullEquals(statuses);
        }
    }
    
    /**
     * Test that boards have the same zobrist hash and heuristic independently 
     * of their variation (random movements version).
     */
    @Test
    public void testSimetricBoardRandom() {
        Random r = new Random();
        
        for (int game = 0; game < 50000; game++) {
            BoardVariation[] bvs = BoardVariation.values();

            // Init statues
            Status[] statuses = new Status[bvs.length];
            for (int i = 0; i < bvs.length; i++) {
                statuses[i] = new Status(rotateBoard(
                        sampleBoards[0], 
                        bvs[i]
                ), Status.P1_BIT);
            }
            assertFullEquals(statuses);

            // Generate random movements
            while (!statuses[0].isTerminal()) {
                ArrayList<Point> nextMoves = new ArrayList<>();
                statuses[0].getNextMoves(nextMoves);
                if(nextMoves.isEmpty()) {
                    for (int i = 0; i < 8; i++)
                        statuses[i].skipTurn();
                } else {
                    Point p = nextMoves.get(r.nextInt(nextMoves.size()));
                    for (int i = 0; i < 8; i++) {
                        statuses[i].movePiece(BoardVariation.applyTransformation(
                                p,
                                bvs[i]
                        ));
                    }
                }
                
                assertFullEquals(statuses);
            }
        }
    }
    
        
    /**
     * Test that boards have the same zobrist hash and heuristic independently 
     * of their variation (random movements version).
     */
    @Test
    public void testHeuristicVal() {
        int[][] sampleBoard = {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  1,  0,  0,  0,  0,  1,  0},
            { 0,  0, -1,  0,  0, -1,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0, -1,  0,  0, -1,  0,  0},
            { 0,  1,  0,  0,  0,  0,  1,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
        };
        
        Status s = new Status(sampleBoard, Status.P1_BIT);
        
        System.out.println(s.getHeuristic(Status.P1_COLOR));
        System.out.println(s.toString(true));
        assertTrue(s.getHeuristic(Status.P1_COLOR) < 0);
    } 
    
    @Test
    public void testStabilityVal() {
        int[][] sampleBoard = {
            { 0, -1, -1, -1,  1,  1,  1,  1},
            { 0, -1, -1, -1,  1,  1,  1,  1},
            { 0, -1, -1,  1, -1,  1,  1,  1},
            {-1, -1, -1,  1,  1,  1,  1,  1},
            {-1, -1, -1,  1,  1,  1,  1,  1},
            {-1, -1, -1,  1,  1,  1,  1,  1},
            {-1, -1, -1, -1,  0,  1,  1,  1},
            {-1,  0, -1,  0,  1,  1, -1, -1},
        };
        
        String expected[] = {
            "\t  0  1  2  3  4  5  6  7 ",
            "\t0 m  @  @  @  Ø  Ø  Ø  Ø ",
            "\t1 m  @  @  @  Ø  Ø  Ø  Ø ",
            "\t2 m  @  @  O  @  Ø  Ø  Ø ",
            "\t3 #  @  @  O  O  Ø  Ø  Ø ",
            "\t4 #  @  @  O  O  Ø  Ø  Ø ",
            "\t5 #  @  @  O  O  O  Ø  Ø ",
            "\t6 #  @  @  @  N  O  O  Ø ",
            "\t7 #  m  @  N  O  O  #  # ",
        };
        
        Status s1 = new Status(new GameStatus(sampleBoard));
        Status s2 = new Status(sampleBoard, Status.P1_BIT);
        
        System.out.println(s1.toString(true));
        System.out.println(s2.toString(true));
            
        String actual1[] = s1.toString(true).split("\n");
        String actual2[] = s2.toString(true).split("\n");
        
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual1[i]);
            assertEquals(expected[i], actual2[i]);
        }
    }
}
