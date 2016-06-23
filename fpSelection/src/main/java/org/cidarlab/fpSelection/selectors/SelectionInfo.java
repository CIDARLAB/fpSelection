/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class SelectionInfo implements Comparable<SelectionInfo>{

    public Laser selectedLaser;
    public Detector selectedDetector;
    public ArrayList<Fluorophore> rankedFluorophores;

    public int selectedIndex;
    public double score;
    public double SNR;
    public double SNDiff;
    
    public TreeMap<Double, Double> noise;
    
    public PointDataSet makeDataSet()
    {
        PointDataSet dataSet = new PointDataSet();

        for (Map.Entry<Double, Double> entry : noise.entrySet()) {

            dataSet.add(new Point(entry.getKey(), entry.getValue()));
        }
        return dataSet;
    }
    public Fluorophore getFP()
    {
        return rankedFluorophores.get(selectedIndex);
    }
    public Fluorophore getFP(int index)
    {
        return rankedFluorophores.get(index);
    }
    
    @Override
    public int compareTo(SelectionInfo si) { 
        return (int) (si.selectedLaser.wavelength - this.selectedLaser.wavelength);
    }
}
