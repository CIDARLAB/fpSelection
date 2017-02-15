/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelectionImproved;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.algorithms.SemiExhaustiveSelection;
import org.cidarlab.fpSelection.parsers.ScrapedCSVParse;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import static org.cidarlab.fpSelection.parsers.fpSpectraParse.parse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;

/**
 *
 * @author david
 */
public class API {
    
    //Not really necessary, but it's nice to have a centralized location for calling our functions.
    
    public static HashMap<String, Fluorophore> parseMasterList(File fpList) throws IOException
    {
        //to do
        HashMap<String, Fluorophore> returnList = parse(fpList);
        return returnList;
    }
    
    public static HashMap<String, Fluorophore> parseFPDir(File directory) throws IOException
    {
        //to do
        HashMap<String, Fluorophore> returnList = ScrapedCSVParse.parse(directory);
        return returnList;        
    }
    
    public static Cytometer parseCytometer(File csvFortessa) throws IOException
    {
        Cytometer returnCyto = fpFortessaParse.parse(csvFortessa);
        return returnCyto;
    }
    
    public static ArrayList<SelectionInfo> exhaustiveSearch(int n, HashMap<String, Fluorophore> fps, Cytometer cyto) throws IOException
    {
        return ExhaustiveSelection.run(n, fps, cyto);
    }
    public static ArrayList<SelectionInfo> beamWidthSearch(int n, double width,  HashMap<String, Fluorophore> fps, Cytometer cyto) throws IOException
    {
        return SemiExhaustiveSelection.run(n, fps, cyto, width);
    }
    public static ArrayList<SelectionInfo> hillClimbSearch(int n, HashMap<String, Fluorophore> fps, Cytometer cyto) throws IOException
    {
        return HillClimbingSelection.run(n, fps, cyto);
    }
    public static ArrayList<SelectionInfo> simulatedAnnealSearch(int n, HashMap<String, Fluorophore> fps, Cytometer cyto) throws IOException
    {
        return SimulatedAnneal.simulateAnnealing(fps, cyto, n);
    }
    //For validating existing setups, provide a Hashmap of fluorophores chosen and your full cytometer data.
    public static ArrayList<SelectionInfo> validator(HashMap<String, Fluorophore> fps, Cytometer cyto) throws IOException
    {
        //By running it like this, it'll just suggest the strongest expressing filter-fp matchups, 
        //and the hill climbing portion will be skipped since we aren't clipping any fp's
        return HillClimbingSelection.run(fps.size(), fps, cyto);
    }
    
}
