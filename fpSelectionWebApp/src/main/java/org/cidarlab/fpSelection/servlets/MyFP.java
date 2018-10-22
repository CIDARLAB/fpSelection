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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cidarlab.fpSelection.algorithms.MyFPSelection;
import org.cidarlab.fpSelection.GUI.PlotAdaptor;
import org.cidarlab.fpSelection.algorithms.ExhaustiveSelectionMultiThreaded;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.cidarlab.fpSelection.selectors.FilterSelector;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.json.JSONObject;

/**
 *
 * @author Alex
 */
@MultipartConfig
public class MyFP extends HttpServlet {

    private ServletContext context;

    public void init(ServletConfig config) throws ServletException {
        this.context = config.getServletContext();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/") + "/_LayoutHeader.html")));
        String str;
        PrintWriter out = response.getWriter();
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
        in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/") + "/MyFP.html")));
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
        in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/") + "/_LayoutFooter.html")));
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /////////////////////////////////////////
        // Parse CSVs and turn them into Files //
        /////////////////////////////////////////
        String errMsg = "";
        InputStream fpInput;
        InputStream cytoInput;
        try {
            fpInput = request.getPart("FPMasterList").getInputStream();
            cytoInput = request.getPart("cytometer").getInputStream();
        } catch (Exception e) {
            errMsg += "Error downloading CSV's: Error " + e.toString() + " \n ";
            PrintWriter writer = response.getWriter();
            JSONObject result = new JSONObject();
            result.put("SNR", errMsg);
            writer.println(result);
            writer.flush();
            writer.close();
            return;
        }
        
        /////////////////////
        // Parse the files //
        /////////////////////
        Map<String, Fluorophore> spectralMaps = null;
        Cytometer cytoSettings = null;
        try {
            spectralMaps = fpSpectraParse.parse(fpInput);
            cytoSettings = fpFortessaParse.parse(cytoInput, false);
        } catch (Exception x) {
            errMsg += "CSVs formatted incorrectly or unreadable: Error " + x.toString() + " \n ";
            x.printStackTrace();          
            PrintWriter writer = response.getWriter();
            JSONObject result = new JSONObject();
            result.put("SNR", errMsg);
            writer.println(result);
            writer.flush();
            writer.close();
            return;
        }
        fpInput.close();
        cytoInput.close();

        ////////////////////////////////////////////
        // Parse the rest of the request variables//
        ////////////////////////////////////////////
        int n = spectralMaps.size();
        ExhaustiveSelectionMultiThreaded algo = new ExhaustiveSelectionMultiThreaded();
        
        List<SelectionInfo> optimal = new ArrayList<>();
        ArrayList<SelectionInfo> everything = new ArrayList<>();
        try {
            optimal = algo.run(n, spectralMaps, cytoSettings, 8);
        } catch (InterruptedException ex) {
            Logger.getLogger(ExhaustiveServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(ExhaustiveServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        ProteinSelector.calcSumSigNoise(optimal);
        ProteinSelector.generateNoise(optimal);
        
        everything = MyFPSelection.run(n, spectralMaps, cytoSettings);

        LinkedList<String> info = PlotAdaptor.webPlot(everything);

        PrintWriter writer = response.getWriter();
        JSONObject result = new JSONObject();
        
        String optimalInfo = "Optimal Selection:\r\n";
        for (SelectionInfo si : optimal)
        {
            optimalInfo += si.selectedFluorophore.name + " Detector: " + si.selectedDetector.identifier + " Laser: " + si.selectedLaser.getName() + " SNR : " + String.format("%.3f", si.SNR) + "\r\n";;
        }

        result.put("img", info.get(0));
        result.put("SNR", optimalInfo);
        writer.println(result);
        writer.flush();
        writer.close();

    }
}
