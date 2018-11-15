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
import org.cidarlab.fpSelection.algorithms.SemiExhaustiveSelection;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.ScrapedCSVParse;

/**
 *
 * @author Alex
 */
public class SemiExhaustiveSelectionTest {

    public static void main(String[] args) throws IOException {
        
        //Get fluorophore set
        //File input = new File("src/main/resources/fp_spectra.csv");
        //HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);
        File input = new File("resources/Fluorophores.org/");
        HashMap<String, Fluorophore> spectralMaps = ScrapedCSVParse.parse(input);

        //Get cytometer settings
        Cytometer cytometer = fpFortessaParse.parse(ParserTest.BUfortessafp, false);

        //User input number of FPs
        //String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        //int n = Integer.parseInt(numString);
        int n = 6;
        
        double topPercent = .005;
         
        ArrayList<SelectionInfo> selected = SemiExhaustiveSelection.run(n, spectralMaps, cytometer, topPercent);

        ProteinSelector.plotSelection(selected);
    }
}
