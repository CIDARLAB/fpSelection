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
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Laser;

public class fpFortessaParse {
    
    public Cytometer parseFortessa(File fortessaCSV) throws FileNotFoundException, IOException
    {
        // Reader takes in our CSV file
        BufferedReader reader = new BufferedReader(new FileReader(fortessaCSV.getAbsolutePath()));
        
        //Initialize Cytometer object and variables used to parse CSV
        Cytometer configurations = new Cytometer();
        String line = "";
        String splitter = ",";
        String[] fields = null;
        String category = "";
        boolean laserSection = false;
        
        //When not in laser half of the document, we are going to be saving physical settings of the flow cytometer
        while(laserSection == false)
        {
            line = reader.readLine();
            fields = line.split(splitter);
            category = fields[0];

            
            if(category == "Sheath Pressure")
            {
                configurations.setSheathPressure(Integer.parseInt(fields[1]));
                
                //Set Nozzle Size on next line
                fields = reader.readLine().split(splitter);
                configurations.setNozzleSize(Integer.parseInt(fields[1]));
                
                //Set Window Extension on next line
                fields = reader.readLine().split(splitter);
                configurations.setWindowExt(Integer.parseInt(fields[1]));
            }
            if(category == "Laser Name")
            {
                //Next line should be first laser
                line = reader.readLine();
                fields = line.split(splitter);
                category = fields[0];
                
                laserSection = true;
                
            }
        }
        
        //The rest of the file should be Laser + Detector configurations
        while(line != null)
        {
            //Laser settings - functions are descriptive enough.
            Laser newLaser = new Laser();
            newLaser.setType(fields[1]);
            newLaser.setWavelength(Integer.parseInt(fields[2]));
            newLaser.setPower(Integer.parseInt(fields[3]));
            newLaser.setDetectorArray(fields[4]);

            //Receive all of the detector settings associated with the above Laser.
            do {
                //if fields[6] is empty, there's no detector on that line; empty slot.
                if (fields[6] != "") {
                    Detector newDetector = new Detector();
                    newDetector.setIdentifier(fields[5]);
                    newDetector.setChannel(Integer.parseInt(fields[6]));
                    
                    //Mirror in the form of "### LP"
                    //parse substring before the " LP"
                    newDetector.setMirror(Integer.parseInt(fields[7].substring(0, fields[7].length() - 3)));

                    //Filter settings in the form "###/@@ BP" where ### = Filter midpoint and @@ is width.
                    int slash = fields[8].indexOf("/");
                    
                    //parse substring before the '/' to get the midpoint
                    newDetector.setFilterMidpoint(Integer.parseInt(fields[8].substring(0, slash)));
                    //parse substring after the '/' and before the " BP"
                    newDetector.setFilterWidth(Integer.parseInt(fields[8].substring(slash + 1, fields[8].length() - 3)));
                    
                    //Add detector to laser and move forward
                    newLaser.addDetector(newDetector);

                    line = reader.readLine();
                    fields = line.split(splitter);
                    category = fields[0];
                }
            } while (category == "");
            
            //Add completed laser-detectors combo to Cytometer object and move onto next until EOF.
            configurations.addLaser(newLaser);
        }
        //Tada, we have the cytometer object for use to be saved somewhere else.
        return configurations;
    }
}
