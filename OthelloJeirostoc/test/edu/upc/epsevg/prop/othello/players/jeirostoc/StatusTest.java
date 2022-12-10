package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    
    private final int SIZE = 8;
    
    private Point rotateMovement(Point p, ZobristKeyGen.BoardVariation bv) {
        return switch (bv) {
            case BASE       -> new Point(p);
            case ROT90      -> new Point(p.y, SIZE-p.x-1);
            case ROT180     -> new Point(SIZE-p.x-1, SIZE-p.y-1);
            case ROT270     -> new Point(SIZE-p.y-1, p.x);
            case FLIP       -> new Point(p.x, SIZE-p.y-1);
            case FLIPROT90  -> new Point(p.y, p.x);
            case FLIPROT180 -> new Point(SIZE-p.x-1, p.y);
            case FLIPROT270 -> new Point(SIZE-p.y-1, SIZE-p.x-1);
            default         -> null;
        };
    }
    
    private int[][] rotateBoard(int[][] board, ZobristKeyGen.BoardVariation bv) {
        int[][] rotatedBoard = new int[SIZE][SIZE];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Point p = rotateMovement(new Point(x, y), bv);
                rotatedBoard[p.x][p.y] = board[x][y];
            }
        }
        return rotatedBoard;
    }
    
    /**
     * Test of status.
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
        System.out.println(reference);
        System.out.println(direct);
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
        }
    }
    
    /**
     * Test of status.
     */
    @Test
    public void testSameResultsRandom() {
        Random r = new Random();
        
        for (int game = 0; game < 5000; game++) {
            System.out.println("Same results game " + game);
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
    
    @Test
    public void testZobristHashGuided() {
        ZobristKeyGen.BoardVariation[] bvs = ZobristKeyGen.BoardVariation.values();
        
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
            System.out.println("Move " + move);
            for (int i = 0; i < 8; i++) {
                // Given board
                statuses[i] = new Status(rotateBoard(
                        sampleBoards[move], 
                        bvs[i]
                ), (move%2 == 0));
                
                // Incremental movement
                statuses[bvs.length+i].movePiece(rotateMovement(
                        sampleMovements[move],
                        bvs[i]
                ));
            }
            
            assertFullEquals(statuses);
        }
    }
    
    @Test
    public void testZobristHashRandom() {
        Random r = new Random();
        
        for (int game = 0; game < 5000; game++) {
            System.out.println("Same zobrist hash and heuristic game " + game);
            ZobristKeyGen.BoardVariation[] bvs = ZobristKeyGen.BoardVariation.values();

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
                        statuses[i].movePiece(rotateMovement(
                                p,
                                bvs[i]
                        ));
                    }
                }
                
                assertFullEquals(statuses);
            }
        }
    }
    
    
    @Test
    public void test() {
        Status s = new Status(new GameStatus(sampleBoards[5]));
        System.out.println(s);
        System.out.println(new GameStatus(sampleBoards[5]));
    }
}
