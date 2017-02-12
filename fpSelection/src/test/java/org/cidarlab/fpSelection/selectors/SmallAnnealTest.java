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
import javax.swing.JOptionPane;
import org.cidarlab.fpSelection.parsers.ScrapedCSVParse;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;

/**
 *
 * @author david
 */
public class SmallAnnealTest {

    public static void main(String[] args) throws IOException {
//        File input = new File("src/main/resources/fp_spectra.csv");
//        HashMap<String, Fluorophore> spectralMaps = parse(input);

        File input = new File("src/main/resources/Fluorophores.org/");
        HashMap<String, Fluorophore> spectralMaps = ScrapedCSVParse.parse(input);
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parse(cyto);

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Give an integer n for the number of you would like: ");
//        int n = scanner.nextInt();
        String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        if (numString == "") {
            numString = "1";
        }
        int n = Integer.parseInt(numString);

        
        //LET THE MAGIC OCCUR.
        ArrayList<SelectionInfo> solution = RestrictedAnneal.AnnealMeBaby(spectralMaps, testCyto, n);
        
        ProteinSelector.generateNoise(solution);
        ProteinSelector.plotSelection(solution);
    }
}
