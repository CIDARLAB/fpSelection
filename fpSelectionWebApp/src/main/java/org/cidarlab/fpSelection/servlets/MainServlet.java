/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import static org.cidarlab.fpSelection.adaptors.fpSelectionAdaptor.uploadFluorescenceSpectrums;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.selectors.ProteinSelector;
import org.json.JSONObject;

/**
 *
 * @author prash
 */
public class MainServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            FileItem field = upload.parseRequest(request).get(0);
            InputStream input = field.getInputStream();

            /*
             HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);

             JavaPlot javaPlot = new JavaPlot();
             javaPlot.setTitle("FP Spectrum", "Helvetica", 14);
             javaPlot.getAxis("x").setLabel("Wavelength (nm)");
             javaPlot.getAxis("x").setBoundaries(0, 1000);
             javaPlot.getAxis("y").setLabel("Intensity (%)");
             javaPlot.getAxis("y").setBoundaries(0, 100);
             javaPlot.set("key font", "',7'");

             PlotStyle myPlotStyle = new PlotStyle();
             myPlotStyle.setStyle(Style.LINES);
             myPlotStyle.setLineWidth(1);

             for (HashMap.Entry pair : spectralMaps.entrySet()) {
             Fluorophore f = (Fluorophore) pair.getValue();
             AbstractPlot emDataSetPlot = new DataSetPlot(f.makeEMDataSet());
             AbstractPlot exDataSetPlot = new DataSetPlot(f.makeEXDataSet());
             emDataSetPlot.setTitle(pair.getKey() + " (EM)");
             exDataSetPlot.setTitle(pair.getKey() + " (EX)");
             emDataSetPlot.setPlotStyle(myPlotStyle);
             exDataSetPlot.setPlotStyle(myPlotStyle);
             javaPlot.addPlot(emDataSetPlot);
             javaPlot.addPlot(exDataSetPlot);
             }

             ImageTerminal png = new ImageTerminal();

             javaPlot.setTerminal(png);
             javaPlot.plot();

             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageIO.write(png.getImage(), "jpg", baos);
             baos.flush();
             byte[] img = baos.toByteArray();
             baos.close();

             PrintWriter writer = response.getWriter();

             JSONObject result = new JSONObject();
             String baseString = "data:image/jpeg;base64, " + Base64.getEncoder().encodeToString(img);
             */
            HashMap<String, Fluorophore> spectralMaps = uploadFluorescenceSpectrums(input);

//        File input = new File("src/main/resources/ScrapedCSVs/");
//        HashMap<String,Fluorophore> spectralMaps = generateFPs(input);
            File cyto = new File("src/main/resources/ex_fortessa.csv");
            Cytometer testCyto = fpFortessaParse.parseFortessa(cyto);
            Laser testLaser = testCyto.getLasers().getFirst();

            System.out.println(spectralMaps.isEmpty());
            System.out.println(testCyto.getSheathPressure());
            
            String[] list = ProteinSelector.laserFiltersToFPs(spectralMaps, testLaser);
            String baseString = list[0];
            
            PrintWriter writer = response.getWriter();

            JSONObject result = new JSONObject();
            result.put("img", baseString);
            result.put("info", list[1]);

            writer.println(result);
            writer.flush();
            writer.close();
        } catch (FileUploadException e) {
            System.out.println(e.getMessage());
        }
    }
}
