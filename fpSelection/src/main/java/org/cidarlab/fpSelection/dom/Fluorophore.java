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
    
    @Getter
    @Setter
    private String name;
    
    //Emission or Excitation
    
    @Getter
    @Setter
    private HashMap<Integer, Float> EMspectrum;
    
    @Getter
    @Setter
    private HashMap<Integer, Float> EXspectrum;
    
}
