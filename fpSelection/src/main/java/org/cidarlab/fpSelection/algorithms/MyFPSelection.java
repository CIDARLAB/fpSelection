/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.algorithms;

import java.util.ArrayList;
import java.util.Map;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;

/**
 *
 * @author Alex
 */
public class MyFPSelection {

    public static ArrayList<SelectionInfo> run(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer) {
        
        ArrayList<SelectionInfo> selected = new ArrayList<>();
        for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
            SelectionInfo si = new SelectionInfo();
            si.myFPCytometer = cytometer;
            si.selectedFluorophore = entry.getValue();
            selected.add(si);
        }        

        //prepare data for graphs
        
        return selected;
    }
}