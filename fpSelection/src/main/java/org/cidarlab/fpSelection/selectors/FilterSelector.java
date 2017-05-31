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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.RankedInfo;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import static org.cidarlab.fpSelection.parsers.fpSpectraParse.parse;

/**
 *
 * @author david
 */
public class FilterSelector {

    //////////////////////////////////////
    //      ALGORITHM EXPLANATION       // -preliminary algorithm-
    //////////////////////////////////////
    /*
        1. For each FP, find the best laser for it in terms of excitation wavelength. That's our preliminary guess as to where it will go.
        2. Fit a 60 nm wide filter on each fluorophore peak in each laser.
        3. Loop to fix overlaps - 
            3a. shrink filter boundaries slightly, 
            3b. push filter bounds apart from each other    
        4. Run FP selection to suggest n filters and n FPs
     */
    //took all filter widths from guides at bdbiosciences, left out ones that seemed niche like 28 nm wide, 44 nm wide, etc
    //https://www.bdbiosciences.com/documents/BD_Accuri_Optical_Filter_Guide.pdf
    //https://www.bdbiosciences.com/documents/multicolor_fluorochrome_laser_chart.pdf
    //:D
    private static int[] filterWidths = {15, 20, 25, 30, 40, 45, 50};


//    public static void main(String[] args) throws IOException {
//        File input = new File("src/main/resources/fp_spectra.csv");
//        HashMap<String, Fluorophore> spectralMaps = parse(input);
//
////        File input = new File("src/main/resources/Fluorophores.org/");
////        HashMap<String, Fluorophore> spectralMaps = generateFPs(input);
//        File cyto = new File("src/main/resources/ex_fortessa.csv");
//        Cytometer testCyto = fpFortessaParse.parse(cyto);
//
////        Scanner scanner = new Scanner(System.in);
////        System.out.println("Give an integer n for the number of you would like: ");
////        int n = scanner.nextInt();
//        ArrayList<SelectionInfo> pls = LFPtoFilter(spectralMaps, testCyto.lasers, 7);
//
//        ProteinSelector.calcSumSigNoise(pls);
//        ProteinSelector.generateNoise(pls);
//        ProteinSelector.plotSelection(pls);
//
//    }


    public static ArrayList<SelectionInfo> run(int n, Map<String, Fluorophore> masterList, Cytometer cyto) {
        ArrayList<SelectionInfo> pls = LFPtoFilter(masterList, cyto.lasers, n);

        ProteinSelector.calcSumSigNoise(pls);
        ProteinSelector.generateNoise(pls);
        return pls;
    }

