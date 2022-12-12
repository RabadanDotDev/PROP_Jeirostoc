package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author raul
 */
public class TranspositionTableTest {
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
    
    private final int SIZE = 8;
    
    private int[][] rotateBoard(int[][] board, BoardVariation bv) {
        int[][] rotatedBoard = new int[SIZE][SIZE];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Point p = BoardVariation.applyTransformation(new Point(x, y), bv);
                rotatedBoard[p.x][p.y] = board[x][y];
            }
        }
        return rotatedBoard;
    }
    
    private TT tt;
    
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
    }
    
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
        System.out.println(String.format("%64s", Long.toBinaryString(entry)).replace(' ', '0'));
    }
    
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
    
    @Test
    public void testTTCorrectMovementEntries() {
        Random r = new Random();
        tt = new TT();
        
        for (int game = 0; game < 5000; game++) {
            System.out.println("Test TT correct movement entries " + game);
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
                                (byte)(rotatedP.x*SIZE+rotatedP.y), 
                                (byte)0, true, true
                        );
                        
                        
                        // Check
                        long entry = tt.readEntry(statuses[i]);
                        byte bitIndex = TT.extractSelectedMovement(statuses[i], entry);
                        Point extractedP = new Point(bitIndex/SIZE, bitIndex%SIZE);
                        assertEquals(rotatedP, extractedP);
                        assertEquals(bitIndex, rotatedP.x*SIZE+rotatedP.y);
                        assertTrue(statuses[i].canMovePiece(extractedP.x, extractedP.y));
                        
                        
                        // Move
                        statuses[i].movePiece(rotatedP);
                    }
                }
            }
        }
    }
}
