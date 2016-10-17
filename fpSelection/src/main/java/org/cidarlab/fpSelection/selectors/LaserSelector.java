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
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import org.cidarlab.fpSelection.adaptors.ScrapedCSVParse;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.ProteinComparator;
import org.cidarlab.fpSelection.dom.SelectionInfo;

/**
 *
 * @author david
 */
public class LaserSelector {
    
    
    /////////////
    //Test Main//
    /////////////
    public static void main(String[] args) throws IOException {
        File input = new File("src/main/resources/Fluorophores.org/");
        HashMap<String, Fluorophore> spectralMaps = ScrapedCSVParse.parse(input);

        HashMap<String, Fluorophore> choose = new HashMap<>();
        Random next = new Random();

        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parse(cyto);

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
    
    public static ArrayList<SelectionInfo> run(HashMap<String, Fluorophore> masterList, Cytometer cyto) {
        ArrayList<Detector> detect = new ArrayList();
        Random next = new Random();
        for (int i = 0; i < 5; i++) {
            Laser get = cyto.lasers.get(next.nextInt(cyto.lasers.size()));
            detect.add(get.detectors.get(next.nextInt(get.detectors.size())));
        }
        ArrayList<SelectionInfo> pls = FilterFPtoLasers(masterList, detect, 3);

        ProteinSelector.calcSumSigNoise(pls);
        ProteinSelector.generateNoise(pls);
        return pls;
    }
    
    
    ///////////////////////////////
    ///         PROCESS         ///
    ///////////////////////////////
    /*
        1. Group the proteins by adding them to n lists for equally spaced n lasers.
        2. Separate each laser's list into wide generic emission ranges, skip empty emission ranges.
        3. Fit each given detector to the most responsive ranges.
                i.e.: Between two 400-500 nm spaces on different lasers, FPs in one respond more than the other. You fit the detector onto that laser's range.
        4. Cull the herd. Any ranges that still don't have a proper detector are deleted.
        5. For each laser, scan over the space given (defined by generalSpace) to find the laser wavelength that produces the most response from all proteins that were placed in that range.
        6. Sort the protein lists based on expression and leakage in it's laser detector combo.
    
    */

    public static ArrayList<SelectionInfo> FilterFPtoLasers(HashMap<String, Fluorophore> fpList, List<Detector> selectedDetectors, int nLasers) {

        double spectralMin = 200;
        double spectralMax = 1000;
        double generalSpace = 800 / (nLasers);
        double peak;
        double divisions;

        ArrayList<SelectionInfo> generalList = new ArrayList<>();

        ///////////////////////////////////////
        //Group proteins by n general lasers.//
        ///////////////////////////////////////
        ArrayList<Laser> theLasers = new ArrayList();

        for (Fluorophore each : fpList.values()) {
            boolean insert = false;

            peak = each.EXPeak();
            divisions = peak % generalSpace;
            if (divisions != 0) {
                if (divisions >= generalSpace / 2) {
                    peak += generalSpace - divisions;
                } else {
                    peak -= divisions;
                }
            }

            for (SelectionInfo info : generalList) {
                if (info.selectedLaser.wavelength == peak) {
                    info.rankedFluorophores.add(each);
                    insert = true;
                }
            }
            if (insert == false) {
                SelectionInfo newInfo = new SelectionInfo();
                Laser newLase = new Laser();
                newLase.wavelength = (int) peak;
                newInfo.selectedLaser = newLase;
                newInfo.rankedFluorophores = new ArrayList<>();
                newInfo.rankedFluorophores.add(each);
                theLasers.add(newLase);
                generalList.add(newInfo);
            }

        }
        ////////////////////////////////////////
        //Separate lasers into emission ranges//
        ////////////////////////////////////////

        ArrayList<SelectionInfo> EMspecific = new ArrayList<>();

        for (SelectionInfo each : generalList) {
            Detector testDetect;
            for (int i = (int) spectralMin; i < spectralMax; i += 100) {
                SelectionInfo specificInfo = new SelectionInfo();
                specificInfo.rankedFluorophores = new ArrayList<>();
                specificInfo.score = 0;
                specificInfo.noise = new TreeMap<>();
                testDetect = new Detector();
                testDetect.filterMidpoint = i + 50;
                testDetect.filterWidth = 100;
                testDetect.identifier = "";
                testDetect.mirror = -5;

                for (Fluorophore fp : each.rankedFluorophores) {
                    double expression = fp.express(each.selectedLaser, testDetect);

                    specificInfo.rankedFluorophores.add(fp);
                    specificInfo.score += expression;

                }
                if (specificInfo.rankedFluorophores.isEmpty()) {
                    continue;
                } else {
                    specificInfo.selectedDetector = testDetect;
                    specificInfo.selectedLaser = each.selectedLaser;
                    EMspecific.add(specificInfo);
                }
            }
        }
        /////////////////////////////////////////////////
        //Fit the filters to the most responsive ranges//
        /////////////////////////////////////////////////
        

        for (Detector detector : selectedDetectors) {
            SelectionInfo highestScore = new SelectionInfo();
            highestScore.score = 0;
            double detectMin = detector.filterMidpoint - .5 * detector.filterWidth;
            double detectMax = detectMin + detector.filterWidth;
            for (SelectionInfo each : EMspecific) {
                Detector eachDetector = each.selectedDetector;
                if (detectMin >= (eachDetector.filterMidpoint - eachDetector.filterWidth / 2)) {
                    if (detectMax <= (eachDetector.filterMidpoint + eachDetector.filterWidth / 2)) {
                        if (each.score > highestScore.score) {
                            highestScore = each;
                        }
                    }
                }
            }
            highestScore.selectedDetector = detector;
        }

        
        /////////////////
        //Cull the herd//
        /////////////////
        
        //aka where n comes to die
        
        ArrayList<SelectionInfo> delete = new ArrayList();

        for (SelectionInfo check : EMspecific) {
            if (check.selectedDetector.identifier == "" && check.selectedDetector.mirror == -5) {
                delete.add(check);
            }
        }
        for (SelectionInfo yes : delete) {
            EMspecific.remove(yes);

        }

        ///////////////////////////////////////////////////////////
        //Scan wavelengths to finely adjust the n general lasers.//
        ///////////////////////////////////////////////////////////
        ArrayList<SelectionInfo> averageMe;

        for (Laser each : theLasers) {
            averageMe = new ArrayList();
            int laserlength = each.wavelength;

            for (SelectionInfo info : EMspecific) {
                if (info.selectedLaser == each) {
                    averageMe.add(info);
                }
            }
            int bestWave = laserlength;
            double highestResponse = 0;

            for (int i = (int) (laserlength - generalSpace / 2); i < laserlength + generalSpace / 2; i++) {
                double sum = 0;
                each.wavelength = i;

                for (SelectionInfo info : averageMe) {
                    for (Fluorophore fp : info.rankedFluorophores) {
                        sum += fp.express(each, info.selectedDetector);
                    }
                }

                if (sum > highestResponse) {
                    bestWave = i;
                    highestResponse = sum;
                }
            }

            each.wavelength = bestWave;
            each.name = Integer.toString(bestWave) + " nm Laser";
        }

        /////////////////////////////////////////////////////////////////////
        //Sort each ArrayList of Fluorophores to get the best one in front.//
        /////////////////////////////////////////////////////////////////////
        for (SelectionInfo each : EMspecific) {
            ProteinComparator qCompare = new ProteinComparator();
            qCompare.setDefaults();
            qCompare.laser = each.selectedLaser;
            qCompare.detect = each.selectedDetector;
            each.selectedIndex = 0;

            each.rankedFluorophores.sort(qCompare);
        }

        return EMspecific;
    }

}
