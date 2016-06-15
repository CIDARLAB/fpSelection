/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.adaptors;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.swing.JPlot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import javax.swing.JFrame;
import org.cidarlab.fpSelection.dom.Fluorophore;

/**
 *
 * @author david
 */
public class ScrapedCSVParse {

    public static void main(String[] args) throws IOException {
        File folder = new File("/home/david/Desktop/CIDAR/fpSelection/fpSelection/src/main/resources/ScrapedCSVs/");
    
        System.out.println("Generating fluorophore objects from folder of csv's...");
        HashMap<String, Fluorophore> masterList = generateFPs(folder);
        System.out.println("Process complete.");
    
    
    }


    public static HashMap<String, Fluorophore> generateFPs(File directory) throws FileNotFoundException, IOException {
        HashMap<String, Fluorophore> masterList = new HashMap<>();

        File[] listOfFiles = directory.listFiles();

        BufferedReader reader;
        Fluorophore fp;
        
        //JFrame frame = new JFrame("FP Spectrum");

        for (File next : listOfFiles) {
            String name = next.getName().replace(".csv", "");
            fp = new Fluorophore();
            fp.setName(name);

            TreeMap<Double, Double> EMSpectrum = new TreeMap<>();
            TreeMap<Double, Double> EXSpectrum = new TreeMap<>();

            reader = new BufferedReader(new FileReader(next));

            String line = reader.readLine();
            String splitter = ";";
            String tokens[];

            //Read again cause first line is labels showing [0];[1];[2];[3];
            line = reader.readLine();
            while (line != null) {
                //[0]   Absorption wavelength (nm); 
                //[1]   Normalized Absorption; 
                //[2]   Emission wavelength (nm); 
                //[3]   Normalized emission;

                tokens = line.split(splitter);

                if (tokens != null && !tokens[0].isEmpty()) {
                    EXSpectrum.put(Double.parseDouble(tokens[0]), 100 * Double.parseDouble(tokens[1]));
                }
                if (tokens != null && tokens.length >= 3 && !tokens[2].isEmpty()) {
                    EMSpectrum.put(Double.parseDouble(tokens[2]), 100 * Double.parseDouble(tokens[3]));
                }

                line = reader.readLine();

            }
            fp.EMspectrum = EMSpectrum;
            fp.EXspectrum = EXSpectrum;
            
            masterList.put(fp.getName(), fp);

//            JavaPlot plotPls = new JavaPlot();
//            AbstractPlot styled = new DataSetPlot(fp.makeEMDataSet());
//            PlotStyle stylish = new PlotStyle(Style.LINES);
//            
//            styled.setPlotStyle(stylish);
//            styled.setTitle(fp.getName());
//
//            plotPls.addPlot(styled);
//
//            JPlot jj = new JPlot(plotPls);
//            jj.plot();
//            jj.repaint();
//
//            frame.getContentPane().removeAll();
//            frame.getContentPane().add(jj);
//            frame.pack();
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setVisible(true);

        }

        return masterList;
    }

}
