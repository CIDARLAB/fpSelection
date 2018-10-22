/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import java.util.ArrayList;
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
    
    @Getter
    @Setter
    private boolean noiseZero = false;
    
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
        str += "Fluorophore: " + this.selectedFluorophore.name + " :: ";
        str += "Laser: " + this.selectedLaser.getName() + " :: ";
        str += "Detector: " + this.selectedDetector.identifier + ".";
        return str;
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

            dataSet.add(new Point(entry.getKey(), entry.getValue()));
        }
        return dataSet;
    }
    
    public Fluorophore getFP()
    {
        return selectedFluorophore;
    }
    
}
