/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import java.util.Map;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment;
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
public class DataAnalyticsTest {
    
    public DataAnalyticsTest() {
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
     * Test of walk method, of class DataAnalytics.
     */
    @Test
    public void testWalk() {
        System.out.println("walk");
        String path = Utilities.getResourcesFilepath()  + "fpSelectionData" + Utilities.getSeparater() + "FP_selection_R1" + Utilities.getSeparater() + "analysis" + Utilities.getSeparater();
        String resultsRoot = path;
        Map<String, Map<String,AnalyticsExperiment>> result = DataAnalytics.walk(path);
        
    }
    
}
