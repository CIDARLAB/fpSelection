/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author david
 */
public class Fluorophore {
    
    public String name;
    
    //Emission or Excitation
    
    public HashMap<Double, Double> EMspectrum;

    public HashMap<Double, Double> EXspectrum;
    
}
