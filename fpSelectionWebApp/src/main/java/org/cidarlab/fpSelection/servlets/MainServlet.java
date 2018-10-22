/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.cidarlab.fpSelection.GUI.PlotAdaptor;
import org.cidarlab.fpSelection.adaptors.ScrapedCSVParse;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.dom.WrappedFluorophore;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.json.JSONObject;

/**
 *
 * @author prash
 */
public class MainServlet extends HttpServlet {

    public static LinkedList<int[]> filterCombinations;
    public static LinkedList<int[]> fluorophorePermutations;
    private ServletContext context;

    public void init(ServletConfig config) throws ServletException
    {
        this.context = config.getServletContext();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/")+"/_LayoutHeader.html")));
        String str;
        PrintWriter out = response.getWriter();
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
        in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/")+"/MainServlet.html")));
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
        in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/")+"/_LayoutFooter.html")));
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        FileItem field = null;
        try {
            field = upload.parseRequest(request).get(0);
        } catch (FileUploadException ex) {
            Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        Enumeration<String> stuff = request.getParameterNames();
        Map<String, String[]> button = request.getParameterMap();        
        
        //InputStream input = field.getInputStream();
        File input = new File("src/main/resources/Fluorophores/org/");
        HashMap<String, Fluorophore> spectralMaps = ScrapedCSVParse.parse(input);
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer cytometer = fpFortessaParse.parse(cyto, false);
        int n = 6;
        double topPercent = .005;
        int numFluorophores = spectralMaps.size();
        int numFilters = 0;
        for (Laser laser : cytometer.lasers) {
            numFilters += laser.detectors.size();
        }
        WrappedFluorophore[][] ranked = new WrappedFluorophore[numFilters][numFluorophores];
        double[][] riemannSum = new double[numFilters][numFluorophores];
        int filterIndex = 0;
        for (Laser laser : cytometer.lasers) {
            for (Detector detector : laser.detectors) {
                int fluorophoreIndex = 0;
                for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
                    Fluorophore fluorophore = entry.getValue();
                    ranked[filterIndex][fluorophoreIndex] = new WrappedFluorophore();
                    ranked[filterIndex][fluorophoreIndex].index = fluorophoreIndex;
                    ranked[filterIndex][fluorophoreIndex].fluorophore = fluorophore;
                    ranked[filterIndex][fluorophoreIndex].detector = detector;
                    ranked[filterIndex][fluorophoreIndex].laser = laser;
                    double express = fluorophore.express(laser, detector);
                    riemannSum[filterIndex][fluorophoreIndex] = express;
                    ranked[filterIndex][fluorophoreIndex].riemannSum = express;
                    fluorophoreIndex++;
                }
                filterIndex++;
            }
        }
        for (int i = 0; i < numFilters; i++) {
            Arrays.sort(ranked[i]);
        }
        filterCombinations = new LinkedList<>();
        int tempData[] = new int[n];
        getCombinations(tempData, 0, numFilters - 1, 0, n);
        fluorophorePermutations = new LinkedList<>();
        tempData = new int[n];
        getPermutations(tempData, (int) (numFluorophores * topPercent), n);
        double bestSignal = 0;
        int[] bestFilters = new int[n];
        int[] bestFluorophores = new int[n];
        int totalComputations = filterCombinations.size() * fluorophorePermutations.size();
        int onePercent = (int) (totalComputations * .01);
        int computationIndex = 0;
        int percent = 0;
        for (int[] filterCombo : filterCombinations) {
            for (int[] fluorophorePerm : fluorophorePermutations) {
                if (++computationIndex % onePercent == 0) {
                    System.out.println(++percent + " percent");
                }
                double signal = 0;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        //desired signal
                        if (i == j) {
                            signal += riemannSum[filterCombo[i]][ranked[filterCombo[i]][fluorophorePerm[j]].index];
                        } //undesired noise
                        else {
                            signal -= riemannSum[filterCombo[i]][ranked[filterCombo[j]][fluorophorePerm[j]].index];
                        }
                    }
                }
                if (signal > bestSignal) {
                    bestSignal = signal;
                    bestFilters = filterCombo;
                    bestFluorophores = fluorophorePerm;
                }
            }
        }
        ArrayList<SelectionInfo> selected = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            SelectionInfo si = new SelectionInfo();
            si.rankedFluorophores = new ArrayList<>();
            si.rankedFluorophores.add(ranked[bestFilters[i]][bestFluorophores[i]].fluorophore);
            si.selectedIndex = 0;
            si.selectedDetector = ranked[bestFilters[i]][bestFluorophores[i]].detector;
            si.selectedLaser = ranked[bestFilters[i]][bestFluorophores[i]].laser;
            selected.add(si);
        }
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

    static void getCombinations(int data[], int start, int n, int index, int k) {
        if (index == k) {
            filterCombinations.add(data.clone());
            return;
        }
        for (int i = start; i <= n && n - i + 1 >= k - index; i++) {
            data[index] = i;
            getCombinations(data, i + 1, n, index + 1, k);
        }
    }

    static void getPermutations(int data[], int n, int k) {
        if (k == 0) {
            fluorophorePermutations.add(data.clone());
            return;
        }
        for (int i = 0; i < n; ++i) {
            data[k - 1] = i;
            getPermutations(data, n, k - 1);
        }
    }
}
