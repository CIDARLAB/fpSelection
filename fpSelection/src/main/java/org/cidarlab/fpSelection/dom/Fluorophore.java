/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author david
 */
public class Fluorophore {

public String name;

    public boolean isProtein = false;

    //Emission or Excitation 
    public TreeMap<Double, Double> EMspectrum;
    public TreeMap<Double, Double> EXspectrum;

    //Generates PointDataSets for javaplot graphing
    public PointDataSet makeEXDataSet() {
        PointDataSet dataSet = new PointDataSet();

        for (Map.Entry<Double, Double> entry : EXspectrum.entrySet()) {

            dataSet.add(new Point(entry.getKey(), entry.getValue()));
        }
        return dataSet;
    }

    public PointDataSet makeEMDataSet(Laser aLaser) {
        if (!EXspectrum.containsKey((double) aLaser.wavelength)) { 
            PointDataSet dataSet = new PointDataSet();

            for (Map.Entry<Double, Double> entry : EMspectrum.entrySet()) {

                dataSet.add(new Point(entry.getKey(), 0));
            }
            return dataSet;

        }
        double multiplier = EXspectrum.get((double) aLaser.wavelength);
        PointDataSet dataSet = new PointDataSet();

        for (Map.Entry<Double, Double> entry : EMspectrum.entrySet()) {

            dataSet.add(new Point(entry.getKey(), multiplier / 100 * entry.getValue()));
        }
        return dataSet;
    }

    //Produces an averaged left Riemann sum of emission values within a certain range of the spectrum.
    public double express(Laser theLaser, Detector theDetector) {

        if (!EXspectrum.containsKey((double)theLaser.wavelength)) {
            return 0;
        }
        double multiplier = EXspectrum.get((double)theLaser.wavelength) / 100;
        double sum = 0;
        double min = theDetector.filterMidpoint - theDetector.filterWidth / 2;
        double max = min + theDetector.filterWidth;

        //Get the least entry that has a key >= the parameter key or null if none exists.
        Map.Entry<Double, Double> previousEntry = EMspectrum.ceilingEntry(min);
        if(previousEntry == null)
        {
            //nothing to iterate through.
            return 0;
        }
        //Get the least entry that has a key > the parameter key or null if none exists.
        Map.Entry<Double, Double> startEntry = EMspectrum.higherEntry(previousEntry.getKey());
        if(startEntry == null)
        {
            //nothing to iterate through
            return 0;
        }
        
        for (Map.Entry<Double, Double> thisEntry : EMspectrum.tailMap(startEntry.getKey()).entrySet()) {
            double width = thisEntry.getKey() - previousEntry.getKey();
            double height = previousEntry.getValue() * multiplier;
            previousEntry = thisEntry;
            
            sum += width * height;

            if (thisEntry.getKey() >= max) {
                break;
            }
        }

        //Average it to 0-100 by dividing by range

        return sum / (theDetector.filterWidth);

    }

    //Calculates the percentage of fluorescence generated outside of the filter desired.
    public double leakageCalc(Detector theDetector) {
        double total = 0;
        double sumOutside = 0;
        int min = theDetector.filterMidpoint - theDetector.filterWidth / 2;
        int max = theDetector.filterWidth + min;

        for (Map.Entry<Double, Double> entry : EMspectrum.entrySet()) {
            //If within bounds, do nothing
            if (entry.getKey() >= min && entry.getKey() <= max) {
            } else //If not within bounds, add to sumOutside
            {
                sumOutside += entry.getValue();
            }
            //Get total sum for later comparison
            total += entry.getValue();

        }

        //Push it up to [0-100] range for comparison w/ brightness.
        return sumOutside / total * 100;
    }
    
    public double EXPeak()
    {
        double highestPeak = 0;
        double peakPoint = 0;
        for(Map.Entry<Double, Double> point : EXspectrum.entrySet())
        {
            if(point.getValue() > highestPeak)
            {
                highestPeak = point.getValue();
                peakPoint = point.getKey();
            }
        }
        return peakPoint;
    }

}
