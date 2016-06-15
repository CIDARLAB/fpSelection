/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.adaptors;

/**
 *
 * @author david
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Laser;

public class fpFortessaParse {

    public static void main(String[] args) throws IOException {

        File input = new File("src/main/resources/ex_fortessa.csv");

        Cytometer shinyCytometer;
        shinyCytometer = parseFortessa(input);

        System.out.println("Sheath Pressure: " + shinyCytometer.getSheathPressure()); 
        System.out.println("Nozzle Size: " + shinyCytometer.getNozzleSize());
        System.out.println("Window Extension: " + shinyCytometer.getWindowExt());

        LinkedList<Laser> lasers = shinyCytometer.getLasers();
        LinkedList<Detector> detectors;
        for (int i = 0; i < lasers.size(); i++) {
            Laser thisLaser = lasers.get(i);
            System.out.println("Laser " + thisLaser.getPosition() + ": " + thisLaser.getName());

            System.out.println(thisLaser.getType());
            System.out.println(thisLaser.getWavelength());
            System.out.println(thisLaser.getPower());
            System.out.println(thisLaser.getDetectorArray());
            detectors = thisLaser.getDetectors();

            for (int j = 0; j < detectors.size(); j++) {
                Detector thisDetector = detectors.get(j);

                System.out.println(thisDetector.getIdentifier() + ": " + thisDetector.getChannel() + " " + thisDetector.getMirror() + " " + thisDetector.getFilterMidpoint() + " " + thisDetector.getFilterWidth());
            }
            System.out.println("");
        }

    }

    public static Cytometer parseFortessa(File fortessaCSV) throws FileNotFoundException, IOException {

        System.out.println("Initializing reader, cytometer object, and variables...");
        // Reader takes in our CSV file
        BufferedReader reader = new BufferedReader(new FileReader(fortessaCSV.getAbsolutePath()));

        //Initialize Cytometer object and variables used to parse CSV
        Cytometer configurations = new Cytometer();
        String line = reader.readLine();
        String splitter = ",";
        String[] fields;
        String category;
        boolean laserSection = false;

        //When not in laser half of the document, we are going to be saving physical settings of the flow cytometer
        while (laserSection == false && line != null) {
            line = reader.readLine();
            fields = line.split(",");
            category = fields[0];

            if (category.equalsIgnoreCase("Sheath Pressure")) {
                System.out.println("Saving system settings...");
                configurations.setSheathPressure(Integer.parseInt(fields[1]));

                //Set Nozzle Size on next line
                fields = reader.readLine().split(splitter);
                configurations.setNozzleSize(Integer.parseInt(fields[1]));

                //Set Window Extension on next line
                fields = reader.readLine().split(splitter);
                configurations.setWindowExt(Integer.parseInt(fields[1]));
            }
            if (category.equalsIgnoreCase("Laser Name")) {
                //Next line should be first laser
                laserSection = true;
            }
        }

        line = reader.readLine();
        fields = line.split(splitter);
        category = fields[0];
        boolean end = false;

        System.out.println("Saving Laser + Detector configurations");
        //The rest of the file should be Laser + Detector configurations
        while (line != null) {

            //Laser settings - functions are descriptive enough.
            Laser newLaser = new Laser();
            newLaser.setName(fields[0]);
            newLaser.setType(fields[1]);
            newLaser.setWavelength(Integer.parseInt(fields[2]));
            newLaser.setPower(Integer.parseInt(fields[3]));
            newLaser.setDetectorArray(fields[4]);
            newLaser.setPosition(Integer.parseInt(fields[12]));

            //Receive all of the detector settings associated with the above Laser.
            do {

                if (!end) {
                    Detector newDetector = new Detector();
                    newDetector.setIdentifier(fields[5]);
                    newDetector.setChannel(Integer.parseInt(fields[6]));

                    //Mirror in the form of "### LP"
                    //parse substring before the " LP"
                    if (!fields[7].isEmpty()) {
                        newDetector.setMirror(Integer.parseInt(fields[7].substring(0, (fields[7].length() - 3))));
                    } else {
                        newDetector.setMirror(0);

                        //end of this laser's detectors. If I check using fields[], I will go out of bounds as anything past fields[5] does not exist.
                        end = true;
                    }
                    //Filter settings in the form "###/@@ BP" where ### = Filter midpoint and @@ is width.
                    int slash = fields[8].indexOf("/");

                    //parse substring before the '/' to get the midpoint
                    newDetector.setFilterMidpoint(Integer.parseInt(fields[8].substring(0, slash)));
                    //parse substring after the '/' and before the " BP"
                    newDetector.setFilterWidth(Integer.parseInt(fields[8].substring(slash + 1, (fields[8].length() - 3))));

                    //Add detector to laser and move forward
                    newLaser.addDetector(newDetector);

                    line = reader.readLine();
                    fields = line.split(splitter);
                    category = fields[0];
                } else {
                    //Push to the next laser 
                    line = reader.readLine();

                    //so that EOF doesn't crush my soul
                    if (line == null) {
                        break;
                    }
                    fields = line.split(splitter);
                    category = fields[0];
                }

            } while (category.isEmpty());

            //Add completed laser-detectors combo to Cytometer object and move onto next until EOF.
            end = false;
            configurations.addLaser(newLaser);
        }
        //Tada, we have the cytometer object for use to be saved somewhere else.
        return configurations;
    }
}
