package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import java.awt.Point;
import java.util.BitSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author raul
 * @author raul
 */
public class ZobristKeyGenTest {
    Point pBase       = new Point(1, 0);
    Point pRot90      = new Point(7, 1);
    Point pRot180     = new Point(6, 7);
    Point pRot270     = new Point(0, 6);
    Point pFlip       = new Point(1, 7);
    Point pFlipRot90  = new Point(0, 1);
    Point pFlipRot180 = new Point(6, 0);
    Point pFlipRot270 = new Point(7, 6);

    private Point getPos(ZobristKeyGen.BoardVariation bv) {
        return switch (bv) {
            case BASE       -> pBase;
            case ROT90      -> pRot90;
            case ROT180     -> pRot180;
            case ROT270     -> pRot270;
            case FLIP       -> pFlip;
            case FLIPROT90  -> pFlipRot90;
            case FLIPROT180 -> pFlipRot180;
            case FLIPROT270 -> pFlipRot270;
            default         -> pBase;
        };
    }
    
    private void assertEqualZobristKeyWith(CellType cellType, ZobristKeyGen.BoardVariation bv) {
        long k1,k2;
        
        // Test 
        k1 = ZobristKeyGen.getZobristValue(
                pBase, cellType, bv
        );
        k2 = ZobristKeyGen.getZobristValue(
                getPos(bv), cellType, ZobristKeyGen.BoardVariation.BASE
        );
        
        assertEquals(k1, k2);
    }
    
    private void assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation bv) {
        assertEqualZobristKeyWith(CellType.PLAYER1, bv);
        assertEqualZobristKeyWith(CellType.PLAYER2, bv);
    }
    
    @Test
    public void testGetZobristValue() {
        assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation.BASE);
        assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation.ROT90);
        assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation.ROT180);
        assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation.ROT270);
        assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation.FLIP);
        assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation.FLIPROT90);
        assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation.FLIPROT180);
        assertEqualZobristKeyWith(ZobristKeyGen.BoardVariation.FLIPROT270);
    }
}
