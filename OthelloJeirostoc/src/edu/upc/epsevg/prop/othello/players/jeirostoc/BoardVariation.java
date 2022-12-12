package edu.upc.epsevg.prop.othello.players.jeirostoc;

import java.awt.Point;


/**
 * Enumeration to indicate the 8 possible variations of the board.
 */
public enum BoardVariation{        
    BASE(0),
    ROT90(1),
    ROT180(2),
    ROT270(3),
    FLIP(4),
    FLIPROT90(5),
    FLIPROT180(6),
    FLIPROT270(7);

    public final int v;
    public final static int NUM_VARIATIONS = BoardVariation.values().length;

    private BoardVariation(int v) {
        this.v = v;
    }

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
     * Apply transformation at point p by the variation bv
     * 
     * @param p The point to transform
     * @param bv The variation to transform with
     * @return The point with the transformation applied
     */
    public static Point applyTransformation(Point p, BoardVariation bv) {
        return switch (bv) {
            case BASE       -> new Point(p);
            case ROT90      -> new Point(p.y, Status.SIZE-p.x-1);
            case ROT180     -> new Point(Status.SIZE-p.x-1, Status.SIZE-p.y-1);
            case ROT270     -> new Point(Status.SIZE-p.y-1, p.x);
            case FLIP       -> new Point(p.x, Status.SIZE-p.y-1);
            case FLIPROT90  -> new Point(p.y, p.x);
            case FLIPROT180 -> new Point(Status.SIZE-p.x-1, p.y);
            case FLIPROT270 -> new Point(Status.SIZE-p.y-1, Status.SIZE-p.x-1);
            default         -> null;
        };
    }
    
    /**
     * Apply transformation at point indexed by bitsetIndex with the form x*SIZE
     * +y by the variation BoardVariation.valueof(variationIndex)
     * 
     * @param bitsetIndex The bitsetIndex index to transform
     * @param variationIndex The index of the variation to transform with
     * @return The bitsetIndex with the transformation applied
     */
    public static byte applyTransformation(byte bitsetIndex, int variationIndex) {
        int x = bitsetIndex/Status.SIZE;
        int y = bitsetIndex%Status.SIZE;
        
        int x2, y2;
        
        switch (variationIndex) {
            case 0  -> {x2 = x;               y2 = y;              } // BASE
            case 1  -> {x2 = y;               y2 = Status.SIZE-x-1;} // ROT90
            case 2  -> {x2 = Status.SIZE-x-1; y2 = Status.SIZE-y-1;} // ROT180
            case 3  -> {x2 = Status.SIZE-y-1; y2 = x;              } // ROT270
            case 4  -> {x2 = x;               y2 = Status.SIZE-y-1;} // FLIP
            case 5  -> {x2 = y;               y2 = x;              } // FLIPROT90
            case 6  -> {x2 = Status.SIZE-x-1; y2 = y;              } // FLIPROT180
            case 7  -> {x2 = Status.SIZE-y-1; y2 = Status.SIZE-x-1;} // FLIOROT270
            default -> {x2 = x;               y2 = y;              }
        }
        
        return (byte)(x2*Status.SIZE+y2);
    }
}