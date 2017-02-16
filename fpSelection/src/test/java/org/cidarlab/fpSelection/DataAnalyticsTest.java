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
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.parsers.MetadataParser;
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

    /**
     * Test of walk method, of class DataAnalytics.
     */
    //@Test
    public void testWalk() {
        System.out.println("walk");
        String path = Utilities.getResourcesFilepath()  + "fpSelectionData" + Utilities.getSeparater() + "FP_selection_R1" + Utilities.getSeparater() + "analysis" + Utilities.getSeparater();
        String resultsRoot = path;
        Map<String, Map<String,AnalyticsExperiment>> result = DataAnalytics.walk(path);
        
    }
    
    
    //@Test
    public void testFPSpectraParse(){
        String filepath = Utilities.getResourcesFilepath() + "fp_spectra.csv";
        try {
            Map<String, Fluorophore> spectraMap = fpSpectraParse.parse(filepath);
            System.out.println(spectraMap.keySet());
        } catch (IOException ex) {
            Logger.getLogger(DataAnalyticsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testVoltagePlots(){
        String path = Utilities.getResourcesFilepath()  + "fpSelectionData" + Utilities.getSeparater() + "FP_selection_R1" + Utilities.getSeparater() + "analysis" + Utilities.getSeparater();
        String resultsRoot = path;
        Map<String, Map<String,AnalyticsExperiment>> result = DataAnalytics.walk(path);
        String plotfilepath = Utilities.getResourcesFilepath() + "fpSelectionData" + Utilities.getSeparater()+ "FP_selection_R1" + Utilities.getSeparater() + "plots" + Utilities.getSeparater();
        Map<String, AnalyticsPlot> voltagePlots = DataAnalytics.getVoltagePlots(result.get("voltage"));
        for(AnalyticsPlot voltageplot : voltagePlots.values()){
            DataAnalytics.plotGraph(voltageplot, plotfilepath);
        }
    }
    
    @Test
    public void testEcoliPlots(){
        String path = Utilities.getResourcesFilepath()  + "fpSelectionData" + Utilities.getSeparater() + "FP_selection_R1" + Utilities.getSeparater() + "analysis" + Utilities.getSeparater();
        String resultsRoot = path;
        Map<String, Map<String,AnalyticsExperiment>> result = DataAnalytics.walk(path);
        String plotfilepath = Utilities.getResourcesFilepath() + "fpSelectionData" + Utilities.getSeparater()+ "FP_selection_R1" + Utilities.getSeparater() + "plots" + Utilities.getSeparater();
        Map<String, AnalyticsPlot> ecoliplots = DataAnalytics.getEcoliPlots(result.get("ecoli"));
        for(AnalyticsPlot ecoliplot : ecoliplots.values()){
            DataAnalytics.plotGraph(ecoliplot, plotfilepath);
        }
    }
    
    @Test
    public void testBeadsPlots(){
        String path = Utilities.getResourcesFilepath()  + "fpSelectionData" + Utilities.getSeparater() + "FP_selection_R1" + Utilities.getSeparater() + "analysis" + Utilities.getSeparater();
        String resultsRoot = path;
        Map<String, Map<String,AnalyticsExperiment>> result = DataAnalytics.walk(path);
        String plotfilepath = Utilities.getResourcesFilepath() + "fpSelectionData" + Utilities.getSeparater()+ "FP_selection_R1" + Utilities.getSeparater() + "plots" + Utilities.getSeparater();
        Map<String, AnalyticsPlot> beadsplots = DataAnalytics.getBeadsPlots(result.get("beads"));
        for(AnalyticsPlot beadsplot : beadsplots.values()){
            DataAnalytics.plotGraph(beadsplot, plotfilepath);
        }
    }
    
    //@Test
    public void testOneMediaPlots() throws IOException{
        String path = Utilities.getResourcesFilepath()  + "fpSelectionData" + Utilities.getSeparater() + "FP_selection_R1" + Utilities.getSeparater() + "analysis" + Utilities.getSeparater();
        String resultsRoot = path;
        Map<String, Map<String,AnalyticsExperiment>> result = DataAnalytics.walk(path);
        
        String plotfilepath = Utilities.getResourcesFilepath() + "fpSelectionData" + Utilities.getSeparater()+ "FP_selection_R1" + Utilities.getSeparater() + "plots" + Utilities.getSeparater();
        Map<String, AnalyticsPlot> oneMediaPlots = DataAnalytics.getOneMediaPlots(result.get("onemedia"));
        System.out.println("Number of OM Plots " + oneMediaPlots.values().size());
        
        String spectrafilepath = Utilities.getResourcesFilepath() + "fp_spectra.csv";
        String metadatafilepath = Utilities.getResourcesFilepath() + "fluorophore_meta_data.csv";
        
        Map<String, Fluorophore> metadata = MetadataParser.parse(metadatafilepath);
        Map<String, Fluorophore> spectramap = fpSpectraParse.parse(spectrafilepath);
        Map<String, AnalyticsPlot> adjusted = DataAnalytics.normalizeOneMediaValues(oneMediaPlots,metadata,spectramap);
        for(AnalyticsPlot omplot : adjusted.values()){
            DataAnalytics.plotGraph(omplot, plotfilepath);
        }
        List<AnalyticsPlot> plotlist = new ArrayList<AnalyticsPlot>();
        plotlist.addAll(adjusted.values());
        JavaPlot mashed = DataAnalytics.getMashedOneMediaPlot(plotlist);
        DataAnalytics.plotToFile(mashed, plotfilepath + Utilities.getSeparater() + "mashed.png");
    }
    
}
