/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cidarlab.fpSelection.Algorithms.SemiExhaustiveSelection;
import org.cidarlab.fpSelection.GUI.PlotAdaptor;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import org.cidarlab.fpSelection.adaptors.fpSpectraParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.json.JSONObject;

/**
 *
 * @author Alex
 */
public class SemiExhaustiveServlet extends HttpServlet {
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Get fluorophore set
        File input = new File("src/main/resources/fp_spectra.csv");
        HashMap<String, Fluorophore> spectralMaps = fpSpectraParse.parse(input);
        //File input = new File("src/main/resources/Fluorophores/org");
        //HashMap<String, Fluorophore> spectralMaps = generateFPs(input);

        //Get cytometer settings
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer cytometer = fpFortessaParse.parse(cyto);

        //User input number of FPs
        //String numString = JOptionPane.showInputDialog("Input an integer n for the number of FPs you'd like");
        //int n = Integer.parseInt(numString);
        int n =  Integer.parseInt(request.getParameter("n"));
        
        double topPercent = Double.parseDouble(request.getParameter("topPercent")) * .01;
        
        ArrayList<SelectionInfo> selected = SemiExhaustiveSelection.run(n, spectralMaps, cytometer, topPercent);
        
        LinkedList<String> info = PlotAdaptor.webPlot(selected);
        
        PrintWriter writer = response.getWriter();
        JSONObject result = new JSONObject();
        
        result.put("img", info.get(0));
        result.put("SNR", info.get(1));
        writer.println(result);
        writer.flush();
        writer.close();
    }
}
