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
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.CompensationMatrix;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.OneMedia;
import org.cidarlab.fpSelection.dom.AnalyticsExperiment.Voltage;
import org.cidarlab.fpSelection.dom.AnalyticsPlot;
import org.cidarlab.fpSelection.dom.Fluorophore;

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
                        //Something for Baseline as well?
                        
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
                    String pathPieces[] = filepathPieces(path, resultsRoot);
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

    public static Map<String, AnalyticsPlot> normalizeOneMediaValues(Map<String, AnalyticsPlot> aplot, Map<String, Fluorophore> metadata, Map<String, Fluorophore> spectradata){
        Map<String, AnalyticsPlot> adjusted = new HashMap<String, AnalyticsPlot>();
        
        for(String fp: aplot.keySet()){
            String fpName = aplot.get(fp).getFpname();
            AnalyticsPlot adjustedPlot = new AnalyticsPlot();
            for(Point p:aplot.get(fp).getPoints()){
                double x = p.get(0).doubleValue();
                //System.out.println(getFPMetaDataMap().get(fpName));
                //System.out.println(metadata.get(getFPMetaDataMap().get(fpName)).name);
                double brightness = (metadata.get(getFPMetaDataMap().get(fpName)).brightness);
                double excitation = spectradata.get(getFPSpectraMap().get(fpName)).EXspectrum.get((double)aplot.get(fp).getLaserWavelength());
                double y = p.get(1).doubleValue() / (brightness * excitation);
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
    
    private static List<Integer> getAllLaserWavelengths(String filepath){
        List<Integer> wl = new ArrayList<Integer>();
        List<String> lines = Utilities.getFileContentAsStringList(filepath);
        for(int i=1;i<lines.size();i++){
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
    
    private static Map<String,Integer> getFilterFPMap(){
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
    
    private static Map<String,String> getFilterMap(){
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
    
    public static Map<String, AnalyticsPlot> getOneMediaPlots(Map<String, AnalyticsExperiment> expList){
        Map<String, AnalyticsPlot> plots = new HashMap<String, AnalyticsPlot>();
        
        Set<String> keylist = expList.keySet();
        for(String key:keylist){
            AnalyticsExperiment exp = expList.get(key);
            int laserWavelength = exp.getLaserWavelength();
            for(OneMedia om:exp.getOneMediaValues()){
                //System.out.println(getFilterFPMap().get(om.part));
                //System.out.println(laserWavelength);
                if(! (getFilterFPMap().get(om.part) == laserWavelength)){
                    continue;
                }
                
                String plotkey = "LASER_" + laserWavelength + "_" + om.part;
                //System.out.println(plotkey);
                if(!plots.containsKey(plotkey)){
                    AnalyticsPlot aplot = new AnalyticsPlot();
                    String xlabel = "Actual Laser Power";
                    String ylabel = "Measurement";
                    aplot.setPlotlabel(plotkey);
                    aplot.setXlabel(xlabel);
                    aplot.setXlabel(ylabel);
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
    
    public static JavaPlot getMashedOneMediaPlot(List<AnalyticsPlot> plotlist){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.LINES);
        ps.setLineType(NamedPlotColor.BLACK);
        
        for (AnalyticsPlot ap : plotlist) {
            PointDataSet pds = new PointDataSet(sortPoints(ap.getPoints()));
            DataSetPlot dsp = new DataSetPlot(pds);
            dsp.setPlotStyle(ps);
            plot.addPlot(dsp);
        }
        
        String title = "AllData";
        plot.set("style fill", "transparent solid 0.5");
        
        plot.getAxis("x").setLabel(plotlist.get(0).getXlabel());
        plot.getAxis("y").setLabel(plotlist.get(0).getXlabel());
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
