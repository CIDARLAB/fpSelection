/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alex
 */
public class ExhaustiveSelectionMultiThreadedTest {
    
    public ExhaustiveSelectionMultiThreadedTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of getCombinations method, of class ExhaustiveSelectionMultiThreaded.
     */
    @Test
    public void testGetCombinations() {
        System.out.println("getCombinations");
        int n = 4;
        int numFilters = 10;
        int[] data = new int[n];
        ExhaustiveSelectionMultiThreaded instance = new ExhaustiveSelectionMultiThreaded();
        instance.filterCombinations = new LinkedList<>();
        instance.getCombinations(data, 0, numFilters - 1, 0, n); 
        assertEquals(instance.filterCombinations.size(), 210);
    }

    /**
     * Test of getPermutations method, of class ExhaustiveSelectionMultiThreaded.
     */
    @Test
    public void testGetPermutations() {
        System.out.println("getPermutations");
        int n = 4;
        int[] data = new int[n];
        int numFluorophores = 10;
        ExhaustiveSelectionMultiThreaded instance = new ExhaustiveSelectionMultiThreaded();        
        instance.fluorophorePermutations = new LinkedList<>();
        instance.getPermutations(data, numFluorophores, n);
        assertEquals(instance.fluorophorePermutations.size(), 5040);
    }
    
}
