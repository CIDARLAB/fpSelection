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
    private List<Entry<Double, Double>> signalNoiseList;

    public SNR(List<SelectionInfo> selection) {
        signalNoiseList = new ArrayList<>();
        for (int i = 0; i < selection.size(); i++) {
            SelectionInfo current = selection.get(i);
            double signal = current.getSelectedFluorophore().express(current.getSelectedLaser(), current.getSelectedDetector());
            double noise = 0;
            for (int j = 0; j < selection.size(); j++) {
                if (i == j) {
                    continue;
                }
                SelectionInfo other = selection.get(j);
                noise += other.getSelectedFluorophore().express(current.getSelectedLaser(), current.getSelectedDetector());
            }
            if (signal == 0) {
                zeroSignal++;
            } else {
                if (noise == 0) {
                    zeroNoise++;
                } else {
                    //snr *= (signal/noise);
                    snr += (signal / noise);
                }
            }
            totalSignal += signal;
            totalNoise += noise;
            if (noise < (noiseThreshold * signal)) {
                noiseWithinThreshold++;
            }
            signalNoiseList.add(new AbstractMap.SimpleEntry<>(signal, noise));

        }

        /*Collections.sort(signalNoiseList, new Comparator<Map.Entry<Double, Double>>() {
            @Override
            public int compare(Map.Entry<Double, Double> o1, Map.Entry<Double, Double> o2) {
                return compareSN(o1, o2);
            }
        });*/

    }

    public int compareSN(Map.Entry<Double, Double> o1, Map.Entry<Double, Double> o2) {
        double o1Signal = o1.getKey();
        double o1Noise = o1.getValue();
        double o2Signal = o2.getKey();
        double o2Noise = o2.getValue();

        if ((o1Signal == o2Signal) && (o1Noise == o2Noise)) {
            return 0;
        } else {
            if (o1Signal == 0) {
                //<editor-fold desc="o1 Signal = 0">
                if (o2Signal == 0) {
                    //<editor-fold desc="Both o1 and o2 Signal = 0">
                    if (o1Noise > o2Noise) {
                        return -1;
                    } else if (o1Noise < o2Noise) {
                        return 1;
                    } else {
                        return 0;
                    }
                    //</editor-fold>
                } else {
                    //o1 Signal = 0 and o2 > 0....
                    return -1;
                }
                //</editor-fold>
            } else {
                //<editor-fold desc="o1 Signal > 0">
                if (o2Signal == 0) {
                    //o1 Signal > 0 and o2 Signal = 0....
                    return 1;
                } else {
                    //<editor-fold desc="Both o1 and o2 Signal > 0">
                    if (o1Noise == 0) {
                        if (o2Noise == 0) {
                            //o1 and o2 Noise = 0....
                            if (o1Signal > o2Signal) {
                                return 1;
                            } else if (o1Signal < o2Signal) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else {
                            //o1 Noise = 0 and o2 Noise > 0....
                            if (o2Noise > (noiseThreshold * o2Signal)) {
                                return 1;
                            } else {
                                //NOW THIS COULD CHANGE???
                                double diff1 = o1Signal - o1Noise;
                                double diff2 = o2Signal - o2Noise;
                                if (diff1 < diff2) {
                                    return -1;
                                } else if (diff1 > diff2) {
                                    return 1;
                                } else {
                                    return 1;
                                }
                            }
                        }
                    } else {
                        //o1 Noise > 0
                        if (o2Noise == 0) {
                            //o1 Noise > 0 and o2 Noise = 0....
                            if (o1Noise > (noiseThreshold * o1Signal)) {
                                return -1;
                            } else {
                                //NOW THIS COULD CHANGE???
                                double diff1 = o1Signal - o1Noise;
                                double diff2 = o2Signal - o2Noise;
                                if (diff1 < diff2) {
                                    return -1;
                                } else if (diff1 > diff2) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        } else {
                            //Both o1 and o2 Noise > 0. 
                            if (o1Noise > (noiseThreshold * o1Signal)) {
                                if (o2Noise > (noiseThreshold * o2Signal)) {
                                    //o1 and o2 Noise greater than threshold
                                    double snr1 = o1Signal/o1Noise;
                                    double snr2 = o2Signal/o2Noise;
                                    if(snr1 > snr2){
                                        return 1;
                                    } else if(snr1 < snr2){
                                        return -1;
                                    } else {
                                        return 0;
                                    }
                                    /*double diff1 = o1Signal - o1Noise;
                                    double diff2 = o2Signal - o2Noise;
                                    if (diff1 < diff2) {
                                        return -1;
                                    } else if (diff1 > diff2) {
                                        return 1;
                                    } else {
                                        if (o1Signal < o2Signal) {
                                            return -1;
                                        } else if (o1Signal > o2Signal) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }*/
                                } else {
                                    //o1 noise greater than threshold. o2 noise within threshold. 
                                    return -1;
                                }
                            } else {
                                if (o2Noise > (noiseThreshold * o2Signal)) {
                                    //o1 within threshold. o2 Noise greater than threshold. 
                                    return 1;
                                } else {
                                    double snr1 = o1Signal/o1Noise;
                                    double snr2 = o2Signal/o2Noise;
                                    if(snr1 > snr2){
                                        return 1;
                                    } else if(snr1 < snr2){
                                        return -1;
                                    } else {
                                        return 0;
                                    }
                                    //o1 and o2 Noise within threshold.
                                    /*double diff1 = o1Signal - o1Noise;
                                    double diff2 = o2Signal - o2Noise;
                                    if (diff1 < diff2) {
                                        return -1;
                                    } else if (diff1 > diff2) {
                                        return 1;
                                    } else {
                                        if (o1Signal < o2Signal) {
                                            return -1;
                                        } else if (o1Signal > o2Signal) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }*/
                                }
                            }
                        }
                    }
                    //</editor-fold>
                }
                //</editor-fold>
            }
        }

        //return 0;
    }

    /**
     * Returns true if the current SNR is greater than the Input Parameter SNR.
     *
     * @param snr
     * @return
     */
    public boolean greaterThan(SNR snr) {
        return (this.compare(snr) > 0);
        //<editor-fold desc="old way of doing this.">  
        /*
        if (this.zeroSignal < snr.zeroSignal) {
        //Fewer number of Zero Signals is always more desirable.
        return true;
        } else if (this.zeroSignal < snr.zeroSignal) {
        return false;
        } else {
        if (this.noiseWithinThreshold > snr.noiseWithinThreshold) {
        return true;
        } else if (this.noiseWithinThreshold < snr.noiseWithinThreshold) {
        return false;
        } else {
        int currentCount = 0;
        int otherCount = 0;
        int equalCount = 0;
        for (int i = 0; i < snr.signalNoiseList.size(); i++) {
        int compare = compareSN(this.signalNoiseList.get(i), snr.signalNoiseList.get(i));
        if (compare > 0) {
        currentCount++;
        } else if (compare < 0) {
        otherCount++;
        } else {
        equalCount++;
        }
        }
        if (currentCount > otherCount) {
        return true;
        } else if (currentCount < otherCount) {
        return false;
        } else {
        if (this.snr > snr.snr) {
        return true;
        } else {
        return false;
        }
        }
        }
        }*/
        //</editor-fold>
    }

    public int compare(SNR snr) {

        boolean allEqual = true;
        for (int i = 0; i < snr.signalNoiseList.size(); i++) {
            double s1 = this.getSignalNoiseList().get(i).getKey();
            double s2 = snr.getSignalNoiseList().get(i).getKey();
            double n1 = this.getSignalNoiseList().get(i).getValue();
            double n2 = snr.getSignalNoiseList().get(i).getValue();
            if ((s1 != s2) || (n1 != n2)) {
                allEqual = false;
                break;
            }
        }
        if (allEqual) {
            return 0;
        } else {
            if (this.zeroSignal < snr.zeroSignal) {
                //Fewer number of Zero Signals is always more desirable.
                return 1;
            } else if (this.zeroSignal > snr.zeroSignal) {
                return -1;
            } else {
                if (this.noiseWithinThreshold > snr.noiseWithinThreshold) {
                    return 1;
                } else if (this.noiseWithinThreshold < snr.noiseWithinThreshold) {
                    return -1;
                } else {
                    //return 0;
                    double thisSignalProd = 1;
                    double thisNoiseSum = 0;
                    
                    double otherSignalProd = 1;
                    double otherNoiseSum = 0;
                    
                    for (int i = 0; i < snr.signalNoiseList.size(); i++) {
                        //System.out.println("Compare :: " + compare + " for s1 = " + this.signalNoiseList.get(i).getKey() + "; n1 = " + this.signalNoiseList.get(i).getValue() + "; s2 = " + snr.getSignalNoiseList().get(i).getKey() + "; n2 = " + snr.getSignalNoiseList().get(i).getValue());
                        thisSignalProd *= this.signalNoiseList.get(i).getKey();
                        thisNoiseSum += this.signalNoiseList.get(i).getValue();
                        
                        otherSignalProd *= snr.signalNoiseList.get(i).getKey();
                        otherNoiseSum += snr.signalNoiseList.get(i).getValue();
                    }
                    
                    int n = snr.signalNoiseList.size();
                    double exp = (1.0)/n;
                    double thisSignal = Math.pow(thisSignalProd, exp);
                    double thisNoise = (thisNoiseSum/n);
                    double otherSignal = Math.pow(otherSignalProd, exp);
                    double otherNoise = (otherNoiseSum/n);
                    
                    if(thisSignal > otherSignal) {
                        return 1;
                    } else if (thisSignal < otherSignal) {
                        return -1;
                    } else {
                        if (thisNoise < otherNoise) {
                            return 1;
                        } else if (thisNoise > otherNoise) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                    
                    /*if (currentCount > otherCount) {
                        return 1;
                    } else if (currentCount < otherCount) {
                        return -1;
                    } else {
                        return 0;
                    }
                    */
                    
                    
                }
            }
        }

        //return false;
    }

}
