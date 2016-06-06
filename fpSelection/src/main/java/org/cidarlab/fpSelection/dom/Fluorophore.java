/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.swing.JPlot;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author david
 */
public class Fluorophore {
    
    @Getter
    @Setter
    private String name;
    
    //Emission or Excitation
    public PointDataSet EMspectrum;

    public PointDataSet EXspectrum;
    
    public double express(Laser theLaser, Detector theDetector)
    {
        double sum = 0;
        int min = theDetector.getFilterMidpoint() - theDetector.getFilterWidth()/2;
        int max = min + theDetector.getFilterWidth();
        
        for(int i = min; i <= max; i++)
        {
            sum += Double.parseDouble(EMspectrum.getPointValue(i, 1));
            System.out.println(i + " : " + EMspectrum.getPointValue(i, 1));
            
        }
        
        return sum;
    }
    
}
