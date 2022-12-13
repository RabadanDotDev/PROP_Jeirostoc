package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Transposition table tests.
 * 
 * @author raul
 * @author josep
 */
public class TTTest {
    /**
     * The starting board to make tests with.
     */
    int[][] sampleBoard = {
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  1, -1,  0,  0,  0},
        { 0,  0,  0, -1,  1,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0}
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
     * Test to check suposition of the bitpacking for the TT.
     */
    @Test
    public void testSupositionsBitPacking() {
        float heuristic        = 7;
        byte  bitIndexMovement = 0b1010010;
        byte  depthBelow       = 0b1111111;
        byte  flags            = 0b1001001;
        
        long field1 = Float.floatToRawIntBits(heuristic);
        long field2 = bitIndexMovement;
        long field3 = depthBelow;
        long field4 = flags;
        
        long entry = 
                field1       | // 32 bits
                field2 << 32 | // 8  bits
                field3 << 40 | // 8  bits
                field4 << 48;  // 8  bits
        
        long extractedField1 = entry       & 0xffffffffL;
        long extractedField2 = entry >> 32 & 0x000000ff;
        long extractedField3 = entry >> 40 & 0x000000ff;
        long extractedField4 = entry >> 48 & 0x000000ff;
        
        assertEquals(field1, extractedField1);
        assertEquals(field2, extractedField2);
        assertEquals(field3, extractedField3);
        assertEquals(field4, extractedField4);
        
        float extractedHeuristic        = Float.intBitsToFloat((int)field1);
        byte  extractedBitIndexMovement = (byte)field2;
        byte  extractedDepthBelow       = (byte)field3;
        byte  extractedFlags            = (byte)field4;
        
        
        assertEquals(heuristic, extractedHeuristic, 0.0);
        assertEquals(bitIndexMovement, extractedBitIndexMovement, 0.0);
        assertEquals(depthBelow, extractedDepthBelow, 0.0);
        assertEquals(flags, extractedFlags, 0.0);
        
        assertTrue(0 < Long.remainderUnsigned(-1L, 100));
        assertTrue(0 < Long.remainderUnsigned(-184496264L, 134204621));
        assertTrue(-1 == (byte)-1);
    }
    
    /**
     * Check that we can retrieve the same results of an entry in the TT. 
     * Version 1.
     */
    @Test
    public void testToEntryAndExtract1() {
        float selectedHeuristic = 1.051f;
        byte selectedMovementBitIndex = 53;
        byte depthBelow = 7;
        boolean isExact = true;
        boolean isAlpha = true;
        
        long entry = TT.toEntry(selectedHeuristic, selectedMovementBitIndex, depthBelow, isExact, isAlpha);
        
        assertEquals(TT.extractSelectedHeuristic(entry), selectedHeuristic, 0.0f);
        assertEquals(TT.extractSelectedMovement(entry),  selectedMovementBitIndex);
        assertEquals(TT.extractDepthBelow(entry),        depthBelow);
        assertEquals(TT.extractIsExact(entry),           isExact);
        assertEquals(TT.extractIsAlpha(entry),           isAlpha);
        assertEquals(TT.extractIsValidEntry(entry),      true);
    }
    
    /**
     * Check that we can retrieve the same results of an entry in the TT. 
     * Version 2.
     */
    @Test
    public void testToEntryAndExtract2() {
        float selectedHeuristic = Float.POSITIVE_INFINITY;
        byte selectedMovementBitIndex = 3;
        byte depthBelow = 10;
        boolean isExact = false;
        boolean isAlpha = false;
        
        long entry = TT.toEntry(selectedHeuristic, selectedMovementBitIndex, depthBelow, isExact, isAlpha);
        
        assertEquals(TT.extractSelectedHeuristic(entry), selectedHeuristic, 0.0f);
        assertEquals(TT.extractSelectedMovement(entry),  selectedMovementBitIndex);
        assertEquals(TT.extractDepthBelow(entry),        depthBelow);
        assertEquals(TT.extractIsExact(entry),           isExact);
        assertEquals(TT.extractIsAlpha(entry),           isAlpha);
        assertEquals(TT.extractIsValidEntry(entry),      true);
    }
    
    /**
     * Test in random games that the entries in the TT are consistent.
     */
    @Test
    public void testTTCorrectMovementEntries() {
        Random r = new Random();
        TT tt = new TT();
        
        for (int game = 0; game < 50000; game++) {
            BoardVariation[] bvs = BoardVariation.values();

            // Init statues
            Status[] statuses = new Status[bvs.length];
            for (int i = 0; i < bvs.length; i++) {
                statuses[i] = new Status(rotateBoard(
                        sampleBoard, 
                        bvs[i]
                ), Status.P1_BIT);
            }

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
                        Point rotatedP = BoardVariation.applyTransformation(
                                p,
                                bvs[i]
                        );
                        
                        // Write movement to TT
                        tt.register(
                                statuses[i], 
                                (byte)0,
                                (byte)(rotatedP.x*Status.SIZE+rotatedP.y), 
                                (byte)0, true, true
                        );
                        
                        // Check
                        long entry = tt.readEntry(statuses[i]);
                        byte bitIndex = TT.extractSelectedMovement(statuses[i], entry);
                        Point extractedP = new Point(bitIndex/Status.SIZE, bitIndex%Status.SIZE);
                        assertEquals(rotatedP, extractedP);
                        assertEquals(bitIndex, rotatedP.x*Status.SIZE+rotatedP.y);
                        assertTrue(statuses[i].canMovePiece(extractedP.x, extractedP.y));
                        
                        // Move
                        statuses[i].movePiece(rotatedP);
                    }
                }
            }
        }
    }
    
    /**
     * Test prevention of extraction of invalid movements and correction of 
     * insertion of invalid movements.
     */
    @Test
    public void testTTinvalidMove() {
        Status s = new Status();
        TT tt = new TT();
        
        for (byte move = Byte.MIN_VALUE; move < Byte.MAX_VALUE; move++) {
            tt.register(
                    s, 
                    0, 
                    (byte)move,
                    (byte)1, 
                    true, 
                    true
            );
            
            long entry = tt.readEntry(s);
            if (-1 < move && move < Status.SIZE*Status.SIZE) {
                if (s.canMovePiece(move/Status.SIZE, move%Status.SIZE)) {
                    assertEquals(move, TT.extractSelectedMovement(s, entry));
                } else {
                    assertEquals(0, entry);
                }
            } else {
                assertEquals(-1, TT.extractSelectedMovement(s, entry));
            }
        }
    }
}
