package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.awt.Point;

/**
 * Enumeration to indicate the 8 possible variations of the board.
 * 
 * @author raul
 * @author josep
 */
public enum BoardVariation {
    /**
     * Board without rotation nor flipping.
     */
    BASE(0),
    
    /**
     * Board rotated 90 degrees.
     */
    ROT90(1),
    
    /**
     * Board rotated 180 degrees.
     */
    ROT180(2),
    
    /**
     * Board rotated 270 degrees.
     */
    ROT270(3),
    
    /**
     * Board flipped.
     */
    FLIP(4),
    
    /**
     * Board flipped and rotated 90 degrees.
     */
    FLIPROT90(5),
    
    /**
     * Board flipped and rotated 180 degrees.
     */
    FLIPROT180(6),
    
    /**
     * Board flipped and rotated 270 degrees.
     */
    FLIPROT270(7);

    /**
     * Number of total variations.
     */
    public final static int NUMBER = BoardVariation.values().length;
    
    /**
     * Index of the variation.
     */
    public final int v;

    /**
     * Default constructor.
     * 
     * @param v Index of the variation.
     */
    private BoardVariation(int v) {
        this.v = v;
    }

    /**
     * Get the respective enum from an index.
     * 
     * @param v The index.
     * @return The respective enum from an index.
     */
    public static BoardVariation valueOf(int v) {
        return switch (v) {
            case 0  -> BASE;
            case 1  -> ROT90;
            case 2  -> ROT180;
            case 3  -> ROT270;
            case 4  -> FLIP;
            case 5  -> FLIPROT90;
            case 6  -> FLIPROT180;
            case 7  -> FLIPROT270;
            default -> BASE;
        };
    }
    
    /**
     * Apply transformation at point p by the variation bv. If it is an invalid 
     * point or variation, it returns null.
     * 
     * @param p The point to transform.
     * @param bv The variation to transform with.
     * @return The point with the transformation applied.
     */
    public static Point applyTransformation(Point p, BoardVariation bv) {
        if(p.x < 0 || Status.SIZE <= p.x || p.y < 0 || Status.SIZE <= p.y)
            return null;
        
        return switch (bv) {
            case BASE       -> new Point(p);
            case ROT90      -> new Point(Status.SIZE-p.y-1, p.x);
            case ROT180     -> new Point(Status.SIZE-p.x-1, Status.SIZE-p.y-1);
            case ROT270     -> new Point(p.y, Status.SIZE-p.x-1);
            case FLIP       -> new Point(p.x, Status.SIZE-p.y-1);
            case FLIPROT90  -> new Point(p.y, p.x);
            case FLIPROT180 -> new Point(Status.SIZE-p.x-1, p.y);
            case FLIPROT270 -> new Point(Status.SIZE-p.y-1, Status.SIZE-p.x-1);
            default         -> null;
        };
    }
    
    /**
     * Apply transformation at point indexed by bitsetIndex with the form x*SIZE
     * +y by the variation BoardVariation.valueof(variationIndex). If the
     * bitsetIndex is invalid, it will return -1
     * 
     * @param bitsetIndex The bitsetIndex index to transform.
     * @param variationIndex The index of the variation to transform with.
     * @return The bitsetIndex with the transformation applied.
     */
    public static byte applyTransformation(byte bitsetIndex, int variationIndex) {
        if (bitsetIndex < 0 || Status.SIZE*Status.SIZE <= bitsetIndex)
            return -1;
        
        int x = bitsetIndex/Status.SIZE;
        int y = bitsetIndex%Status.SIZE;
        
        int x2, y2;
        
        switch (variationIndex) {
            case 0  -> {x2 = x;               y2 = y;              } // BASE
            case 1  -> {x2 = Status.SIZE-y-1; y2 = x;              } // ROT90
            case 2  -> {x2 = Status.SIZE-x-1; y2 = Status.SIZE-y-1;} // ROT180
            case 3  -> {x2 = y;               y2 = Status.SIZE-x-1;} // ROT270
            case 4  -> {x2 = x;               y2 = Status.SIZE-y-1;} // FLIP
            case 5  -> {x2 = y;               y2 = x;              } // FLIPROT90
            case 6  -> {x2 = Status.SIZE-x-1; y2 = y;              } // FLIPROT180
            case 7  -> {x2 = Status.SIZE-y-1; y2 = Status.SIZE-x-1;} // FLIOROT270
            default -> {x2 = x;               y2 = y;              }
        }
        
        return (byte)(x2*Status.SIZE+y2);
    }
    
    /**
     * Apply the inverse transformation at point indexed by bitsetIndex with the
     * form x*SIZE+y by the variation BoardVariation.valueof(variationIndex). If the
     * bitsetIndex is invalid, it will return -1
     * 
     * @param bitsetIndex The bitsetIndex index to transform
     * @param variationIndex The index of the variation to transform with
     * @return The bitsetIndex with the inverse transformation applied
     */
    public static byte applyInverseTransformation(byte bitsetIndex, int variationIndex) {
        if (bitsetIndex < 0 || Status.SIZE*Status.SIZE <= bitsetIndex)
            return -1;
        
        int x = bitsetIndex/Status.SIZE;
        int y = bitsetIndex%Status.SIZE;
        
        int invX, invY;
        switch (variationIndex) {
            case 0  -> {invX = x;               invY = y;              } // BASE
            case 1  -> {invX = y;               invY = Status.SIZE-x-1;} // ROT90
            case 2  -> {invX = Status.SIZE-x-1; invY = Status.SIZE-y-1;} // ROT180
            case 3  -> {invX = Status.SIZE-y-1; invY = x;              } // ROT270
            case 4  -> {invX = x;               invY = Status.SIZE-y-1;} // FLIP
            case 5  -> {invX = y;               invY = x;              } // FLIPROT90
            case 6  -> {invX = Status.SIZE-x-1; invY = y;              } // FLIPROT180
            case 7  -> {invX = Status.SIZE-y-1; invY = Status.SIZE-x-1;} // FLIPROT270
            default -> {invX = x;               invY = y;              }
        }
        
        return (byte)(invX*Status.SIZE+invY);   
    }
}