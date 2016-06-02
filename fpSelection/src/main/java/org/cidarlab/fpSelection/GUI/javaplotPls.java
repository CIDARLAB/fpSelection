/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.GUI;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class javaplotPls {

    public static void main(String[] args) {
        double[][] values = new double[3][2];
        values[0][0] = 0.1;
        values[0][1] = 0.3;
        values[1][0] = 0.4;
        values[1][1] = 0.3;
        values[2][0] = 0.5;
        values[2][1] = 0.5;

        double[][] values2 = new double[3][2];
        values2[0][0] = 0.2;
        values2[0][1] = 0.0;
        values2[1][0] = 0.7;
        values2[1][1] = 0.1;
        values2[2][0] = 0.6;
        values2[2][1] = 0.5;

        PlotStyle styleDeleted = new PlotStyle();
        styleDeleted.setStyle(Style.POINTS);
        styleDeleted.setLineType(NamedPlotColor.GRAY80);

        PlotStyle styleExist = new PlotStyle();
        styleExist.setStyle(Style.POINTS);
        styleExist.setLineType(NamedPlotColor.BLACK);

        DataSetPlot setDeleted = new DataSetPlot(values);
        setDeleted.setPlotStyle(styleDeleted);
        setDeleted.setTitle("deleted EMs");

        DataSetPlot setExist = new DataSetPlot(values2);
        setExist.setPlotStyle(styleExist);
        setExist.setTitle("remaining EMs");

        ImageTerminal png = new ImageTerminal();
        File file = new File("/home/david/plot.png");
        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }

        JavaPlot p = new JavaPlot();
        p.setTerminal(png);

        p.getAxis("x").setLabel("yield");
        p.getAxis("y").setLabel("biomass");
        p.getAxis("x").setBoundaries(0.0, 1.0);
        p.getAxis("y").setBoundaries(0.0, 1.0);
        p.addPlot(setDeleted);
        p.addPlot(setExist);
        p.setTitle("remaining EMs");
        p.setPersist(false);
        p.plot();

        try {
            ImageIO.write(png.getImage(), "png", file);
        } catch (IOException ex) {
            System.err.print(ex);
        }
    }
}
