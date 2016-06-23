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
import javax.swing.JOptionPane;
import static org.cidarlab.fpSelection.adaptors.ScrapedCSVParse.generateFPs;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import static org.cidarlab.fpSelection.adaptors.fpSelectionAdaptor.uploadFluorescenceSpectrums;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;

/**
 *
 * @author david
 */
public class SimulatedAnnealTest {

    
    
    
    
    
    //Simulated Annealing here is performed with too much randomness. 
    //Produces results worse than the existing.
    
    public static void main(String[] args) throws IOException {
        File input = new File("src/main/resources/fp_spectra.csv");
        HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);

//        File input = new File("src/main/resources/Fluorophores.org/");
//        HashMap<String, Fluorophore> spectralMaps = generateFPs(input);
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parseFortessa(cyto);

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
