/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import org.cidarlab.fpSelection.Utilities;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.parsers.MetadataParser;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.junit.Test;

/**
 *
 * @author prash
 */
public class ParserTest {
    
    public static String fortessafp = Utilities.getResourcesFilepath() + "ex_fortessa.csv";
    public static String fluorophoreMetafp = Utilities.getResourcesFilepath() + "fluorophore_meta_data.csv";
    public static String fpSpectrafp = Utilities.getResourcesFilepath() + "test_spectra.csv";
    
    
    
    //@Test
    public void testFPMetadataParser(){
        System.out.println("Testing FP Metadata Parser");
        String filepath = Utilities.getResourcesFilepath() + "fluorophore_meta_data.csv";
        Map<String, Fluorophore> masterlist = MetadataParser.parse(filepath);
        
        System.out.println("Total Fluorophores :: " + masterlist.size());
        
        //System.out.println("All Fluorophores ::\n" + masterlist.keySet());
        System.out.println("--------------------------");
        
    }
    
    
    //@Test
    public void testFPSpectraParser() throws IOException{
        System.out.println("Testing FP Spectra Parser");
        String filepath = Utilities.getResourcesFilepath() + "fp_spectra.csv";
        Map<String, Fluorophore> fpMap = fpSpectraParse.parse(filepath);
        System.out.println("Map Size :: " + fpMap.size());
        System.out.println("Keys : " + fpMap.keySet());
        System.out.println("--------------------------");
    }
    
    
    @Test
    public void testFortessaParse() throws IOException {
        

        Cytometer shinyCytometer;
        shinyCytometer = fpFortessaParse.parse(ParserTest.fortessafp, false);

        System.out.println("Sheath Pressure: " + shinyCytometer.getSheathPressure()); 
        System.out.println("Nozzle Size: " + shinyCytometer.getNozzleSize());
        System.out.println("Window Extension: " + shinyCytometer.getWindowExt());
        
        
        LinkedList<Laser> lasers = shinyCytometer.lasers;
        LinkedList<Detector> detectors;
        for (int i = 0; i < lasers.size(); i++) {
            Laser thisLaser = lasers.get(i);
            System.out.println("Laser " + thisLaser.position + ": " + thisLaser.getName());

            System.out.println(thisLaser.type);
            System.out.println(thisLaser.wavelength);
            System.out.println(thisLaser.power);
            System.out.println(thisLaser.detectorArray);
            detectors = thisLaser.detectors;

            for (int j = 0; j < detectors.size(); j++) {
                Detector thisDetector = detectors.get(j);

                System.out.println(thisDetector.identifier + ": " + thisDetector.channel + " " + thisDetector.mirror + " " + thisDetector.filterMidpoint + " " + thisDetector.filterWidth);
            }
            System.out.println("");
        }
    }
    
    
}
