/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.adaptors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.cidarlab.fpSelection.dom.Fluorophore;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

/**
 *
 * @author Alex
 */
public class fpSelectionAdaptor {

    /*
     * This method is for uploading fluorescence spectrum data to be associated with Fluorphore objects
     */
    public static final Pattern p = Pattern.compile(" \\((EX|EM|AB)\\)"); //regex pattern to remove (EX),(EM), and (AB)

    public static HashMap<String, Fluorophore> uploadFluorescenceSpectrums(InputStream input) throws FileNotFoundException, IOException {

        //Import file, begin reading
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
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
            f.EMspectrum = new LinkedHashMap<>();
            f.EXspectrum = new LinkedHashMap<>();
            spectralMaps.put(spectraTrimmedStrings[i], f);
        }
        line = reader.readLine();

        //Read each line of the input file to parse parts
        double k, v;
        while (line != null) {
            String[] tokens = line.split(",");
            for (int j = 1; j < tokens.length; j++) {
                if (!tokens[j].isEmpty()) {
                    if (spectra[j].contains("EX") || spectra[j].contains("AB")) {

                        k = Double.parseDouble(tokens[0]);
                        v = Double.parseDouble(tokens[j]) * 100;

                        spectralMaps.get(spectraTrimmedStrings[j]).EXspectrum.put(k, v);

                    } else if (spectra[j].contains("EM")) {
                        k = Double.parseDouble(tokens[0]);
                        v = Double.parseDouble(tokens[j]) * 100;

                        spectralMaps.get(spectraTrimmedStrings[j]).EMspectrum.put(k, v);

                    }
                }
            }
            line = reader.readLine();
        }

        return spectralMaps;
    }
}
