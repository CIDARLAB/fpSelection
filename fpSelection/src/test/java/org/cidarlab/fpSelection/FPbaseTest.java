package org.cidarlab.fpSelection;


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
import org.cidarlab.fpSelection.algorithms.SimulatedAnnealingThread;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import java.util.Random;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.cidarlab.fpSelection.TestUtilities;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author prash
 */
public class FPbaseTest {

    private static String basefp = Utilities.getCaseStudyFilepath();

    private static String spectrafp = basefp + "fpbase" + Utilities.getSeparater() + "spectra.csv";
    
    private static String harvardFortessafp = basefp + "HarvardFortessa.csv";
    private static String harvardMacsquantfp = basefp + "HarvardMacsquant.csv";
    private static String harvardCytoFlexfp = basefp + "HarvardCytoFlex.csv";

    private static ThreadLocal<Random> threadrandom = Utilities.threadRandom(0);
    
    @Test
    public void testFPbase()  throws IOException, InterruptedException{

        Cytometer harvardFortessa = fpFortessaParse.parse(harvardFortessafp, false);
        Cytometer harvardMacsquant = fpFortessaParse.parse(harvardMacsquantfp, false);
        Cytometer harvardCytoflex = fpFortessaParse.parse(harvardCytoFlexfp, false);


        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(spectrafp);
        System.out.println(caseStudySpectralMap.size());

        runSA(caseStudySpectralMap,harvardCytoflex,10);

    }

    @Test
    public void testIterationCount(){
        double temp = 1000000;
        double rate = 0.001;

        int count = 0;

        while(temp > 1){
            count++;
            temp *= (1 - rate);
        }
        Assert.assertEquals(count,13809);

    }

    @Test
    public void testRandom(){
        
        int a = Utilities.getRandom(0,5,threadrandom);
        Assert.assertEquals(a,0);
        a = Utilities.getRandom(0,5,threadrandom);
        Assert.assertEquals(a,4);
        a = Utilities.getRandom(0,5,threadrandom);
        Assert.assertEquals(a,1);
        a = Utilities.getRandom(0,5,threadrandom);
        Assert.assertEquals(a,5);
        a = Utilities.getRandom(0,5,threadrandom);
        Assert.assertEquals(a,5);
    }

    

    private static String toString(List<SelectionInfo> selection) {
        Map<String, Integer> maps = new HashMap<>();

        int n = selection.size();
        for (int i = 0; i < n; i++) {
            maps.put(selection.get(i).getSelectedFluorophore().name, i);
        }
        List<String> keys = new ArrayList<>(maps.keySet());

        String line = "";
        for (int i = 0; i < n - 1; i++) {
            String key = keys.get(i);
            line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
            //line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue() + ",";
        }
        String key = keys.get(n - 1);
        line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
        //line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue();
        return line;
    }
    


    private static void runSA(Map<String,Fluorophore> fpmap, Cytometer cyto, int n){
        
        List<String> lines = new ArrayList<>();
        List<Map.Entry<List<SelectionInfo>, SNR>> uniqueResults = new ArrayList<>();

        long current = System.currentTimeMillis();
        
            
        for(int i=0;i<5000;i++){
            List<SelectionInfo> selection = TestUtilities.runSeededSA(fpmap,cyto,n,1000000.0,0.001,threadrandom);
            SNR snr = new SNR(selection);
            uniqueResults.add(new AbstractMap.SimpleEntry<>(selection, snr));
            
        }

        System.out.println("Time taken = " + ((System.currentTimeMillis() - current)) + " milliseconds.");

        Collections.sort(uniqueResults, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
            SNR s1 = o1.getValue();
            SNR s2 = o2.getValue();
            return s1.compare(s2);
        });
        Collections.reverse(uniqueResults);

        for (Map.Entry<List<SelectionInfo>, SNR> entry : uniqueResults) {
            List<SelectionInfo> selection = entry.getKey();
            String line = toString(selection);
            lines.add(line);
        }

        Utilities.writeToFile(basefp + "fpbase" + Utilities.getSeparater() + "results.csv",lines);
        
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FPbaseTest testinstance = new FPbaseTest();
        testinstance.testFPbase();
    }

}