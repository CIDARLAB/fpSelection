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
    public static HashMap<Laser, HashMap<Detector, Fluorophore>> laserFiltersToFPs(HashMap<String, Fluorophore> masterList, Laser theLaser, double threshold) {

        //Pull Detector objects out.
        LinkedList<Detector> listDetectors = new LinkedList<>();

        //Populate the array to prep for sorting
        for (Detector eachDetector : theLaser.getDetectors()) {
            listDetectors.add(eachDetector);
        }

        //Each Detector has a best fluorophore:
        HashMap<Detector, Fluorophore> bestFP = new HashMap<>();

        ArrayList<Fluorophore> tempList;

        //  For each filter, create list of proteins ranked in terms of expression.  
        for (Detector aDetector : listDetectors) {
            //Comparator is based on expression in filter - need laser & filter references
            ProteinComparator qCompare = new ProteinComparator();
            qCompare.laser = theLaser;
            qCompare.detect = aDetector;
            qCompare.setDefaults();
//            qCompare.absolute = true;

            tempList = new ArrayList<>();

            for (Map.Entry<String, Fluorophore> entry : masterList.entrySet()) {
                Fluorophore value = entry.getValue();
                tempList.add(value);
            }

            tempList.sort(qCompare);

            //Put into the rankedProteins hashmap
            bestFP.put(aDetector, tempList.get(0));
        }

        HashMap<Laser, HashMap<Detector, Fluorophore>> returnMap = new HashMap<>();
        returnMap.put(theLaser, bestFP);

        //After all of that, check noise intensity in other filters. If too large, move to next protein in filter's list.
        //
        //
        //
        //      FOR NOW, let's not. We'll figure it out after the simplest case.
        //
        //
        //OUTPUTS FOR DEBUG
        //
        //
        //
        plotSelection(theLaser, returnMap);
        return returnMap;

    }

    static void plotSelection(Laser theLaser, HashMap<Laser, HashMap<Detector, Fluorophore>> rankedProteins) {
        JavaPlot newPlot = new JavaPlot();
        newPlot.setTitle("Selected Proteins");
        newPlot.getAxis("x").setLabel("Wavelength (nm)");
        newPlot.getAxis("y").setLabel("Intensity (%)");
        newPlot.getAxis("y").setBoundaries(0, 125);

        PlotStyle myStyle = new PlotStyle(Style.LINES);

        for (Map.Entry<Laser, HashMap<Detector, Fluorophore>> entry : rankedProteins.entrySet()) {
            for (Map.Entry<Detector, Fluorophore> DFentry : entry.getValue().entrySet()) {
                Fluorophore fp = DFentry.getValue();
                //                Filter Midpoint as identifier               Fluorophore Name as suggestion
                System.out.println(DFentry.getKey().getFilterMidpoint() + " : " + fp.getName());
                System.out.println("% Leakage = " + fp.leakageCalc(DFentry.getKey()));
                System.out.println("Avg. Intensity = " + fp.express(theLaser, DFentry.getKey()));
                //Graph continuous line & attach name in legend
                PointDataSet EMDataSet = (fp.makeEMDataSet(theLaser));
                AbstractPlot emPlot = new DataSetPlot(EMDataSet);
                emPlot.setTitle(fp.getName());
                emPlot.setPlotStyle(myStyle);

                newPlot.addPlot(emPlot);

                //Graph filter bounds
                PointDataSet bounds = DFentry.getKey().drawBounds();
                AbstractPlot boundsPlot = new DataSetPlot(bounds);
                boundsPlot.setPlotStyle(myStyle);

                newPlot.addPlot(boundsPlot);
            }

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
