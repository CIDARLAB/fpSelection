package org.cidarlab.fpSelection;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cidarlab.fpSelection.algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.algorithms.SimulatedAnnealing;
import org.cidarlab.fpSelection.algorithms.SimulatedAnnealingThread;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import java.util.Random;
import org.junit.Test;

/**
 * @author prash
 */
public class TestUtilities {


    public static String toString(List<SelectionInfo> selection) {
        Map<String, Integer> maps = new HashMap<>();

        int n = selection.size();
        for (int i = 0; i < n; i++) {
            maps.put(selection.get(i).getSelectedFluorophore().name, i);
        }
        List<String> keys = new ArrayList<>(maps.keySet());

        String line = "";
        for (int i = 0; i < n - 1; i++) {
            String key = keys.get(i);
            line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
            //line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue() + ",";
        }
        String key = keys.get(n - 1);
        line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
        //line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue();
        return line;
    }
    

    //Get Random set of fluorophores - Seeded
    private static List<Fluorophore> getRandomFluorophores(int n, List<Fluorophore> fluorophores,ThreadLocal<Random> threadrandom){
        List<Fluorophore> selected = new ArrayList<>();
        Set<Integer> added = new HashSet<>();
        int index;
        for(int i=0;i<n;i++){
            do{
                index = Utilities.getRandom(0, fluorophores.size() -1,threadrandom);
            } while(added.contains(index));
            added.add(index);
            selected.add(fluorophores.get(index));
        }
        
        return selected;
    }
    
    //Get Random set of Detectors - Seeded
    private static List<Detector> getRandomDetectors(int n, List<Detector> detectors, ThreadLocal<Random> threadrandom){
        List<Detector> selected = new ArrayList<>();
        Set<Integer> added = new HashSet<>();
        int index;
        for(int i=0;i<n;i++){
            do{
                index = Utilities.getRandom(0, detectors.size() -1,threadrandom);
            } while(added.contains(index));
            added.add(index);
            selected.add(detectors.get(index));
        }
        
        return selected;
    }

    public static List<Fluorophore> swapFluorophore(int index, List<Fluorophore> selected, List<Fluorophore> all, ThreadLocal<Random> threadrandom) {
        List<Fluorophore> newList = new ArrayList<>();
        Fluorophore newFP = all.get(Utilities.getRandom(0, all.size() - 1,threadrandom));
        if (selected.contains(newFP)) {
            int swapIndex = selected.indexOf(newFP);
            Fluorophore indexFP = selected.get(index);
            for (int i = 0; i < selected.size(); i++) {
                if (i == index) {
                    newList.add(newFP);
                } else if (i == swapIndex) {
                    newList.add(indexFP);
                } else {
                    newList.add(selected.get(i));
                }
            }
        } else {
            for (int i = 0; i < selected.size(); i++) {
                if (i == index) {
                    newList.add(newFP);
                } else {
                    newList.add(selected.get(i));
                }
            }
        }
        return newList;
    }

    public static List<Detector> swapDetector(int index, List<Detector> selected, List<Detector> all, ThreadLocal<Random> threadrandom) {
        List<Detector> newList = new ArrayList<>();
        Detector newD = all.get(Utilities.getRandom(0, all.size() - 1,threadrandom));
        if (selected.contains(newD)) {
            int swapIndex = selected.indexOf(newD);
            Detector indexD = selected.get(index);
            for (int i = 0; i < selected.size(); i++) {
                if (i == index) {
                    newList.add(newD);
                } else if (i == swapIndex) {
                    newList.add(indexD);
                } else {
                    newList.add(selected.get(i));
                }
            }
        } else {
            for (int i = 0; i < selected.size(); i++) {
                if (i == index) {
                    newList.add(newD);
                } else {
                    newList.add(selected.get(i));
                }
            }
        }
        return newList;
    }
    
