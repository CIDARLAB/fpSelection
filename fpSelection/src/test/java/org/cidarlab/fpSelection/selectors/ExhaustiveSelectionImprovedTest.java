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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelectionImproved;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;

/**
 *
 * @author Alex
 */
public class ExhaustiveSelectionImprovedTest {

    public static void main(String[] args) throws IOException {
        
        //Get fluorophore set
        //File input = new File("src/main/resources/fp_spectra.csv");
        //HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);
        Map<String, Fluorophore> spectralMaps = fpSpectraParse.parse(ParserTest.fpSpectrafp);
        System.out.println(spectralMaps.keySet());
        //Get cytometer settings
        //File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer cytometer = fpFortessaParse.parse(ParserTest.BUfortessafp, false);

        //User input number of FPs
        //String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        //int n = Integer.parseInt(numString);
        int bigN = 2;
        
        List<SelectionInfo> selected = ExhaustiveSelectionImproved.run(bigN, spectralMaps, cytometer);
        
        ProteinSelector.plotSelection(selected);
    }
}
