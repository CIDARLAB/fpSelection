/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.adaptors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import java.util.regex.Pattern; 

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
            f.name = spectraTrimmedStrings[i];
            f.EMspectrum = new HashMap<>();
            f.EXspectrum = new HashMap<>();
            spectralMaps.put(spectraTrimmedStrings[i],f);
        }
        line = reader.readLine();

        //Read each line of the input file to parse parts
        while (line != null) {
            String[] tokens = line.split(",");
            for (int j = 1; j < tokens.length; j++) {
                if (!tokens[j].isEmpty()) {
                    if (spectra[j].contains("EX") || spectra[j].contains("AB")) {
                        spectralMaps.get(spectraTrimmedStrings[j]).EXspectrum.put(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[j]));
                    } else if (spectra[j].contains("EM")) {
                        spectralMaps.get(spectraTrimmedStrings[j]).EMspectrum.put(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[j]));
                    }
                    
                }
            }
            line = reader.readLine();
        }
        return spectralMaps;
    }
}
