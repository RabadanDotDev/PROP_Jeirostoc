package edu.upc.epsevg.prop.othello.players.jeirostoc;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author raul
 */
public class TranspositionTableTest {    
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
    public void testEmptyEntry() {
        
    }
}
