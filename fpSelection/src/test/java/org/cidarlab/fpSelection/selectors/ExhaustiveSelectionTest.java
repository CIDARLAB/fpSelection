/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;

/**
 *
 * @author Alex
 */
public class ExhaustiveSelectionTest {

    public static void main(String[] args) throws IOException {
        
        //Get fluorophore set
        
        Map<String, Fluorophore> spectralMaps = fpSpectraParse.parse(ParserTest.fpSpectrafp);
        //File input = new File("src/main/resources/Fluorophores.org/");
        //HashMap<String, Fluorophore> spectralMaps = generateFPs(input);

        //Get cytometer settings
        Cytometer cytometer = fpFortessaParse.parse(ParserTest.BUfortessafp, false);
        
        //User input number of FPs
        //String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        //int n = Integer.parseInt(numString);

        int n = 1;
        final long startTime = System.currentTimeMillis();

        List<SelectionInfo> selected = ExhaustiveSelection.run(n,spectralMaps,cytometer);
        System.out.println("time: " + (System.currentTimeMillis() - startTime) + " ms");
        
        ProteinSelector.plotSelection(selected);
    }
}
