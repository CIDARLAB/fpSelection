/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.parsers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cidarlab.fpSelection.Utilities;
import org.cidarlab.fpSelection.dom.Fluorophore;

/**
 *
 * @author prash
 */
public class FPParser {
    
    public static Map<String, Fluorophore> parseChroma(String path){
        Map<String, Fluorophore> fps = new HashMap<String, Fluorophore>();
        File root = new File(path);
        File[] list = root.listFiles();
        for(File f: list){
            String filename = f.getName();
            String[] namepieces = filename.split("_");
            if(!fps.containsKey(namepieces[0].trim())){
                fps.put(namepieces[0].trim(), new Fluorophore());
                
            }
            if (namepieces[1].trim().startsWith("em")) {
                fps.get(namepieces[0].trim()).EMspectrum = parseChromaExEm(f.getAbsolutePath());
            }
            if (namepieces[1].trim().startsWith("ex")) {
                fps.get(namepieces[0].trim()).EXspectrum = parseChromaExEm(f.getAbsolutePath());
            }
            fps.get(namepieces[0].trim()).name = namepieces[0].trim();
        }
        
        return fps;
    }
    
    private static TreeMap<Double, Double> parseChromaExEm(String filepath){
        TreeMap<Double, Double> exem = new TreeMap<Double, Double>();
        List<String> lines = Utilities.getFileContentAsStringList(filepath);
        for(String line: lines){
            String[] pieces = line.split("\\t");
            exem.put(Double.valueOf(pieces[0].trim()), Double.valueOf(pieces[1].trim()));
        }
        return exem;
    }
    
    
    public static Map<String, Fluorophore> parseFluorophoreOrg(String path){
        Map<String, Fluorophore> fps = new HashMap<String, Fluorophore>();
        File root = new File(path);
        File[] list = root.listFiles();
        for(File f: list){
            String name = f.getName();
            name = name.substring(0, name.lastIndexOf(".csv"));
            Map<String, TreeMap<Double, Double>> emex = getExEmForg(f.getAbsolutePath());
            Fluorophore fp = new Fluorophore();
            fp.EMspectrum = emex.get("em");
            fp.EXspectrum = emex.get("ex");
            fp.name = name;
            fps.put(name, fp);
        }
        return fps;
    }
    
    private static Map<String, TreeMap<Double, Double>> getExEmForg(String path){
        Map<String, TreeMap<Double, Double>> exem = new HashMap<String, TreeMap<Double, Double>>();
        TreeMap<Double, Double> ex = new TreeMap<Double, Double>();
        TreeMap<Double, Double> em = new TreeMap<Double, Double>();
        
        List<String> lines = Utilities.getFileContentAsStringList(path);
        for(int i=1;i<lines.size();i++ ){
            String line = lines.get(i);
            String[] pieces = line.split(";");
            if(pieces.length < 3){
                if(!pieces[0].trim().isEmpty()){
                    ex.put(Double.valueOf(pieces[0].trim()), Double.valueOf(pieces[1].trim()));
                }
            } else {
                if(!pieces[0].trim().isEmpty()){
                    ex.put(Double.valueOf(pieces[0].trim()), Double.valueOf(pieces[1].trim()));
                }
                if(!pieces[2].trim().isEmpty()){
                    em.put(Double.valueOf(pieces[2].trim()), Double.valueOf(pieces[3].trim()));
                }
            }
        }
        
        
        exem.put("ex", ex);
        exem.put("em", em);
        return exem;
    }
}
