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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JFrame;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class ProteinSelector {

    
    //Given a Laser & Filters, Suggest List of Proteins
    //Suggest proteins based on a laser & filters
    //Works best for n >= 2;
    public static void laserFiltersToFPs(int n, HashMap<String, Fluorophore> masterList, LinkedList<Laser> theLasers) {

        //I don't know the expression formula yet, so I haven't actually integrated the laser wavelength into the calculations.
        Laser defaultLaser = theLasers.getFirst();

        //Pull Detector objects out.
        LinkedList<Detector> listDetectors = new LinkedList<>();

        //Populate the array to prep for sorting
        Detector[] selectedDetectors = new Detector[n];
        for (int j = 0; j < n; j++) {
            selectedDetectors[j] = defaultLaser.getDetectors().getFirst();
        }

        for (Laser eachLaser : theLasers) {
            for (Detector eachDetector : eachLaser.getDetectors()) {
                listDetectors.add(eachDetector);

                if (eachDetector.getFilterMidpoint() < selectedDetectors[0].getFilterMidpoint()) {
                    selectedDetectors[0] = eachDetector;
                }
                if (eachDetector.getFilterMidpoint() > selectedDetectors[n - 1].getFilterMidpoint()) {
                    selectedDetectors[n - 1] = eachDetector;
                }
            }
        }
        if (n > 2) {

            int[] targetPeaks = new int[n - 2];
            int split = (selectedDetectors[n - 1].getFilterMidpoint() - selectedDetectors[0].getFilterMidpoint()) / n;

            for (int i = 0; i < n - 2; i++) {
                //We want the FP peaks to be as far apart as possible -> even distances
                targetPeaks[i] += selectedDetectors[0].getFilterMidpoint() + split * (i + 1);
            }

            for (Detector eachDetector : listDetectors) {

                for (int i = 0; i < n - 2; i++) {
                    int difference = Math.abs(eachDetector.getFilterMidpoint() - targetPeaks[i]);
                    int selectDiff = Math.abs(selectedDetectors[i + 1].getFilterMidpoint() - targetPeaks[i]);

                    //If it's closer to our target than what's currently existing there, replace.
                    if (difference < selectDiff) {
                        selectedDetectors[i + 1] = eachDetector;
                    }
                }

            }

        }

        //Each Detector has a list of ranked fluorophores:
        HashMap<Detector, ArrayList<Fluorophore>> rankedProteins = new HashMap<>();

        //Keep track of each detector's "best" protein
        HashMap<Detector, Integer> optimals = new HashMap<>();

        ArrayList<Fluorophore> tempList;

        //  For each filter, create list of proteins ranked in terms of expression.  
        for (Detector aDetector : selectedDetectors) {
            //Comparator is based on expression in filter - need laser & filter references
            ProteinComparator qCompare = new ProteinComparator();
            qCompare.laser = defaultLaser;
            qCompare.detect = aDetector;
            qCompare.setDefaults();
            qCompare.absolute = true;

            tempList = new ArrayList<>();

            for (Map.Entry<String, Fluorophore> entry : masterList.entrySet()) {
                Fluorophore value = entry.getValue();
                tempList.add(value);
            }

            tempList.sort(qCompare);

            //Put into ranked hashmap
            rankedProteins.put(aDetector, tempList);

            //The first element should be the best thanks to qCompare
            optimals.put(aDetector, 0);
        }

        //After all of that, check noise intensity in other filters. If too large, move to next protein in filter's list. O(nFilters * nProteins)
        //
        //
        //
        //      FOR NOW, let's not. We'll figure it out after the simplest case.
        //
        //
        //
        //
        //
        //
        //OUTPUTS FOR DEBUG
        //
        //
        //
        plotSelection(optimals, rankedProteins);
        printSNR(optimals, rankedProteins);
    }

    static void printSNR(HashMap<Detector, Integer> optimals, HashMap<Detector, ArrayList<Fluorophore>> rankedProteins) {
        Laser fakeLaser = new Laser();
        //currently all of the best proteins are rank 0, so no need to search.
        for (Map.Entry<Detector, Integer> optimalEntry : optimals.entrySet()) {
            Fluorophore desired = rankedProteins.get(optimalEntry.getKey()).get(optimalEntry.getValue());
            double noiseSum = 0;
            for (Map.Entry<Detector, ArrayList<Fluorophore>> rankedEntry : rankedProteins.entrySet()) {
                if (rankedEntry.getKey() != optimalEntry.getKey()) {

                    noiseSum += rankedEntry.getValue().get(0).express(fakeLaser, optimalEntry.getKey());
                }
            }

            System.out.println(optimalEntry.getKey().getFilterMidpoint()+ "'s SNR : " + (desired.express(fakeLaser, optimalEntry.getKey())) / noiseSum);

        }
    }

    static void plotSelection(HashMap<Detector, Integer> optimals, HashMap<Detector, ArrayList<Fluorophore>> rankedProteins) {
        JavaPlot newPlot = new JavaPlot();
        newPlot.setTitle("Selected Proteins");
        newPlot.getAxis("x").setLabel("Wavelength (nm)");
        newPlot.getAxis("y").setLabel("Intensity (%)");
        newPlot.getAxis("y").setBoundaries(0, 125);

        PlotStyle myStyle = new PlotStyle(Style.LINES);

        for (Map.Entry<Detector, Integer> entry : optimals.entrySet()) {
            Fluorophore fp = rankedProteins.get(entry.getKey()).get(entry.getValue());

            //                Filter Midpoint as identifier               Fluorophore Name as suggestion
            System.out.println(entry.getKey().getFilterMidpoint() + " : " + fp.getName());
            System.out.println("% leakage = " + fp.leakageCalc(entry.getKey()));
            System.out.println("Avg. Intensity = " + fp.express(new Laser(), entry.getKey()));

            //Graph continuous line & attach name in legend
            PointDataSet EMDataSet = (fp.makeEMDataSet());
            AbstractPlot emPlot = new DataSetPlot(EMDataSet);
            emPlot.setTitle(fp.getName());
            emPlot.setPlotStyle(myStyle);

            newPlot.addPlot(emPlot);

            //Graph filter bounds
            PointDataSet bounds = entry.getKey().drawBounds();
            AbstractPlot boundsPlot = new DataSetPlot(bounds);
            boundsPlot.setPlotStyle(myStyle);

            newPlot.addPlot(boundsPlot);

        }

        //Throw up in JFrame onto screen
        JPlot graph = new JPlot(newPlot);
        graph.plot();
        graph.repaint();

        JFrame frame = new JFrame("FP Spectrum");
        frame.getContentPane().add(graph);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
