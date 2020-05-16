/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import com.panayotis.gnuplot.dataset.PointDataSet;
import java.util.TreeMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author David
 */
public class FluorophoreTest {
    
    public FluorophoreTest() {
    }
    
    Fluorophore instance;

    @Before
    public void setUp() throws Exception {
        instance = new Fluorophore();
        instance.EXspectrum = new TreeMap<>();
        instance.EMspectrum = new TreeMap<>();
        for(double i = 400; i <= 600; i++)
        {
            double blah = Math.abs(500 - i);
            instance.EXspectrum.put(i, Math.abs(100-blah));
            instance.EMspectrum.put(i, Math.abs(100-blah));
        }
    }

    @After
    public void tearDown() throws Exception {
        instance = null;
    }

    /**
     * Test of express method, of class Fluorophore.
     */
    @Test
    public void testExpress() {
        System.out.println("express");
        Laser theLaser = new Laser();
        theLaser.wavelength = 450;
        Detector theDetector = new Detector();
        theDetector.filterMidpoint = 500;
        theDetector.filterWidth = 20;
        //express = (intensity/100 * riemann sum)/filterWidth
        double expResult = 950.0/20.0;
        double result = instance.express(theLaser, theDetector);
        assertEquals(expResult, result, 0.0);
    }

    
    
}
