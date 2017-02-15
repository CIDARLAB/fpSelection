/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.CompensationMatrix;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.OneMedia;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.Voltage;

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
                //System.out.println( "Dir:" + f.getAbsoluteFile() );
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
                            //Baseline?
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
                    else {
                        //Something for Baseline as well?
                    }
                } 
                else if (f.getName().equals("oneMediaPlotPoints.csv")) {
                    String pathPieces[] = filepathPieces(path, resultsRoot);
                    List<OneMedia> onemedialist = parseOnemedia(f.getAbsolutePath());
                    if (pathPieces[pathPieces.length - 2].contains("Baseline")) {
                        //Baseline?
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
        List<String> bleedoverOrder = new ArrayList<String>();
        for(int i=1;i<bleedover.length;i++){
            bleedoverOrder.add(bleedover[i]);
        }
        for(int i=1;i<lines.size();i++){
            String[] line = lines.get(i);
            String measuredFilter = line[0];
            for(int j=1;j<line.length;j++){
                String bleedOverFilter = bleedoverOrder.get(i-1);
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
            String filter = getFilterMap().get(part);
            double value = Double.valueOf(line[filterorder.get(filter)]);
            onemedialist.add(new OneMedia(part,filter,value));
        }
        return onemedialist;
    }
    
    private static Map<String,String> getFilterMap(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("RFP_B8_M9_glucose", "MEAN_PE.Texas_Red.A");
        map.put("GFP_B8_M9_glucose", "MEAN_GFP.A");
        map.put("BFP_B8_M9_glucose", "MEAN_Pacific_Blue.A");
        map.put("TS_B2_M9_glucose", "MEAN_YFP.A");
        map.put("GFP_B2_M9_glucose", "MEAN_GFP.A");
        map.put("BFP_B2_M9_glucose", "MEAN_Pacific_Blue.A");
        map.put("RPF_B2_M9_glucose", "MEAN_PE.Texas_Red.A");
        map.put("mCitrine_B2_M9_glucose", "MEAN_Pacific_Orange.A");
        
        return map;
    }
    //</editor-fold>
    
    
    
    
}
