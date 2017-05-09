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
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.parsers.ScrapedCSVParse;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import static org.cidarlab.fpSelection.parsers.ScrapedCSVParse.parse;

/**
 *
 * @author david
 */
public class TheAmazingRaceTest {

    public static void main(String[] args) throws IOException {
//        File input = new File("src/main/resources/fp_spectra.csv");
//        HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);


        //Large folder
        File input = new File("src/main/resources/Fluorophores.org/");
        HashMap<String, Fluorophore> spectralMaps = ScrapedCSVParse.parse(input);

        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parse(cyto, false);

        for (int n = 5; n < 9; n++) {
            System.out.println();
            System.out.println();
            System.out.println("_________________________________");
            System.out.println("RACE # " + n);

            ArrayList<SelectionInfo> annealSelect;
            ArrayList<SelectionInfo> hillSelect;

            long annealTime;
            long hillTime;

            double annealScore;
            double hillScore;

            long time = System.currentTimeMillis();
            annealSelect = RestrictedAnneal.AnnealMeBaby(spectralMaps, testCyto, n);
            annealTime = System.currentTimeMillis() - time;

            time = System.currentTimeMillis();
            hillSelect = HillClimbingSelection.run(n, spectralMaps, testCyto);
            hillTime = System.currentTimeMillis() - time;

            annealScore = ProteinSelector.calcSumSigNoise(annealSelect);
            hillScore = ProteinSelector.calcSumSigNoise(hillSelect);

            System.out.println("Anneal: ");
            System.out.println("Time: " + annealTime);
            System.out.println("Score: " + annealScore);
            for (SelectionInfo each : annealSelect) {
                System.out.println(annealSelect.indexOf(each) + " : " + each.SNR);
            }
            System.out.println();

            System.out.println("Hill: ");
            System.out.println("Time: " + hillTime);
            System.out.println("Score: " + hillScore);
            for (SelectionInfo each : hillSelect) {
                System.out.println(hillSelect.indexOf(each) + " : " + each.SNR);
            }
            
            System.out.println();
            if(hillScore > annealScore)
            {
                System.out.println("HILL CLIMBER");
            }
            else if(hillScore == annealScore)
            {
                System.out.println("A TIE");
            }
            else
            {
                System.out.println("SIMULATED ANNEAL");
            }
        }
    }

}
