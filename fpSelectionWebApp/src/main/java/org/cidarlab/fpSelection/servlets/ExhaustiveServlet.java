/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cidarlab.fpSelection.adaptors.fpSpectraParse;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.Algorithms.ExhaustiveSelectionMultiThreaded;
import org.cidarlab.fpSelection.GUI.PlotAdaptor;
import org.json.JSONObject;

/**
 *
 * @author Alex
 */
@MultipartConfig
public class ExhaustiveServlet extends HttpServlet {

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

        /////////////////////////////////////////
        // Parse CSVs and turn them into Files //
        /////////////////////////////////////////
        boolean fileErr = false;
        String errMsg = "";
        InputStream fpInput;
        InputStream cytoInput;
        try {
            fpInput = request.getPart("FPMasterList").getInputStream();
            cytoInput = request.getPart("cytometer").getInputStream();
        } catch (Exception e) {
            errMsg += "Error downloading CSV's, using sample cytometer and fluorophores : Error " + e.toString() + " \n ";
            fileErr = true;
            fpInput = new FileInputStream("src/main/resources/fp_spectra.csv");
            cytoInput = new FileInputStream("src/main/resources/ex_fortessa.csv");
        }
        int n = Integer.parseInt(new BufferedReader(new InputStreamReader(request.getPart("n").getInputStream())).readLine());
        
        /////////////////////
        // Parse the files //
        /////////////////////
        HashMap<String, Fluorophore> spectralMaps = null;
        Cytometer cytoSettings = null;
        try {
            spectralMaps = fpSpectraParse.parse(fpInput);
        } catch (Exception x) {
            errMsg += "Fluorophore CSV formatted incorrectly or unreadable, using sample fluorophores : Error " + x.toString() + " \n ";
            x.printStackTrace();
            fileErr = true;
            fpInput = new FileInputStream("src/main/resources/fp_spectra.csv");
            spectralMaps = fpSpectraParse.parse(fpInput);
        }
        try {
            cytoSettings = fpFortessaParse.parse(cytoInput);
        } catch (Exception x) {
            errMsg += "Cytometer CSV formatted incorrectly or unreadable, using sample cytometer : Error " + x.toString() + " \n ";
            x.printStackTrace();
            fileErr = true;
            cytoInput = new FileInputStream("src/main/resources/ex_fortessa.csv");
            cytoSettings = fpFortessaParse.parse(cytoInput);
        }

        fpInput.close();
        cytoInput.close();

        ////////////////////////////////////////////
        // Parse the rest of the request variables//
        ////////////////////////////////////////////
        ExhaustiveSelectionMultiThreaded algo = new ExhaustiveSelectionMultiThreaded();
        
        ArrayList<SelectionInfo> selected = new ArrayList<>();
        try {
            selected = algo.run(n, spectralMaps, cytoSettings, 8);
        } catch (InterruptedException ex) {
            Logger.getLogger(ExhaustiveServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(ExhaustiveServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        ProteinSelector.calcSumSigNoise(selected);
        ProteinSelector.generateNoise(selected);

        LinkedList<String> info = PlotAdaptor.webPlot(selected);

        PrintWriter writer = response.getWriter();
        JSONObject result = new JSONObject();

        result.put("img", info.get(0));
        if (fileErr) {
            result.put("SNR", errMsg + info.get(1));
        } else {
            result.put("SNR", info.get(1));
        }
        writer.println(result);
        writer.flush();
        writer.close();

    }
}
