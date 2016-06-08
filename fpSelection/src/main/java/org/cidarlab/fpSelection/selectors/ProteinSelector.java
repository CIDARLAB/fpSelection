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

    //Assume that # filters = numFPsWanted
    //Given a Laser & Filters, Suggest List of Proteins
    //Suggest proteins based on a laser & filters
    public static void laserFiltersToFPs(HashMap<String, Fluorophore> masterList, Laser theLaser) {
//        if (masterList.isEmpty()) {
//            System.out.println("y u empty");
//        } else {
//            System.out.println("phew not empty");
//            System.out.println("here's what's inside");
//            System.out.println("");
//            System.out.println(masterList.size() + " fluorophores");
//        }

        //Pull Detector objects out.
        LinkedList<Detector> listDetectors = theLaser.getDetectors();

        //Each Detector has a list of ranked fluorophores:
        HashMap<Detector, ArrayList<Fluorophore>> rankedProteins = new HashMap<>();

        //Keep track of each detector's "best" protein
        HashMap<Detector, Integer> optimals = new HashMap<>();

        ArrayList<Fluorophore> tempList;

        //  For each filter, create list of proteins ranked in terms of expression.  
        //  O(nFilters * nProteins * PriorityQAdd).
        System.out.println("Initial");

        for (Detector theDetector : listDetectors) {
            //Comparator is based on expression in filter - need laser & filter references
            ProteinComparator qCompare = new ProteinComparator();
            qCompare.laser = theLaser;
            qCompare.detect = theDetector;
            qCompare.setDefaults();

            tempList = new ArrayList<>();
            System.out.println("Made temp list");

            for (Map.Entry<String, Fluorophore> entry : masterList.entrySet()) {
                Fluorophore value = entry.getValue();
                System.out.println(value.getName());
                tempList.add(value);
            }

            tempList.sort(qCompare);
            System.out.println("Sorted temp list");

            //Put into ranked hashmap
            rankedProteins.put(theDetector, tempList);

            //The first element should be the best thanks to qCompare
            optimals.put(theDetector, 0);
        }
        System.out.println("hashmaps done");

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
    }

    //Try to revise list for better SNR. Work in progress....
//    void checkOptimals(HashMap<Detector, ArrayList<Fluorophore>> ranked, HashMap<Detector, Integer> optimals) {
//        
//        return;
//    }
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
