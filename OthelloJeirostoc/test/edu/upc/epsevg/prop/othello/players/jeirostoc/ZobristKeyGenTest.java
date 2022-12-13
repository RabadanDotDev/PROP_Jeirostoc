package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.CellType;
import java.awt.Point;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Zobrist Key generation tests.
 * 
 * @author raul
 * @author josep
 */
public class ZobristKeyGenTest {
    /**
     * Base point to rotate.
     */
    Point pBase = new Point(1, 0);
    
    /**
     * Base point rotated 90 degrees.
     */
    Point pRot90 = new Point(7, 1);
    
    /**
     * Base point rotated 180 degrees.
     */
    Point pRot180 = new Point(6, 7);
    
    /**
     * Base point rotated 270 degrees.
     */
    Point pRot270 = new Point(0, 6);
    
    /**
     * Base point flipped.
     */
    Point pFlip = new Point(1, 7);
    
    /**
     * Base point flipped and rotated 90 degrees.
     */
    Point pFlipRot90  = new Point(0, 1);
    
    /**
     * Base point flipped and rotated 180 degrees.
     */
    Point pFlipRot180 = new Point(6, 0);
    
    /**
     * Base point flipped and rotated 270 degrees.
     */
    Point pFlipRot270 = new Point(7, 6);

    /**
     * Get one of the sample positions rotated by bv.
     * 
     * @param bv The board variation.
     * @return The respective sample position.
     */
    private Point getPos(BoardVariation bv) {
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
    
    /**
     * Assert that the Zobrist key using cellType of the base point and the
     * rotated board by bv are the same.
     * 
     * @param cellType The cell type.
     * @param bv The board variation.
     */
    private void assertEqualZobristKeyWith(CellType cellType, BoardVariation bv) {
        long k1,k2;
        
        // Test 
        k1 = ZobristKeyGen.getZobristValue(
                pBase, cellType, bv
        );
        k2 = ZobristKeyGen.getZobristValue(
                getPos(bv), cellType, BoardVariation.BASE
        );
        
        assertEquals(k1, k2);
    }
    
    /**
     * Assert that the Zobrist key using the two player's celltypes of the base
     * point and the rotated board by bv are the same.
     * 
     * @param bv The board variation.
     */
    private void assertEqualZobristKeyWith(BoardVariation bv) {
        assertEqualZobristKeyWith(CellType.PLAYER1, bv);
        assertEqualZobristKeyWith(CellType.PLAYER2, bv);
    }
    
    /**
     * Test all rotations get the same Zobrist hash with the base points.
     */
    @Test
    public void testGetZobristValue() {
        assertEqualZobristKeyWith(BoardVariation.BASE);
        assertEqualZobristKeyWith(BoardVariation.ROT90);
        assertEqualZobristKeyWith(BoardVariation.ROT180);
        assertEqualZobristKeyWith(BoardVariation.ROT270);
        assertEqualZobristKeyWith(BoardVariation.FLIP);
        assertEqualZobristKeyWith(BoardVariation.FLIPROT90);
        assertEqualZobristKeyWith(BoardVariation.FLIPROT180);
        assertEqualZobristKeyWith(BoardVariation.FLIPROT270);
    }
}
