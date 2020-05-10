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
import static org.cidarlab.fpSelection.CaseStudyTest.filterCombinations;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import static org.cidarlab.fpSelection.algorithms.HillClimbingSelection.getRandomDetectors;
import static org.cidarlab.fpSelection.algorithms.HillClimbingSelection.getRandomFluorophores;
import static org.cidarlab.fpSelection.algorithms.HillClimbingSelection.getSelection;
import org.cidarlab.fpSelection.algorithms.RandomWalk;
import org.cidarlab.fpSelection.algorithms.SimulatedAnnealing;
import static org.cidarlab.fpSelection.algorithms.SimulatedAnnealingThread.acceptanceProbability;
import static org.cidarlab.fpSelection.algorithms.SimulatedAnnealingThread.swapDetector;
import static org.cidarlab.fpSelection.algorithms.SimulatedAnnealingThread.swapFluorophore;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
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
    
    private static int exhaustiveLim = 1000000;
    
    @Test
    public void testExhausiveCount()  throws IOException, InterruptedException {
        Cytometer harvardFortessa = fpFortessaParse.parse(harvardFortessafp, false);
        //Cytometer harvardSony = fpFortessaParse.parse(harvardSonyfp, false);
        Cytometer harvardMacsquant = fpFortessaParse.parse(harvardMacsquantfp, false);
        Cytometer harvardCytoflex = fpFortessaParse.parse(harvardCytoFlexfp, false);
        

        String spectra = basefp + "inputFiles" + Utilities.getSeparater() + "set9" + Utilities.getSeparater() + "spectra.csv";
        String brightness = basefp + "inputFiles" + Utilities.getSeparater() + "set9" + Utilities.getSeparater() + "brightness.csv";
        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(spectra);
        fpSpectraParse.addBrightness(new File(brightness), caseStudySpectralMap);
        
        
        Map<String,Cytometer> cytos = new HashMap<String,Cytometer>();
        

        //cytos.put("Macs", harvardMacsquant);
        //cytos.put("Fort",harvardFortessa);
        cytos.put("Flex",harvardCytoflex);
        
        for(String key:cytos.keySet()){
            Cytometer cyto = cytos.get(key);
            for(int n = 2; n<=4;n++){                                
                List<Map.Entry<List<SelectionInfo>, SNR>> results = getExhaustiveCount(n,caseStudySpectralMap,cyto);                
            }
                    
        }

        
    }

    @Test
    public void testStochasticAlgorithms() throws IOException, InterruptedException {
        Cytometer harvardFortessa = fpFortessaParse.parse(harvardFortessafp, false);
        //Cytometer harvardSony = fpFortessaParse.parse(harvardSonyfp, false);
        Cytometer harvardMacsquant = fpFortessaParse.parse(harvardMacsquantfp, false);
        Cytometer harvardCytoflex = fpFortessaParse.parse(harvardCytoFlexfp, false);
        

        String spectra = basefp + "inputFiles" + Utilities.getSeparater() + "set9" + Utilities.getSeparater() + "spectra.csv";
        String brightness = basefp + "inputFiles" + Utilities.getSeparater() + "set9" + Utilities.getSeparater() + "brightness.csv";
        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(spectra);
        fpSpectraParse.addBrightness(new File(brightness), caseStudySpectralMap);
        
        String resfp = basefp + "performance" + Utilities.getSeparater();

        List<Fluorophore> fluorophores = new ArrayList<Fluorophore>(caseStudySpectralMap.values());
        Map<String,Cytometer> cytos = new HashMap<String,Cytometer>();
        

        //cytos.put("Macs", harvardMacsquant);
        //cytos.put("Fort",harvardFortessa);
        cytos.put("Flex",harvardCytoflex);
        
        for(String key:cytos.keySet()){
            Cytometer cyto = cytos.get(key);
            for(int n = 4; n<=6;n++){                
                
                List<Map.Entry<List<SelectionInfo>, SNR>> results = exhaustiveRunList(n,caseStudySpectralMap,cyto);
                List<Detector> detectors = new ArrayList<Detector>();
                Map<Detector,Laser> detectorMap = new HashMap<>();
                
                for(Laser l:cyto.lasers){
                    for(Detector d:l.detectors){
                        detectors.add(d);
                        detectorMap.put(d, l);
                    }
                }
                System.out.println("Finished Exhaustive Search. Total Number of Solutions: " + results.size());

                List<String> hashedresult = new ArrayList<String>();
                for(Map.Entry<List<SelectionInfo>, SNR> entry:results){
                    hashedresult.add(selectionInfoToString(entry.getKey()));
                }

                System.out.println("Finished Creating a hash.");
            
                List<String> salines = new ArrayList<>();
                List<String> hclines = new ArrayList<>();
                List<String> rwlines = new ArrayList<>();
                
                
                for(int i=0;i<200;i++){
                    System.out.println("Start SA iteration - " + i);
                    String line = getSARankList(hashedresult,n,fluorophores,detectors,detectorMap);
                    salines.add(line);
                }
                
                for(int i=0;i<200;i++){
                    System.out.println("Start HC iteration - " + i);
                    String line = getHCRankList(hashedresult,n,caseStudySpectralMap,cyto);
                    hclines.add(line);
                }
                


                /*
                for(int i=0;i<200;i++){
                    System.out.println("Start RW iteration - " + i);
                    String line = getRWRankList(hashedresult,n,caseStudySpectralMap,cyto);
                    rwlines.add(line);
                }
                */
                
                String prefix = key + n;
                String safilefp = resfp + prefix + "sarank.csv";
                Utilities.writeToFile(safilefp, salines);
                
                String hcfilefp = resfp + prefix + "hcrank.csv";
                Utilities.writeToFile(hcfilefp, hclines);
                
                //String rwfilefp = resfp + prefix + "rwrank.csv";
                //Utilities.writeToFile(rwfilefp, rwlines);
            }
                    
        }

        
        
        


    }
    
    public static String selectionInfoToString(List<SelectionInfo> si){
        List<String> sinfos = new ArrayList<String>();
        
        for(SelectionInfo s:si){
            String line = s.selectedFluorophore.name + s.selectedLaser.getName() + s.selectedDetector.identifier;
            sinfos.add(line);
        }
        Collections.sort(sinfos);
        
        String str = "";
        for(String s:sinfos){
            str += s;
        }
        
        return str;
        
    }
    
    public static String getSARankList(List<String> results, int n, List<Fluorophore> fluorophores, List<Detector> detectors, Map<Detector, Laser> detectorMap) {
        List<Fluorophore> currentFluorophores = HillClimbingSelection.getRandomFluorophores(n, fluorophores);
        List<Detector> currentDetectors = HillClimbingSelection.getRandomDetectors(n, detectors);

        List<SelectionInfo> current = HillClimbingSelection.getSelection(currentFluorophores, currentDetectors, detectorMap);
        SNR bestSNR = new SNR(current);
        
        List<SelectionInfo> best = current;
        SNR currentSNR = bestSNR;
        double temp = 10000;
        double rate = 0.001;
        
        
        String line = "";
        
        while (temp > 1) {

            int swapIndex = Utilities.getRandom(0, n - 1);

            List<Fluorophore> nextFluorophores;
            List<Detector> nextDetectors;

            if (Utilities.getRandom(0, 1) == 0) {
                //Heads Swap an FP
                nextFluorophores = swapFluorophore(swapIndex, currentFluorophores, fluorophores);
                nextDetectors = new ArrayList<>(currentDetectors);
            } else {
                //Tails Swap Detector
                nextFluorophores = new ArrayList<>(currentFluorophores);
                nextDetectors = swapDetector(swapIndex, currentDetectors, detectors);
            }
            List<SelectionInfo> next = HillClimbingSelection.getSelection(nextFluorophores, nextDetectors, detectorMap);
            SNR nextSNR = new SNR(next);

            if (nextSNR.greaterThan(bestSNR)) {
                bestSNR = nextSNR;
                best = new ArrayList<>(next);
            }
            
            int rank = exhaustiveLim + 10000;
            if (results.contains(selectionInfoToString(best))) {
                rank = results.indexOf(selectionInfoToString(best));
            }
            //System.out.println("Current Best Rank" + rank);
            line += (rank + ",");
            double random = Math.random();
            double ap = acceptanceProbability(currentSNR,nextSNR,temp);
            if(random <= ap){
                //SWAP!!!
                current = new ArrayList<>(next);
                currentSNR = nextSNR;
                currentFluorophores = new ArrayList<>(nextFluorophores);
                currentDetectors = new ArrayList<>(nextDetectors);
                
            }
            //System.out.println("Thread " + id + " current temperature = " + temp);
            temp *= (1 - rate);
        }

        return line;

    }
    
    public static String getHCRankList(List<String> results, int n, Map<String, Fluorophore> masterList,Cytometer cyto){
        
        String line = "";
        int iterations = 10000;
        List<Fluorophore> fluorophores = new ArrayList<>(masterList.values());
        Map<Detector,Laser> detectorMap = new HashMap<>();
        List<Detector> detectors = new ArrayList<>();
        for(Laser laser:cyto.lasers){
            for(Detector detector:laser.detectors){
                detectorMap.put(detector, laser);
                detectors.add(detector);
            }
        }
        List<Fluorophore> selectedFluorophores = getRandomFluorophores(n,fluorophores);
        List<Detector> selectedDetectors = getRandomDetectors(n,detectors);
        List<SelectionInfo> bestSelection = getSelection(selectedFluorophores, selectedDetectors, detectorMap);
        SNR currentSNR = new SNR(bestSelection);    
        SNR bestSNR = new SNR(bestSelection);
        
       for(int i=0;i<iterations;i++){
            int index = Utilities.getRandom(0, n-1);
            List<Fluorophore> nextFluorophores = new ArrayList<>(selectedFluorophores);
            List<Detector> nextDetectors = new ArrayList<>(selectedDetectors);
            
            if(Utilities.getRandom(0, 1) == 0){
                //Heads: Swap a Fluorophore
                nextFluorophores = swapFluorophore(index,selectedFluorophores,fluorophores);
            } else {
                //Tails: Swap a Detector
                nextDetectors = swapDetector(index,selectedDetectors,detectors);
            }
            List<SelectionInfo> nextSelection = getSelection(nextFluorophores, nextDetectors, detectorMap);
            SNR nextSNR = new SNR(nextSelection);
            if(nextSNR.greaterThan(bestSNR)){
                bestSNR = nextSNR;
                bestSelection = new ArrayList<>(nextSelection);
            }
            
            
            //int rank = results.indexOf(selectionInfoToString(bestSelection));
            int rank = exhaustiveLim + 10000;
            if (results.contains(selectionInfoToString(bestSelection))) {
                rank = results.indexOf(selectionInfoToString(bestSelection));
            }
            //System.out.println("Current Best Rank" + rank);
            line += (rank + ",");
            
            if(nextSNR.greaterThan(currentSNR)){
                selectedFluorophores = new ArrayList<>(nextFluorophores);
                selectedDetectors = new ArrayList<>(nextDetectors);
                currentSNR = nextSNR;
            }
            
        }
        return line;

    }
    
    public static String getRWRankList(List<String> results, int n, Map<String, Fluorophore> spectralMaps,Cytometer cytometer){
        
        String line = "";
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
        
        for(int i=0;i<10000;i++){
            currentFluorophores = HillClimbingSelection.getRandomFluorophores(n, fluorophores);
            currentDetectors = HillClimbingSelection.getRandomDetectors(n, detectors);
            selection = HillClimbingSelection.getSelection(currentFluorophores, currentDetectors, detectorMap);
            snr = new SNR(selection);
            
            if(snr.greaterThan(bestsnr)){
                best = new ArrayList<>(selection);
                bestsnr = new SNR(best);
            }
            
            int rank = exhaustiveLim + 10000;
            if (results.contains(selectionInfoToString(best))) {
                rank = results.indexOf(selectionInfoToString(best));
            }
            
            //System.out.println("Current Best Rank" + rank);
            line += (rank + ",");
        }
        
        return line;

    }

    private static Map<String,Map<String,Double>> getLims(){
        Map<String,Map<String,Double>> lims = new HashMap<>();
        Map<String,Double> mScarlet = new HashMap<>();
        mScarlet.put("exl", 384.0);
        mScarlet.put("exh", 644.0);
        mScarlet.put("eml", 538.0);
        mScarlet.put("emh", 773.0);
        
        Map<String,Double> mCherry = new HashMap<>();
        mCherry.put("exl", 300.0);
        mCherry.put("exh", 650.0);
        mCherry.put("eml", 550.0);
        mCherry.put("emh", 800.0);
        
        Map<String,Double> mOrange = new HashMap<>();
        mOrange.put("exl", 300.0);
        mOrange.put("exh", 600.0);
        mOrange.put("eml", 530.0);
        mOrange.put("emh", 700.0);
        
        Map<String,Double> dsRed2 = new HashMap<>();
        dsRed2.put("exl", 351.0);
        dsRed2.put("exh", 596.0);
        dsRed2.put("eml", 562.0);
        dsRed2.put("emh", 712.0);
        
        Map<String,Double> irfp713 = new HashMap<>();
        irfp713.put("exl", 535.0);
        irfp713.put("exh", 726.0);
        irfp713.put("eml", 650.0);
        irfp713.put("emh", 800.0);
        
        Map<String,Double> sirius = new HashMap<>();
        sirius.put("exl", 304.0);
        sirius.put("exh", 422.0);
        sirius.put("eml", 381.0);
        sirius.put("emh", 551.0);
        
        Map<String,Double> cerulean = new HashMap<>();
        cerulean.put("exl", 300.0);
        cerulean.put("exh", 700.0);
        cerulean.put("eml", 433.0);
        cerulean.put("emh", 700.0);
        
        Map<String,Double> ko = new HashMap<>();
        ko.put("exl", 300.0);
        ko.put("exh", 570.0);
        ko.put("eml", 520.0);
        ko.put("emh", 700.0);
        
        Map<String,Double> mPlum = new HashMap<>();
        mPlum.put("exl", 450.0);
        mPlum.put("exh", 630.0);
        mPlum.put("eml", 584.0);
        mPlum.put("emh", 800.0);
        
        Map<String,Double> tagRFP = new HashMap<>();
        tagRFP.put("exl", 320.0);
        tagRFP.put("exh", 600.0);
        tagRFP.put("eml", 535.0);
        tagRFP.put("emh", 750.0);
        
        Map<String,Double> tdTomato = new HashMap<>();
        tdTomato.put("exl", 300.0);
        tdTomato.put("exh", 630.0);
        tdTomato.put("eml", 550.0);
        tdTomato.put("emh", 700.0);
        
        Map<String,Double> irfp720 = new HashMap<>();
        irfp720.put("exl", 535.0);
        irfp720.put("exh", 729.0);
        irfp720.put("eml", 650.0);
        irfp720.put("emh", 800.0);
        
        lims.put("mScarlet", mScarlet);
        lims.put("mCherry", mCherry);
        lims.put("mOrange", mOrange);
        lims.put("DsRed2", dsRed2);
        lims.put("iRFP713", irfp713);
        lims.put("Sirius", sirius);
        lims.put("Cerulean", cerulean);
        lims.put("KO", ko);
        lims.put("mPlum", mPlum);
        lims.put("TagRFP", tagRFP);
        lims.put("tdTomato", tdTomato);
        lims.put("iRFP720", irfp720);
        
        return lims;
        
        
    }

    
    private static boolean weHaveSignalData(List<SelectionInfo> selection){
        
        Map<String,Map<String,Double>> lims = getLims();
        
        for(SelectionInfo info:selection){
            int laserwl = info.getSelectedLaser().wavelength;
            double detectorl = info.getSelectedDetector().filterMidpoint - (info.getSelectedDetector().filterWidth/2.0);
            double detectorh = info.getSelectedDetector().filterMidpoint + (info.getSelectedDetector().filterWidth/2.0);
            
            String fp = info.getSelectedFluorophore().name;
            if ((laserwl < lims.get(fp).get("exl")) || (laserwl > lims.get(fp).get("exh"))) {
                return false;
            }

            if (detectorl < lims.get(fp).get("eml")) {
                return false;
            }
            if (detectorh > lims.get(fp).get("emh")) {
                return false;
            }
        }
        return true;
    }

    
    private static List<Map.Entry<List<SelectionInfo>, SNR>> getExhaustiveCount(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer) throws IOException, InterruptedException {
        
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
                
                
                if(weHaveSignalData(currentSelection)){
                    count++;
                } else {
                    continue;
                }
                
            }
        }
        
        System.out.println("Number of solutions for " + n + " fp are: " + count );

        return results;

    }
    
    

    private static List<Map.Entry<List<SelectionInfo>, SNR>> exhaustiveRunList(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer) throws IOException, InterruptedException {
        
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
                
                
                if(weHaveSignalData(currentSelection)){
                    count++;
                } else {
                    continue;
                }
                

                SNR snr = new SNR(currentSelection);
                
                
                if (results.size() < (exhaustiveLim-1) ){
                    results.add(new AbstractMap.SimpleEntry<>(currentSelection, snr));
                } else if (results.size() == (exhaustiveLim-1)) {
                    results.add(new AbstractMap.SimpleEntry<>(currentSelection, snr));
                    Collections.sort(results, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
                        SNR s1 = o1.getValue();
                        SNR s2 = o2.getValue();
                        return s1.compare(s2);
                    });
                    Collections.reverse(results);
                } else {
                    
                    if(snr.compare(results.get(exhaustiveLim-1).getValue()) > 0){
                        //If SNR is "Better"
                        int replaceAt = 0;
                        for(int i=0;i<results.size();i++){
                            if(snr.compare(results.get(i).getValue()) > 0){
                                replaceAt = i;
                                break;
                            } else {
                                
                            }
                        }
                        results.add(replaceAt,new AbstractMap.SimpleEntry<>(currentSelection, snr));
                        results.remove(exhaustiveLim);
                    } else {
                        
                    }
                }
                
                //results.add(new AbstractMap.SimpleEntry<>(currentSelection, snr));
                

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
        
        System.out.println("Number of solutions for " + n + " fp are: " + count );

        return results;

    }
    
    
    //@Test
    public void getRuntimes() throws IOException, InterruptedException{
        Cytometer harvardFortessa = fpFortessaParse.parse(harvardFortessafp, false);
        Cytometer harvardMacsquant = fpFortessaParse.parse(harvardMacsquantfp, false);
        Cytometer harvardCytoflex = fpFortessaParse.parse(harvardCytoFlexfp, false);
        
        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(caseSpectrafp);
        fpSpectraParse.addBrightness(new File(caseBrightnessfp), caseStudySpectralMap);
        
        List<String> lines = new ArrayList<>();
        
        String flexex = "Flex,EX,";
        String flexsa = "Flex,SA,";
        String flexhc = "Flex,HC,";
        
        String fortex = "Fort,EX,";
        String fortsa = "Fort,SA,";
        String forthc = "Fort,HC,";
        
        String macsex = "Macs,EX,";
        String macssa = "Macs,SA,";
        String macshc = "Macs,HC,";
        
        lines.add("Cyto,Algo,2,3,4,5,6,");
        
        for(int i=2;i<=6;i++){
            System.out.println("Starting SA Runs for n = " + i);
            long fortsatimes = 0;
            long flexsatimes = 0;
            long macssatimes = 0;
            for(int j=0;j<200;j++){
                flexsatimes += (testSAPerformance(i,caseStudySpectralMap,harvardCytoflex));
                fortsatimes += (testSAPerformance(i,caseStudySpectralMap,harvardFortessa));
                macssatimes += (testSAPerformance(i,caseStudySpectralMap,harvardMacsquant));
            }
            double flexsatime = flexsatimes/(200.0);
            flexsa += (flexsatime + ",");
            double fortsatime = fortsatimes/(200.0);
            fortsa += (fortsatime + ",");
            double macssatime = macssatimes/(200.0);
            macssa += (macssatime + ",");
        }
        lines.add(flexsa);
        lines.add(fortsa);
        lines.add(macssa);
        
        
        for(int i=2;i<=6;i++){
            System.out.println("Starting HC Runs for n = " + i);
            long flexhctimes = 0;
            long forthctimes = 0;
            long macshctimes = 0;
            for(int j=0;j<200;j++){
                flexhctimes += (testHCPerformance(i,caseStudySpectralMap,harvardCytoflex));
                forthctimes += (testHCPerformance(i,caseStudySpectralMap,harvardFortessa));
                macshctimes += (testHCPerformance(i,caseStudySpectralMap,harvardMacsquant));
            }
            double flexhctime = flexhctimes/(200.0);
            flexhc += (flexhctime + ",");
            double forthctime = forthctimes/(200.0);
            forthc += (forthctime + ",");
            double macshctime = macshctimes/(200.0);
            macshc += (macshctime + ",");
        }
        lines.add(flexhc);
        lines.add(forthc);
        lines.add(macshc);
        
        
        for(int i=2;i<=5;i++){
            System.out.println("Starting EX Runs for n = " + i);
            long flexextime = testExhaustivePerformance(i,caseStudySpectralMap,harvardCytoflex);
            flexex += (flexextime + ",");
            long fortextime = testExhaustivePerformance(i,caseStudySpectralMap,harvardFortessa);
            fortex += (fortextime + ",");
            long macsextime = testExhaustivePerformance(i,caseStudySpectralMap,harvardMacsquant);
            macsex += (macsextime + ",");
        }
        
        
        flexex += ",";
        fortex += ",";
        long macsextime = testExhaustivePerformance(6,caseStudySpectralMap,harvardMacsquant);
        macsex += (macsextime + ",");
        
        lines.add(flexex);
        lines.add(fortex);
        lines.add(macsex);
        
        String runtimesfp = basefp + "runtimes.csv";
        Utilities.writeToFile(runtimesfp, lines);
    }
    
    public static long testExhaustivePerformance(int n, Map<String, Fluorophore> maps, Cytometer c) throws IOException{
        long current = 0;
        current = System.currentTimeMillis();
        ExhaustiveSelection.run(n, maps, c);
        return ((System.currentTimeMillis() - current));
    }
    
    public static long testSAPerformance(int n, Map<String, Fluorophore> maps, Cytometer c) {
        
        List<Fluorophore> fluorophores = new ArrayList<Fluorophore>(maps.values());
        List<Detector> detectors = new ArrayList<Detector>();
        Map<Detector,Laser> detectorMap = new HashMap<>();
        
        
        for(Laser l:c.lasers){
            for(Detector d:l.detectors){
                detectors.add(d);
                detectorMap.put(d, l);
            }
        }
        
        long currenttime = 0;
        currenttime = System.currentTimeMillis();
        
        List<Fluorophore> currentFluorophores = HillClimbingSelection.getRandomFluorophores(n, fluorophores);
        List<Detector> currentDetectors = HillClimbingSelection.getRandomDetectors(n, detectors);

        List<SelectionInfo> current = HillClimbingSelection.getSelection(currentFluorophores, currentDetectors, detectorMap);
        SNR bestSNR = new SNR(current);
        
        List<SelectionInfo> best = current;
        SNR currentSNR = bestSNR;
        double temp = 10000;
        double rate = 0.001;
        
        while (temp > 1) {

            int swapIndex = Utilities.getRandom(0, n - 1);

            List<Fluorophore> nextFluorophores;
            List<Detector> nextDetectors;

            if (Utilities.getRandom(0, 1) == 0) {
                //Heads Swap an FP
                nextFluorophores = swapFluorophore(swapIndex, currentFluorophores, fluorophores);
                nextDetectors = new ArrayList<>(currentDetectors);
            } else {
                //Tails Swap Detector
                nextFluorophores = new ArrayList<>(currentFluorophores);
                nextDetectors = swapDetector(swapIndex, currentDetectors, detectors);
            }
            List<SelectionInfo> next = HillClimbingSelection.getSelection(nextFluorophores, nextDetectors, detectorMap);
            SNR nextSNR = new SNR(next);

            if (nextSNR.greaterThan(bestSNR)) {
                bestSNR = nextSNR;
                best = new ArrayList<>(next);
            }
            
            double random = Math.random();
            double ap = acceptanceProbability(currentSNR,nextSNR,temp);
            if(random <= ap){
                //SWAP!!!
                current = new ArrayList<>(next);
                currentSNR = nextSNR;
                currentFluorophores = new ArrayList<>(nextFluorophores);
                currentDetectors = new ArrayList<>(nextDetectors);
                
            }
            //System.out.println("Thread " + id + " current temperature = " + temp);
            temp *= (1 - rate);
        }

        return ((System.currentTimeMillis() - currenttime));
    }
    
    public static long testHCPerformance(int n, Map<String, Fluorophore> masterList,Cytometer cyto){
        long currenttime = 0;
        currenttime = System.currentTimeMillis();
        
        int iterations = 10000;
        List<Fluorophore> fluorophores = new ArrayList<>(masterList.values());
        Map<Detector,Laser> detectorMap = new HashMap<>();
        List<Detector> detectors = new ArrayList<>();
        for(Laser laser:cyto.lasers){
            for(Detector detector:laser.detectors){
                detectorMap.put(detector, laser);
                detectors.add(detector);
            }
        }
        List<Fluorophore> selectedFluorophores = getRandomFluorophores(n,fluorophores);
        List<Detector> selectedDetectors = getRandomDetectors(n,detectors);
        List<SelectionInfo> bestSelection = getSelection(selectedFluorophores, selectedDetectors, detectorMap);
        SNR currentSNR = new SNR(bestSelection);    
        SNR bestSNR = new SNR(bestSelection);
        
       for(int i=0;i<iterations;i++){
            int index = Utilities.getRandom(0, n-1);
            List<Fluorophore> nextFluorophores = new ArrayList<>(selectedFluorophores);
            List<Detector> nextDetectors = new ArrayList<>(selectedDetectors);
            
            if(Utilities.getRandom(0, 1) == 0){
                //Heads: Swap a Fluorophore
                nextFluorophores = swapFluorophore(index,selectedFluorophores,fluorophores);
            } else {
                //Tails: Swap a Detector
                nextDetectors = swapDetector(index,selectedDetectors,detectors);
            }
            List<SelectionInfo> nextSelection = getSelection(nextFluorophores, nextDetectors, detectorMap);
            SNR nextSNR = new SNR(nextSelection);
            if(nextSNR.greaterThan(bestSNR)){
                bestSNR = nextSNR;
                bestSelection = new ArrayList<>(nextSelection);
            }
            
            
            if(nextSNR.greaterThan(currentSNR)){
                selectedFluorophores = new ArrayList<>(nextFluorophores);
                selectedDetectors = new ArrayList<>(nextDetectors);
                currentSNR = nextSNR;
            }
            
        }
        return ((System.currentTimeMillis() - currenttime));

    }
    
    
    
    @Test
    public void testNumberOfRunsSimmAnn(){
        double temp = 10000;
        double rate = 0.001;
        int count = 0;
        
        while(temp > 1){       
            count++;
            temp *= (1 - rate);
        }
        
        System.out.println("Number of iterations:: " + count);
            
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
