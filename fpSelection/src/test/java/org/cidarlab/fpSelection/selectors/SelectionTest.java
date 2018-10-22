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
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;

/**
 *
 * @author david
 */
public class SelectionTest {

    public static void main(String[] args) throws IOException {

        Map<String, Fluorophore> spectralMaps = fpSpectraParse.parse(ParserTest.fpSpectrafp);

//        File input = new File("src/main/resources/Fluorophores.org/");
//        HashMap<String, Fluorophore> spectralMaps = parse(input);
        

        Cytometer testCyto = fpFortessaParse.parse(ParserTest.fortessafp, false);

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Give an integer n for the number of you would like: ");
//        int n = scanner.nextInt();
        String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        if(numString == "")
        {
            numString = "1";
        }
        int n = Integer.parseInt(numString);
        

        List<SelectionInfo> solution = HillClimbingSelection.run(n, spectralMaps, testCyto);

        ProteinSelector.plotSelection(solution);
    }

}
