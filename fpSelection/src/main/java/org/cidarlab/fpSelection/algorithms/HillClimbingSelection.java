/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cidarlab.fpSelection.Utilities;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.ProteinComparator;
import org.cidarlab.fpSelection.dom.RankedInfo;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author Alex
 */
public class HillClimbingSelection {
    
    private static final int iterations = 2000;
    
    public static List<SelectionInfo> run(int n, Map<String, Fluorophore> masterList, Cytometer cyto) {
        
        List<Fluorophore> fluorophores = new ArrayList<>(masterList.values());
        Map<Detector,Laser> detectorMap = new HashMap<>();
        List<Detector> detectors = new ArrayList<>();
        for(Laser laser:cyto.lasers){
            for(Detector detector:laser.detectors){
                detectorMap.put(detector, laser);
                detectors.add(detector);
            }
        }
        List<Fluorophore> selectedFluorophores = getRandomFluorophores(n,fluorophores);
        List<Detector> selectedDetectors = getRandomDetectors(n,detectors);
        List<SelectionInfo> bestSelection = getSelection(selectedFluorophores, selectedDetectors, detectorMap);
        SNR currentSNR = new SNR(bestSelection);    
        SNR bestSNR = new SNR(bestSelection);
        
       for(int i=0;i<iterations;i++){
            int index = Utilities.getRandom(0, n-1);
            List<Fluorophore> nextFluorophores = new ArrayList<>(selectedFluorophores);
            List<Detector> nextDetectors = new ArrayList<>(selectedDetectors);
            
            if(Utilities.getRandom(0, 1) == 0){
                //Heads: Swap a Fluorophore
                nextFluorophores = swapFluorophore(index,selectedFluorophores,fluorophores);
            } else {
                //Tails: Swap a Detector
                nextDetectors = swapDetector(index,selectedDetectors,detectors);
            }
            List<SelectionInfo> nextSelection = getSelection(nextFluorophores, nextDetectors, detectorMap);
            SNR nextSNR = new SNR(nextSelection);
            if(nextSNR.greaterThan(bestSNR)){
                bestSNR = nextSNR;
                bestSelection = new ArrayList<>(nextSelection);
            }
            
            if(nextSNR.greaterThan(currentSNR)){
                selectedFluorophores = new ArrayList<>(nextFluorophores);
                selectedDetectors = new ArrayList<>(nextDetectors);
                currentSNR = nextSNR;
            }
            
        }
        
        ProteinSelector.generateNoise(bestSelection);
        return bestSelection;
    }
    
    private static List<Fluorophore> swapFluorophore(int index, List<Fluorophore> selected, List<Fluorophore> all){
        List<Fluorophore> next = new ArrayList<>(selected);
        Fluorophore newFP = all.get(Utilities.getRandom(0, all.size()-1));
        if(selected.contains(newFP)){
            int swapIndex = selected.indexOf(newFP);
            Fluorophore indexFP = selected.get(index);
            next.set(index, newFP);
            next.set(swapIndex, indexFP);
            if(selected.get(index) == selected.get(swapIndex)){
                //System.out.println("ERROR!! Something is very wrong here");
            }
        } else {
            next.set(index, newFP);
        }
        return next;
    }
    
    private static List<Detector> swapDetector(int index, List<Detector> selected, List<Detector> all){
        List<Detector> next = new ArrayList<>(selected);
        Detector newD = all.get(Utilities.getRandom(0, all.size()-1));
        if(selected.contains(newD)){
            int swapIndex = selected.indexOf(newD);
            Detector indexD = selected.get(index);
            next.set(index, newD);
            next.set(swapIndex, indexD);
            if(selected.get(index) == selected.get(swapIndex)){
                //System.out.println("ERROR!! Something is very wrong here");
            }
        } else {
            next.set(index, newD);
        }
        return next;
    }
    
    public static List<SelectionInfo> getSelection(List<Fluorophore> fluorophores, List<Detector> detectors, Map<Detector,Laser> detectorMap){
        List<SelectionInfo> selection = new ArrayList<>();
        for(int i=0;i<fluorophores.size();i++){
            SelectionInfo si = new SelectionInfo();
            si.setSelectedFluorophore(fluorophores.get(i));
            si.setSelectedDetector(detectors.get(i));
            si.setSelectedLaser(detectorMap.get(detectors.get(i)));
            selection.add(si);
        }
        return selection;
    }
    
    public static List<Fluorophore> getRandomFluorophores(int n, List<Fluorophore> fluorophores){
        List<Fluorophore> selected = new ArrayList<>();
        Set<Integer> added = new HashSet<>();
        int index;
        for(int i=0;i<n;i++){
            do{
                index = Utilities.getRandom(0, n-1);
            } while(added.contains(index));
            added.add(index);
            selected.add(fluorophores.get(index));
        }
        
        return selected;
    }
    
    public static List<Detector> getRandomDetectors(int n, List<Detector> detectors){
        List<Detector> selected = new ArrayList<>();
        Set<Integer> added = new HashSet<>();
        int index;
        for(int i=0;i<n;i++){
            do{
                index = Utilities.getRandom(0, n-1);
            } while(added.contains(index));
            added.add(index);
            selected.add(detectors.get(index));
        }
        
        return selected;
    }

    private static List<RankedInfo> laserFiltersToFPs(Map<String, Fluorophore> masterList, Laser theLaser) {

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

    private static List<SelectionInfo> hillClimber(List<RankedInfo> suggestions, int n) {
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
