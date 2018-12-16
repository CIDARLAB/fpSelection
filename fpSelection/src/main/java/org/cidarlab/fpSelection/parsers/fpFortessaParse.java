/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.parsers;

/**
 *
 * @author david
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.cidarlab.fpSelection.Utilities;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Laser;

public class fpFortessaParse {

    public static Cytometer parse(String filepath, boolean filterSelection) throws IOException  {
        System.out.println("Initializing reader, cytometer object, and variables...");
        return parse(Utilities.getCSVFileContentAsList(filepath), filterSelection);
    }
    
    public static Cytometer parse(File f, boolean filterSelection) throws IOException  {
        System.out.println("Initializing reader, cytometer object, and variables...");
        return parse(Utilities.getCSVFileContentAsList(f), filterSelection);
    }
    
    public static Cytometer parse(InputStream is, boolean filterSelection) throws IOException  {
        System.out.println("Initializing reader, cytometer object, and variables...");
        return parse(Utilities.getCSVFileContentAsList(is), filterSelection);
    }
    
    private static Cytometer parse(List<String[]> lines, boolean filterSelection) throws FileNotFoundException, IOException {
        
        //Initialize Cytometer object and variables used to parse CSV
        Cytometer cytometer = new Cytometer();
        String category;
        
        
        int laserNameIndex = 0;
        
        List<List<String[]>> laserLinesList = new ArrayList<>();
        List<String[]> laserLines = new ArrayList<>();
        boolean configuration = true;
        for(String[] line:lines){
            if(line[0].equalsIgnoreCase("Laser Name")){
                configuration = false;
                continue;
            }
            
            if(configuration){
                if(line[0].equalsIgnoreCase("Sheath Pressure")){
                    cytometer.setSheathPressure(Integer.valueOf(line[1].trim()));
                } else if(line[0].equalsIgnoreCase("Nozzle Size")){
                    cytometer.setNozzleSize(Integer.valueOf(line[1].trim()));
                } else if(line[0].equalsIgnoreCase("Window Extension")){
                    cytometer.setWindowExt(Integer.valueOf(line[1].trim()));
                }
            } else {
                if(!line[laserNameIndex].isEmpty()){
                    if(!laserLines.isEmpty()){
                        laserLinesList.add(laserLines);
                    }
                    laserLines = new ArrayList<>();
                }
                laserLines.add(line);
            }
        }
        laserLinesList.add(laserLines);
        
        for(List<String[]> laserLine:laserLinesList){
            cytometer.addLaser(parseLaserLines(laserLine));
        }
        return cytometer;
    }
    
    private static Laser parseLaserLines(List<String[]> laserLines){
        Laser laser = new Laser();
        int laserNameIndex = 0;
        int laserTypeIndex = 1;
        int wavelengthIndex = 2;
        int powerIndex = 3;
        int detectorArrayIndex = 4;
        int detectorIndex = 5;
        int channelIndex = 6;
        int mirrorIndex = 7;
        int filterIndex = 8;
        int parameterIndex = 9;
        int fscChannelIndex = 10;
        int referenceIndex = 11;
        int positionIndex = 12;
        
        String[] header = laserLines.get(0);
        laser.setName(header[laserNameIndex].trim());
        laser.type = header[laserTypeIndex].trim();
        laser.wavelength = Integer.valueOf(header[wavelengthIndex].trim());
        if(!header[powerIndex].isEmpty()){
            laser.power = Integer.valueOf(header[powerIndex].trim());
        }
        laser.detectorArray = header[detectorArrayIndex].trim();
        laser.position = Integer.valueOf(header[positionIndex].trim());
        
        for(String[] line:laserLines){
            Detector detector = new Detector();
            detector.identifier = line[detectorIndex].trim();
            if(!line[channelIndex].isEmpty()){
                detector.channel = Integer.valueOf(line[channelIndex].trim());
            }
            if(!line[mirrorIndex].isEmpty()){
                String mirror = line[mirrorIndex].trim();
                mirror = mirror.substring(0, mirror.indexOf("LP")).trim();
                detector.mirror = Integer.valueOf(mirror);
            }
            if(!line[filterIndex].isEmpty()){
                String filter = line[filterIndex].trim();
                filter = filter.substring(0, filter.indexOf("BP")).trim();
                String[] filterpieces = filter.split("/");
                detector.filterMidpoint = Integer.valueOf(filterpieces[0].trim());
                detector.filterWidth = Integer.valueOf(filterpieces[1].trim());
            } else {
                continue;
            }
            if(!line[parameterIndex].isEmpty()){
                String parameter = line[parameterIndex].trim();
                detector.parameter = parameter;
            }
            
            laser.addDetector(detector);
        }
        
        return laser;
    }
}
