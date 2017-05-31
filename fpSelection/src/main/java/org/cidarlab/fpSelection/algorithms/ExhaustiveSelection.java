/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author Alex
 */
public class ExhaustiveSelection {
    
    public static LinkedList<int[]> filterCombinations;
    public static LinkedList<int[]> fluorophorePermutations;

    public static ArrayList<SelectionInfo> run(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer) throws IOException {
        
        //count fluorophores
        int numFluorophores = spectralMaps.size();
        
        //count filters
        int numFilters = 0;
        for (Laser laser : cytometer.lasers) {
            numFilters += laser.detectors.size();
        }
        
        //preprocess data structures
        
        //fluorophore index --> fluorophore object
        Fluorophore[] fluorophores = new Fluorophore[numFluorophores];      
        int fpi = 0;
        for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
            Fluorophore fluorophore = entry.getValue();
            fluorophores[fpi] = fluorophore;
            fpi++;
        }
        
        //filter index --> fluorophore index --> riemann sun
        double[][] filterSignal = new double[numFilters][numFluorophores];       
        //filter index --> laser
        Laser[] lasers = new Laser[numFilters]; //Check
        //filter index --> detector
        Detector[] detectors = new Detector[numFilters];
        int filterIndex = 0;
        for (Laser laser : cytometer.lasers) {
            for (Detector detector : laser.detectors) {
                lasers[filterIndex] = laser;
                detectors[filterIndex] = detector;
                int fluorophoreIndex = 0;
                for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
                    Fluorophore fluorophore = entry.getValue();
                    filterSignal[filterIndex][fluorophoreIndex] = fluorophore.express(laser, detector); //Incorporate brightness and laser
                    fluorophoreIndex++;
                }
                filterIndex++;
            }
        }
        
        //get all combinations of filters (order not important)
        filterCombinations = new LinkedList<>();
        int tempData[] = new int[n];
        getCombinations(tempData, 0, numFilters - 1, 0, n); 
        
        //get all permutations of fluorophores to match to filters (order is important)
        fluorophorePermutations = new LinkedList<>();
        tempData = new int[n];
        getPermutations(tempData, numFluorophores, n);
        
        //iterate through all possible combinations of filters/fluorophores
        double bestSignal = 0;
        int[] bestFilters = new int[n];
        int[] bestFluorophores = new int[n];
        int totalComputations = filterCombinations.size() * fluorophorePermutations.size();
        int onePercent = (int)(totalComputations * .01);
        int computationIndex = 0;
        int percent = 0;
        for (int[] filterCombo : filterCombinations)
        {
            for (int[] fluorophorePerm : fluorophorePermutations)
            {
                //if(++computationIndex % onePercent == 0) 
                    //System.out.println(++percent + " percent");
                double signal = 0;
                for (int i = 0; i < n; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        //desired signal
                        if (i == j) signal += filterSignal[filterCombo[i]][fluorophorePerm[j]];
                        //undesired noise
                        else signal -= filterSignal[filterCombo[i]][fluorophorePerm[j]];
                    }
                }
                if (signal > bestSignal)
                {
                    bestSignal = signal;
                    bestFilters = filterCombo;
                    bestFluorophores = fluorophorePerm;
                }
            }
        }     

        //prepare data for graphs
        ArrayList<SelectionInfo> selected = new ArrayList<>();
        for (int i = 0; i < n; i++)
        {
            SelectionInfo si = new SelectionInfo();
            si.selectedFluorophore = fluorophores[bestFluorophores[i]];
            si.selectedDetector = detectors[bestFilters[i]];
            si.selectedLaser = lasers[bestFilters[i]];
            selected.add(si);
        }
        
        ProteinSelector.calcSumSigNoise(selected);
        ProteinSelector.generateNoise(selected);
        
        return selected;
    }
    
    static void getCombinations(int data[], int start, int n, int index, int k) {
        if (index == k) {
            filterCombinations.add(data.clone());
            return;
        }
        for (int i = start; i <= n && n - i + 1 >= k - index; i++) {
            data[index] = i;
            getCombinations(data, i + 1, n, index + 1, k);
        }
    }
    static void getPermutations(int data[], int n, int k) {
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
