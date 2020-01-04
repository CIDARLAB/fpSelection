/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author Alex
 */
public class ExhaustiveSelection {
    
    public static LinkedList<int[]> filterCombinations;
    public static LinkedList<int[]> fluorophorePermutations;
    
    public static List<SelectionInfo> getSelection(int n, int[] fluorophorePerm, int[] filterCombo, Fluorophore[] fluorophores, Laser[] lasers, Detector[] detectors){
        List<SelectionInfo> selection = new ArrayList<>();
        for(int i=0;i<n;i++){
            SelectionInfo si = new SelectionInfo();
            si.setSelectedFluorophore(fluorophores[fluorophorePerm[i]]);
            si.setSelectedLaser(lasers[filterCombo[i]]);
            si.setSelectedDetector(detectors[filterCombo[i]]);
            selection.add(si);
        }
        return selection;
    }
    
    public static List<SelectionInfo> run(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer) throws IOException {
        
        //count fluorophores
        int numFluorophores = spectralMaps.size();
        
        //count filters
        int numFilters = 0;
        for (Laser laser : cytometer.lasers) {
            numFilters += laser.detectors.size();
        }
        
        //fluorophore index --> fluorophore object
        Fluorophore[] fluorophores = new Fluorophore[numFluorophores];      
        int fpi = 0;
        for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
            Fluorophore fluorophore = entry.getValue();
            fluorophores[fpi] = fluorophore;
            fpi++;
        }
        
        Laser[] lasers = new Laser[numFilters]; 
        Detector[] detectors = new Detector[numFilters];
        int filterIndex = 0;
        for (Laser laser : cytometer.lasers) {
            for (Detector detector : laser.detectors) {
                lasers[filterIndex] = laser;
                detectors[filterIndex] = detector;
                
                filterIndex++;
            }
        }
        
        //get all combinations of filters (order not important)
        filterCombinations=  new LinkedList<>();
        int tempData[] = new int[n];
        getCombinations(tempData, 0, numFilters - 1, 0, n); 
        
        //get all permutations of fluorophores to match to filters (order is important)
        fluorophorePermutations = new LinkedList<>();
        tempData = new int[n];
        getPermutations(tempData, numFluorophores, n);
        
        //iterate through all possible combinations of filters/fluorophores
        long totalComputations = filterCombinations.size() * fluorophorePermutations.size();
        //System.out.println("Filter Combinations :: " + filterCombinations.size());
        //System.out.println("FP Permutations     :: " + fluorophorePermutations.size());
        System.out.println("Total Computations : " + totalComputations);
        long onePercent = (long)(totalComputations * .01);
        long computationIndex = 0;
        int percent = 0;
        
        List<SelectionInfo> bestSelection = getSelection(n,fluorophorePermutations.get(0),filterCombinations.get(0),fluorophores,lasers,detectors);
        SNR bestSNR = new SNR(bestSelection);
        for (int[] filterCombo : filterCombinations)
        {
            for (int[] fluorophorePerm : fluorophorePermutations)
            {
                //if(++computationIndex % onePercent == 0) 
                //    System.out.println(++percent + " percent");
                double signal = 0;
                List<SelectionInfo> currentSelection = getSelection(n,fluorophorePerm,filterCombo,fluorophores,lasers,detectors);
                SNR snr = new SNR(currentSelection);
                if(snr.greaterThan(bestSNR)){
                    bestSNR = snr;
                    bestSelection = new ArrayList<>(currentSelection);
                }
            }
        }     
        
        ProteinSelector.generateNoise(bestSelection);
        
        return bestSelection;
    }
    
    public static void getCombinations(int data[], int start, int n, int index, int k) {
        if (index == k) {
            filterCombinations.add(data.clone());
            return;
        }
        for (int i = start; i <= n && n - i + 1 >= k - index; i++) {
            data[index] = i;
            getCombinations(data, i + 1, n, index + 1, k);
        }
    }
    public static void getPermutations(int data[], int n, int k) {
        if (k == 0) {
            fluorophorePermutations.add(data.clone());
            return;
        }      
        outerloop:
        for (int i = 0; i < n; ++i) {
            for (int j = data.length-1; j >= k; j--) {
                if (data[j] == i) continue outerloop;
            }
            data[k - 1] = i;
            getPermutations(data, n, k - 1);
        }
    }
}
