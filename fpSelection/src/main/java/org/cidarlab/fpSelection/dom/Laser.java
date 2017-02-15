/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import java.util.LinkedList;

/**
 *
 * @author david
 */
public class Laser {
    
public String name;
    
public String type;
    
public int wavelength;
    
public double power;
    
public String detectorArray;
    
public int position;

public LinkedList<Detector> detectors;
    
    //Default initialization
    public Laser()
    {
        type = null;
        wavelength = 0;
        power = 0;
        detectorArray = null;
        detectors = new LinkedList<>();
    }
   
    
    //Hooray more detectors! I thought this might be easier than gathering them all at once and then setting the whole list
    public void addDetector(Detector newDetector)
    {
        detectors.add(newDetector);
        return;
    }    
}
