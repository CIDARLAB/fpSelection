/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

/**
 *
 * @author Alex
 */
public class WrappedFluorophore implements Comparable<WrappedFluorophore> {
    public int index;
    public Fluorophore fluorophore;
    public Laser laser;
    public Detector detector;
    public double riemannSum;
    
    @Override
    public int compareTo(WrappedFluorophore wf) {        
        return (int) (wf.riemannSum - this.riemannSum);
    }
}
