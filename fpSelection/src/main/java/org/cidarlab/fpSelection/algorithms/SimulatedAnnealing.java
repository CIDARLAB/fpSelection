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
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author prash
 */
public class SimulatedAnnealing {

    private static double temperature = 10000;
    private static double coolingRate = 0.001;
    private static int numberOfThreads = 1;

    public static List<SelectionInfo> run(int n, Map<String, Fluorophore> masterList, Cytometer cyto) {
        
        List<SelectionInfo> result = null;
        SimulatedAnnealing sa = new SimulatedAnnealing();
        List<Fluorophore> fps = new ArrayList<Fluorophore>();
        Map<Detector,Laser> detectorMap = new HashMap<Detector,Laser>();
        List<Detector> detectors = new ArrayList<Detector>();
        
        for(String name:masterList.keySet()){
            fps.add(masterList.get(name));
        }
        
        int count = 0;
        for(Laser l:cyto.lasers){
            for(Detector d:l.detectors){
                detectors.add(d);
                detectorMap.put(d, l);
                count++;
            }
        }
        
        try {
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            List<SimulatedAnnealingThread> threads = new ArrayList<SimulatedAnnealingThread>();
            for (int i = 0; i < numberOfThreads; i++) {

                SimulatedAnnealingThread t = new SimulatedAnnealingThread(temperature, coolingRate, i, latch, n, fps, detectors, detectorMap);
                t.start();
                threads.add(t);
            }
            latch.await();
            
            int index = 0;
            SNR maxSNR = threads.get(0).getSelectionSNR();
            for(int i=0;i<threads.size();i++){
                SimulatedAnnealingThread t = threads.get(i);
                if(t.getSelectionSNR().greaterThan(maxSNR)){
                    maxSNR = t.getSelectionSNR();
                    index = i;
                }                
            }
            
            result = threads.get(index).getSelection();
            ProteinSelector.generateNoise(result);
            /*
            for(SelectionInfo si:result){
                System.out.println(si.getFP().name);
                System.out.println("Laser    :: " + si.selectedLaser.getName());
                System.out.println("Detector :: " + si.selectedDetector.identifier);
                System.out.println("--------------------------------------");
            }
            System.out.println("=====================================");
            System.out.println("Done");
            */
        } catch (InterruptedException ex) {
            Logger.getLogger(SimulatedAnnealing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

}
