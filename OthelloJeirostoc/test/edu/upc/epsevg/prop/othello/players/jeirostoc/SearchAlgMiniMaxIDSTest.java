/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.jeirostoc;

import edu.upc.epsevg.prop.othello.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Class to test edge cases of the MiniMaxIDS
 * 
 * @author raul
 * @author josep
 */
public class SearchAlgMiniMaxIDSTest {

    @Test
    public void testDoSearchEdgeCase1() {
        int[][] sampleBoard = {
            { -1,  1, -1, -1, -1, -1, -1, -1},
            {  1,  1,  1, -1, -1,  1, -1, -1},
            {  1,  1, -1, -1, -1, -1, -1, -1},
            {  1,  1, -1, -1,  1, -1,  1, -1},
            {  1, -1, -1,  1, -1,  1, -1, -1},
            {  1,  1,  1,  1,  1, -1, -1, -1},
            {  1,  1,  1,  1,  1,  1,  1,  0},
            { -1, -1, -1, -1, -1,  1,  1,  1}
        };
        
        GameStatus s = new GameStatus(sampleBoard);
        PlayerMiniMax p = new PlayerMiniMax(10);
        assertNotNull(p.move(s).getTo());
    }
    
}
