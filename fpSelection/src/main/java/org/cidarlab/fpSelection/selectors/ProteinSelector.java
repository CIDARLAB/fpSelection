/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import org.cidarlab.fpSelection.dom.SelectionInfo;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.plot.Graph;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.swing.JPlot;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.InfDouble;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class ProteinSelector {

    public static void plotSelection(ArrayList<SelectionInfo> info) {
        //sort selection info so that filters with same lasers are plotted on same graph
        Collections.sort(info);

        //hash set that checks if lasers have been plotted already
        HashSet<Laser> usedLasers = new HashSet<>();

        //create the plot and initialize style/axes
        JavaPlot newPlot = new JavaPlot();
        Graph g = new Graph();

        //get dimensions of computer screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        newPlot.set("terminal", "png transparent truecolor nocrop enhanced size " + Integer.toString(width) + "," + Integer.toString(height - 150) + "font 'arial,7'");
        newPlot.set("style fill", "transparent solid 0.3");
        newPlot.set("style data", "lines");
        newPlot.set("style data filledcurves", "x1");
        newPlot.set("key", "font ',8'");

        //if first plot to be added
        boolean first = true;
        boolean later = false;

        //iterate through laser/filter/protein combos
        for (SelectionInfo entry : info) {
            if (!usedLasers.contains(entry.selectedLaser)) {
                usedLasers.add(entry.selectedLaser);

                //add noise plot
                PointDataSet noiseDataSet = (entry.makeDataSet());
                AbstractPlot noisePlot = new DataSetPlot(noiseDataSet);
                noisePlot.setTitle("Noise in " + entry.selectedLaser.name);
                noisePlot.set("fs", "transparent solid 0.2 noborder");

                //add emission plot
                Fluorophore fp = entry.selectedFluorophore.get(entry.selectedIndex);
                System.out.println(fp.name + " SNR : " + String.format("%.3f", entry.SNR));
                PointDataSet EMDataSet = (fp.makeEMDataSet(entry.selectedLaser));
                AbstractPlot emPlot = new DataSetPlot(EMDataSet);
                emPlot.setTitle(fp.name);

                //add filter bounds plot
                PlotStyle ps = new PlotStyle(Style.LINES);
                PointDataSet bounds = entry.selectedDetector.drawBounds();
                AbstractPlot boundsPlot = new DataSetPlot(bounds);
                boundsPlot.setPlotStyle(ps);
                boundsPlot.setTitle("");

                //first round add to javaplot (otherwise extra plot is added)
                if (first) {
                    newPlot.addPlot(noisePlot);
                    newPlot.addPlot(emPlot);
                    newPlot.addPlot(boundsPlot);
                    newPlot.getAxis("x").setLabel("Wavelength (nm)'\r\nset title '" + entry.selectedLaser.name);
                    newPlot.getAxis("x").setBoundaries(300, 900);
                    newPlot.getAxis("y").setLabel("Intensity (%)");
                    newPlot.getAxis("y").setBoundaries(0, 125);
                    first = false;
                    later = true;
                } //otherwise add to graph object that is added to javaplot
                else {
                    g = new Graph();
                    g.addPlot(noisePlot);
                    g.addPlot(emPlot);
                    g.addPlot(boundsPlot);
                    g.getAxis("x").setLabel("Wavelength (nm)'\r\nset title '" + entry.selectedLaser.name);
                    g.getAxis("x").setBoundaries(300, 900);
                    g.getAxis("y").setLabel("Intensity (%)");
                    g.getAxis("y").setBoundaries(0, 125);
                    newPlot.addGraph(g);
                    later = false;
                }

            } else {

                //add emission plot
                Fluorophore fp = entry.selectedFluorophore.get(entry.selectedIndex);
                System.out.println(fp.name + " SNR : " + String.format("%.3f", entry.SNR));
                PointDataSet EMDataSet = (fp.makeEMDataSet(entry.selectedLaser));
                AbstractPlot emPlot = new DataSetPlot(EMDataSet);
                emPlot.setTitle(fp.name);

                //add filter bounds plot
                PlotStyle ps = new PlotStyle(Style.LINES);
                PointDataSet bounds = entry.selectedDetector.drawBounds();
                AbstractPlot boundsPlot = new DataSetPlot(bounds);
                boundsPlot.setPlotStyle(ps);
                boundsPlot.setTitle("");

                //in case the second graph shares the same laser as the first, it must be added to the javaplot
                if (later) {
                    newPlot.addPlot(emPlot);
                    newPlot.addPlot(boundsPlot);
                } else {
                    g.addPlot(emPlot);
                    g.addPlot(boundsPlot);
                }
            }
        }

        JPlot graph = new JPlot(newPlot);
        graph.plot();
        graph.repaint();
        JFrame frame = new JFrame("FP Spectrum");
        frame.getContentPane().add(graph);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static InfDouble totalSNR(ArrayList<SelectionInfo> selection) {
        InfDouble snr = new InfDouble(InfDouble.InfDoubleMode.add);
        for (SelectionInfo si : selection) {
            snr.includeSNR(si.SNR);
        }
        return snr;
    }

    public static InfDouble prodSNR(ArrayList<SelectionInfo> selection) {
        InfDouble snr = new InfDouble(InfDouble.InfDoubleMode.multiply);
        for (SelectionInfo si : selection) {
            snr.includeSNR(si.SNR);
        }
        return snr;
    }

    public static boolean snrLessThanOne(ArrayList<SelectionInfo> candidate) {
        for (SelectionInfo si : candidate) {
            if (si.isSNRlessThanOne()) {
                return true;
            }
        }
        return false;
    }
    
    public static double calcSumSigNoise(ArrayList<SelectionInfo> allInfo) {
//        double sumSNR = 0;
        double sumDiff = 0;

        for (SelectionInfo info : allInfo) {

            Fluorophore fp = info.selectedFluorophore.get(info.selectedIndex);

            //signal is info expressing in it's own channel with it's own laser.
            double signal = fp.express(info.selectedLaser, info.selectedDetector);
            double noise = 0;
            if (allInfo.size() == 1) {
                noise = 1;
            }

            for (SelectionInfo otherInfo : allInfo) {
                if (info == otherInfo) {
                    continue;
                }

                //noise is otherInfo's fluorophore expressing in info's channel with info's laser
                Fluorophore noiseFP = otherInfo.selectedFluorophore.get(otherInfo.selectedIndex);
                noise += noiseFP.express(info.selectedLaser, info.selectedDetector);

            }
            if (signal == 0) {
                info.setSignalZero(true);
            }

            if (noise == 0) {
                info.setNoiseZero(true);
            }
            info.SNR = signal / noise;
            if (info.SNR < 1) {
                info.setSNRlessThanOne(true);
            }
            info.SNDiff = signal - noise;
//            sumSNR += info.SNR;
            sumDiff += info.SNDiff;
//            sumDiff += info.SNR;
        }

        //SOOOOOOOOO:
        //SNR provides higher average but not everything will be readable.
        //Signal - Noise provides lower average, but all signals are readable.
        //Ex: SNR yields 9 proteins, 1 has <1 SNR
        //    Signal - Noise yeilds 9, minimum has SNR = 3.
        return sumDiff;
    }

    public static int getTotalOligos(ArrayList<SelectionInfo> selected) {
        int count = 0;

        //Map<String, Fluorophore> chosenOnes = new HashMap<String, Fluorophore>();
        for (SelectionInfo si : selected) {
            count += si.selectedFluorophore.get(si.selectedIndex).oligomerization;
        }

        return count;
    }

    public static double getTotalCost(ArrayList<SelectionInfo> selected) {
        double price = 0;
        for (SelectionInfo si : selected) {
            price += si.selectedFluorophore.get(si.selectedIndex).price;
        }

        return price;
    }

    public static void generateNoise(ArrayList<SelectionInfo> selected) {
        //noise is otherInfo's fluorophore expressing in info's channel with info's laser
        for (SelectionInfo info : selected) {
            //in case something got in there
            if (info.noise != null) {
                info.noise.clear();
            } else {
                info.noise = new TreeMap<>();
            }

            for (SelectionInfo otherInfo : selected) {
                if (info.selectedLaser == otherInfo.selectedLaser) {
                    continue;
                }

                Fluorophore noiseFp = otherInfo.selectedFluorophore.get(otherInfo.selectedIndex);

                if (noiseFp.EXspectrum.containsKey((double) info.selectedLaser.wavelength)) {
                    //Get a decimal of how excited the noiseFPs are
                    double multiplier = noiseFp.EXspectrum.get((double) info.selectedLaser.wavelength) / 100;

                    //Add the entire noise graph
                    for (Map.Entry<Double, Double> entry : noiseFp.EMspectrum.entrySet()) {
                        double addVal = entry.getValue() * multiplier;

                        if (info.noise.containsKey(entry.getKey())) {
                            //current amount of noise on point
                            double currentVal = info.noise.get(entry.getKey());

                            info.noise.put(entry.getKey(), (currentVal + addVal));
                        } else {
                            info.noise.put(entry.getKey(), addVal);
                        }
                    }
                }
            }
        }
    }

    public static void weightedSelection(int n, HashMap<String, Fluorophore> spectralMaps, Cytometer cytometer, Algorithm algorithm, double oligo_weight, double price_weight, double snr_weight) {

    }

}
