package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author raul
 */
public class HeuristicStatusTest {
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
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Point p = rotateMovement(new Point(x, y), bv);
                rotatedBoard[p.x][p.y] = board[x][y];
            }
        }
        return rotatedBoard;
    }
    
    private void assertFullEquals(HeuristicStatus[] hss) {
        for (int i = 0; i < 1; i++) {
            for (int j = 8; j < 9; j++) {
                if(i != j) {
                    assertEquals(hss[i].getHeuristic(), hss[j].getHeuristic(), 0.001);
                    assertEquals(hss[i].getZobristHash(), hss[j].getZobristHash());
                }
            }
        }
    }
    
    @Test
    public void testZobristHash() {
        Point[] moves = {
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
        
        int[][][] boards = {
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
        
        
        ZobristKeyGen.BoardVariation[] bvs = ZobristKeyGen.BoardVariation.values();
        
        // Init statues
        HeuristicStatus[] statuses = new HeuristicStatus[bvs.length*2];
        for (int i = 0; i < bvs.length; i++) {
            statuses[i] = new HeuristicStatus(rotateBoard(
                    boards[0], 
                    bvs[i]
            ));
            
            statuses[bvs.length+i] = new HeuristicStatus(rotateBoard(
                    boards[0], 
                    bvs[i]
            ));
        }
        assertFullEquals(statuses);
        
        // Reproduce movements
        for (int move = 1; move < boards.length; move++) {
            for (int i = 0; i <1; i++) {
                statuses[i] = new HeuristicStatus(rotateBoard(
                        boards[move], 
                        bvs[i]
                ));
                
                if(move%2 == 1)
                    statuses[i].skipTurn();
                
                statuses[bvs.length+i].movePiece(rotateMovement(
                        moves[move],
                        bvs[i]
                ));
            }
            
            assertFullEquals(statuses);
        }
    }
}
