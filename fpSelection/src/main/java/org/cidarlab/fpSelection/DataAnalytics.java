/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.CompensationMatrix;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.OneMedia;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.Voltage;
import org.cidarlab.fpSelection.dom.AnalyticsPlot;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author prash
 */
public class DataAnalytics {

        
    //<editor-fold desc="Parsers">
    public static Map<String, Map<String, AnalyticsExperiment>> walk(String path) {
        Map<String, Map<String, AnalyticsExperiment>> exp = new HashMap<String, Map<String, AnalyticsExperiment>>();
        exp.put("ecoli", new HashMap<String, AnalyticsExperiment>());
        exp.put("beads", new HashMap<String, AnalyticsExperiment>());
        exp.put("onemedia", new HashMap<String, AnalyticsExperiment>());
        exp.put("voltage", new HashMap<String, AnalyticsExperiment>());
        walk(path, path, exp);
        
        System.out.println(exp.get("ecoli").size());
        System.out.println(exp.get("beads").size());
        System.out.println(exp.get("onemedia").size());
        System.out.println(exp.get("voltage").size());
        return exp;
    }

    private static void walk(String path, String resultsRoot, Map<String, Map<String, AnalyticsExperiment>> exp) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), resultsRoot, exp);
            } 
            else {
                if (f.getName().equals("compensation_matrix.csv")) {
                    String pathPieces[] = filepathPieces(path, resultsRoot);
                    List<CompensationMatrix> matrix = parseCompensationMatrix(f.getAbsolutePath());
                    if (pathPieces[pathPieces.length - 1].equals("ecoli")) {
                        if (pathPieces[pathPieces.length - 2].contains("Baseline")) {
                            //Baseline?
                        } 
                        else {
                            if (!exp.get("ecoli").containsKey(pathPieces[pathPieces.length - 2])) {
                                exp.get("ecoli").put(pathPieces[pathPieces.length - 2], new AnalyticsExperiment());
                            }
                            exp.get("ecoli").get(pathPieces[pathPieces.length - 2]).addCompensationMatrixValue(matrix);
                        }
                    } 
                    else if (pathPieces[pathPieces.length - 1].equals("beads")) {
                        if(pathPieces[pathPieces.length-2].contains("Voltages")){
                            if(!exp.get("voltage").containsKey(pathPieces[pathPieces.length-2])){
                                exp.get("voltage").put(pathPieces[pathPieces.length-2], new AnalyticsExperiment());
                            }
                            exp.get("voltage").get(pathPieces[pathPieces.length-2]).addCompensationMatrixValue(matrix);
                        } 
                        else if(pathPieces[pathPieces.length-2].contains("Lasers")){
                            if(!exp.get("beads").containsKey(pathPieces[pathPieces.length-2])){
                                exp.get("beads").put(pathPieces[pathPieces.length-2], new AnalyticsExperiment());
                            }
                            exp.get("beads").get(pathPieces[pathPieces.length-2]).addCompensationMatrixValue(matrix);
                        } 
                        else {
                            
                        }
                        
                    }
                } 
                else if (f.getName().equals("settings.txt")) {
                    String pathPieces[] = filepathPieces(path, resultsRoot);
                    if(pathPieces[pathPieces.length - 1].contains("Voltages")){
                        if(!exp.get("voltage").containsKey(pathPieces[pathPieces.length - 1])){
                            exp.get("voltage").put(pathPieces[pathPieces.length - 1], new AnalyticsExperiment());
                            exp.get("voltage").get(pathPieces[pathPieces.length - 1]).setType(AnalyticsExperiment.ExperimentType.voltage);
                        }
                        List<Voltage> voltageValues = parseVoltageSettings(f.getAbsolutePath());
                        exp.get("voltage").get(pathPieces[pathPieces.length - 1]).addVoltageValue(voltageValues);
                    }
                    else if(pathPieces[pathPieces.length -1].contains("Lasers")) {
                        int wavelength = getWavelenth(pathPieces[pathPieces.length-1]);
                        String laserLine = getLaserSettingLine(f.getAbsolutePath(),wavelength);
                        double maxPow = getMaxLaserPower(laserLine);
                        double actualPow = getActualLaserPower(laserLine);
                        double attemptedPow = getAttemptedPower(pathPieces[pathPieces.length-1]);
                        if(!exp.get("ecoli").containsKey(pathPieces[pathPieces.length-1])){
                            exp.get("ecoli").put(pathPieces[pathPieces.length-1], new AnalyticsExperiment());
                            exp.get("ecoli").get(pathPieces[pathPieces.length-1]).setType(AnalyticsExperiment.ExperimentType.ecoli);
                        }
                        if(!exp.get("beads").containsKey(pathPieces[pathPieces.length-1])){
                            exp.get("beads").put(pathPieces[pathPieces.length-1], new AnalyticsExperiment());
                            exp.get("beads").get(pathPieces[pathPieces.length-1]).setType(AnalyticsExperiment.ExperimentType.beads);
                        
                        }
                        if(!exp.get("onemedia").containsKey(pathPieces[pathPieces.length-1])){
                            exp.get("onemedia").put(pathPieces[pathPieces.length-1], new AnalyticsExperiment());
                            exp.get("onemedia").get(pathPieces[pathPieces.length-1]).setType(AnalyticsExperiment.ExperimentType.onemedia);
                        
                        }
                        
                        exp.get("ecoli").get(pathPieces[pathPieces.length-1]).setLaserWavelength(wavelength);
                        exp.get("ecoli").get(pathPieces[pathPieces.length-1]).setActualLaserPower(actualPow);
                        exp.get("ecoli").get(pathPieces[pathPieces.length-1]).setMaxLaserPower(maxPow);
                        exp.get("ecoli").get(pathPieces[pathPieces.length-1]).setAttemptedLaserPower(attemptedPow);
                        
                        
                        exp.get("beads").get(pathPieces[pathPieces.length-1]).setLaserWavelength(wavelength);
                        exp.get("beads").get(pathPieces[pathPieces.length-1]).setActualLaserPower(actualPow);
                        exp.get("beads").get(pathPieces[pathPieces.length-1]).setMaxLaserPower(maxPow);
                        exp.get("beads").get(pathPieces[pathPieces.length-1]).setAttemptedLaserPower(attemptedPow);
                        
                        exp.get("onemedia").get(pathPieces[pathPieces.length-1]).setLaserWavelength(wavelength);
                        exp.get("onemedia").get(pathPieces[pathPieces.length-1]).setActualLaserPower(actualPow);
                        exp.get("onemedia").get(pathPieces[pathPieces.length-1]).setMaxLaserPower(maxPow);
                        exp.get("onemedia").get(pathPieces[pathPieces.length-1]).setAttemptedLaserPower(attemptedPow);
                        
                    }
                    else if(pathPieces[pathPieces.length -1].contains("Baseline")){
                        
                        List<Integer> wl = getAllLaserWavelengths(f.getAbsolutePath());
                        for(Integer wavelength:wl){
                            String laserLine = getLaserSettingLine(f.getAbsolutePath(), wavelength);
                            double maxPow = getMaxLaserPower(laserLine);
                            double actualPow = getActualLaserPower(laserLine);
                            String basekey = "Baseline"+wavelength;
                            if(!exp.get("onemedia").containsKey(basekey)){
                                exp.get("onemedia").put(basekey, new AnalyticsExperiment());
                                exp.get("onemedia").get(basekey).setType(AnalyticsExperiment.ExperimentType.onemedia);
                            }
                            exp.get("onemedia").get(basekey).setActualLaserPower(actualPow);
                            exp.get("onemedia").get(basekey).setMaxLaserPower(maxPow);
                            exp.get("onemedia").get(basekey).setLaserWavelength(wavelength);
                            
                        }
                                
                    }
                } 
                else if (f.getName().equals("oneMediaPlotPoints.csv")) {
                    //System.out.println(f.getAbsolutePath());
                    String pathPieces[] = filepathPieces(path, resultsRoot);
                    if (pathPieces[pathPieces.length - 2].contains("Voltages")) {
                        continue;
                    }
                    List<OneMedia> onemedialist = parseOnemedia(f.getAbsolutePath());
                    String settingsfilepath = f.getAbsolutePath().substring(0,f.getAbsolutePath().indexOf(pathPieces[pathPieces.length - 1]));
                    settingsfilepath += "settings.txt";
                    List<Integer> allWavelengths = getAllLaserWavelengths(settingsfilepath);
                    if (pathPieces[pathPieces.length - 2].contains("Baseline")) {
                        for(Integer wavelength:allWavelengths){
                            String basekey = "Baseline"+wavelength;
                            if(!exp.get("onemedia").containsKey(basekey)){
                                exp.get("onemedia").put(basekey, new AnalyticsExperiment());
                                exp.get("onemedia").get(basekey).setType(AnalyticsExperiment.ExperimentType.onemedia);
                            }
                            exp.get("onemedia").get(basekey).addOneMediaValue(onemedialist);
                        }
                    } 
                    else {
                        
                        if (!exp.get("onemedia").containsKey(pathPieces[pathPieces.length - 2])) {
                            exp.get("onemedia").put(pathPieces[pathPieces.length - 2], new AnalyticsExperiment());
                        }
                        exp.get("onemedia").get(pathPieces[pathPieces.length - 2]).addOneMediaValue(onemedialist);
                    }
                }
            }
        }
    }

    public static Map<String, AnalyticsPlot> normalizeOneMediaValues(Map<String, AnalyticsPlot> aplot, Map<String, Fluorophore> metadata, Map<String, Fluorophore> spectradata, Map<Integer,Laser> cytometerData){
        Map<String, AnalyticsPlot> adjusted = new HashMap<String, AnalyticsPlot>();
        
        for(String fp: aplot.keySet()){
            String fpName = aplot.get(fp).getFpname();
            AnalyticsPlot adjustedPlot = new AnalyticsPlot();
            double brightness = (metadata.get(getFPMetaDataMap().get(fpName)).brightness);
            double excitation = spectradata.get(getFPSpectraMap().get(fpName)).EXspectrum.get((double) aplot.get(fp).getLaserWavelength());
            
            
            Laser l = cytometerData.get(getFPToLaserWavelengthMap().get(fpName));
            Detector d = getDetector(getFPtoCytoFilter().get(fpName),l);
            double emission = getEmissionValue(spectradata.get(getFPSpectraMap().get(fpName)),d,excitation);
            for(Point p:aplot.get(fp).getPoints()){
                double x = p.get(0).doubleValue();
                //System.out.println(getFPMetaDataMap().get(fpName));
                //System.out.println(metadata.get(getFPMetaDataMap().get(fpName)).name);
                
                double y = p.get(1).doubleValue() / (brightness * excitation * emission);
                adjustedPlot.addPoint(new Point(x,y));
            }
            adjustedPlot.setLaserWavelength(aplot.get(fp).getLaserWavelength());
            adjustedPlot.setPlotlabel(aplot.get(fp).getPlotlabel());
            adjustedPlot.setXlabel(aplot.get(fp).getXlabel());
            adjustedPlot.setYlabel(aplot.get(fp).getYlabel());
            adjusted.put(fp, adjustedPlot);
        }
        
        return adjusted;
    }
    
    private static double getEmissionValue(Fluorophore fp, Detector d, double excitation){
        double emission = 0;
        int low = (int)Math.ceil(d.filterMidpoint - (d.filterWidth/2));
        double high = (int)Math.floor(d.filterMidpoint + (d.filterWidth/2));
        
        for(int i=low;i<=high;i++){
            emission += fp.EMspectrum.get((double)i);
        }
        emission *= excitation;
        return emission;
    }
    
    private static Detector getDetector(String detector, Laser l){
        
        for(Detector d:l.detectors){
            if(d.name.equals(detector)){
                return d;
            }
        }
        
        return null;
    }
    
    private static Map<String,String> getFPtoCytoFilter(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("RFP_B8_M9_glucose", "PE-Texas Red");
        map.put("GFP_B8_M9_glucose", "GFP");
        map.put("BFP_B8_M9_glucose", "Pacific Blue");
        map.put("TS_B2_M9_glucose", "Pacific Orange");
        map.put("GFP_B2_M9_glucose", "GFP");
        map.put("BFP_B2_M9_glucose", "Pacific Blue");
        map.put("RPF_B2_M9_glucose", "PE-Texas Red");
        map.put("mCitrine_B2_M9_glucose", "YFP");
        
        return map;
    }
    
    private static String[] filepathPieces(String filepath, String rootFilepath) {
        String relativeFilepath = filepath.substring(filepath.lastIndexOf(rootFilepath) + rootFilepath.length());
        return relativeFilepath.split("/");
    }

    private static List<Voltage> parseVoltageSettings(String filepath){
        List<String> filelines = Utilities.getFileContentAsStringList(filepath);
        List<Voltage> voltageValues = new ArrayList<Voltage>();
        for(String line:filelines){
            if(line.contains(":")){
                String pieces[] = line.split(":");
                String voltVal = pieces[1].trim().substring(0, pieces[1].length()-2);
                voltageValues.add(new Voltage(pieces[0].trim(),Integer.valueOf(voltVal)));
            }
        }
        return voltageValues;
    }
    
    private static List<CompensationMatrix> parseCompensationMatrix(String filepath){
        List<CompensationMatrix> matrix = new ArrayList<CompensationMatrix>();
        List<String[]> lines = Utilities.getCSVFileContentAsList(filepath);
        String[] bleedover = lines.get(0);
        List<String> measuredFilterOrder = new ArrayList<String>();
        for(int i=1;i<bleedover.length;i++){
            measuredFilterOrder.add(bleedover[i]);
        }
        for(int i=1;i<lines.size();i++){
            String[] line = lines.get(i);
            String bleedOverFilter = line[0];
            for(int j=1;j<line.length;j++){
                String measuredFilter = measuredFilterOrder.get(j-1);
                matrix.add(new CompensationMatrix(measuredFilter, bleedOverFilter,Double.valueOf(line[j])));
            }
        }
        return matrix;
    }
    
    private static int getWavelenth(String folder){
        String pieces[] = folder.split("_");
        return Integer.valueOf(pieces[1].trim());
    }
    
    private static double getAttemptedPower(String folder){
        String pieces[] = folder.split("_");
        return Double.valueOf(pieces[2].trim());
    }
    
    private static List<Integer> getAllLaserWavelengths(String filepath){
        List<Integer> wl = new ArrayList<Integer>();
        List<String> lines = Utilities.getFileContentAsStringList(filepath);
        for(int i=1;i<lines.size();i++){
            if(lines.get(i).trim().isEmpty() || lines.get(i).startsWith("Threshold") || lines.get(i).startsWith("Beads") || lines.get(i).startsWith("E. coli")){
                continue;
            }
            String wlString = lines.get(i).substring(0,lines.get(i).indexOf("("));
            wl.add(Integer.valueOf(wlString.trim()));
        }
        return wl;
    }
    
    
    private static String getLaserSettingLine(String filepath, int wavelength){
        List<String> lines = Utilities.getFileContentAsStringList(filepath);
        String wl = "";
        wl += wavelength;
        for(String line:lines){
            if(line.startsWith(wl)){
                return line;
            }
        }
        return null;
    }
    
    private static double getActualLaserPower(String line){
        String[] pieces = line.split(":");
        String pow = pieces[1].trim().substring(0,pieces[1].trim().indexOf("mW"));
        return Double.valueOf(pow);
    }
    
    private static double getMaxLaserPower(String line){
        String pow = line.substring(line.indexOf("(")+1, line.indexOf(")"));
        pow = pow.substring(0,pow.indexOf("mW"));
        return Double.valueOf(pow);
    }
    
    public static Map<Integer,Laser> getWavelengthToLaserMap(Cytometer c){
        Map<Integer,Laser> map = new HashMap<Integer, Laser>();
        
        for(Laser l:c.lasers){
            map.put(l.wavelength, l);
        }
        return map;
    }
    
    private static List<OneMedia> parseOnemedia(String filepath){
        List<String[]> lines = Utilities.getCSVFileContentAsList(filepath);
        List<OneMedia> onemedialist = new ArrayList<OneMedia>();
        String header[] = lines.get(0);
        Map<String,Integer> filterorder = new HashMap<String,Integer>();
        for(int i=1;i<header.length;i++){
            filterorder.put(header[i].trim(), i);
        }
        for(int i=1;i<lines.size();i++){
            String[] line = lines.get(i);
            String part = line[0].trim();
            String filter = getFPToFilterDataMap().get(part);
            double value = Double.valueOf(line[filterorder.get(filter)]);
            onemedialist.add(new OneMedia(part,filter,value));
        }
        return onemedialist;
    }
    
    private static Map<String,String> getFPMetaDataMap(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("RFP_B8_M9_glucose", "mRFP1");
        map.put("GFP_B8_M9_glucose", "EGFP");
        map.put("BFP_B8_M9_glucose", "mTagBFP");
        map.put("TS_B2_M9_glucose", "mT-Sapphire");
        map.put("GFP_B2_M9_glucose", "EGFP");
        map.put("BFP_B2_M9_glucose", "mTagBFP");
        map.put("RPF_B2_M9_glucose", "mRFP1");
        map.put("mCitrine_B2_M9_glucose", "mCitrine");
        return map;
    }
    
    private static Map<Integer,String> getDyeWavelengthMap(){
        Map<Integer,String> map = new HashMap<Integer,String>();
        
        map.put(640,"APC-A");
        map.put(561,"PE-A");
        map.put(488,"PerCP-A");
        
        return map;
    }
    
    private static Map<String, String> getFPSpectraMap(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("RFP_B8_M9_glucose", "mRFP1.4m");
        map.put("GFP_B8_M9_glucose", "EGFPm (Tsien)");
        map.put("BFP_B8_M9_glucose", "EBFP2");
        map.put("TS_B2_M9_glucose", "T-Sapphire");
        map.put("GFP_B2_M9_glucose", "EGFPm (Tsien)");
        map.put("BFP_B2_M9_glucose", "EBFP2");
        map.put("RPF_B2_M9_glucose", "mRFP1.4m");
        map.put("mCitrine_B2_M9_glucose", "mCitrine");
        return map;
    
    }
    
    private static Map<Integer, List<String>> getLaserWavelengthToFPMap(){
        Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        map.put(405, new ArrayList<String>());
        map.put(488, new ArrayList<String>());
        map.put(561, new ArrayList<String>());
        
        map.get(405).add("Pacific_Blue-A");
        map.get(405).add("Pacific_Orange-A");
        map.get(488).add("GFP-A");
        map.get(488).add("YFP-A");
        map.get(561).add("PE-Texas_Red-A");
        
        return map;
    }
    
    private static Map<String,Integer> getFPToLaserWavelengthMap(){
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("RFP_B8_M9_glucose", 561);
        map.put("GFP_B8_M9_glucose", 488);
        map.put("BFP_B8_M9_glucose", 405);
        map.put("TS_B2_M9_glucose", 405);
        map.put("GFP_B2_M9_glucose", 488);
        map.put("BFP_B2_M9_glucose", 405);
        map.put("RPF_B2_M9_glucose", 561);
        map.put("mCitrine_B2_M9_glucose", 488);
        return map;
    }
    
    private static Map<String,String> getFPToFilterDataMap(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("RFP_B8_M9_glucose", "MEAN_PE.Texas_Red.A");
        map.put("GFP_B8_M9_glucose", "MEAN_GFP.A");
        map.put("BFP_B8_M9_glucose", "MEAN_Pacific_Blue.A");
        map.put("TS_B2_M9_glucose", "MEAN_Pacific_Orange.A");
        map.put("GFP_B2_M9_glucose", "MEAN_GFP.A");
        map.put("BFP_B2_M9_glucose", "MEAN_Pacific_Blue.A");
        map.put("RPF_B2_M9_glucose", "MEAN_PE.Texas_Red.A");
        map.put("mCitrine_B2_M9_glucose", "MEAN_YFP.A");
        
        return map;
    }
    //</editor-fold>
    
    public static Map<String, AnalyticsPlot> getBeadsPlots(Map<String, AnalyticsExperiment> expList){
        Map<String, AnalyticsPlot> plots = new HashMap<String, AnalyticsPlot>();
        
        for(String folder:expList.keySet()){
            if(!getDyeWavelengthMap().containsKey(expList.get(folder).getLaserWavelength())){
                continue;
            }
            String dyefilter = getDyeWavelengthMap().get(expList.get(folder).getLaserWavelength());
            String keyval = dyefilter;
            String noisekey = keyval + "-noise";
            String snrkey = keyval + "-snr";
            if(!plots.containsKey(snrkey)){
                plots.put(snrkey, new AnalyticsPlot());
                plots.get(snrkey).setXlabel("Laser "+ expList.get(folder).getLaserWavelength() +"Power");
                plots.get(snrkey).setYlabel("SNR");
                plots.get(snrkey).setPlotlabel(snrkey);
            }
            if(!plots.containsKey(noisekey)){
                plots.put(noisekey, new AnalyticsPlot());
                plots.get(noisekey).setXlabel("Laser "+ expList.get(folder).getLaserWavelength() +"Power");
                plots.get(noisekey).setYlabel("Noise");
                plots.get(noisekey).setPlotlabel(noisekey);
            }
            double noise = 0;
            for(CompensationMatrix m:expList.get(folder).getMatrix()){
                if(m.measuredFilter.equals(dyefilter)){
                    if(!m.bleedOverFilter.equals(dyefilter)){
                        noise += m.value;
                    }
                }
            }
            double snr = 1/noise;
            plots.get(snrkey).addPoint(new Point(expList.get(folder).getActualLaserPower(),snr));
            plots.get(noisekey).addPoint(new Point(expList.get(folder).getActualLaserPower(),noise));
        }
        
        return plots;
    }
    
    public static Map<String, AnalyticsPlot> getEcoliPlots(Map<String, AnalyticsExperiment> expList){
        Map<String, AnalyticsPlot> plots = new HashMap<String, AnalyticsPlot>();
        
        for(String folder : expList.keySet()){
            if(!getLaserWavelengthToFPMap().containsKey(expList.get(folder).getLaserWavelength())){
                continue;
            }
            for(String filter : getLaserWavelengthToFPMap().get(expList.get(folder).getLaserWavelength())){
                String keyval_underscore = filter;
                String keyval = keyval_underscore.replaceAll("_", "-");
                String noisekey = keyval + "-noise";
                String snrkey = keyval + "-snr";
                if(!plots.containsKey(snrkey)){
                    plots.put(snrkey, new AnalyticsPlot());
                    plots.get(snrkey).setPlotlabel(snrkey);
                    plots.get(snrkey).setXlabel("Laser " + expList.get(folder).getLaserWavelength() + " Power");
                    plots.get(snrkey).setYlabel("SNR");
                }
                if(!plots.containsKey(noisekey)){
                    plots.put(noisekey, new AnalyticsPlot());
                    plots.get(noisekey).setPlotlabel(noisekey);
                    plots.get(noisekey).setXlabel("Laser " + expList.get(folder).getLaserWavelength() + " Power");
                    plots.get(noisekey).setYlabel("Noise");
                }
                double noise = 0;
                for(CompensationMatrix m:expList.get(folder).getMatrix()){
                    if(m.measuredFilter.equals(filter)){
                        if(!m.bleedOverFilter.equals(filter)){
                            noise += m.value;
                        }
                    }
                }
                double snr = 1/noise;
                plots.get(snrkey).addPoint(new Point(expList.get(folder).getActualLaserPower(),snr));
                plots.get(noisekey).addPoint(new Point(expList.get(folder).getActualLaserPower(),noise));
                
            }
        }
        
        return plots;
    }
    
    public static Map<String, AnalyticsPlot> getVoltagePlots(Map<String, AnalyticsExperiment> expList){
        Map<String, AnalyticsPlot> plots = new HashMap<String, AnalyticsPlot>();
        
        String prefix = "Voltages_1";
        for(int i=1;i<4;i++){
            
            String title = expList.get(prefix + i + "1").getVoltageValues().get(0).voltage + "V-" + expList.get(prefix + i + "1").getVoltageValues().get(1).voltage + "V";
            
            String snrtitle =  title + "-SNR";
            String noisetitle = title + "-Noise";
            
            
            double voltageVal=0;
            double noise = 0;
            for(int j=1;j<4;j++){
                String folder = prefix + i + j;
                expList.get(folder).getVoltageValues().get(j-1);
                voltageVal = expList.get(folder).getVoltageValues().get(j-1).voltage;
                String mainfilter = expList.get(folder).getVoltageValues().get(j-1).filter;
                for(CompensationMatrix m: expList.get(folder).getMatrix()){
                    if(m.measuredFilter.equals(mainfilter)){ 
                        if(!m.bleedOverFilter.equals(mainfilter)){
                            noise += m.value;
                        }
                    }
                }
                String mainFilterLabel = mainfilter.replaceAll("_", " ");
                double snr = 1 / noise;
                //System.out.println("Noise Value :: " + noise);
                //System.out.println("SNR Value :: " + snr);
                if (!plots.containsKey(noisetitle)) {
                    plots.put(noisetitle, new AnalyticsPlot());
                    plots.get(noisetitle).setPlotlabel(noisetitle);
                    plots.get(noisetitle).setXlabel(mainFilterLabel + " Voltage");
                    plots.get(noisetitle).setYlabel("Noise");
                }
                if (!plots.containsKey(snrtitle)) {
                    plots.put(snrtitle, new AnalyticsPlot());
                    plots.get(snrtitle);
                    plots.get(snrtitle).setPlotlabel(snrtitle);
                    plots.get(snrtitle).setXlabel(mainFilterLabel + " Voltage");
                    plots.get(snrtitle).setYlabel("SNR");
                }
                //System.out.println("NOISE :: (" + voltageVal + "," + noise + ")");
                //System.out.println("SNR   :: (" + voltageVal + "," + snr + ")");
                plots.get(noisetitle).addPoint(new Point(voltageVal, noise));
                plots.get(snrtitle).addPoint(new Point(voltageVal, snr));

            }
            
        }
        
        return plots;
    }
    
    public static Map<String, AnalyticsPlot> getOneMediaPlots(Map<String, AnalyticsExperiment> expList){
        Map<String, AnalyticsPlot> plots = new HashMap<String, AnalyticsPlot>();
        
        Set<String> keylist = expList.keySet();
        for(String key:keylist){
            AnalyticsExperiment exp = expList.get(key);
            int laserWavelength = exp.getLaserWavelength();
            for(OneMedia om:exp.getOneMediaValues()){
                //System.out.println(getFilterFPMap().get(om.part));
                //System.out.println(laserWavelength);
                if(! (getFPToLaserWavelengthMap().get(om.part) == laserWavelength)){
                    continue;
                }
                String partname = om.part.replaceAll("_", " ");
                String plotkey = "LASER" + laserWavelength + " " + partname;
                //System.out.println(plotkey);
                if(!plots.containsKey(plotkey)){
                    AnalyticsPlot aplot = new AnalyticsPlot();
                    String xlabel = "Actual Laser Power";
                    String ylabel = partname;
                    aplot.setPlotlabel(plotkey);
                    aplot.setXlabel(xlabel);
                    aplot.setYlabel(ylabel);
                    aplot.setLaserWavelength(laserWavelength);
                    aplot.setFpname(om.part);
                    plots.put(plotkey, aplot);
                }
                plots.get(plotkey).addPoint(new Point(exp.getActualLaserPower(),om.value));
            }
        }
        return plots;
    }
    
    public static void plotGraph(AnalyticsPlot plot, String filepath){
        String filename = plot.getPlotlabel();
        filename += ".png";
        plotToFile(getJavaPlot(plot),filepath + filename);
    }
    
    
    
    private static List<Point> sortPoints(List<Point> points){
        Map<Double, Double> pointMap = new HashMap<Double, Double>();
        List<Double> x = new ArrayList<Double>();
        List<Point> sorted = new ArrayList<Point>();
        for(Point p:points){
            pointMap.put(p.get(0).doubleValue(), p.get(1).doubleValue());
        }
        x.addAll(pointMap.keySet());
        for(int i=0;i<x.size();i++){
            for(int j=i;j<x.size();j++){
                if(x.get(i) > x.get(j)){
                    double temp = x.get(i);
                    x.set(i, x.get(j));
                    x.set(j, temp);
                }
            }
        }
        for(double d:x){
            sorted.add(new Point(d,pointMap.get(d)));
        }
        return sorted;
    }
    
    private static NamedPlotColor getRandomNamedPlotColor(){
        Random random = new Random();
        return NamedPlotColor.values()[random.nextInt(NamedPlotColor.values().length)];
    }
    
    public static JavaPlot getMashedOneMediaPlot(List<AnalyticsPlot> plotlist){
        JavaPlot plot = new JavaPlot();
        
        Set<NamedPlotColor> usedColors = new HashSet<NamedPlotColor>();
        for (AnalyticsPlot ap : plotlist) {
            PlotStyle ps = new PlotStyle();
            ps.setStyle(Style.LINES);
            NamedPlotColor npc = getRandomNamedPlotColor();
            while(usedColors.contains(npc)){
                npc = getRandomNamedPlotColor();
            }
            usedColors.add(npc);
            ps.setLineType(npc);
            PointDataSet pds = new PointDataSet(sortPoints(ap.getPoints()));
            DataSetPlot dsp = new DataSetPlot(pds);
            dsp.setPlotStyle(ps);
            plot.addPlot(dsp);
        }
        
        String title = "AllData";
        plot.set("style fill", "transparent solid 0.5");
        
        plot.getAxis("x").setLabel(plotlist.get(0).getXlabel());
        plot.getAxis("y").setLabel("MEFL");
        plot.setTitle(title);
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        
        return plot;
        
    }
    
    private static JavaPlot getJavaPlot(AnalyticsPlot plotdata){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.LINES);
        ps.setLineType(NamedPlotColor.BLACK);
        
        PointDataSet pds = new PointDataSet(sortPoints(plotdata.getPoints()));
        DataSetPlot dsp = new DataSetPlot(pds);
        dsp.setPlotStyle(ps);
        plot.addPlot(dsp);
        
        String title = plotdata.getPlotlabel();
        plot.set("style fill", "transparent solid 0.5");
        
        plot.getAxis("x").setLabel(plotdata.getXlabel());
        plot.getAxis("y").setLabel(plotdata.getYlabel());
        plot.setTitle(title);
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        
        return plot;
    }
    
    public static void plotToFile(JavaPlot plot, String filepath){
        
        ImageTerminal png = new ImageTerminal();
        File file = new File(filepath);
        
        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            System.err.print("File " + filepath + " not found.\n");
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }
        
        plot.setPersist(false);
        plot.setTerminal(png);
        plot.plot();
        
        try {
            ImageIO.write(png.getImage(), "png", file);
        } catch (IOException ex) {
            System.err.print(ex);
        }
    }
    
}
