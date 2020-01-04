/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

/**
 *
 * @author prash
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.algorithms.RandomWalk;
import org.cidarlab.fpSelection.algorithms.SimulatedAnnealing;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.junit.Test;

/**
 *
 * @author prash
 */
public class Performance {

    static LinkedList<int[]> filterCombinations;
    static LinkedList<int[]> fluorophorePermutations;

    private static String basefp = Utilities.getCaseStudyFilepath();

    private static String harvardFortessafp = basefp + "inputFiles" + Utilities.getSeparater() + "HarvardFortessa.csv";
    private static String harvardSonyfp = basefp + "inputFiles" + Utilities.getSeparater() + "HarvardSony.csv";
    private static String harvardMacsquantfp = basefp + "inputFiles" + Utilities.getSeparater() + "HarvardMacsquant.csv";
    private static String harvardCytoFlexfp = basefp + "inputFiles" + Utilities.getSeparater() + "HarvardCytoFlex.csv";
    
    private static String figure1Sonyfp = basefp + "inputFiles" + Utilities.getSeparater() + "figure1Sony.csv";

    private static String largerSpectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "largerSpectra.csv";
    private static String largerBrightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + "largerBrightness.csv";
    private static String smallerSpectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "smallerSpectra.csv";
    private static String smallerBrightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + "smallerBrightness.csv";
    
    private static String caseSpectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "caseStudySpectra.csv";
    private static String caseBrightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + "caseStudyBrightness.csv";
    

    private static String figure1Spectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "figure1Spectra.csv";
    private static String figure1Brightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + "figure1Brightness.csv";
    
    private static String runExptfp = basefp + "comp" + Utilities.getSeparater();
   
    private static String plotfp = basefp + "plots" + Utilities.getSeparater();
    
    
    public static void testExhaustivePerformance(int n, Map<String, Fluorophore> maps, Cytometer c) throws IOException{
        long current = 0;
        current = System.currentTimeMillis();
        ExhaustiveSelection.run(n, maps, c);
        System.out.println("Time taken for n = " + n +  " is: " + ((System.currentTimeMillis() - current)) + " milliseconds.");
        
    }
    
    public static void testSimulatedAnnealing(int n, Map<String, Fluorophore> maps, Cytometer c) throws IOException{
        long current = 0;
        current = System.currentTimeMillis();
        SimulatedAnnealing.run(n, maps, c);
        System.out.println("Time taken for n = " + n +  " is: " + ((System.currentTimeMillis() - current)) + " milliseconds.");
        
    }
    
    public static void testHillClimbing(int n, Map<String, Fluorophore> maps, Cytometer c) throws IOException{
        long current = 0;
        current = System.currentTimeMillis();
        HillClimbingSelection.run(n, maps, c);
        System.out.println("Time taken for n = " + n +  " is: " + ((System.currentTimeMillis() - current)) + " milliseconds.");
        
    }
    
