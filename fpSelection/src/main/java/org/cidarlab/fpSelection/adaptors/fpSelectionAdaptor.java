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

/**
 *
 * @author Alex
 */
public class fpSelectionAdaptor {
    
    public static void main(String args[]) throws FileNotFoundException, Exception {
        System.out.println("Doing stuff!!");
        File input = new File("src/main/resources/ex_fortessa.csv"); 
        testCsv(input);
    }
    
    
    /*
     * This method is for uploading fluorescence spectrum data to be associated with Fluorphore objects
     */
    public static void uploadFluorescenceSpectrums(File input, Clotho clothoObject) throws FileNotFoundException, IOException {

        //Import file, begin reading
        BufferedReader reader = new BufferedReader(new FileReader(input.getAbsolutePath()));
        HashMap<String, HashMap<Double, Double>> spectralMaps = new HashMap<>();

        //The first line describes the spectra
        String line = reader.readLine();
        String[] spectra = line.split(",");
        int numSpectra = spectra.length;
        for (int i = 1; i < numSpectra; i++) {
            spectralMaps.put(spectra[i], new HashMap<Double, Double>());
        }
        line = reader.readLine();

        //Read each line of the input file to parse parts
        while (line != null) {
            String[] tokens = line.split(",");
            for (int j = 1; j < tokens.length; j++) {
                if (!tokens[j].isEmpty()) {
                    spectralMaps.get(spectra[j]).put(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[j]));
                }
            }
            line = reader.readLine();
        }

        //Look for each Fluorophore and see if their names match any of these spectrums
        Map fluorophoreQuery = new HashMap();
        fluorophoreQuery.put("schema", Fluorophore.class.getCanonicalName());
        List<Fluorophore> queryFluorophores = queryFluorophores(fluorophoreQuery, clothoObject);
        for (String spectrum_name : spectralMaps.keySet()) {
            for (Fluorophore fl : queryFluorophores) {

                //Match spectrums to fluorophore names
                String flName = fl.getName();
                if (spectrum_name.contains(flName.replaceAll(".ref", ""))) {

                    //Match excitation or emmission spectra
                    if (spectrum_name.contains("EX") || spectrum_name.contains("AB")) {
                        fl.setEx_spectrum(spectralMaps.get(spectrum_name));
                    } else if (spectrum_name.contains("EM")) {
                        fl.setEm_spectrum(spectralMaps.get(spectrum_name));
                    }
                }
            }
        }
        for (Fluorophore fluorophore : queryFluorophores) {
            createFluorophore(fluorophore, clothoObject);
        }
    }
}
