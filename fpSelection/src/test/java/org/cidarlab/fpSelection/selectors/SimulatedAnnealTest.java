/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import org.cidarlab.fpSelection.dom.SelectionInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import static org.cidarlab.fpSelection.parsers.ScrapedCSVParse.parse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import static org.cidarlab.fpSelection.parsers.fpSpectraParse.parse;

/**
 *
 * @author david
 */
public class SimulatedAnnealTest {

    
    
    
    
    
    //Simulated Annealing here is performed with too much randomness. 
    //Produces results worse than the existing.
    
    public static void main(String[] args) throws IOException {
        File input = new File("src/main/resources/fp_spectra.csv");
        Map<String, Fluorophore> spectralMaps = fpSpectraParse.parse(input);

//        File input = new File("src/main/resources/Fluorophores.org/");
//        HashMap<String, Fluorophore> spectralMaps = parse(input);
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parse(cyto);

        String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        if (numString.isEmpty()) {
            numString = "1";
        }
        int n = Integer.parseInt(numString);

        //Anneal that shit
        ArrayList<SelectionInfo> selected = SimulatedAnneal.simulateAnnealing(spectralMaps, testCyto, n);
        
//        System.out.println("Return didn't break me");
        
        ProteinSelector.generateNoise(selected);
        
//        System.out.println("Noise didn't break me");

        SimulatedAnneal.plotSelection(selected);
//        System.out.println("Nothing broke me wtf");
    }

}
