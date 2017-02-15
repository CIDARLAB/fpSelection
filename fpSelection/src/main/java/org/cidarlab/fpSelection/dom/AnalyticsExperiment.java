/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author prash
 */
public class AnalyticsExperiment {
    
    @Getter
    @Setter
    private ExperimentType type;
    
    @Getter
    @Setter
    private Laser laser;
    
    @Getter
    @Setter
    private double actualLaserPower;
    
    @Getter
    @Setter
    private double attemptedLaserPower;
    
    @Getter
    @Setter
    private List<CompensationMatrix> matrix = new ArrayList<CompensationMatrix>(); 
    
    @Getter
    @Setter
    private List<OneMedia> oneMediaValues = new ArrayList<OneMedia>();
    
    public void addOneMediaValue(String _part, double _value){
        oneMediaValues.add(new OneMedia(_part, _value));
    }
    
    public void addOneMediaValue(String _part, String _filter, double _value){
        oneMediaValues.add(new OneMedia(_part, _filter, _value));
    }
    
    public void addCompensationMatrixValue(String _measuredFilter, String _bleedOverFilter, double _value){
        matrix.add(new CompensationMatrix(_measuredFilter,_bleedOverFilter,_value));
    }
    
    public int getLaserWavelength(){
        return laser.wavelength;
    }
    
    public void setLaserWavelength(int wavelength){
        laser.wavelength = wavelength;
    }
    
    public void setMaxLaserPower(double power){
        laser.power = power;
    }
    
    public double getMaxLaserPower(){
        return laser.power;
    }
    
    public enum ExperimentType{
        ecoli,
        beads,
        oneMedia
    }
    
    public class CompensationMatrix{
        public String measuredFilter;
        public String bleedOverFilter;
        public double value;
        
        public CompensationMatrix(String _measuredFilter, String _bleedOverFilter, double _value){
            measuredFilter = _measuredFilter;
            bleedOverFilter = _bleedOverFilter;
            value = _value;
        }
        
    }
    
    public class OneMedia{
        public String part;
        public double value;
        public String filter;
        
        public OneMedia(String _part, double _value){
            part = _part;
            value = _value;
        }
        
        public OneMedia(String _part, String _filter, double _value){
            part = _part;
            value = _value;
            filter = _filter;
        }
        
    }
    
}
