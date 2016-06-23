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
import java.util.Scanner;
import javax.swing.JOptionPane;
import static org.cidarlab.fpSelection.adaptors.ScrapedCSVParse.generateFPs;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import static org.cidarlab.fpSelection.adaptors.fpSelectionAdaptor.uploadFluorescenceSpectrums;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.dom.SelectionInfo;

/**
 *
 * @author david
 */
public class SelectionTest {

    public static void main(String[] args) throws IOException {
        File input = new File("src/main/resources/fp_spectra.csv");
        HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);

//        File input = new File("src/main/resources/Fluorophores.org/");
//        HashMap<String, Fluorophore> spectralMaps = generateFPs(input);
        
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parseFortessa(cyto);

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Give an integer n for the number of you would like: ");
//        int n = scanner.nextInt();
        String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        if(numString == "")
        {
            numString = "1";
        }
        int n = Integer.parseInt(numString);
        

        ArrayList<SelectionInfo> solution = ProteinSelector.chooseFPs(spectralMaps, testCyto, n);

        ProteinSelector.plotSelection(solution);
    }

}