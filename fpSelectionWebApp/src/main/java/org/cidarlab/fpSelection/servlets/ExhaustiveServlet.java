/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.cidarlab.fpSelection.adaptors.fpSpectraParse;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.cidarlab.fpSelection.Algorithms.ExhaustiveSelection;
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
//        File fpInput = new File("src/main/resources/fpList.csv");
//        File cytoInput = new File("src/main/resources/cyto.csv");
        Collection<Part> parts = request.getParts();
        
        

        InputStream fpInput = request.getPart("FPMasterList").getInputStream();
        InputStream cytoInput = request.getPart("cytometer").getInputStream();
//        
//        try {
//            if (request.getPart("FPMasterList").getSize() != 0 && request.getPart("cytometer").getSize() != 0) {
//
//                InputStream fpStream = request.getPart("FPMasterList").getInputStream();
//                InputStream cytoStream = request.getPart("cytometer").getInputStream();
//
//                fpInput = new File("src/main/resources/fpList.txt");
//                cytoInput = new File("src/main/resources/cyto.txt");
//
//                OutputStream fpOut = new FileOutputStream(fpInput);
//                OutputStream cytoOut = new FileOutputStream(cytoInput);
//
//                byte[] bytes = new byte[1024];
//                
//                try {
//                    while (fpStream.read(bytes) != -1) {
//                        fpOut.write(bytes);
//                    }
//                    fpOut.close();"src/main/resources/cyto.csv"
//                    while (cytoStream.read(bytes) != -1) {
//                        cytoOut.write(bytes);
//                    }
//                    cytoOut.close();
//
//                } catch (Exception e) {
//                    System.out.println("Exception encountered when parsing CSV's: Defaulting...");
//                    fpInput = new File("src/main/resources/fp_spectra.csv");
//                    cytoInput = new File("src/main/resources/ex_fortessa.csv");
//                }
//            } else {
//                System.out.println("Shit was empty, sucks bro");
//                fpInput = new File("src/main/resources/fp_spectra.csv");
//                cytoInput = new File("src/main/resources/ex_fortessa.csv");
//            }
//        } catch (Throwable e) {
//            Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, "YOU DONE FUCKED UP: ", e);
//        }
        /////////////////////
        // Parse the files //
        /////////////////////
        HashMap<String, Fluorophore> spectralMaps = fpSpectraParse.parse(fpInput);
        Cytometer cytoSettings = fpFortessaParse.parse(cytoInput);

        ////////////////////////////////////////////
        // Parse the rest of the request variables//
        ////////////////////////////////////////////
        int n = Integer.parseInt(request.getParameter("n"));

        ArrayList<SelectionInfo> selected = ExhaustiveSelection.run(n, spectralMaps, cytoSettings);

        ProteinSelector.calcSumSigNoise(selected);
        ProteinSelector.generateNoise(selected);

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