    //*** Algorithm ***//
    public static ArrayList<SelectionInfo> LFPtoFilter(Map<String, Fluorophore> FPList, List<Laser> lasers, int nDetectors) {
        ArrayList<RankedInfo> skeleton = new ArrayList();
        
        // Temp solution while fortessa parse is broken
        lasers = new ArrayList<>();
        Laser violet = new Laser();
        violet.name = "Violet";
        violet.wavelength = 405;
        lasers.add(violet);
        Laser blue = new Laser();
        blue.name = "Blue";
        blue.wavelength = 488;
        lasers.add(blue);
        Laser bluegreen = new Laser();
        bluegreen.name = "Blue-Green";
        bluegreen.wavelength = 514;
        lasers.add(bluegreen);
        Laser yelgreen = new Laser();
        yelgreen.name = "Yellow-Green";
        yelgreen.wavelength = 561;
        lasers.add(yelgreen);
        Laser red = new Laser();
        red.name = "Red";
        red.wavelength = 637;
        lasers.add(red);

        //////////////////////////////////////////
        // Find the best laser for each protein //
        //////////////////////////////////////////
        for (Laser each : lasers) {
            System.out.println(each.wavelength);
            RankedInfo newInfo = new RankedInfo();
            newInfo.noise = new TreeMap<>();
            newInfo.rankedFluorophores = new ArrayList();
            newInfo.selectedIndex = 0;
            newInfo.selectedLaser = each;
            skeleton.add(newInfo);
        }

        for (Fluorophore fp : FPList.values()) {
            RankedInfo mostExcite = null;
            double howExcite = 0;
            int exciteIndex = -1;

            int index = 0;
            for (RankedInfo each : skeleton) {
                if (mostExcite == null) {
                    mostExcite = each;
                    exciteIndex = 0;

                } else {
                    if (fp.EXspectrum.containsKey((double) each.selectedLaser.wavelength)) {
                        if (fp.EXspectrum.get((double) each.selectedLaser.wavelength) > howExcite) {
                            mostExcite = each;
                            howExcite = fp.EXspectrum.get((double) each.selectedLaser.wavelength);
                            exciteIndex = index;
                        }
                    }
                }
                index++;
            }
            mostExcite.rankedFluorophores.add(fp);
            Detector dumDetect = new Detector();
            dumDetect.filterWidth = 60;
            dumDetect.filterMidpoint = (int) fp.EMPeak();
            mostExcite.selectedLaser.addDetector(dumDetect);

            skeleton.set(exciteIndex, mostExcite);

        }

        /////////////////////////////////////////////
        // Shrink/Push filter bounds apart from eachother //
        /////////////////////////////////////////////
        for (RankedInfo laser : skeleton) {
            fixOverlaps(laser);
        }

        ////////////////////////////////////////////////////////
        // Wiggle filter boundaries to check for SNR increase //
        ////////////////////////////////////////////////////////
        
        
        //T o  B e  D o n e
        
        //////////////////////////////
        // Create a new list to be displayed on the graph
        ////////////////////////////
        ArrayList<SelectionInfo> all = new ArrayList<>();

        for (RankedInfo laser : skeleton) {

            Iterator<Detector> iter = laser.selectedLaser.detectors.listIterator();
            for (int i = 0; i < laser.selectedLaser.detectors.size(); i++) {
                SelectionInfo info = new SelectionInfo();
                info.selectedLaser = laser.selectedLaser;
                info.selectedDetector = iter.next();
                info.selectedFluorophore = laser.rankedFluorophores.get(i);
                System.out.println(info.selectedLaser.wavelength + " : " + info.selectedDetector.filterMidpoint + "/" + info.selectedDetector.filterWidth + " LP, FP: " + info.selectedFluorophore.name);
                all.add(info);
            }
        }
        
        
        
        //After making slew of filters, optimize and pick the best setup of n FPs and n filters.
        
        Cytometer cyto = new Cytometer();
        cyto.lasers = new LinkedList<>();
        
        for(RankedInfo each : skeleton)
        {
            cyto.lasers.add(each.selectedLaser);
        }
        
        all = HillClimbingSelection.run(nDetectors, FPList, cyto);

        return all;
    }

    static void fixOverlaps(RankedInfo laserSetup) {

        //I need the ability to randomly access
        ArrayList<Detector> sortList = new ArrayList<>(laserSetup.selectedLaser.detectors);

        for (int i = 0; i < sortList.size() - 1; i++) {
            boolean overlaps = true;
            Detector first = sortList.get(i);
            Detector second = sortList.get(i + 1);
            if(checkOverlap(first, second) <= 0)
            {
                overlaps = false;
            }

            while (overlaps) {
                
                /////////////////////////////
                //  if overlap, let's shrink width of left
                ////////////////////////////
                if (!(widthDown(first) && widthDown(second))) {
                    second.filterMidpoint += checkOverlap(first, second);
                    sortList.set(i, first);
                    sortList.set(i + 1, second);
                    overlaps = false;
                }

                ///////////////////////
                //  else push down until they don't overlap
                //////////////////////
                int over = checkOverlap(first, second);
                first.filterMidpoint -= 3 * over / 10;
                second.filterMidpoint += 3 * over / 10;

                if (checkOverlap(first, second) == 0) {
                    first.identifier = (first.filterMidpoint + "/" + first.filterWidth);
                    second.identifier = (second.filterMidpoint + "/" + second.filterWidth);
                    sortList.set(i, first);
                    sortList.set(i + 1, second);
                    overlaps = false;
                }

            }
            //save the changes to the laser's detectors.
            laserSetup.selectedLaser.detectors = new LinkedList<>(sortList);

        }
    }

    static int checkOverlap(Detector first, Detector second) {
        int overlap = (first.filterMidpoint + first.filterWidth / 2) - (second.filterMidpoint - second.filterWidth / 2);
        if (overlap <= 0) {
            return 0;
        } else {
            return overlap;
        }
    }

    static boolean widthDown(Detector d) {
        if (d.filterWidth >= 15) {
            d.filterWidth -= 5;
            return true;
        } else {
            return false;
        }
    }
}
