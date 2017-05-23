/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment;
import org.cidarlab.fpSelection.dom.AnalyticsPlot;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.parsers.FPParser;
import org.cidarlab.fpSelection.parsers.MetadataParser;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author prash
 */
public class DataAnalyticsTest {
    
    private String _path = "";
    private Map<String, Map<String,AnalyticsExperiment>> _result = new HashMap<String,Map<String,AnalyticsExperiment>>();  
    private String _plotfilepathroot = "";
    private static String FluorophoresOrgFolder = Utilities.getResourcesFilepath() + "Fluorophores.org" + Utilities.getSeparater();
        
    
    public DataAnalyticsTest() {
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

    private static void createFolder(String filepath){
        if(!Utilities.isDirectory(filepath)){
            Utilities.makeDirectory(filepath);
        }
    }
    
    @Test
    public void testCreateAllPlots() throws IOException{
        String mainTest = "FP_selection_R3";
        DataAnalyticsTest test = new DataAnalyticsTest();
        for(int i=1;i<=5;i++){
            test = new DataAnalyticsTest();
            test._path = Utilities.getResourcesFilepath()  + "fpSelectionData" + Utilities.getSeparater() + mainTest + Utilities.getSeparater() + "analysis" + getFolderSuffix(i) + Utilities.getSeparater();
            test._plotfilepathroot = Utilities.getResourcesFilepath() + "fpSelectionData" + Utilities.getSeparater()+ mainTest + Utilities.getSeparater() + "plots" + getFolderSuffix(i) + Utilities.getSeparater();
            createFolder(test._plotfilepathroot);
            test._result = DataAnalytics.walk(test._path);
            test.testOneMediaPlots(test._path,test._plotfilepathroot,test._result);
            test.testVoltagePlots(test._path,test._plotfilepathroot,test._result);
            test.testBeadsPlots(test._path,test._plotfilepathroot,test._result);
            test.testEcoliPlots(test._path,test._plotfilepathroot,test._result);
        }
        
    }
    
    private static String getFolderSuffix(int i){
        switch(i){
            case 0: 
                return "";
            case 1: 
                return "_autofluorescence";
            case 2:
                return "_base";
            case 3:
                return "_cellsize";
            case 4: 
                return "_curve1";
            case 5:
                return "_MEFL";
        }
        return "";
    }
    
    //@Test
    public void testVoltagePlots(String path, String plotfilepathroot, Map<String, Map<String,AnalyticsExperiment>> result){
        System.out.println("Test Voltage Plots");
        String plotfilepath = plotfilepathroot  + "voltage" + Utilities.getSeparater();
        createFolder(plotfilepath);
        Map<String, AnalyticsPlot> voltagePlots = DataAnalytics.getVoltagePlots(result.get("voltage"));
        for(AnalyticsPlot voltageplot : voltagePlots.values()){
            DataAnalytics.plotGraph(voltageplot, plotfilepath);
        }
    }
    
    //@Test
    public void testEcoliPlots(String path, String plotfilepathroot, Map<String, Map<String,AnalyticsExperiment>> result){
        System.out.println("Test Ecoli Plots");
        String plotfilepath = plotfilepathroot  + "ecoli" + Utilities.getSeparater();
        createFolder(plotfilepath);
        Map<String, AnalyticsPlot> ecoliplots = DataAnalytics.getEcoliPlots(result.get("ecoli"));
        for(AnalyticsPlot ecoliplot : ecoliplots.values()){
            DataAnalytics.plotGraph(ecoliplot, plotfilepath);
        }
    }
    
    //@Test
    public void testBeadsPlots(String path, String plotfilepathroot, Map<String, Map<String,AnalyticsExperiment>> result){
        System.out.println("Test Beads Plots");
        String plotfilepath = plotfilepathroot + "beads" + Utilities.getSeparater();
        createFolder(plotfilepath);
        Map<String, AnalyticsPlot> beadsplots = DataAnalytics.getBeadsPlots(result.get("beads"));
        for(AnalyticsPlot beadsplot : beadsplots.values()){
            DataAnalytics.plotGraph(beadsplot, plotfilepath);
        }
    }
    
    //@Test
    public void testOneMediaPlots(String path, String plotfilepathroot, Map<String, Map<String,AnalyticsExperiment>> result) throws IOException{
        System.out.println("Test OneMedia Plots");
        String plotfilepath = plotfilepathroot  + "oneMedia" + Utilities.getSeparater();
        createFolder(plotfilepath);
        Map<String, AnalyticsPlot> oneMediaPlots = DataAnalytics.getOneMediaPlots(result.get("onemedia"));
        System.out.println("Number of OM Plots " + oneMediaPlots.values().size());
        
        String spectrafilepath = Utilities.getResourcesFilepath() + "fp_spectra.csv";
        String metadatafilepath = Utilities.getResourcesFilepath() + "fluorophore_meta_data.csv";
        
        String pathcytometer = Utilities.getResourcesFilepath() + "HarvardFortessa.csv";
        Cytometer c = fpFortessaParse.parse(pathcytometer, false);
        Map<Integer,Laser> laserMap = DataAnalytics.getWavelengthToLaserMap(c);
        Map<String, Fluorophore> metadata = MetadataParser.parse(metadatafilepath);
        Map<String, Fluorophore> spectramap = fpSpectraParse.parse(spectrafilepath);
        spectramap.putAll(FPParser.parseFluorophoreOrg(FluorophoresOrgFolder));
        Map<String, AnalyticsPlot> adjusted = DataAnalytics.normalizeOneMediaValues(oneMediaPlots,metadata,spectramap,laserMap);
        for(AnalyticsPlot omplot : oneMediaPlots.values()){
            DataAnalytics.plotGraph(omplot, plotfilepath);
        }
        List<AnalyticsPlot> plotlist = new ArrayList<AnalyticsPlot>();
        plotlist.addAll(oneMediaPlots.values());
        JavaPlot mashed = DataAnalytics.getMashedOneMediaPlot(plotlist);
        DataAnalytics.plotToFile(mashed, plotfilepath + Utilities.getSeparater() + "mashed.png");
    }
    
}
