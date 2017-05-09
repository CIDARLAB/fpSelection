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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
public class ExhaustiveSelectionMultiThreaded {
    
    public LinkedList<int[]> filterCombinations;
    public LinkedList<int[]> fluorophorePermutations;
    

    public double[][] filterSignal;
    
    public volatile int computationIndex = 0;
    public int onePercent = 0;
    
    public synchronized void syncPercent() {
        if(++computationIndex % onePercent == 0) System.out.println(computationIndex/onePercent + " percent");
    }

    public ArrayList<SelectionInfo> run(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, int threads) throws IOException, InterruptedException, ExecutionException {
        
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
        onePercent = (int)(totalComputations * .01);
        
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        List<Future<BestResult>> resultList = new ArrayList<>();

        int chunkSize = filterCombinations.size() / threads;
        int start = 0;
        int finish = chunkSize;
        for(int i = 0; i < threads; i++) 
        {
            Future<BestResult> result = exec.submit(new SignalThread(start, finish, n));
            resultList.add(result);
            start += chunkSize;
            finish += chunkSize;
            if(i == (threads - 2)) finish = filterCombinations.size();
        }    
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        
        for (Future<BestResult> result : resultList)
        {
            BestResult br = result.get();
            if (br.bestSignal > bestSignal)
            {
                bestSignal = br.bestSignal;
                bestFilters = br.bestFilters;
                bestFluorophores = br.bestFluorophores;
            }
        }
            

        //prepare data for graphs
        ArrayList<SelectionInfo> selected = new ArrayList<>();
        for (int i = 0; i < n; i++)
        {
            SelectionInfo si = new SelectionInfo();
            si.rankedFluorophores = new ArrayList<>();
            si.rankedFluorophores.add(fluorophores[bestFluorophores[i]]);
            si.selectedIndex = 0;
            si.selectedDetector = detectors[bestFilters[i]];
            si.selectedLaser = lasers[bestFilters[i]];
            selected.add(si);
        }
        
        ProteinSelector.calcSumSigNoise(selected);
        ProteinSelector.generateNoise(selected);
        
        return selected;
    }
    
    public void getCombinations(int data[], int start, int n, int index, int k) {
        if (index == k) {
            filterCombinations.add(data.clone());
            return;
        }
        for (int i = start; i <= n && n - i + 1 >= k - index; i++) {
            data[index] = i;
            getCombinations(data, i + 1, n, index + 1, k);
        }
    }
    public void getPermutations(int data[], int n, int k) {
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
    
    class SignalThread implements Callable<BestResult> {
        
        int start;
        int finish;
        int n;
   
        SignalThread(int start, int finish, int n)
        {
            this.start = start;
            this.finish = finish;
            this.n = n;
        }
        
        @Override
        public BestResult call() {
            BestResult bestResult = new BestResult(n);
            for (int i = start; i < finish; i++)
            {
                int[] filterCombo = filterCombinations.get(i);
                for (int[] fluorophorePerm : fluorophorePermutations)
                {
                    //syncPercent();
                    double signal = 0;
                    for (int j = 0; j < n; j++)
                    {
                        for (int k = 0; k < n; k++)
                        {
                            //desired signal
                            if (j == k) signal += filterSignal[filterCombo[j]][fluorophorePerm[k]];
                            //undesired noise
                            else signal -= filterSignal[filterCombo[j]][fluorophorePerm[k]];
                        }
                    }
                    if (signal > bestResult.bestSignal)
                    {
                        bestResult.bestSignal = signal;
                        bestResult.bestFilters = filterCombo;
                        bestResult.bestFluorophores = fluorophorePerm;
                    }
                }
            }
            return bestResult;
        }
    }
    class BestResult
    {
        double bestSignal;
        int[] bestFilters;
        int[] bestFluorophores;

        public BestResult(int n) 
        {
            bestSignal = 0;
            bestFilters = new int[n];
            bestFluorophores = new int[n];
        }       
    }
}
