/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.util.ArrayList;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author prash
 */
public class ProteinSelectorTest {
    
    public ProteinSelectorTest() {
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
     * Test of plotSelection method, of class ProteinSelector.
     */
    //@Test
    public void testPlotSelection() {
        System.out.println("plotSelection");
        ArrayList<SelectionInfo> info = null;
        ProteinSelector.plotSelection(info);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calcSumSigNoise method, of class ProteinSelector.
     */
    @Test
    public void testCalcSumSigNoise() {
        int i = 2;
        int j = 3;
        assertEquals((i+j),5);
    }

    /**
     * Test of generateNoise method, of class ProteinSelector.
     */
    //@Test
    public void testGenerateNoise() {
        System.out.println("generateNoise");
        ArrayList<SelectionInfo> selected = null;
        ProteinSelector.generateNoise(selected);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
