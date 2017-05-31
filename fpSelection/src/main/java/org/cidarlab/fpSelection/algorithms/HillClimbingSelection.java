/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.ProteinComparator;
import org.cidarlab.fpSelection.dom.RankedInfo;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author Alex
 */
public class HillClimbingSelection {
    //Given a Laser & Filters, Suggest List of Proteins that works optimally for each filter
    //Suggest proteins based on a laser & filters
    //Works best for n >= 2;

    public static ArrayList<SelectionInfo> run(int n, Map<String, Fluorophore> masterList, Cytometer cyto) {
        ArrayList<RankedInfo> total = new ArrayList<>();
        for (Laser lase : cyto.lasers) {
            total.addAll(laserFiltersToFPs(masterList, lase));

        }

        //Prune the arrayList of the worst FPs until the size of the ArrayList is equal to 'n'
        return hillClimber(total, n);
    }

    public static ArrayList<RankedInfo> laserFiltersToFPs(Map<String, Fluorophore> masterList, Laser theLaser) {

        //Pull Detector objects out.
        LinkedList<Detector> listDetectors = new LinkedList<>();

        //Populate the array to prep for sorting
        for (Detector eachDetector : theLaser.detectors) {

            listDetectors.add(eachDetector);
        }

        //Each Detector has a best fluorophore:
        ArrayList<RankedInfo> bestFPs = new ArrayList<>();

        ArrayList<Fluorophore> tempList;
        RankedInfo choiceInfo;

        //  For each filter, create list of proteins ranked in terms of expression.  
        for (Detector aDetector : listDetectors) {
            //Comparator is based on expression in filter - need laser & filter references
            ProteinComparator qCompare = new ProteinComparator();
            qCompare.laser = theLaser;
            qCompare.detect = aDetector;
            qCompare.setDefaults();

            tempList = new ArrayList<>();
            int threshold = 80;

            while (threshold > 0 && tempList.size() < (.1 * masterList.size() + 3)) {
                threshold--;

                for (Map.Entry<String, Fluorophore> entry : masterList.entrySet()) {
                    Fluorophore value = entry.getValue();

                    if (value.express(theLaser, aDetector) < threshold) {
                        continue;
                    } else {

                        tempList.add(value);
                    }
                }
            }
            if (tempList.isEmpty()) {
                continue;
            }
            tempList.sort(qCompare);

            //Put into the selectionInfo object, one for each channel
            choiceInfo = new RankedInfo();
            choiceInfo.selectedLaser = theLaser;
            choiceInfo.selectedDetector = aDetector;
            choiceInfo.rankedFluorophores = tempList;
            choiceInfo.selectedIndex = 0;
            choiceInfo.noise = new TreeMap<>();

            bestFPs.add(choiceInfo);
        }

        return bestFPs;

    }

    public static ArrayList<SelectionInfo> hillClimber(ArrayList<RankedInfo> suggestions, int n) {
        //Build list of things to check.

        double sumSNR = 0;
        ArrayList<RankedInfo> allInfo = new ArrayList<>();
        ArrayList<RankedInfo> iterateInfo = new ArrayList<>();

        //Test each FP with the other lasers based on simple SNR, keep the best.
//        for (SelectionInfo info : suggestions) {
//            allInfo.add(info);
//            iterateInfo.add(info);
//        }
        allInfo.addAll(suggestions);
        iterateInfo.addAll(suggestions);

        boolean duplicates = true;
        ArrayList<RankedInfo> removes = new ArrayList<>();

        while (duplicates) {
            duplicates = false;

            for (RankedInfo info : iterateInfo) {

                Fluorophore fp1 = info.rankedFluorophores.get(info.selectedIndex);
                for (RankedInfo otherInfo : allInfo) {
                    Fluorophore fp2 = otherInfo.rankedFluorophores.get(otherInfo.selectedIndex);

                    //if the same FP is chosen 
                    if (fp1 == fp2 && info.selectedDetector != otherInfo.selectedDetector) {

                        //if true, keep info. False, keep otherInfo
                        if (ProteinComparator.dupeCompare(info, otherInfo, ProteinComparator.compareTypes.Brightness, false)) {
                            if (otherInfo.rankedFluorophores.size() - 1 == otherInfo.selectedIndex) {
                                if (!removes.contains(otherInfo)) {

                                    removes.add(otherInfo);
                                }
                                continue;
                            } else {

                                otherInfo.selectedIndex++;
                            }
                        } else if (info.rankedFluorophores.size() - 1 == info.selectedIndex) {
                            if (!removes.contains(info)) {

                                removes.add(info);
                            }
                            continue;
                        } else {

                            info.selectedIndex++;
                        }

                        duplicates = true;
                    }

                }

            }
        }

        for (RankedInfo info : removes) {
            if (iterateInfo.contains(info)) {
                iterateInfo.remove(info);
            }
            if (allInfo.contains(info)) {
                allInfo.remove(info);
            }
        }

        sumSNR = ProteinSelector.calcRankedSumSigNoise(allInfo);

        //Start with all of the proteins, clip one by one and record how the SNR changes.
        //After each loop of clipping, whichever had the most positive change stays clipped.
        //Positive change = sumSNR++
        double SNR;

        //Pick the N best proteins.
        while (allInfo.size() > n) {
            RankedInfo highestScore = iterateInfo.get(0);

            for (RankedInfo info : iterateInfo) {

                //Remove the protein in question from the arraylist
                allInfo.remove(info);

                //Calculate the total Signal - Noise from that removal
                SNR = ProteinSelector.calcRankedSumSigNoise(allInfo);

                //Add the protein back into the arraylist
                allInfo.add(info);

                //if removal positive, score should be positive since SNR should have increased
                //The more positive the impact, the higher the score.                
                info.score = SNR - sumSNR;
                
                if (info.score > highestScore.score) {
                    highestScore = info;
                }
                
//                if(info.isZeroNoise()){
//                    highestScore = info;
//                    
//                }
//                if(info.isSNRlessThanOne()){
//                    highestScore = info;
//                }

            }
            iterateInfo.remove(highestScore);
            allInfo.remove(highestScore);

        }
        
        ArrayList<SelectionInfo> result = new ArrayList<>();
        
        for(RankedInfo ri: allInfo){
            SelectionInfo si = new SelectionInfo(ri);
            result.add(si);
        }
        
        ProteinSelector.generateNoise(result);
        sumSNR = ProteinSelector.calcSumSigNoise(result);

        return result;
    }

}
