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
    private List<CompensationMatrix> matrix; 
    
    @Getter
    @Setter
    private List<OneMedia> oneMediaValues;
    
    @Getter
    @Setter
    private List<Voltage> voltageValues;
    
    public AnalyticsExperiment(){
        laser = new Laser();
        voltageValues = new ArrayList<Voltage>();
        oneMediaValues = new ArrayList<OneMedia>();
        matrix = new ArrayList<CompensationMatrix>(); 
    }
    
    public void addVoltageValue(String _filter, int _voltage){
        voltageValues.add(new Voltage(_filter, _voltage));
    }
    
    public void addVoltageValue(List<Voltage> values){
        voltageValues.addAll(values);
    }
    
    public void addOneMediaValue(String _part, double _value){
        oneMediaValues.add(new OneMedia(_part, _value));
    }
    
    public void addOneMediaValue(String _part, String _filter, double _value){
        oneMediaValues.add(new OneMedia(_part, _filter, _value));
    }
    
    public void addOneMediaValue(List<OneMedia> values){
        oneMediaValues.addAll(values);
    }
    
    public void addCompensationMatrixValue(String _measuredFilter, String _bleedOverFilter, double _value){
        matrix.add(new CompensationMatrix(_measuredFilter,_bleedOverFilter,_value));
    }
    
    public void addCompensationMatrixValue(List<CompensationMatrix> values){
        matrix.addAll(values);
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
        onemedia,
        voltage
    }
    
    public static class CompensationMatrix{
        public String measuredFilter;
        public String bleedOverFilter;
        public double value;
        
        public CompensationMatrix(String _measuredFilter, String _bleedOverFilter, double _value){
            measuredFilter = _measuredFilter;
            bleedOverFilter = _bleedOverFilter;
            value = _value;
        }
        
    }
    
    public static class Voltage{
        public String filter;
        public int voltage;
        
        public Voltage(String _filter, int _voltage){
            filter = _filter;
            voltage = _voltage;
        }
    }
    
    public static class OneMedia{
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
