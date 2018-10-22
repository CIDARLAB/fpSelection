/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.cidarlab.fpSelection.Utilities;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.ScrapedCSVParse;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import static org.cidarlab.fpSelection.selectors.LaserSelecTwoR.FilterFPtoLasers;
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
public class LaserSelecTwoRTest {
    
    public LaserSelecTwoRTest() {
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

  
    
    /////////////////////////////////
    // Incomplete, wanted to redo as a side project
    /////////////////////////////////
    
     public static void main(String[] args) throws IOException {
        File input = new File("resources/Fluorophores.org/");
        HashMap<String, Fluorophore> spectralMaps = ScrapedCSVParse.parse(input);

//        File input = new File("src/main/resources/fp_spectra.csv");
//        HashMap<String, Fluorophore> spectralMaps = fpSpectraParse.parse(input);

        HashMap<String, Fluorophore> choose = new HashMap<>();
        Random next = new Random();

        Cytometer testCyto = fpFortessaParse.parse(Utilities.getResourcesFilepath() + "ex_fortessa.csv", true);

        ArrayList<Detector> detect = new ArrayList();

        for (int i = 0; i < 5; i++) {
            Laser get = testCyto.lasers.get(next.nextInt(testCyto.lasers.size()));
            detect.add(get.detectors.get(next.nextInt(get.detectors.size())));
        }

        ArrayList<SelectionInfo> pls = FilterFPtoLasers(spectralMaps, detect, 3);

        ProteinSelector.calcSumSigNoise(pls);
        ProteinSelector.generateNoise(pls);
        ProteinSelector.plotSelection(pls);

    }
     
     
    
}
