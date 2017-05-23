/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelectionMultiThreaded;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.algorithms.SimulatedAnnealing;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.FPParser;
import org.cidarlab.fpSelection.parsers.MetadataParser;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;

/**
 *
 * @author prash
 */
public class HarvardTest {
    
    private static String chromaFolder = Utilities.getResourcesFilepath() + "Chroma" + Utilities.getSeparater();
    private static String FluorophoresOrgFolder = Utilities.getResourcesFilepath() + "Fluorophores.org" + Utilities.getSeparater();
    private static String ManuallyCuratedFolder = Utilities.getResourcesFilepath() + "ManuallyCurated" + Utilities.getSeparater();
    
    
    private static Set<String> initialize(){
        Set<String> fps = new HashSet<String>();
        
//        fps.add("Sirius");
//        fps.add("CFP");
//        fps.add("EGFP");
//        fps.add("copGFP");
//        fps.add("eYFP");
//        fps.add("mOrange");
//        fps.add("tdTomato");
//        fps.add("DsRed2");
//        fps.add("mApple");
//        fps.add("TagRFP");
//        fps.add("mCherry");
//        fps.add("mScarlet");
//        fps.add("iRFP670");
//        fps.add("iRFP713");
//        fps.add("iRFP720");
//        fps.add("EYFP");
//        fps.add("mPlum");
//        fps.add("mKO");
//        fps.add("ECFP");
//        fps.add("mKate2");
//        fps.add("Cerulean");
//        fps.add("EBFP2");
        
        fps.add("iRFP670");
        fps.add("Sirius");
        fps.add("ECFP");
        fps.add("mKO");
        fps.add("iRFP713");
        fps.add("mKate2");
        fps.add("tagRFP");
        fps.add("mOrange");
        fps.add("mApple");
        fps.add("mCherry");
        fps.add("mPlum");
        fps.add("EBFP2");
        fps.add("EYFP");
        
        return fps;
    }
    
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Set<String> list = initialize();
        
        
        System.out.println(Utilities.getResourcesFilepath());
        Map<String, Fluorophore> fps = FPParser.parseChroma(chromaFolder);

        try {
            fps.putAll(fpSpectraParse.parse(ManuallyCuratedFolder + "fp_spectra.csv"));
        } catch (IOException ex) {
            Logger.getLogger(HarvardTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        fps.putAll(FPParser.parseFluorophoreOrg(FluorophoresOrgFolder));
        
        String pathcytometer = Utilities.getResourcesFilepath() + "HarvardFortessa.csv";
        Cytometer c = fpFortessaParse.parse(pathcytometer, false);
      
        String metadatafilepath = Utilities.getResourcesFilepath() + "fluorophore_meta_data.csv";
        Map<String, Fluorophore> metadata = MetadataParser.parse(metadatafilepath);
        
        Map<String, Fluorophore> finalFPList = new HashMap<String, Fluorophore>();
        
        for(String fp:list){
            if(metadata.containsKey(fp)){
                if(fps.containsKey(fp)){
                    if(metadata.get(fp).brightness > 0){
                        //System.out.println(fp);
                        //System.out.println("brightness :: " + metadata.get(fp).brightness);
                        Fluorophore finalfp = fps.get(fp);
                        finalfp.rewriteEMBrightness(metadata.get(fp).brightness);
                        finalFPList.put(fp, finalfp);
                    }
                }
                
            }
        }
        String resultFilepath = Utilities.getResourcesFilepath() + "HarvardTestResult.txt";
        List<String> lines = new ArrayList<String>();
        int startIndx = 1;
        int endIndx = 6;
        for(int i=startIndx;i<=endIndx;i++){
            long start = System.nanoTime();
            
            //Exhaustive approach
//            ExhaustiveSelectionMultiThreaded multithreaded = new ExhaustiveSelectionMultiThreaded();
//            ArrayList<SelectionInfo> result = multithreaded.run(i, finalFPList, c,10);
            
            //Hill Climbing
//            HillClimbingSelection hill = new HillClimbingSelection();
//            ArrayList<SelectionInfo> result = hill.run(i, finalFPList,c);
            
            //SimulatedAnnealing
            SimulatedAnnealing anneal = new SimulatedAnnealing();
            ArrayList<SelectionInfo> result = anneal.run(i, finalFPList,c);
            
            //System.out.println("RESULT " + i + " === FPs ===");
            long end = System.nanoTime();
            double elapsed = elapsedTime(start,end);
            System.out.println("RESULT " + i + " === FPs ===");
            lines.add("RESULT " + i + " === FPs ===");
            for(SelectionInfo si : result){

                lines.add(si.getFP().name);
                lines.add("SNR      :: " + si.SNR);
                lines.add("Laser    :: " + si.selectedLaser.name);
                lines.add("Detector :: " + si.selectedDetector.identifier);
                lines.add("--------------------------------------");
            }
            System.out.println(i + " completed in " + elapsed + " seconds.");
            //System.out.println("======================================\n\n");
            lines.add("======================================\n\n");
            
        }
        Utilities.writeToFile(resultFilepath, lines);
        
    }
    private static double elapsedTime(long start, long end){
        long elapsed = end - start;
        double seconds = (double)elapsed / 1000000000.0;
        return seconds;
    }
}
