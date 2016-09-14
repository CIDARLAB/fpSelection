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
import org.cidarlab.fpSelection.Algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import static org.cidarlab.fpSelection.adaptors.fpSpectraParse.parse;

/**
 *
 * @author Alex
 */
public class ExhaustiveSelectionTest {

    public static void main(String[] args) throws IOException {
        
        //Get fluorophore set
        File input = new File("src/main/resources/fp_spectra.csv");
        HashMap<String, Fluorophore> spectralMaps = parse(input);
        //File input = new File("src/main/resources/Fluorophores.org/");
        //HashMap<String, Fluorophore> spectralMaps = generateFPs(input);

        //Get cytometer settings
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer cytometer = fpFortessaParse.parse(cyto);

        //User input number of FPs
        //String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        //int n = Integer.parseInt(numString);

        int n = 5;
        final long startTime = System.currentTimeMillis();

        ArrayList<SelectionInfo> selected = ExhaustiveSelection.run(n,spectralMaps,cytometer);
        System.out.println("time: " + (System.currentTimeMillis() - startTime) + " ms");
        
        ProteinSelector.plotSelection(selected);
    }
}
