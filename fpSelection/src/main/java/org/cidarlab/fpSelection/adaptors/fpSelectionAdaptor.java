/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.adaptors;

/**
 *
 * @author Alex
 */
public class fpSelectionAdaptor {
    
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
    
    /*
     * This method is for uploading fluorescence spectrum data to be associated with Fluorphore objects
     */
    public static void uploadCytometer(File input, Clotho clothoObject) throws FileNotFoundException, IOException {

        //Import file, begin reading
        BufferedReader reader = new BufferedReader(new FileReader(input.getAbsolutePath()));

        //Initialize parameters
        String name = "";
        HashSet<String> lasers = new HashSet<>();
        HashSet<String> filters = new HashSet<>();
        HashMap<String, ArrayList<String[]>> config = new HashMap<>();

        //The first line describes the spectra
        String line = reader.readLine();
        while (line != null) {

            String[] vals = line.split(",");

            //Find name
            if (vals[0].equalsIgnoreCase("Configuration Name")) {
                name = vals[1];
            }

            //Once cytometer key rows are hit
            if (vals[0].equalsIgnoreCase("Laser Name")) {

                line = reader.readLine();

                //Internal loop for each laser
                while (line != null) {

                    String[] cvals = line.split(",");
                    String laser = cvals[2] + ":" + cvals[3];
                    lasers.add(laser);

                    String mirror = cvals[7].substring(0, cvals[7].length() - 3);
                    String filter = cvals[8].substring(0, cvals[8].length() - 3).replaceAll("/", ":");
                    filters.add(mirror);
                    filters.add(filter);
                    ArrayList<String[]> filterList = new ArrayList<>();
                    filterList.add(new String[]{mirror, filter});
                    config.put(laser, filterList);

                    line = reader.readLine();
                    cvals = line.split(",");

                    //Loop kicked into once it reaches the lasers section of the file
                    while (cvals[0].equals("")) {

                        //Only look at row with filters, not empty slots
                        if (cvals.length >= 9) {
                            String newMirror;
                            String newFilter;

                            //If there is a longpass filter
                            if (!cvals[7].isEmpty()) {
                                newMirror = cvals[7].substring(0, cvals[7].length() - 3);
                                filters.add(newMirror);

                                //If there is a bandpass filter
                                if (!cvals[8].isEmpty()) {
                                    newFilter = cvals[8].substring(0, cvals[8].length() - 3).replaceAll("/", ":");
                                    filters.add(newFilter);
                                    filterList.add(new String[]{newMirror, newFilter});
                                }

                            } else {

                                //If there is a bandpass filter
                                if (!cvals[8].isEmpty()) {
                                    newFilter = cvals[8].substring(0, cvals[8].length() - 3).replaceAll("/", ":");
                                    filters.add(newFilter);
                                    filterList.add(new String[]{newFilter});
                                }
                            }
                        }

                        line = reader.readLine();
                        if (line != null) {
                            cvals = line.split(",");
                        } else {
                            cvals = new String[]{"end"};
                        }
                    }
                }
            } else {
                line = reader.readLine();
            }
        }

        Cytometer c = new Cytometer(name, lasers, filters, config);
        createCytometer(c, clothoObject);
    }
}
