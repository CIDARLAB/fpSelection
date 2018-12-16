/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.cidarlab.fpSelection.algorithms.ExhaustiveSelection.filterCombinations;
import static org.cidarlab.fpSelection.algorithms.ExhaustiveSelection.fluorophorePermutations;
import static org.cidarlab.fpSelection.algorithms.ExhaustiveSelection.getCombinations;
import static org.cidarlab.fpSelection.algorithms.ExhaustiveSelection.getPermutations;
import static org.cidarlab.fpSelection.algorithms.ExhaustiveSelection.getSelection;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author prash
 */
public class RandomWalk {
    
    public static List<SelectionInfo> run(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer) throws IOException {
        
        List<Fluorophore> fluorophores = new ArrayList<Fluorophore>();
        Map<Detector,Laser> detectorMap = new HashMap<Detector,Laser>();
        List<Detector> detectors = new ArrayList<Detector>();
        
        for(String name:spectralMaps.keySet()){
            fluorophores.add(spectralMaps.get(name));
        }
        
        int count = 0;
        for(Laser l:cytometer.lasers){
            for(Detector d:l.detectors){
                detectors.add(d);
                detectorMap.put(d, l);
                count++;
            }
        }
        
        List<Fluorophore> currentFluorophores = HillClimbingSelection.getRandomFluorophores(n, fluorophores);
        List<Detector> currentDetectors = HillClimbingSelection.getRandomDetectors(n, detectors);

        List<SelectionInfo> selection = HillClimbingSelection.getSelection(currentFluorophores, currentDetectors, detectorMap);
        SNR snr = new SNR(selection);
        
        List<SelectionInfo> best = new ArrayList<>(selection);
        SNR bestsnr = new SNR(best);
        
        for(int i=0;i<1000;i++){
            currentFluorophores = HillClimbingSelection.getRandomFluorophores(n, fluorophores);
            currentDetectors = HillClimbingSelection.getRandomDetectors(n, detectors);
            selection = HillClimbingSelection.getSelection(currentFluorophores, currentDetectors, detectorMap);
            snr = new SNR(selection);
            
            if(snr.greaterThan(bestsnr)){
                best = new ArrayList<>(selection);
                bestsnr = new SNR(best);
            }
        }
        
        return best;
    }
    
}
