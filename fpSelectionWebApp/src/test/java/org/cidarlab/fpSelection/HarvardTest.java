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
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelectionMultiThreaded;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.FPParser;
import org.cidarlab.fpSelection.parsers.MetadataParser;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author prash
 */
public class HarvardTest {
    
    public HarvardTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    private static String chromaFolder = Utilities.getResourcesFilepath() + "Chroma" + Utilities.getSeparater();
    private static String FluorophoresOrgFolder = Utilities.getResourcesFilepath() + "Fluorophores.org" + Utilities.getSeparater();
    private static String ManuallyCuratedFolder = Utilities.getResourcesFilepath() + "ManuallyCurated" + Utilities.getSeparater();
    
    
    private static Set<String> initialize(){
        Set<String> fps = new HashSet<String>();
        
        fps.add("TagBFP");
        fps.add("CFP");
        fps.add("EGFP");
        fps.add("copGFP");
        fps.add("eYFP");
        fps.add("mOrange");
        fps.add("tdTomato");
        fps.add("DsRed2");
        fps.add("mApple");
        fps.add("TagRFP");
        fps.add("mCherry");
        fps.add("mScarlet");
        fps.add("iRFP670");
        fps.add("iRFP713");
        fps.add("iRFP720");
        fps.add("EYFP");
        fps.add("mPlum");
        fps.add("mKO");
        fps.add("ECFP");
        fps.add("mKate2");
        fps.add("Cerulean");
        fps.add("EBFP2");
        
        
        
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
        
//        for(String fp:fps.keySet()){
//            if(fp.contains("CFP")){
//                System.out.println(fp);
//            }
//            if(fp.contains("eYFP")){
//                System.out.println(fp);
//            }
//            if(fp.contains("copGFP")){
//                System.out.println(fp);
//            }
//            if(fp.contains("DsRed2")){
//                System.out.println(fp);
//            }
//        }
//        System.out.println(fps.size());
    
//        int count = 0;
//        for(String fp:list){
//            if(fps.containsKey(fp)){
//                count++;
//            } 
//        }
//        System.out.println("List Size :: " + list.size());
//        System.out.println("Total Count :: " + count);
        
        String pathcytometer = Utilities.getResourcesFilepath() + "HarvardFortessa.csv";
        Cytometer c = fpFortessaParse.parse(pathcytometer, false);
      
        String metadatafilepath = Utilities.getResourcesFilepath() + "fluorophore_meta_data.csv";
        Map<String, Fluorophore> metadata = MetadataParser.parse(metadatafilepath);
        
        Map<String, Fluorophore> finalFPList = new HashMap<String, Fluorophore>();
        
        for(String fp:list){
            if(metadata.containsKey(fp)){
                if(fps.containsKey(fp)){
                    if(metadata.get(fp).getBrightness() > 0){
                        //System.out.println(fp);
                        //System.out.println("brightness :: " + metadata.get(fp).brightness);
                        Fluorophore finalfp = fps.get(fp);
                        finalfp.rewriteEMBrightness(metadata.get(fp).getBrightness());
                        finalFPList.put(fp, finalfp);
                    }
                }
                
            }
        }
        for(int i=1;i<=10;i++){
            ExhaustiveSelectionMultiThreaded multithreaded = new ExhaustiveSelectionMultiThreaded();
            List<SelectionInfo> result = multithreaded.run(i, finalFPList, c,10);
            System.out.println("RESULT " + i + " === FPs ===");
            for(SelectionInfo si : result){
                
                System.out.println(si.getFP().name);
            }
            
            System.out.println("======================================\n\n");
            
        }
        
    }
    
    
}
