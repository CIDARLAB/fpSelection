/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.algorithms.SimulatedAnnealing;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.cidarlab.fpSelection.selectors.ParserTest;

/**
 *
 * @author prash
 */
public class CaseStudyTest {

    static LinkedList<int[]> filterCombinations;
    static LinkedList<int[]> fluorophorePermutations;
    
    private static String basefp = Utilities.getCaseStudyFilepath();

    public static void main(String[] args) throws IOException, InterruptedException {
        
        String spectrafp = basefp + "fp_spectra.csv";
        String brightnessfp = basefp + "brightness.csv";
        Map<String, Fluorophore> spectralMaps = fpSpectraParse.parse(spectrafp);
        fpSpectraParse.addBrightness(new File(brightnessfp), spectralMaps);
        
        Cytometer bucytometer = fpFortessaParse.parse(ParserTest.BUfortessafp, false);
        Cytometer harvardcytometer = fpFortessaParse.parse(ParserTest.Harvardfortessafp, false);
        
        exhaustiveTests(spectralMaps, bucytometer, "BU");
        exhaustiveTests(spectralMaps, harvardcytometer, "Harvard");
        
        simAnnealingTests(spectralMaps, bucytometer, "BU");
        simAnnealingTests(spectralMaps, harvardcytometer, "Harvard");
        
        higherSimAnnealingTests(spectralMaps, bucytometer, "BU");
        higherSimAnnealingTests(spectralMaps, harvardcytometer, "Harvard");
        
        hillClimbingTests(spectralMaps, bucytometer, "BU");
        hillClimbingTests(spectralMaps, harvardcytometer, "Harvard");
        
        higherHillClimbingTests(spectralMaps, bucytometer, "BU");
        higherHillClimbingTests(spectralMaps, harvardcytometer, "Harvard");
        
    }
    
    private static void exhaustiveTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException, InterruptedException{
        long current = 0;
        
        System.out.println("Starting Exhaustive with n = 1");
        current = System.currentTimeMillis();
        exhaustivePlots(1, spectralMaps, cytometer, prefix);
        System.out.println("Time taken = " + ((System.currentTimeMillis() - current))+ " milliseconds.");
        System.out.println("------------------------------");
        
        System.out.println("Starting Exhaustive with n = 2");
        current = System.currentTimeMillis();
        exhaustivePlots(2, spectralMaps, cytometer, prefix);
        System.out.println("Time taken = " + ((System.currentTimeMillis() - current))+ " milliseconds.");
        System.out.println("------------------------------");
        
        System.out.println("Starting Exhaustive with n = 3");
        current = System.currentTimeMillis();
        exhaustivePlots(3, spectralMaps, cytometer, prefix);
        System.out.println("Time taken = " + ((System.currentTimeMillis() - current)/1000) + " seconds.");
        System.out.println("------------------------------");
        
        //exhaustivePlots(4, spectralMaps, cytometer);
        
        //String exhaustivefp = basefp + "exhaustivePlots" + Utilities.getSeparater();
        //String fp = exhaustivefp + n + Utilities.getSeparater();
        //flattenImages(fp);
    }
    
