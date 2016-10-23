/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.Algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;

/**
 *
 * @author Alex
 */
public class MyFPSelection {

    public static ArrayList<SelectionInfo> run(int n, HashMap<String, Fluorophore> spectralMaps, Cytometer cytometer) {
        
        ArrayList<Fluorophore> fluorophores = new ArrayList<>();
        for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
            fluorophores.add(entry.getValue());
        }        

        //prepare data for graphs
        ArrayList<SelectionInfo> selected = new ArrayList<>();
        SelectionInfo si = new SelectionInfo();
        si.myFPCytometer = cytometer;
        si.rankedFluorophores = fluorophores;
        selected.add(si);
        
        return selected;
    }
}