    @Test
    public void testCaseStudy() throws IOException, InterruptedException {
        
        Cytometer harvardFortessa = fpFortessaParse.parse(harvardFortessafp, false);
        Cytometer harvardSony = fpFortessaParse.parse(harvardSonyfp, false);
        Cytometer harvardMacsquant = fpFortessaParse.parse(harvardMacsquantfp, false);
        Cytometer harvardCytoflex = fpFortessaParse.parse(harvardCytoFlexfp, false);
        
        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(caseSpectrafp);
        fpSpectraParse.addBrightness(new File(caseBrightnessfp), caseStudySpectralMap);
        
        
        //Performance.....
        //Exhaustive
        
        

        System.out.println("#################_Thow Away_################");        
        testExhaustivePerformance(2,caseStudySpectralMap,harvardFortessa);
        
        
        
        System.out.println("#################_Exhasutive_################");        
        System.out.println("==================Fortessa===================");
        testExhaustivePerformance(2,caseStudySpectralMap,harvardFortessa);
        testExhaustivePerformance(3,caseStudySpectralMap,harvardFortessa);
        testExhaustivePerformance(4,caseStudySpectralMap,harvardFortessa);
        testExhaustivePerformance(5,caseStudySpectralMap,harvardFortessa);
        
        System.out.println("==================Sony=======================");
        testExhaustivePerformance(2,caseStudySpectralMap,harvardSony);
        testExhaustivePerformance(3,caseStudySpectralMap,harvardSony);
        testExhaustivePerformance(4,caseStudySpectralMap,harvardSony);
        testExhaustivePerformance(5,caseStudySpectralMap,harvardSony);
        testExhaustivePerformance(6,caseStudySpectralMap,harvardSony);
        
        System.out.println("==================Macsquant==================");
        testExhaustivePerformance(2,caseStudySpectralMap,harvardMacsquant);
        testExhaustivePerformance(3,caseStudySpectralMap,harvardMacsquant);
        testExhaustivePerformance(4,caseStudySpectralMap,harvardMacsquant);
        testExhaustivePerformance(5,caseStudySpectralMap,harvardMacsquant);
        testExhaustivePerformance(6,caseStudySpectralMap,harvardMacsquant);
        
        System.out.println("==================CytoFlex===================");
        testExhaustivePerformance(2,caseStudySpectralMap,harvardCytoflex);
        testExhaustivePerformance(3,caseStudySpectralMap,harvardCytoflex);
        testExhaustivePerformance(4,caseStudySpectralMap,harvardCytoflex);
        testExhaustivePerformance(5,caseStudySpectralMap,harvardCytoflex);
        
        
        
        System.out.println("###########_Simulated Annealing_#############");        
        System.out.println("==================Fortessa===================");
        testSimulatedAnnealing(2,caseStudySpectralMap,harvardFortessa);
        testSimulatedAnnealing(3,caseStudySpectralMap,harvardFortessa);
        testSimulatedAnnealing(4,caseStudySpectralMap,harvardFortessa);
        testSimulatedAnnealing(5,caseStudySpectralMap,harvardFortessa);
        testSimulatedAnnealing(6,caseStudySpectralMap,harvardFortessa);
        
        System.out.println("==================Sony=======================");
        testSimulatedAnnealing(2,caseStudySpectralMap,harvardSony); 
        testSimulatedAnnealing(3,caseStudySpectralMap,harvardSony);
        testSimulatedAnnealing(4,caseStudySpectralMap,harvardSony);
        testSimulatedAnnealing(5,caseStudySpectralMap,harvardSony);
        testSimulatedAnnealing(6,caseStudySpectralMap,harvardSony);
        
        System.out.println("==================Macsquant==================");
        testSimulatedAnnealing(2,caseStudySpectralMap,harvardMacsquant);
        testSimulatedAnnealing(3,caseStudySpectralMap,harvardMacsquant);
        testSimulatedAnnealing(4,caseStudySpectralMap,harvardMacsquant);
        testSimulatedAnnealing(5,caseStudySpectralMap,harvardMacsquant);
        testSimulatedAnnealing(6,caseStudySpectralMap,harvardMacsquant);
        
        System.out.println("==================CytoFlex===================");
        testSimulatedAnnealing(2,caseStudySpectralMap,harvardCytoflex);
        testSimulatedAnnealing(3,caseStudySpectralMap,harvardCytoflex);
        testSimulatedAnnealing(4,caseStudySpectralMap,harvardCytoflex);
        testSimulatedAnnealing(5,caseStudySpectralMap,harvardCytoflex);
        testSimulatedAnnealing(6,caseStudySpectralMap,harvardCytoflex);
        
        
        
        System.out.println("###########_Hill Climbing_###################");        
        System.out.println("==================Fortessa===================");
        testHillClimbing(2,caseStudySpectralMap,harvardFortessa);
        testHillClimbing(3,caseStudySpectralMap,harvardFortessa);
        testHillClimbing(4,caseStudySpectralMap,harvardFortessa);
        testHillClimbing(5,caseStudySpectralMap,harvardFortessa);
        testHillClimbing(6,caseStudySpectralMap,harvardFortessa);
        
        System.out.println("==================Sony=======================");
        testHillClimbing(2,caseStudySpectralMap,harvardSony); 
        testHillClimbing(3,caseStudySpectralMap,harvardSony);
        testHillClimbing(4,caseStudySpectralMap,harvardSony);
        testHillClimbing(5,caseStudySpectralMap,harvardSony);
        testHillClimbing(6,caseStudySpectralMap,harvardSony);
        
        System.out.println("==================Macsquant==================");
        testHillClimbing(2,caseStudySpectralMap,harvardMacsquant);
        testHillClimbing(3,caseStudySpectralMap,harvardMacsquant);
        testHillClimbing(4,caseStudySpectralMap,harvardMacsquant);
        testHillClimbing(5,caseStudySpectralMap,harvardMacsquant);
        testHillClimbing(6,caseStudySpectralMap,harvardMacsquant);
        
        System.out.println("==================CytoFlex===================");
        testHillClimbing(2,caseStudySpectralMap,harvardCytoflex);
        testHillClimbing(3,caseStudySpectralMap,harvardCytoflex);
        testHillClimbing(4,caseStudySpectralMap,harvardCytoflex);
        testHillClimbing(5,caseStudySpectralMap,harvardCytoflex);
        testHillClimbing(6,caseStudySpectralMap,harvardCytoflex);
        
        
        
        
        ///////////////////////////////////////////////////////////////////
        ///////////////////////// Simulated Annealing /////////////////////
        ///////////////////////////////////////////////////////////////////
        
        
        /*
        System.out.println("==================Fortessa===================");
        exhaustiveTests(caseStudySpectralMap, harvardFortessa, "EX_HarvFort");
        System.out.println("==================Sony=======================");
        exhaustiveTests(caseStudySpectralMap, harvardSony, "EX_HarvSony");
        System.out.println("==================Macsquant==================");
        exhaustiveTests(caseStudySpectralMap, harvardMacsquant, "EX_HarvMacs");
        System.out.println("==================CytoFlex===================");
        exhaustiveTests(caseStudySpectralMap, harvardCytoflex, "EX_HarvFlex");

        System.out.println("Stochastic Test - Fortessa===================");
        stochasticTests(caseStudySpectralMap,harvardFortessa, "HarvFort");
        
        System.out.println("Stochastic Test - Sony=======================");
        stochasticTests(caseStudySpectralMap,harvardSony, "HarvSony");
        
        System.out.println("Stochastic Test - Macsquant==================");
        stochasticTests(caseStudySpectralMap,harvardMacsquant, "HarvMacs");
        
        System.out.println("Stochastic Test - CytoFlex===================");
        stochasticTests(caseStudySpectralMap,harvardCytoflex, "HarvFlex");
        */
        
    }
    
    
    
