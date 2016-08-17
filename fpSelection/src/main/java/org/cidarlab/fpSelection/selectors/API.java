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
import org.cidarlab.fpSelection.Algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.Algorithms.ExhaustiveSelectionImproved;
import org.cidarlab.fpSelection.Algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.Algorithms.SemiExhaustiveSelection;
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
    
    public static HashMap<String, Fluorophore> parseMasterList(File fpList)
    {
        //to do
        HashMap<String, Fluorophore> returnList = new HashMap<>();
        return returnList;
    }
    
    public static HashMap<String, Fluorophore> parseFPDir(File directory)
    {
        //to do
        HashMap<String, Fluorophore> returnList = new HashMap<>();
        return returnList;        
    }
    
    public static Cytometer parseCytometer(File csvFortessa)
    {
        Cytometer returnCyto = new Cytometer();
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
    
}