    private static void exhaustivePlots(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException, InterruptedException {

        //String exhaustivefp = basefp + "exhaustivePlots" + Utilities.getSeparater();
        //Utilities.makeDirectory(exhaustivefp);
        //String fp = exhaustivefp + n + Utilities.getSeparater();
        //Utilities.makeDirectory(fp);
        
        
        int numFluorophores = spectralMaps.size();

        //count filters
        int numFilters = 0;
        for (Laser laser : cytometer.lasers) {
            numFilters += laser.detectors.size();
        }

        //fluorophore index --> fluorophore object
        Fluorophore[] fluorophores = new Fluorophore[numFluorophores];
        int fpi = 0;
        for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
            Fluorophore fluorophore = entry.getValue();
            fluorophores[fpi] = fluorophore;
            fpi++;
        }

        Laser[] lasers = new Laser[numFilters];
        Detector[] detectors = new Detector[numFilters];
        int filterIndex = 0;
        for (Laser laser : cytometer.lasers) {
            for (Detector detector : laser.detectors) {
                lasers[filterIndex] = laser;
                detectors[filterIndex] = detector;
                filterIndex++;
            }
        }

        //get all combinations of filters (order not important)
        filterCombinations = new LinkedList<>();
        int tempData[] = new int[n];
        getCombinations(tempData, 0, numFilters - 1, 0, n);

        //get all permutations of fluorophores to match to filters (order is important)
        fluorophorePermutations = new LinkedList<>();
        tempData = new int[n];
        getPermutations(tempData, numFluorophores, n);
        
        long totalComputations = filterCombinations.size() * fluorophorePermutations.size();
        System.out.println("Filter Combinations :: " + filterCombinations.size());
        System.out.println("FP Permutations     :: " + fluorophorePermutations.size());
        System.out.println("Total Computations : " + totalComputations);
        
        
        List<Map.Entry<List<SelectionInfo>,SNR>> results = new ArrayList<>();
        int count = 0;
        for (int[] filterCombo : filterCombinations) {
            for (int[] fluorophorePerm : fluorophorePermutations) {
                List<SelectionInfo> currentSelection = ExhaustiveSelection.getSelection(n, fluorophorePerm, filterCombo, fluorophores, lasers, detectors);
                SNR snr = new SNR(currentSelection);
                results.add(new AbstractMap.SimpleEntry<>(currentSelection,snr));
                
                //ProteinSelector.generateNoise(currentSelection);
                //JavaPlot plot = ProteinSelector.getJavaPlot(currentSelection);
                //Utilities.plotToFile(plot, fp + count + ".png");
                //count++;
            }
        }
        
        Collections.sort(results, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
            //-1 s1 is less
            //0 = equal
            //1 s1 is greater
            SNR s1 = o1.getValue();
            SNR s2 = o2.getValue();
            return s1.compare(s2);
            /*if(s1.greaterThan(s2)){
                return 1;
            } else if(s2.greaterThan(s1)){
                return -1;
            } else {
                return 0;
            }*/
        });
        Collections.reverse(results);
        //flattenImages(fp);
        List<String> lines = new ArrayList<>();
        String line = "";
        for(int i=0;i<n-1;i++){
            line += "FP,Laser,Detector,Signal,Noise,";
        }
        line += "FP,Laser,Detector,Signal,Noise";
        lines.add(line);
        for(Map.Entry<List<SelectionInfo>, SNR> entry:results){
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n,selection,snr);
            lines.add(line);
        }
        
        Utilities.writeToFile(basefp + prefix + "_" + n + "fp.csv", lines);
        
    }
    
    private static String toString(int n, List<SelectionInfo> selection, SNR snr){
        Map<String,Integer> maps = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            maps.put(selection.get(i).getSelectedFluorophore().name, i);
        }
        List<String> keys = new ArrayList<>(maps.keySet());
        
        String line = "";
        for (int i = 0; i < n - 1; i++) {
            String key = keys.get(i);
            line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
            line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue() + ",";
            //            "FP,Laser,Detector,Signal,Noise,";
        }
        String key = keys.get(n-1);
        line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
        line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue();
        return line;
    }
    
    private static void flattenImages(String fp) throws IOException, InterruptedException{
        File[] files = (new File(fp)).listFiles();
        for(File f:files){
            String fname = f.getName();
            
            StringBuilder commandBuilder = null;
            commandBuilder = new StringBuilder("/usr/bin/convert " + f.getAbsolutePath() + " -background white -alpha remove " + fp + fname + "Flat.png");
            
            String command = commandBuilder.toString();
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
            proc.waitFor();
            InputStream is = proc.getInputStream();
            InputStream es = proc.getErrorStream();
            OutputStream os = proc.getOutputStream();
            is.close();
            es.close();
            os.close();
        }
        files = (new File(fp)).listFiles();
        for(File f:files){
            if(!f.getName().endsWith("Flat.png")){
                f.deleteOnExit();
            }
                
        }
        
    }
    
    public static void getCombinations(int data[], int start, int n, int index, int k) {
        if (index == k) {
            filterCombinations.add(data.clone());
            return;
        }
        for (int i = start; i <= n && n - i + 1 >= k - index; i++) {
            data[index] = i;
            getCombinations(data, i + 1, n, index + 1, k);
        }
    }
    
    public static void getPermutations(int data[], int n, int k) {
        if (k == 0) {
            fluorophorePermutations.add(data.clone());
            return;
        }      
        outerloop:
        for (int i = 0; i < n; ++i) {
            for (int j = data.length-1; j >= k; j--) {
                if (data[j] == i) continue outerloop;
            }
            data[k - 1] = i;
            getPermutations(data, n, k - 1);
        }
    }
    
    public static void simAnnealingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix){
        testSimAnnealing(1,spectralMaps,cytometer,prefix);
        testSimAnnealing(2,spectralMaps,cytometer,prefix);
        testSimAnnealing(3,spectralMaps,cytometer,prefix);
    }
    
    public static void testSimAnnealing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix){
        String exhaustivefp = basefp + prefix + "_" + n + "fp.csv";
        List<String> exhaustive = Utilities.getFileContentAsStringList(exhaustivefp);
        
        List<String> lines = new ArrayList<>();
        
        for(int i=0;i<200;i++){
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n,selection,snr);
            line += ("," + exhaustive.indexOf(line));
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "SA_" + prefix + "_" + n + "fp.csv", lines);
        
    }
    
    public static void hillClimbingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix){
        testHillClimbing(1,spectralMaps,cytometer,prefix);
        testHillClimbing(2,spectralMaps,cytometer,prefix);
        testHillClimbing(3,spectralMaps,cytometer,prefix);
    }
    
    public static void testHillClimbing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix){
        String exhaustivefp = basefp + prefix + "_" + n + "fp.csv";
        List<String> exhaustive = Utilities.getFileContentAsStringList(exhaustivefp);
        
        List<String> lines = new ArrayList<>();
        
        for(int i=0;i<200;i++){
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n,selection,snr);
            line += ("," + exhaustive.indexOf(line));
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "HC_" + prefix + "_" + n + "fp.csv", lines);
        
    }
    
    
    public static void higherSimAnnealingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix){
        testHigherSimAnnealing(4,spectralMaps,cytometer,prefix);
        testHigherSimAnnealing(5,spectralMaps,cytometer,prefix);
        testHigherSimAnnealing(6,spectralMaps,cytometer,prefix);
        testHigherSimAnnealing(7,spectralMaps,cytometer,prefix);
        testHigherSimAnnealing(8,spectralMaps,cytometer,prefix);
    }
    
    public static void testHigherSimAnnealing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix){
        
        List<String> lines = new ArrayList<>();
        
        for(int i=0;i<200;i++){
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n,selection,snr);
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "SA_" + prefix + "_" + n + "fp.csv", lines);
        
    }
    
    public static void higherHillClimbingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix){
        testHigherHillClimbing(4,spectralMaps,cytometer,prefix);
        testHigherHillClimbing(5,spectralMaps,cytometer,prefix);
        testHigherHillClimbing(6,spectralMaps,cytometer,prefix);
        testHigherHillClimbing(7,spectralMaps,cytometer,prefix);
        testHigherHillClimbing(8,spectralMaps,cytometer,prefix);
    }
    
    public static void testHigherHillClimbing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix){
        
        List<String> lines = new ArrayList<>();
        
        for(int i=0;i<200;i++){
            List<SelectionInfo> selection = HillClimbingSelection.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n,selection,snr);
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "HC_" + prefix + "_" + n + "fp.csv", lines);
        
    }
    
}