    public static List<SelectionInfo> runSeededSA(Map<String,Fluorophore> fpmap, Cytometer cyto, int n, double temp, double rate, ThreadLocal<Random> threadrandom){

        List<Fluorophore> fluorophores = new ArrayList<>();
        for(String key:fpmap.keySet()){
            fluorophores.add(fpmap.get(key));
        }
        
        Collections.sort(fluorophores, (Fluorophore f1, Fluorophore f2) -> {
            return f1.name.compareTo(f2.name);
        });

        List<Detector> detectors = new ArrayList<>();
        Map<Detector,Laser> detectorMap = new HashMap<>();

        for(Laser l:cyto.lasers){
            for(Detector d:l.detectors){
                detectors.add(d);
                detectorMap.put(d,l);
            }
        }
        List<Fluorophore> currentFluorophores = getRandomFluorophores(n, fluorophores,threadrandom);
        List<Detector> currentDetectors = getRandomDetectors(n, detectors,threadrandom);

        
        List<SelectionInfo> current = HillClimbingSelection.getSelection(currentFluorophores, currentDetectors, detectorMap);
        SNR bestSNR = new SNR(current);

        List<SelectionInfo> best = current;
        SNR currentSNR = bestSNR;
         
        while (temp > 1) {

            int swapIndex = Utilities.getRandom(0, n - 1,threadrandom);
            
            List<Fluorophore> nextFluorophores;
            List<Detector> nextDetectors;

            int coinFlip = Utilities.getRandom(0, 1,threadrandom);
            if (coinFlip == 0) {
                //Heads Swap an FP
                nextFluorophores = swapFluorophore(swapIndex, currentFluorophores, fluorophores,threadrandom);
                nextDetectors = new ArrayList<>(currentDetectors);
            } else {
                //Tails Swap Detector
                nextFluorophores = new ArrayList<>(currentFluorophores);
                nextDetectors = swapDetector(swapIndex, currentDetectors, detectors,threadrandom);
            }
            List<SelectionInfo> next = HillClimbingSelection.getSelection(nextFluorophores, nextDetectors, detectorMap);
            SNR nextSNR = new SNR(next);

            if (nextSNR.greaterThan(bestSNR)) {
                bestSNR = nextSNR;
                best = new ArrayList<>(next);
            }
            double random = threadrandom.get().nextDouble();
            double ap = SimulatedAnnealingThread.acceptanceProbability(currentSNR,nextSNR,temp);
            
            
            if(random <= ap){
                //SWAP!!!
                current = new ArrayList<>(next);
                currentSNR = nextSNR;
                currentFluorophores = new ArrayList<>(nextFluorophores);
                currentDetectors = new ArrayList<>(nextDetectors);
                
            }

            
            //System.out.println("Thread " + id + " current temperature = " + temp);
            temp *= (1 - rate);
        }

        return best;
    }

    public static List<SelectionInfo> runSeededHC(Map<String,Fluorophore> fpmap, Cytometer cyto, int n, int iterations, ThreadLocal<Random> threadrandom){

        List<Fluorophore> fluorophores = new ArrayList<>();
        for(String key:fpmap.keySet()){
            fluorophores.add(fpmap.get(key));
        }
        
        Collections.sort(fluorophores, (Fluorophore f1, Fluorophore f2) -> {
            return f1.name.compareTo(f2.name);
        });

        List<Detector> detectors = new ArrayList<>();
        Map<Detector,Laser> detectorMap = new HashMap<>();

        for(Laser l:cyto.lasers){
            for(Detector d:l.detectors){
                detectors.add(d);
                detectorMap.put(d,l);
            }
        }


        List<Fluorophore> selectedFluorophores = getRandomFluorophores(n,fluorophores,threadrandom);
        List<Detector> selectedDetectors = getRandomDetectors(n,detectors,threadrandom);
        List<SelectionInfo> bestSelection = HillClimbingSelection.getSelection(selectedFluorophores, selectedDetectors, detectorMap);
        SNR currentSNR = new SNR(bestSelection);    
        SNR bestSNR = new SNR(bestSelection);
        
       for(int i=0;i<iterations;i++){
            int index = Utilities.getRandom(0, n-1,threadrandom);
            List<Fluorophore> nextFluorophores = new ArrayList<>(selectedFluorophores);
            List<Detector> nextDetectors = new ArrayList<>(selectedDetectors);
            
            int coinFlip = Utilities.getRandom(0, 1,threadrandom);

            if(coinFlip == 0){
                //Heads: Swap a Fluorophore
                nextFluorophores = swapFluorophore(index,selectedFluorophores,fluorophores,threadrandom);
            } else {
                //Tails: Swap a Detector
                nextDetectors = swapDetector(index,selectedDetectors,detectors,threadrandom);
            }
            List<SelectionInfo> nextSelection = HillClimbingSelection.getSelection(nextFluorophores, nextDetectors, detectorMap);
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

        return bestSelection;    
    }

}