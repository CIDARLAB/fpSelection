/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David
 */
public class FilterSelectorTest {
    
    public FilterSelectorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

   

    /**
     * Test of run method, of class FilterSelector.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        int n = 0;
        HashMap<String, Fluorophore> masterList = null;
        Cytometer cyto = null;
        ArrayList<SelectionInfo> expResult = null;
        ArrayList<SelectionInfo> result = FilterSelector.run(n, masterList, cyto);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of LFPtoFilter method, of class FilterSelector.
     */
    @Test
    public void testLFPtoFilter() {
        System.out.println("LFPtoFilter");
        HashMap<String, Fluorophore> FPList = null;
        List<Laser> lasers = null;
        int nDetectors = 0;
        ArrayList<SelectionInfo> expResult = null;
        ArrayList<SelectionInfo> result = FilterSelector.LFPtoFilter(FPList, lasers, nDetectors);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fixOverlaps method, of class FilterSelector.
     */
    

    /**
     * Test of checkOverlap method, of class FilterSelector.
     */
    @Test
    public void testCheckOverlap() {
        System.out.println("checkOverlap");
        Detector first = null;
        Detector second = null;
        int expResult = 0;
        int result = FilterSelector.checkOverlap(first, second);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of widthDown method, of class FilterSelector.
     */
    @Test
    public void testWidthDown() {
        System.out.println("widthDown");
        Detector d = null;
        boolean expResult = false;
        boolean result = FilterSelector.widthDown(d);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
