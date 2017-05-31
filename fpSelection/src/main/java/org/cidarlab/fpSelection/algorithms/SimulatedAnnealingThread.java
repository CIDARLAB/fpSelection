/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.InfDouble;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author prash
 */
public class SimulatedAnnealingThread extends Thread{
        
    
        private Thread t;
        
        private final int id;
        
        private double temp;
        private final double rate;
        private int n;
        private CountDownLatch latch;
        
        private List<Fluorophore> fps;
        private Map<Integer,Laser> lasers;
        private List<Detector> detectors;
        
        List<Integer> selectedFPs;
        List<Integer> selectedDs;
        
        InfDouble.InfDoubleMode mode = InfDouble.InfDoubleMode.multiply;
        
        @Getter
        InfDouble selectionSNR;
        
        @Getter
        private ArrayList<SelectionInfo> selection;
             
        
        SimulatedAnnealingThread(double _temp, double _rate, int _id, CountDownLatch _latch, int _n, List<Fluorophore> _fps, Map<Integer,Laser> _lasers, List<Detector> _detectors){
            temp = _temp;
            rate = _rate;
            id = _id;
            n = _n;
            fps = _fps;
            lasers = _lasers;
            detectors = _detectors;
            latch = _latch;
            selection = new ArrayList<SelectionInfo>();
            selectedFPs = new ArrayList<Integer>();
            selectedDs = new ArrayList<Integer>();
            selectionSNR = new InfDouble(mode);
        }
        
        @Override
        public void run() {
            
            ArrayList<SelectionInfo> current =  startingAssignment();
            ArrayList<SelectionInfo> best =  current;
            
            ProteinSelector.calcSumSigNoise(current);
            
            InfDouble currentSNR = selectionSNR(current);
            InfDouble bestSNR = currentSNR;
            
            while(temp > 1){
                
                ArrayList<SelectionInfo> next = new ArrayList<SelectionInfo>();
                int swapIndex = random(0,n-1);
                
                
                if(random(0,1) == 0){
                    //Swap detector
                   int newD = random(0,detectors.size()-1);
                   while(selectedDs.contains(newD)){
                       newD = random(0,detectors.size()-1);
                   }
                   
                   //Create new solution
                   for(int i=0;i<current.size();i++){
                       if(i == swapIndex){
                           SelectionInfo newSI = new SelectionInfo();
                           newSI.selectedFluorophore = new ArrayList<Fluorophore>();
                           newSI.selectedIndex = 0;
                           newSI.selectedFluorophore.add(current.get(i).getFP());
                           newSI.selectedLaser = lasers.get(newD);
                           newSI.selectedDetector = detectors.get(newD);
                           next.add(newSI);
                       } else {
                           next.add(current.get(i));
                       }
                   }
                   ProteinSelector.calcSumSigNoise(current);
                   ProteinSelector.calcSumSigNoise(next);
                   currentSNR = selectionSNR(current);
                   InfDouble nextSNR = selectionSNR(next);
                   
                    if (!hasZeroSignal(next)) {
                        if (acceptProbability(currentSNR, nextSNR, temp) > Math.random()) {
                            current = new ArrayList<SelectionInfo>();
                            current.addAll(next);
                            selectedDs.set(swapIndex, newD);
                        }
                    }
                   
                } else {
                    //Swap FP
                    int newFP = random(0, fps.size() - 1);
                    while (selectedFPs.contains(newFP)) {
                        newFP = random(0, fps.size() - 1);
                    }
                    
                    //Create new solution
                   for(int i=0;i<current.size();i++){
                       if(i == swapIndex){
                           SelectionInfo newSI = new SelectionInfo();
                           newSI.selectedFluorophore = new ArrayList<Fluorophore>();
                           newSI.selectedIndex = 0;
                           newSI.selectedFluorophore.add(fps.get(newFP));
                           newSI.selectedLaser = current.get(i).selectedLaser;
                           newSI.selectedDetector = current.get(i).selectedDetector;
                           next.add(newSI);
                       } else {
                           next.add(current.get(i));
                       }
                   }
                    
                    
                   ProteinSelector.calcSumSigNoise(current);
                   ProteinSelector.calcSumSigNoise(next);
                   currentSNR = selectionSNR(current);
                   InfDouble nextSNR = selectionSNR(next);
                   
                    if (!hasZeroSignal(next)) {
                        if (acceptProbability(currentSNR, nextSNR, temp) > Math.random()) {
                            current = new ArrayList<SelectionInfo>();
                            current.addAll(next);
                            selectedFPs.set(swapIndex, newFP);
                        }
                    } 
                }
                
                if(currentSNR.compare(bestSNR) > 0){
                    best = new ArrayList<SelectionInfo>();
                    best.addAll(current);
                    ProteinSelector.calcSumSigNoise(best);
                    bestSNR = selectionSNR(best);
                }
                
                //System.out.println("Thread " + id + " current temperature = " + temp);
                temp *= (1-rate);
            }
            
            this.selection = best;
            ProteinSelector.calcSumSigNoise(selection);
            this.selectionSNR = selectionSNR(selection);
            latch.countDown();
            
        }
        
        public InfDouble selectionSNR(ArrayList<SelectionInfo> coll){
            if(mode.equals(InfDouble.InfDoubleMode.add)){
                return ProteinSelector.totalSNR(coll);
            } else {
                return ProteinSelector.prodSNR(coll);
            }
        }
        
        public static double acceptProbability(InfDouble current, InfDouble next, double temp){
        
            if(next.compare(current) > 0){
                return 1;
            } 
            if(current.getSnr() < next.getSnr()){
                return Math.exp((current.getSnr() - next.getSnr())/temp);
            }
            return Math.exp((next.getSnr() - current.getSnr())/temp);
        }
        
        
        public int threadID(){
            return this.id;
        }
        
        public int random(int start, int end){
            return ThreadLocalRandom.current().nextInt(start, end+1);
        }
        
        private ArrayList<SelectionInfo>  startingAssignment(){
            ArrayList<SelectionInfo> current = new ArrayList<SelectionInfo>();
            
            for(int i=0;i<n;i++){
                
                SelectionInfo si = new SelectionInfo();
                int fpIndx = random(0,fps.size()-1);
                while(selectedFPs.contains(fpIndx)){
                    fpIndx = random(0,fps.size()-1);
                }
                selectedFPs.add(fpIndx);
                
                si.selectedFluorophore = new ArrayList<Fluorophore>();
                si.selectedFluorophore.add(fps.get(fpIndx));
                si.selectedIndex = 0;
                
                int dIndx = random(0,detectors.size()-1);
                while(selectedDs.contains(dIndx)){
                    dIndx = random(0,detectors.size()-1);
                }
                selectedDs.add(dIndx);
                si.selectedDetector = detectors.get(dIndx);
                si.selectedLaser = lasers.get(dIndx);
                current.add(si);
            }
            return current;
        }
        
        @Override
        public void start(){
            //System.out.println("Starting thread " + this.id);
            if( t == null){
                t = new Thread(this);
                t.start();
            }
        }
        
        private static boolean hasZeroSignal(ArrayList<SelectionInfo> selection){
            for(SelectionInfo si:selection){
                if(si.isSignalZero()){
                    return true;
                }
            }
            
            return false;
        }
        
    }
