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