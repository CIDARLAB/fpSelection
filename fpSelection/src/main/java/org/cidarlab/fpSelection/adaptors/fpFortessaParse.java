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

public class fpFortessaParse {
    
    public void parseFortessa(File fortessaCSV) throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(fortessaCSV.getAbsolutePath()));
        
        String line = reader.readLine();
        String splitter = ",";
        String[] fields;
        
        while(line != null)
        {
            fields = line.split(splitter);
        }
        
    }
    
}
