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
import java.util.TreeMap;
import javax.swing.JOptionPane;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import static org.cidarlab.fpSelection.selectors.LaserSelector.FilterFPtoLasers;
import static org.cidarlab.fpSelection.adaptors.fpSpectraParse.parse;

/**
 *
 * @author david
 */
public class FilterSelector {

    //////////////////////////////////////
    //      ALGORITHM EXPLANATION       // -preliminary algorithm-
    //////////////////////////////////////
    /*
        1. For each laser, make a selectionInfo object for each fluorophore that gets excited by that laser's wavelength.
        2. Fit a 60 nm wide filter on each fluorophore peak in each laser.
        3. Fix overlaps - 
            3a. push filter bounds apart from eachother if space, else
            3b. shrink filter boundaries until adjacent
        4. Wiggle adjacent boundaries left and right to check for possible SNR increase            
     */
    //took all filter widths from guides at bdbiosciences, left out ones that seemed niche like 28 nm wide, 44 nm wide, etc
    //https://www.bdbiosciences.com/documents/BD_Accuri_Optical_Filter_Guide.pdf
    //https://www.bdbiosciences.com/documents/multicolor_fluorochrome_laser_chart.pdf
    //:D
    private static int[] filterWidths = {15, 20, 25, 30, 40, 45, 50};

    public static void main(String[] args) throws IOException {
        File input = new File("src/main/resources/fp_spectra.csv");
        HashMap<String, Fluorophore> spectralMaps = parse(input);

//        File input = new File("src/main/resources/Fluorophores.org/");
//        HashMap<String, Fluorophore> spectralMaps = generateFPs(input);
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parse(cyto);

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Give an integer n for the number of you would like: ");
//        int n = scanner.nextInt();
        ArrayList<SelectionInfo> pls = LFPtoFilter(spectralMaps, testCyto.lasers, 7);

        ProteinSelector.calcSumSigNoise(pls);
        ProteinSelector.generateNoise(pls);
        ProteinSelector.plotSelection(pls);

    }

    public static ArrayList<SelectionInfo> run(int n, HashMap<String, Fluorophore> masterList, Cytometer cyto) {
        ArrayList<SelectionInfo> pls = LFPtoFilter(masterList, cyto.lasers, n);

        ProteinSelector.calcSumSigNoise(pls);
        ProteinSelector.generateNoise(pls);
        return pls;
    }

    //*** Algorithm ***//
    public static ArrayList<SelectionInfo> LFPtoFilter(HashMap<String, Fluorophore> FPList, List<Laser> lasers, int nDetectors) {
        ArrayList<SelectionInfo> skeleton = new ArrayList();

        ////////////////////////////////////////////////
        // For each laser, make a list of excited FPs //
        ////////////////////////////////////////////////
        double threshold = .2;                                  //The fp's that express more than threshold, higher it is the less acceptance

        //Generate skeleton of returnList.
        for (Laser each : lasers) {
            each.detectors = new LinkedList<>();
            
            SelectionInfo newInfo = new SelectionInfo();
            newInfo.noise = new TreeMap<>();
            newInfo.rankedFluorophores = new ArrayList();
            newInfo.selectedIndex = 0;

            for (Fluorophore fp : FPList.values()) {
                if (fp.EXspectrum.containsKey((double) each.wavelength)) {
                    if (fp.EXspectrum.get((double) each.wavelength) > threshold) {

                        newInfo.rankedFluorophores.add(fp);     //if an fp gets excited at all, add to list.

                        //////////////////////////////////////
                        // Fit a 60 wide filter on each fp //
                        //////////////////////////////////////
                        Detector dumDetect = new Detector();
                        dumDetect.filterWidth = 60;
                        dumDetect.filterMidpoint = (int) fp.EMPeak();

                        each.detectors.add(dumDetect);

                        skeleton.add(newInfo);
                    }
                }
            }
            System.out.println("meow : " + each.detectors.size() + ", " + newInfo.rankedFluorophores.size());
//            each.detectors.sort();
            newInfo.selectedLaser = each;

        }

        /////////////////////////////////////////////
        // Push filter bounds apart from eachother //
        /////////////////////////////////////////////
        for (SelectionInfo laser : skeleton) {
            fixOverlaps(laser);
        }
        
        ////////////////////////////////////////////////////////
        // Wiggle filter boundaries to check for SNR increase //
        ////////////////////////////////////////////////////////
        
        
        //////////////////////////////
        // Create a new list to be displayed on the graph
        ////////////////////////////
        
        ArrayList<SelectionInfo> all = new ArrayList<>();
        
        for(SelectionInfo laser : skeleton)
        {
            Iterator<Detector> iter = laser.selectedLaser.detectors.listIterator();
            for(int i = 0; i < laser.selectedLaser.detectors.size(); i++)
            {
                System.out.println(laser.selectedLaser.detectors.size() + " , " + laser.rankedFluorophores.size());
                SelectionInfo info = new SelectionInfo();
                info.selectedLaser = laser.selectedLaser;
                info.selectedIndex = 0;
                info.selectedDetector = iter.next();
                info.rankedFluorophores = new ArrayList();
                info.rankedFluorophores.add(laser.rankedFluorophores.get(i));
            }
        }

        return all;
    }

    static void fixOverlaps(SelectionInfo laserSetup) {

        //I need the ability to randomly access
        ArrayList<Detector> sortList = new ArrayList<>(laserSetup.selectedLaser.detectors);

        for (int i = 0; i < sortList.size() - 1; i++) {
            boolean overlaps = true;
            Detector first = sortList.get(i);
            Detector second = sortList.get(i + 1);

            while (overlaps) {
                //only push right

                int overlap = checkOverlap(first, second);
                /////////////////////////////
                //  if overlap over 10 nm, let's shrink width of left
                ////////////////////////////
                if (overlap >= 10) {
                    if (widthDown(first)) {
                        continue;
                    }
                }

                ///////////////////////
                //  else push down until they don't overlap
                //////////////////////
                sortList.set(i, first);
                for (int j = i + 1; j < sortList.size(); j++)
                {
                    sortList.get(j).filterMidpoint += overlap;
                    overlaps = false;
                }
            }
            //save the changes to the laser's detectors.
            laserSetup.selectedLaser.detectors = new LinkedList<>(sortList);

        }
    }
    
    static int checkOverlap(Detector first, Detector second)
    {
        int overlap = (first.filterMidpoint + first.filterWidth / 2) - (second.filterMidpoint - second.filterWidth / 2);
        if(overlap <= 0)
        {
            return 0;
        }
        else{
            return overlap;
        }
    }

    static boolean widthDown(Detector d) {
        if (d.filterWidth >= 30) {
            d.filterWidth -= 10;
            return true;
        } else {
            return false;
        }
    }
}