    public static Laser getLaser(Cytometer c, String lname){
        for(Laser l:c.lasers){
            if(l.getName().equals(lname)){
                return l;
            }
        }
        System.out.println("This shouldn't happen");
        return null;
    }
    
    public static Detector getDetector(Laser l, String dname){
        for(Detector d:l.detectors){
            if(d.identifier.equals(dname)){
                return d;
            }
        }
        System.out.println("This shouldn't happen");
        return null;
    }
    
    public static List<SelectionInfo> getConfiguration(String[] row, Map<String, Fluorophore> fpmap, Cytometer c){
        int n = row.length / 5;
        
        List<SelectionInfo> config = new ArrayList<SelectionInfo>();
        for(int i=0;i<row.length;i+=5){
            Fluorophore fp = fpmap.get(row[i]);
            Laser l = getLaser(c,row[i+1]);
            Detector d = getDetector(l,row[i+2]);
            SelectionInfo si = new SelectionInfo();
            si.setSelectedFluorophore(fp);
            si.setSelectedLaser(l);
            si.setSelectedDetector(d);
            config.add(si);
        }
        
        return config;
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {

            
        
    }
    
    private static void stochasticTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException{
        stochasticTest(1,spectralMaps,cytometer,prefix);
        stochasticTest(2,spectralMaps,cytometer,prefix);
        stochasticTest(3,spectralMaps,cytometer,prefix);
        stochasticTest(4,spectralMaps,cytometer,prefix);
        stochasticTest(5,spectralMaps,cytometer,prefix);
        if(!prefix.equals("HarvSony")){
            stochasticTest(6,spectralMaps,cytometer,prefix);
        }
        //stochasticTest(7,spectralMaps,cytometer,prefix);
        //stochasticTest(8,spectralMaps,cytometer,prefix);
    }
    
    private static void stochasticTest(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException{
        
        List<Map.Entry<List<SelectionInfo>, SNR>> uniqueResults = new ArrayList<>();
        Set<String> uniquelines = new HashSet<>();
        List<String> salines = new ArrayList<>();
        
        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            salines.add(line);
            if(!uniquelines.contains(line)){
                uniquelines.add(line);
                uniqueResults.add(new AbstractMap.SimpleEntry<>(selection, snr));
            }
        }
        Utilities.writeToFile(basefp + "SA_" + prefix + "_" + n + "fp.csv", salines);
        
        List<String> hclines = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = HillClimbingSelection.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            hclines.add(line);
            if(!uniquelines.contains(line)){
                uniquelines.add(line);
                uniqueResults.add(new AbstractMap.SimpleEntry<>(selection, snr));
            }
        }
        Utilities.writeToFile(basefp + "HC_" + prefix + "_" + n + "fp.csv", hclines);
        
        List<String> rwlines = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = RandomWalk.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            rwlines.add(line);
            if(!uniquelines.contains(line)){
                uniquelines.add(line);
                uniqueResults.add(new AbstractMap.SimpleEntry<>(selection, snr));
            }
        }
        Utilities.writeToFile(basefp + "RW_" + prefix + "_" + n + "fp.csv", rwlines);
        
