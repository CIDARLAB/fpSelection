/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import org.cidarlab.fpSelection.Utilities;
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
public class SimulatedAnnealingThread extends Thread {

    private Thread t;

    private final int id;

    private double temp;
    private final double rate;
    private int n;
    private CountDownLatch latch;

    private List<Fluorophore> fluorophores;
    private Map<Detector, Laser> detectorMap;
    private List<Detector> detectors;

    List<Integer> selectedFPs;
    List<Integer> selectedDs;

    @Getter
    private SNR selectionSNR;

    @Getter
    private List<SelectionInfo> selection;

    SimulatedAnnealingThread(double _temp, double _rate, int _id, CountDownLatch _latch, int _n, List<Fluorophore> _fluorophores, List<Detector> _detectors, Map<Detector, Laser> _detectorMap) {
        temp = _temp;
        rate = _rate;
        id = _id;
        n = _n;
        fluorophores = _fluorophores;
        detectorMap = _detectorMap;
        detectors = _detectors;
        latch = _latch;
        selection = new ArrayList<SelectionInfo>();
        selectedFPs = new ArrayList<Integer>();
        selectedDs = new ArrayList<Integer>();
    }

    @Override
    public void run() {
        List<Fluorophore> currentFluorophores = HillClimbingSelection.getRandomFluorophores(n, fluorophores);
        List<Detector> currentDetectors = HillClimbingSelection.getRandomDetectors(n, detectors);

        List<SelectionInfo> current = HillClimbingSelection.getSelection(currentFluorophores, currentDetectors, detectorMap);
        SNR bestSNR = new SNR(current);

        List<SelectionInfo> best = current;
        SNR currentSNR = bestSNR;

        while (temp > 1) {

            int swapIndex = Utilities.getRandom(0, n - 1);

            List<Fluorophore> nextFluorophores;
            List<Detector> nextDetectors;

            if (Utilities.getRandom(0, 1) == 0) {
                //Heads Swap an FP
                nextFluorophores = swapFluorophore(swapIndex, currentFluorophores, fluorophores);
                nextDetectors = new ArrayList<>(currentDetectors);
            } else {
                //Tails Swap Detector
                nextFluorophores = new ArrayList<>(currentFluorophores);
                nextDetectors = swapDetector(swapIndex, currentDetectors, detectors);
            }
            List<SelectionInfo> next = HillClimbingSelection.getSelection(nextFluorophores, nextDetectors, detectorMap);
            SNR nextSNR = new SNR(next);

            if (nextSNR.greaterThan(bestSNR)) {
                bestSNR = nextSNR;
                best = new ArrayList<>(next);
            }
            double random = Math.random();
            double ap = acceptanceProbability(currentSNR,nextSNR,temp);
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

        this.selection = best;
        ProteinSelector.generateNoise(selection);
        this.selectionSNR = new SNR(selection);
        latch.countDown();

    }

    private static List<Fluorophore> swapFluorophore(int index, List<Fluorophore> selected, List<Fluorophore> all) {
        List<Fluorophore> newList = new ArrayList<>();
        Fluorophore newFP = all.get(Utilities.getRandom(0, all.size() - 1));
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

    private static List<Detector> swapDetector(int index, List<Detector> selected, List<Detector> all) {
        List<Detector> newList = new ArrayList<>();
        Detector newD = all.get(Utilities.getRandom(0, all.size() - 1));
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

    public static double acceptanceProbability(SNR current, SNR next, double temp) {

        if (next.greaterThan(current)) {
            //System.out.println("Next SNR Greater. Acceptance Probability = 1");
            return 1;
        }
        double currentSignal = 0;
        double currentNoise = 0;
        double nextSignal = 0;
        double nextNoise = 0;
        int size = current.getSignalNoiseList().size();
        for(int i=0;i<size;i++){
            currentSignal += current.getSignalNoiseList().get(i).getKey();
            currentNoise += current.getSignalNoiseList().get(i).getValue();
            nextSignal += next.getSignalNoiseList().get(i).getKey();
            nextNoise += next.getSignalNoiseList().get(i).getValue();
        }
        
        if(currentSignal == nextSignal){
            if(nextSignal == nextNoise){
                return 0.5;
            }
        } 
        
        double deltaE = 0;
        if(currentSignal != nextSignal){
            deltaE = nextSignal - currentSignal;
        } else {
            deltaE = currentNoise - nextNoise;
        }
        
        
        //double deltaE = next.getSnr() - current.getSnr();
        double exp = (-deltaE) / (temp);
        double prob = 1.0 / (1.0 + (Math.pow(Math.E, exp)));
        //System.out.println("Next SNR = " + next.getSnr() + ":: Current SNR = " + current.getSnr() + ":: Acceptance Probability = " + prob);
        return prob;
    }

    public int threadID() {
        return this.id;
    }

    @Override
    public void start() {
        //System.out.println("Starting thread " + this.id);
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    private static boolean hasZeroSignal(ArrayList<SelectionInfo> selection) {
        for (SelectionInfo si : selection) {
            if (si.isSignalZero()) {
                return true;
            }
        }

        return false;
    }

}
