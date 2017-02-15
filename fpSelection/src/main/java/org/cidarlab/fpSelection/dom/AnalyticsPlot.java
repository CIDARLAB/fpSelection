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
    public List<Point> points;
    
    @Getter
    @Setter
    public String xlabel;
    
    @Getter
    @Setter
    public String ylabel;
    
    @Getter
    @Setter
    public String plotlabel;
    
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