        Collections.sort(uniqueResults, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
            SNR s1 = o1.getValue();
            SNR s2 = o2.getValue();
            return s1.compare(s2);
        });
        Collections.reverse(uniqueResults);

        List<String> lines = new ArrayList<>();
        List<String> top = new ArrayList<>();
        List<String> bottom = new ArrayList<>();

        String line = "";
        for (int i = 0; i < n - 1; i++) {
            line += "FP,Laser,Detector,Signal,Noise,";

        }

        line += "FP,Laser,Detector,Signal,Noise";
        lines.add(line);
        top.add(line);
        bottom.add(line);

        for (Map.Entry<List<SelectionInfo>, SNR> entry : uniqueResults) {
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            lines.add(line);
        }

        /*
        for (int i = 0; i < 10; i++) {
            Map.Entry<List<SelectionInfo>, SNR> entry = uniqueResults.get(i);
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            top.add(line);
        }

        for (int i = uniqueResults.size() - 1; i >= uniqueResults.size() - 10; i--) {
            Map.Entry<List<SelectionInfo>, SNR> entry = uniqueResults.get(i);
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            bottom.add(line);
        }
        */
        Utilities.writeToFile(basefp + "Stoch_" + prefix + "_" + n + "fp.csv", lines);
        //Utilities.writeToFile(basefp + "Stoch_" + prefix + "_" + n + "fp_top10.csv", top);
        //Utilities.writeToFile(basefp + "Stoch_" + prefix + "_" + n + "fp_bottom10.csv", bottom);
        
        
        
    }
    
    private static void exhaustiveTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException, InterruptedException {
        long current = 0;

        System.out.println("Starting Exhaustive with n = 1");
        current = System.currentTimeMillis();
        exhaustivePlots(1, spectralMaps, cytometer, prefix);
        System.out.println("Time taken = " + ((System.currentTimeMillis() - current)) + " milliseconds.");
        System.out.println("------------------------------");

        System.out.println("Starting Exhaustive with n = 2");
        current = System.currentTimeMillis();
        exhaustivePlots(2, spectralMaps, cytometer, prefix);
        System.out.println("Time taken = " + ((System.currentTimeMillis() - current)) + " milliseconds.");
        System.out.println("------------------------------");

        System.out.println("Starting Exhaustive with n = 3");
        current = System.currentTimeMillis();
        exhaustivePlots(3, spectralMaps, cytometer, prefix);
        System.out.println("Time taken = " + ((System.currentTimeMillis() - current) / 1000) + " seconds.");
        System.out.println("------------------------------");
              
    }

    
    private static void exhaustiveRun(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer) throws IOException, InterruptedException {

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

        List<Map.Entry<List<SelectionInfo>, SNR>> results = new ArrayList<>();
        int count = 0;
        for (int[] filterCombo : filterCombinations) {
            for (int[] fluorophorePerm : fluorophorePermutations) {
                List<SelectionInfo> currentSelection = ExhaustiveSelection.getSelection(n, fluorophorePerm, filterCombo, fluorophores, lasers, detectors);
                SNR snr = new SNR(currentSelection);
                results.add(new AbstractMap.SimpleEntry<>(currentSelection, snr));

                //ProteinSelector.generateNoise(currentSelection);
                //JavaPlot plot = ProteinSelector.getJavaPlot(currentSelection);
                //Utilities.plotToFile(plot, fp + count + ".png");
                //count++;
            }
        }

        Collections.sort(results, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
            SNR s1 = o1.getValue();
            SNR s2 = o2.getValue();
            return s1.compare(s2);
        });
        Collections.reverse(results);
        
       
        
    }
    
    
    
    private static Map<Double,Double> getEM(Map<Double,Double> em, double mult){
        Map<Double,Double> adjustedEm = new HashMap<>();
        for(Double wl:em.keySet()){
            double adjem = em.get(wl) * mult;
            adjustedEm.put(wl, adjem);
        }
        
        return adjustedEm;
    }
    
    private static double getMultiplier(Fluorophore fp, Laser laser){
        if (!fp.EXspectrum.containsKey((double) laser.wavelength)) {
            return 0;
        }
        
        double multiplier = (fp.EXspectrum.get((double) laser.wavelength) / 100); //This is where laser power and brightness go
        if(fp.getBrightnessNormalizedTo() != null){
            multiplier = (fp.EXspectrum.get((double) laser.wavelength) / 100) * fp.getBrightness(); //This is where laser power and brightness go
        }
        
        return multiplier;
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

        List<Map.Entry<List<SelectionInfo>, SNR>> results = new ArrayList<>();
        int count = 0;
        for (int[] filterCombo : filterCombinations) {
            for (int[] fluorophorePerm : fluorophorePermutations) {
                List<SelectionInfo> currentSelection = ExhaustiveSelection.getSelection(n, fluorophorePerm, filterCombo, fluorophores, lasers, detectors);
                SNR snr = new SNR(currentSelection);
                results.add(new AbstractMap.SimpleEntry<>(currentSelection, snr));

                //ProteinSelector.generateNoise(currentSelection);
                //JavaPlot plot = ProteinSelector.getJavaPlot(currentSelection);
                //Utilities.plotToFile(plot, fp + count + ".png");
                //count++;
            }
        }

        Collections.sort(results, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
            SNR s1 = o1.getValue();
            SNR s2 = o2.getValue();
            return s1.compare(s2);
        });
        Collections.reverse(results);

        //flattenImages(fp);
        List<String> lines = new ArrayList<>();
        List<String> top = new ArrayList<>();
        List<String> bottom = new ArrayList<>();

        String line = "";
        for (int i = 0; i < n - 1; i++) {
            line += "FP,Laser,Detector,Signal,Noise,";

        }

        line += "FP,Laser,Detector,Signal,Noise";
        lines.add(line);
        top.add(line);
        bottom.add(line);

        for (Map.Entry<List<SelectionInfo>, SNR> entry : results) {
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            lines.add(line);
        }

        for (int i = 0; i < 10; i++) {
            Map.Entry<List<SelectionInfo>, SNR> entry = results.get(i);
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            top.add(line);
        }

        for (int i = results.size() - 1; i >= results.size() - 10; i--) {
            Map.Entry<List<SelectionInfo>, SNR> entry = results.get(i);
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            bottom.add(line);
        }

        //Utilities.writeToFile(basefp + prefix + "_" + n + "fp.csv", lines);
        //Utilities.writeToFile(basefp + prefix + "_" + n + "fp_top10.csv", top);
        //Utilities.writeToFile(basefp + prefix + "_" + n + "fp_bottom10.csv", bottom);
        
    }
    
    private static String toString(int n, List<SelectionInfo> selection, SNR snr) {
        Map<String, Integer> maps = new HashMap<>();

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
        String key = keys.get(n - 1);
        line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
        line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue();
        return line;
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
            for (int j = data.length - 1; j >= k; j--) {
                if (data[j] == i) {
                    continue outerloop;
                }
            }
            data[k - 1] = i;
            getPermutations(data, n, k - 1);
        }
    }

    public static void simAnnealingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        testSimAnnealing(1, spectralMaps, cytometer, prefix);
        testSimAnnealing(2, spectralMaps, cytometer, prefix);
        testSimAnnealing(3, spectralMaps, cytometer, prefix);
    }

    public static void testSimAnnealing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        String exhaustivefp = basefp + prefix + "_" + n + "fp.csv";
        List<String> exhaustive = Utilities.getFileContentAsStringList(exhaustivefp);

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            line += ("," + exhaustive.indexOf(line));
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "SA_" + prefix + "_" + n + "fp.csv", lines);

    }

    public static void hillClimbingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        testHillClimbing(1, spectralMaps, cytometer, prefix);
        testHillClimbing(2, spectralMaps, cytometer, prefix);
        testHillClimbing(3, spectralMaps, cytometer, prefix);
    }

    public static void testHillClimbing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        String exhaustivefp = basefp + prefix + "_" + n + "fp.csv";
        List<String> exhaustive = Utilities.getFileContentAsStringList(exhaustivefp);

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            line += ("," + exhaustive.indexOf(line));
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "HC_" + prefix + "_" + n + "fp.csv", lines);

    }

    public static void higherSimAnnealingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        testHigherSimAnnealing(4, spectralMaps, cytometer, prefix);
        testHigherSimAnnealing(5, spectralMaps, cytometer, prefix);
        testHigherSimAnnealing(6, spectralMaps, cytometer, prefix);
        testHigherSimAnnealing(7, spectralMaps, cytometer, prefix);
        testHigherSimAnnealing(8, spectralMaps, cytometer, prefix);
    }

    public static void testHigherSimAnnealing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "SA_" + prefix + "_" + n + "fp.csv", lines);

    }

    public static void higherHillClimbingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        testHigherHillClimbing(4, spectralMaps, cytometer, prefix);
        testHigherHillClimbing(5, spectralMaps, cytometer, prefix);
        testHigherHillClimbing(6, spectralMaps, cytometer, prefix);
        testHigherHillClimbing(7, spectralMaps, cytometer, prefix);
        testHigherHillClimbing(8, spectralMaps, cytometer, prefix);
    }

    public static void testHigherHillClimbing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = HillClimbingSelection.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "HC_" + prefix + "_" + n + "fp.csv", lines);

    }

}
