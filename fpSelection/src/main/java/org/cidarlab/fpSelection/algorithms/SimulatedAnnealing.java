/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cidarlab.fpSelection.dom.Cytometer;
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
public class SimulatedAnnealing {

    private static double temperature = 1000;
    private static double coolingRate = 0.003;
    private static int numberOfThreads = 100;

    public static ArrayList<SelectionInfo> run(int n, Map<String, Fluorophore> masterList, Cytometer cyto) {
        
        ArrayList<SelectionInfo> result = null;
        SimulatedAnnealing sa = new SimulatedAnnealing();
        List<Fluorophore> fps = new ArrayList<Fluorophore>();
        Map<Integer,Laser> lasers = new HashMap<Integer,Laser>();
        List<Detector> detectors = new ArrayList<Detector>();
        
        for(String name:masterList.keySet()){
            fps.add(masterList.get(name));
        }
        
        int count = 0;
        for(Laser l:cyto.lasers){
            for(Detector d:l.detectors){
                detectors.add(d);
                lasers.put(count, l);
                count++;
            }
        }
        
        try {
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            List<SimulatedAnnealingThread> threads = new ArrayList<SimulatedAnnealingThread>();
            for (int i = 0; i < numberOfThreads; i++) {

                SimulatedAnnealingThread t = new SimulatedAnnealingThread(temperature, coolingRate, i, latch, n, fps, lasers, detectors);
                t.start();
                threads.add(t);
            }
            latch.await();
            
            int index = 0;
            InfDouble maxSNR = threads.get(0).selectionSNR;
            for(int i=0;i<threads.size();i++){
//                System.out.println("From Thread " + t.threadID());
//                for(SelectionInfo si:t.getSelection()){
//                    System.out.println(si.getFP().name + "; " + si.selectedLaser.name + "; " + si.selectedDetector.identifier);
//                }
                SimulatedAnnealingThread t = threads.get(i);
                if(t.selectionSNR.compare(maxSNR) > 0){
                    index = i;
                }
                
            }
            
            result = threads.get(index).getSelection();
            ProteinSelector.calcSumSigNoise(result);
            for(SelectionInfo si:result){
                System.out.println(si.getFP().name);
                System.out.println("SNR      :: " + si.SNR);
                System.out.println("Laser    :: " + si.selectedLaser.name);
                System.out.println("Detector :: " + si.selectedDetector.identifier);
                System.out.println("--------------------------------------");
            }
            System.out.println("=====================================");
            System.out.println("Done");

        } catch (InterruptedException ex) {
            Logger.getLogger(SimulatedAnnealing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

}
