/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;


/**
 *
 * @author prash
 */
public class SNR {
    
    private double noiseThreshold = 0.1;    
    
    @Getter
    private int zeroSignal = 0;
    
    @Getter
    private int zeroNoise = 0;
    
    @Getter
    private double totalSignal = 0;
    
    @Getter
    private double totalNoise = 0;
    
    @Getter
    private int noiseWithinThreshold = 0;
    
    @Getter
    //private double snr = 1;
    private double snr = 0;
    
    @Getter
    private List<Entry<Double,Double>> signalNoiseList;
    
    
    public SNR(List<SelectionInfo> selection){
        signalNoiseList = new ArrayList<>();
        for(int i=0;i<selection.size();i++){
            SelectionInfo current = selection.get(i);
            double signal = current.getSelectedFluorophore().express(current.getSelectedLaser(), current.getSelectedDetector());
            double noise = 0;
            for(int j=0;j<selection.size();j++){
                if(i==j){
                    continue;
                }
                SelectionInfo other = selection.get(j);
                noise += other.getSelectedFluorophore().express(current.getSelectedLaser(), current.getSelectedDetector());
            }
            if(signal == 0){
                zeroSignal++;
            } else {
                if (noise == 0) {
                    zeroNoise++;
                } else {
                    //snr *= (signal/noise);
                    snr += (signal/noise);
                }
            }
            totalSignal += signal;
            totalNoise += noise;
            if(noise < (noiseThreshold*signal)){
                noiseWithinThreshold++;
            }
            signalNoiseList.add(new AbstractMap.SimpleEntry<>(signal,noise));
            
        }
        
        Collections.sort(signalNoiseList, new Comparator<Map.Entry<Double, Double>>() {
            @Override
            public int compare(Map.Entry<Double, Double> o1, Map.Entry<Double, Double> o2) {
                return compareSN(o1,o2);
            }
        });
        
        
    }
    
    
    public int compareSN(Map.Entry<Double, Double> o1, Map.Entry<Double, Double> o2){
        double o1Signal = o1.getKey();
        double o1Noise = o1.getValue();
        double o2Signal = o2.getKey();
        double o2Noise = o2.getKey();
        if (o1Signal == 0) {
            if (o2Signal == 0) {
                if (o1Noise > o2Noise) {
                    return -1;
                } else if (o1Noise < o2Noise) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return -1;
            }
        } else {
            if (o2Signal == 0) {
                return 1;
            } else {
                //Both o1 and o2 Signal > 0....
                if(o1Noise == 0){
                    if(o2Noise == 0){
                        return 0;
                    } else {
                        if(o2Noise > (noiseThreshold * o2Signal)){
                            return 1;
                        } else {
                            //NOW THIS COULD CHANGE???
                            double diff1 = o1Signal - o1Noise;
                            double diff2 = o2Signal - o2Noise;
                            if(diff1 < diff2){
                                return -1;
                            } else if(diff1 > diff2){
                                return 1;
                            } else {
                                return 1;
                            }
                        }
                    }
                } else {
                    if(o2Noise == 0){
                        if(o1Noise > (noiseThreshold * o1Signal)){
                            return -1;
                        } else {
                            //NOW THIS COULD CHANGE???
                            double diff1 = o1Signal - o1Noise;
                            double diff2 = o2Signal - o2Noise;
                            if(diff1 < diff2){
                                return -1;
                            } else if(diff1 > diff2){
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    } else {
                        //Both o1 and o2 Noise > 0. 
                        if(o1Noise > (noiseThreshold * o1Signal)){
                            if(o2Noise > (noiseThreshold * o2Signal)){
                                //o1 and o2 Noise greater than threshold
                                double diff1 = o1Signal - o1Noise;
                                double diff2 = o2Signal - o2Noise;
                                if (diff1 < diff2) {
                                    return -1;
                                } else if (diff1 > diff2) {
                                    return 1;
                                } else {
                                    if(o1Signal < o2Signal){
                                        return -1;
                                    } else if(o1Signal > o2Signal){
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            } else {
                                //o2 noise within threshold. o1 noise greater than threshold.
                                return -1;
                            }
                        } else {
                            if(o2Noise > (noiseThreshold * o2Signal)){
                                //o2 Noise greater than threshold. o1 within threshold.
                                return 1;
                            } else {
                                //o1 and o2 Noise within threshold.
                                double diff1 = o1Signal - o1Noise;
                                double diff2 = o2Signal - o2Noise;
                                if (diff1 < diff2) {
                                    return -1;
                                } else if (diff1 > diff2) {
                                    return 1;
                                } else {
                                    if(o1Signal < o2Signal){
                                        return -1;
                                    } else if(o1Signal > o2Signal){
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //return 0;
    }
    
    /**
     * Returns true if the current SNR is greater than the Input Parameter SNR.
     * @param snr
     * @return
     */
    public boolean greaterThan(SNR snr){
        
        if(this.zeroSignal < snr.zeroSignal){
            //Fewer number of Zero Signals is always more desirable.
            return true;
        }
        
        if(this.noiseWithinThreshold > snr.noiseWithinThreshold){
            return true;
        }
        
        int currentCount = 0;
        int otherCount = 0;
        for(int i=0;i<snr.signalNoiseList.size();i++){
            int compare = compareSN(this.signalNoiseList.get(i),snr.signalNoiseList.get(i));
            if(compare == 1){
                currentCount++;
            } else {
                otherCount++;
            }
        }
        
        if(currentCount > otherCount){
            return true;
        } else if(currentCount < otherCount){
            return false;
        } else {
            
            if(this.snr > snr.snr){
                return true;
            }
            
        }
        
        return false;
    }

}


