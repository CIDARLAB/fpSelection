/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JOptionPane;
import static org.cidarlab.fpSelection.adaptors.ScrapedCSVParse.generateFPs;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import static org.cidarlab.fpSelection.adaptors.fpSelectionAdaptor.uploadFluorescenceSpectrums;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.selectors.SelectionInfo;

/**
 *
 * @author Alex
 */
public class ExhaustiveSelectionTestImproved {

    public static LinkedList<int[]> filterCombinations;
    public static LinkedList<int[]> fluorophorePermutations;
    public static int numFluorophores;
    public static int numFilters;
    public static double bestSignal;
    public static int[] bestFilters;
    public static int[] bestFluorophores;
    public static int bigN;
    public static double[][] filterSignal;

    public static void main(String[] args) throws IOException {
        
        //Get fluorophore set
        //File input = new File("src/main/resources/fp_spectra.csv");
        //HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);
        File input = new File("src/main/resources/Fluorophores.org/");
        HashMap<String, Fluorophore> spectralMaps = generateFPs(input);

        //Get cytometer settings
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer cytometer = fpFortessaParse.parseFortessa(cyto);

        //User input number of FPs
        //String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        //int n = Integer.parseInt(numString);
        bigN = 3;

        //count fluorophores
        numFluorophores = spectralMaps.size();
        
        //count filters
        numFilters = 0;
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
        filterSignal = new double[numFilters][numFluorophores];       
        //filter index --> laser
        Laser[] lasers = new Laser[numFilters];
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
                    filterSignal[filterIndex][fluorophoreIndex] = fluorophore.express(laser, detector);
                    fluorophoreIndex++;
                }
                filterIndex++;
            }
        }
        
        //get all filter/fluorophore combinations and find best
        filterCombinations = new LinkedList<>();
        int tempData[] = new int[bigN];
        getCombinations(tempData, 0, numFilters - 1, 0, bigN);   

        //prepare data for graphs
        ArrayList<SelectionInfo> selected = new ArrayList<>();
        for (int i = 0; i < bigN; i++)
        {
            SelectionInfo si = new SelectionInfo();
            si.rankedFluorophores = new ArrayList<>();
            si.rankedFluorophores.add(fluorophores[bestFluorophores[i]]);
            si.selectedIndex = 0;
            si.selectedDetector = detectors[bestFilters[i]];
            si.selectedLaser = lasers[bestFilters[i]];
            selected.add(si);
        }
        ProteinSelector.calcSumSNR(selected);
        ProteinSelector.generateNoise(selected);
        
        ProteinSelector.plotSelection(selected);
    }
    
    static void getCombinations(int data[], int start, int n, int index, int k) {
        if (index == k) {
            getPermutations(new int[bigN], numFluorophores, bigN, data);
            return;
        }
        for (int i = start; i <= n && n - i + 1 >= k - index; i++) {
            data[index] = i;
            getCombinations(data, i + 1, n, index + 1, k);
        }
    }
    static void getPermutations(int [] perm, int n, int k, int [] combo) {
        if (k == 0) {
            double signal = 0;
            outerloop:
            for (int i = 0; i < bigN; i++)
            {
                if (filterSignal[combo[i]][perm[i]] <= 0) return;
                for (int j = 0; j < bigN; j++)
                {
                    //desired signal
                    if (i == j) signal += filterSignal[combo[i]][perm[j]];
                    //undesired noise
                    else signal -= filterSignal[combo[i]][perm[j]];
                }
            }
            if (signal > bestSignal)
            {
                bestSignal = signal;
                bestFilters = combo.clone();
                bestFluorophores = perm.clone();
            }
            return;
        }      
        outerloop:
        for (int i = 0; i < n; ++i) {
            for (int j = perm.length-1; j >= k; j--) {
                if (perm[j] == i) continue outerloop;
            }
            perm[k - 1] = i;
            getPermutations(perm, n, k - 1, combo);
        }
    }
}
