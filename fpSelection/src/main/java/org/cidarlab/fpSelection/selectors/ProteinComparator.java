/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.util.ArrayList;
import java.util.Comparator;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class ProteinComparator implements Comparator<Fluorophore>{
    
    //Populate these fields before use!!!
    public Laser laser;
    public Detector detect;
    
    @Override
    public int compare(Fluorophore o1, Fluorophore o2) {
        double num1 = o1.express(laser, detect);
        double num2 = o2.express(laser, detect);
        
        //return pos, 0, or neg
        //Larger priority goes to front of q
        return (int)(num2 - num1);
    }
  
}
