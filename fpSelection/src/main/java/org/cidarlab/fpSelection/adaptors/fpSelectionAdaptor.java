/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.adaptors;

import com.panayotis.gnuplot.GNUPlotParameters;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.cidarlab.fpSelection.dom.Fluorophore;
import java.util.regex.Pattern; 
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.swing.JPlot;
import javax.swing.JFrame;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Smooth;
import com.panayotis.gnuplot.style.Style;
import java.util.Iterator;


/**
 *
 * @author Alex
 */
public class fpSelectionAdaptor {
    
    public static void main(String args[]) throws FileNotFoundException, Exception {
        File input = new File("src/main/resources/fp_spectra.csv"); 
        HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);
    }
     
    /*
     * This method is for uploading fluorescence spectrum data to be associated with Fluorphore objects
     */
    public static final Pattern p = Pattern.compile(" \\((EX|EM|AB)\\)"); //regex pattern to remove (EX),(EM), and (AB)
    public static HashMap<String, Fluorophore> uploadFluorescenceSpectrums(File input) throws FileNotFoundException, IOException {

        //Import file, begin reading
        BufferedReader reader = new BufferedReader(new FileReader(input.getAbsolutePath()));
        HashMap<String, Fluorophore> spectralMaps = new HashMap<>();

        //The first line describes the spectra
        String line = reader.readLine();
        String[] spectra = line.split(",");
        String[] spectraTrimmedStrings = new String[spectra.length];
        int numSpectra = spectra.length;
        for (int i = 1; i < numSpectra; i++) {
            Fluorophore f = new Fluorophore();
            
            spectraTrimmedStrings[i] = p.matcher(spectra[i]).replaceAll(""); //remove above regex pattern
            f.setName(spectraTrimmedStrings[i]);
            f.EMspectrum = new PointDataSet();
            f.EXspectrum = new PointDataSet();
            spectralMaps.put(spectraTrimmedStrings[i],f);
        }
        line = reader.readLine();

        //Read each line of the input file to parse parts
        Point p; 
        while (line != null) {
            String[] tokens = line.split(",");
            for (int j = 1; j < tokens.length; j++) {
                if (!tokens[j].isEmpty()) {
                    if (spectra[j].contains("EX") || spectra[j].contains("AB")) {
                        p = new Point(Double.parseDouble(tokens[0]),Double.parseDouble(tokens[j])*100);
                        spectralMaps.get(spectraTrimmedStrings[j]).EXspectrum.add(p);
                        if(j == 1)
                        {
                            spectralMaps.get(spectraTrimmedStrings[j]).EXstartx = Integer.parseInt(tokens[0]);
                        }
                    } else if (spectra[j].contains("EM")) {
                        p = new Point(Double.parseDouble(tokens[0]),Double.parseDouble(tokens[j])*100);
                        spectralMaps.get(spectraTrimmedStrings[j]).EMspectrum.add(p);
                        if(j == 1)
                        {
                            spectralMaps.get(spectraTrimmedStrings[j]).EMstartx = Integer.parseInt(tokens[0]);
                        }
                    }                  
                }
            }
            line = reader.readLine();
        }
        
        
        JavaPlot javaPlot = new JavaPlot();
        javaPlot.setTitle("FP Spectrum", "Helvetica", 14);
        javaPlot.getAxis("x").setLabel("Wavelength (nm)");
        javaPlot.getAxis("x").setBoundaries(0, 1000);
        javaPlot.getAxis("y").setLabel("Intensity (%)");
        javaPlot.getAxis("y").setBoundaries(0, 100);
        javaPlot.set("key font", "',7'");
        //javaPlot.set("term qt size", "600,400");


        
        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.LINES);  
        myPlotStyle.setLineWidth(1);
        
        //myPlotStyle.setStyle(Smooth.ACSPLINES);
        //javaPlot.set("smooth", "csplines");
        Iterator it = spectralMaps.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Fluorophore f = (Fluorophore) pair.getValue();
            AbstractPlot emDataSetPlot = new DataSetPlot(f.EMspectrum);
            AbstractPlot exDataSetPlot = new DataSetPlot(f.EXspectrum);
            emDataSetPlot.setTitle(pair.getKey() + " (EM)");
            exDataSetPlot.setTitle(pair.getKey() + " (EX)");
            emDataSetPlot.setPlotStyle(myPlotStyle);
            exDataSetPlot.setPlotStyle(myPlotStyle);
            emDataSetPlot.setSmooth(Smooth.UNIQUE);
            exDataSetPlot.setSmooth(Smooth.UNIQUE);
            javaPlot.addPlot(emDataSetPlot);
            javaPlot.addPlot(exDataSetPlot);
            //it.remove(); // avoids a ConcurrentModificationException
        }
        
        //javaPlot.plot();
        
        JPlot jPlot = new JPlot(javaPlot);
        //jPlot.setBounds(20,20,5000,5000);
        jPlot.plot();
        jPlot.repaint();
        JFrame frame = new JFrame("FP Spectrum");
        frame.getContentPane().add(jPlot);
        frame.pack();
       
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        
        return spectralMaps;
    }
}
