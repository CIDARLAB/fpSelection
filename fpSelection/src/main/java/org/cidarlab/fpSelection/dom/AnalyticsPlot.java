/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import com.panayotis.gnuplot.dataset.Point;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author prash
 */
public class AnalyticsPlot {
    
    @Getter
    @Setter
    private List<Point> points;
    
    @Getter
    @Setter
    private String xlabel;
    
    @Getter
    @Setter
    private String ylabel;
    
    @Getter
    @Setter
    private String plotlabel;
    
    @Getter
    @Setter
    private int laserWavelength;
    
    @Getter
    @Setter
    private String fpname;
    
    public AnalyticsPlot(){
        points = new ArrayList<Point>();
    }
    
    
    public void addPoint(Point p){
        points.add(p);
    }
    
    public void addPoints(List<Point> _points){
        points.addAll(_points);
    }
}
