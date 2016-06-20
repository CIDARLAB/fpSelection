/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.swing.JPlot;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import javax.swing.JFrame;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class SimulatedAnneal {

    public static ArrayList<SelectionInfo> simulateAnnealing(HashMap<String, Fluorophore> masterList, Cytometer cyto, int n) {

        //Simulated Annealing in trying to achieve most cost efficient outcome
//        costprevious = infinite
//        temperature = temperature_start
//        while temperature > temperature_end:
//          costnew = cost_function(
//          ...)
//          difference = costnew - costprevious
//          if difference < 0 or  e(-difference / temperature) > random(0, 1):
//              costprevious = costnew
//              temperature = temperature * cooling_factor
        //For us we want as high an SNR as possible
        //Temperature acts as a timer of sorts
        //Carbon steel process annealing temp can be as high as 700 degrees Celsius
        double temperature = Math.pow(700, 7);  //8.23543×10^19

        double tempEnd = Math.pow(10, -100);
        double coolFactor = .999;

        int replace;
        int counter = 0;

        //Each partition of 100% will point to a fluorophore setup.
        ArrayList<SelectionInfo> optimals = new ArrayList(n);

        ArrayList<SelectionInfo> previousSet = new ArrayList(n);
        ArrayList<SelectionInfo> newSet = new ArrayList(n);

        Random generator = new Random();
        ArrayList<Fluorophore> dupes = new ArrayList<>();
        ArrayList<Detector> dupeD = new ArrayList<>();

        //Generate n random fluorophore setups.
        for (int i = 0; i < n; i++) {
            Laser beam = cyto.lasers.get(generator.nextInt(cyto.lasers.size()));
            Detector filter = beam.detectors.get(generator.nextInt(beam.detectors.size()));

            SelectionInfo info = new SelectionInfo();
            info.rankedFluorophores = new ArrayList<>(masterList.values());
            info.selectedLaser = beam;
            info.selectedDetector = filter;
            info.selectedIndex = generator.nextInt(info.rankedFluorophores.size());
            info.noise = new TreeMap<>();
            info.SNR = 1;

            if (dupes.contains(info.rankedFluorophores.get(info.selectedIndex))) {
                i--;
                continue;
            }
            if (dupeD.contains(info.selectedDetector)) {
                i--;
                continue;
            }

            optimals.add(i, info);
            previousSet.add(i, info);
            newSet.add(i, info);

            dupes.add(info.rankedFluorophores.get(info.selectedIndex));
            dupeD.add(filter);

        }

        double optimalSNR = ProteinSelector.calcSumSNR(optimals) / (n);
        optimalSNR *= geometricSNR(optimals);
        double previousSNR = optimalSNR;
        double newSNR;
        double diff;

        while (temperature > tempEnd) {

            replace = (int) (Math.random() * 100 % n);

            Laser beam = cyto.lasers.get(generator.nextInt(cyto.lasers.size()));
            Detector filter = beam.detectors.get(generator.nextInt(beam.detectors.size()));

            SelectionInfo info = new SelectionInfo();
            info.rankedFluorophores = new ArrayList<>(masterList.values());
            info.selectedLaser = beam;
            info.selectedDetector = filter;
            info.selectedIndex = generator.nextInt(info.rankedFluorophores.size());
            info.noise = new TreeMap<>();

            if (dupeD.contains(filter)) {
                for (SelectionInfo entry : previousSet) {
                    if (entry.selectedDetector == filter) {
                        replace = previousSet.indexOf(entry);
                    }
                }
            } else if (dupes.contains(info.rankedFluorophores.get(info.selectedIndex))) {
                for (SelectionInfo entry : previousSet) {
                    if (entry.rankedFluorophores.get(entry.selectedIndex) == info.rankedFluorophores.get(info.selectedIndex)) {
                        replace = previousSet.indexOf(entry);
                    }
                }
            }

            newSet.set(replace, info);

//            //Generate n random fluorophore setups.
//            dupes.clear();
//            dupeD.clear();
//            for (int i = 0; i < n; i++) {
//                Laser beam = cyto.lasers.get(generator.nextInt(cyto.lasers.size()));
//                Detector filter = beam.detectors.get(generator.nextInt(beam.detectors.size()));
//
//                SelectionInfo info = new SelectionInfo();
//                info.rankedFluorophores = new ArrayList<>(masterList.values());
//                info.selectedLaser = beam;
//                info.selectedDetector = filter;
//                info.selectedIndex = generator.nextInt(info.rankedFluorophores.size());
//                info.noise = new TreeMap<>();
//
//                if (dupes.contains(info.rankedFluorophores.get(info.selectedIndex))) {
//                    i--;
//                    continue;
//                }
//                if (dupeD.contains(info.selectedDetector)) {
//                    i--;
//                    continue;
//                }
//
//                newSet.set(i, info);
//
//                dupes.add(info.rankedFluorophores.get(info.selectedIndex));
//                dupeD.add(filter);
//
//            }
            newSNR = ProteinSelector.calcSumSNR(newSet) / (n);
            newSNR *= geometricSNR(newSet);

            diff = newSNR - previousSNR;

            //We want higher SNR, so diff should be positive.
            //We can also go lower, but do so probabilistically.
            if (diff > 0) {

                previousSet = new ArrayList<>(newSet);
                previousSNR = newSNR;
            } else if (Math.exp(diff / (temperature * 100)) > Math.random()) {
                previousSet = new ArrayList<>(newSet);
                previousSNR = newSNR;
            }

            if (newSNR > optimalSNR) {
                optimals = new ArrayList<>(newSet);
                optimalSNR = newSNR;

//                System.out.println(optimalSNR);
            }

            System.out.println("Current Temp : " + temperature);
            System.out.println(counter + " replacements completed");

            for (SelectionInfo beep : optimals) {
                System.out.println(optimals.indexOf(beep) + ": " + beep.SNR);
            }
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();

            temperature = temperature * coolFactor;
            counter++;

        }

        return optimals;

    }

    public static void plotSelection(ArrayList<SelectionInfo> info) {
        ArrayList<Laser> lazies = new ArrayList<>();
        ArrayList<JavaPlot> plotsies = new ArrayList();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        for (SelectionInfo entry : info) {
            if (!lazies.contains(entry.selectedLaser)) {
                lazies.add(entry.selectedLaser);
                JavaPlot newPlot = new JavaPlot();

                newPlot.getAxis("x").setLabel("Wavelength (nm)");
                newPlot.getAxis("x").setBoundaries(300, 900);
                newPlot.getAxis("y").setLabel("Intensity (%)");
                newPlot.getAxis("y").setBoundaries(0, 125);

                newPlot.set("terminal", "png transparent truecolor nocrop enhanced size 800,600 font 'arial,7'");
                newPlot.set("style fill", "transparent solid 0.3");
                newPlot.set("style data", "lines");
                newPlot.set("style data filledcurves", "x1");
                newPlot.set("key", "font ',8'");

                PointDataSet noiseDataSet = (entry.makeDataSet());
                AbstractPlot noisePlot = new DataSetPlot(noiseDataSet);
                noisePlot.setTitle("Noise in " + entry.selectedLaser.name);
                noisePlot.set("fs", "transparent solid 0.2 noborder");

                newPlot.addPlot(noisePlot);

                Fluorophore fp = entry.rankedFluorophores.get(entry.selectedIndex);
                System.out.println(fp.name + " SNR : " + String.format("%.3f", entry.SNR));

                //Graph continuous line & attach name in legend
                PointDataSet EMDataSet = (fp.makeEMDataSet(entry.selectedLaser));
                AbstractPlot emPlot = new DataSetPlot(EMDataSet);
                emPlot.setTitle(fp.name);

                newPlot.addPlot(emPlot);

                //Graph filter bounds
                PlotStyle ps = new PlotStyle(Style.LINES);
                PointDataSet bounds = entry.selectedDetector.drawBounds();
                AbstractPlot boundsPlot = new DataSetPlot(bounds);
                boundsPlot.setPlotStyle(ps);
                boundsPlot.setTitle("");

                newPlot.addPlot(boundsPlot);

                plotsies.add(newPlot);

            } else {
                int index = lazies.indexOf(entry.selectedLaser);
                JavaPlot plot = plotsies.get(index);

                Fluorophore fp = entry.rankedFluorophores.get(entry.selectedIndex);
                System.out.println(fp.name + " SNR : " + String.format("%.3f", entry.SNR));

                //Graph continuous line & attach name in legend
                PointDataSet EMDataSet = (fp.makeEMDataSet(entry.selectedLaser));
                AbstractPlot emPlot = new DataSetPlot(EMDataSet);
                emPlot.setTitle(fp.name);

                plot.addPlot(emPlot);

                //Graph filter bounds
                PlotStyle ps = new PlotStyle(Style.LINES);
                PointDataSet bounds = entry.selectedDetector.drawBounds();
                AbstractPlot boundsPlot = new DataSetPlot(bounds);
                boundsPlot.setPlotStyle(ps);
                boundsPlot.setTitle("");

                plot.addPlot(boundsPlot);

            }
        }

        for (JavaPlot plot : plotsies) {

            JPlot graph = new JPlot(plot);
            graph.plot();
            graph.repaint();
            JFrame frame = new JFrame("FP Spectrum");
            frame.getContentPane().add(graph);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
    }

    public static double geometricSNR(ArrayList<SelectionInfo> info) {
        double geometric = 1;
        for (SelectionInfo each : info) {
            if (each.SNR == 0) {
                continue;
            }
            geometric *= each.SNR;
        }
        return Math.pow(geometric, (1 / info.size()));
    }

}
