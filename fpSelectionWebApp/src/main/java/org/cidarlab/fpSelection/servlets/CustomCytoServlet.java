/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Laser;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author david
 */
public class CustomCytoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader read = request.getReader();

        String json = read.readLine();
        //JSONArray(Entire Form) of JSONArrays(Laser+Filters) of JSONObjects(fields like laser name, etc)
        JSONArray arrayJSON = new JSONArray(json);
        JSONArray LaserArray;
        
        ArrayList<Laser> setup = new ArrayList<>();

        for (int i = 0; i < arrayJSON.length(); i++) {
            LaserArray = arrayJSON.getJSONArray(i);            
            LinkedList<Detector> DetectorArray = new LinkedList<>();
            Laser lase = new Laser();
            lase.position = i;
            Detector temp = null;
            
            for(int j = 0; j < LaserArray.length();j++)
            {
                JSONObject nameValue = LaserArray.getJSONObject(j);
                String propName = nameValue.getString("name");
                switch(propName)
                {
                    case "laser":
                        lase.name = nameValue.getString("value");
                        break;
                    case "laserWavelength":
                        lase.wavelength = nameValue.getInt("value");
                        break;
                    case "filter":
                        temp = new Detector();
                        temp.filterMidpoint = nameValue.getInt("value");
                        break;
                    case "filterWidth":
                        temp.filterWidth = nameValue.getInt("value");
                        DetectorArray.add(temp);
                        temp.channel = DetectorArray.size();
                        temp.identifier = (String.valueOf((char)48+j));
                        break;
                    default:
                        System.out.println("Encountered an error while parsing JSONArray");
                        break;                            
                }
            }
            lase.detectors = DetectorArray;
            setup.add(lase);
            
        }
        response.setContentType("text/csv");
        PrintWriter writer = response.getWriter();
        BufferedReader in = new BufferedReader(new FileReader("src/main/resources/template.csv"));
        String line;
        while((line = in.readLine()) != null)
        {
            writer.println(line);
        }
        
        
        for(Laser each : setup)
        {
            Iterator<Detector> iter = each.detectors.iterator();
            Detector it = iter.next();
            String out = each.name + ",Custom," + each.wavelength + ",100,Custom," + it.identifier + "," + it.channel +",N/A LP," + it.filterMidpoint + "/" + it.filterWidth + " BP,,,," + setup.indexOf(each);
            //out is the format of the 1st line
            writer.println(out);
            
            for(int i = 1; i < each.detectors.size(); i++)
            {
                it = iter.next();
                writer.println(",,,,," + it.identifier + "," + it.channel +",N/A LP," + it.filterMidpoint + "/" + it.filterWidth + " BP,,,,");
            }
            
        }
        
        writer.flush();
        writer.close();
    }

}
