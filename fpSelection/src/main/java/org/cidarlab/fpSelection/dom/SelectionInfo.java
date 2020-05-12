/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.Setter;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class SelectionInfo implements Comparable<SelectionInfo>{

    //Results
    
    @Getter
    @Setter
    private boolean signalZero = false;
    
    @Getter
    @Setter
    private boolean SNRlessThanOne = false;
    
//    @Getter
    @Setter
    private boolean noiseZero = false;
    
    //TODO: Remove, @djtran ran into lombok issues and needed the getter.
    public boolean getNoiseZero() {
        return noiseZero;
    }
    
    @Getter
    @Setter
    public Laser selectedLaser;
    
    @Getter
    @Setter
    public Detector selectedDetector;
    
    @Getter
    @Setter
    public Fluorophore selectedFluorophore;

    //For use in algorithms
    //public int selectedIndex;
    public double score;
    public double SNR;
    public double SNDiff;
    public double price;
    public int oligo;
    public TreeMap<Double, Double> noise;
    public Cytometer myFPCytometer;

    @Override
    public String toString(){
        String str = "";
        str += this.selectedFluorophore.name + " (";
        str += this.selectedLaser.getName() + " - ";
        str += this.selectedDetector.identifier + ") ";
        return str;
    }
    
    
    public static void printSelection(List<SelectionInfo> selection){
        for(SelectionInfo si:selection){
            System.out.println(si.toString());
        }
    }
    
    public SelectionInfo(){
        
    }
    
    public SelectionInfo(RankedInfo ri){
        
        this.score = ri.score;
        this.SNDiff = ri.SNDiff;
        this.SNR = ri.SNR;
        
        this.price = ri.price;
        this.oligo = ri.oligo;
        
        this.SNRlessThanOne = ri.isSNRlessThanOne();
        this.noiseZero = ri.isNoiseZero();
        this.signalZero = ri.isSignalZero();
        
        this.myFPCytometer = ri.myFPCytometer;
        this.selectedDetector = ri.selectedDetector;
        this.selectedLaser = ri.selectedLaser;
        this.selectedFluorophore = ri.rankedFluorophores.get(ri.selectedIndex);
        
    }
    
    @Override
    public int compareTo(SelectionInfo si) { 
        return (int) (si.selectedLaser.wavelength - this.selectedLaser.wavelength);
    }
    
    
    //Utility functions
    public PointDataSet makeNoiseDataSet()
    {
        PointDataSet dataSet = new PointDataSet();

        for (Map.Entry<Double, Double> entry : noise.entrySet()) {

            dataSet.add(new Point(entry.getKey(), entry.getValue()/100 ));
        }
        return dataSet;
    }
    
    public double expressNoise(Detector detector) {
        double sum = 0;
        double min = detector.filterMidpoint - detector.filterWidth / 2;
        double max = min + detector.filterWidth;
        
        //Get the least entry that has a key >= the parameter key or null if none exists.
        Map.Entry<Double, Double> previousEntry = noise.ceilingEntry(min);
        if (previousEntry == null) {
            //nothing to iterate through.
            return 0;
        }
        //Get the least entry that has a key > the parameter key or null if none exists.
        Map.Entry<Double, Double> startEntry = noise.higherEntry(previousEntry.getKey());
        if (startEntry == null) {
            //nothing to iterate through
            return 0;
        }
        
        for (Map.Entry<Double, Double> thisEntry : noise.tailMap(startEntry.getKey()).entrySet()) {
            double width = thisEntry.getKey() - previousEntry.getKey();
            double height = previousEntry.getValue();
            previousEntry = thisEntry;

            sum += width * height;

            if (thisEntry.getKey() >= max) {
                break;
            }
        }

        //Average it to 0-100 by dividing by range
        return sum / (detector.filterWidth);
    }
    
    public Fluorophore getFP()
    {
        return selectedFluorophore;
    }
    
}
