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

    //Given a Laser & Filters, Suggest List of Proteins that works optimally for each filter
    //Suggest proteins based on a laser & filters
    //Works best for n >= 2;
    public static ArrayList<SelectionInfo> laserFiltersToFPs(HashMap<String, Fluorophore> masterList, Laser theLaser) {

        //Pull Detector objects out.
        LinkedList<Detector> listDetectors = new LinkedList<>();

        //Populate the array to prep for sorting
        for (Detector eachDetector : theLaser.getDetectors()) {
            listDetectors.add(eachDetector);
        }

        //Each Detector has a best fluorophore:
        ArrayList<SelectionInfo> bestFPs = new ArrayList<>();
        
        ArrayList<Fluorophore> tempList;
        SelectionInfo choiceInfo;

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

            //Put into the selectionInfo object, one for each channel
            choiceInfo = new SelectionInfo();
            choiceInfo.selectedLaser = theLaser;
            choiceInfo.selectedDetector = aDetector;
            choiceInfo.rankedFluorophores = tempList;
            choiceInfo.selectedIndex = 0;

            bestFPs.add(choiceInfo);
        }

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
//        I gotta fix this thing before we can use it >.>
//        plotSelection(theLaser, returnMap);
        return bestFPs;

    }

    public static void plotSelection(ArrayList<SelectionInfo> info) {
        ArrayList<Laser> lazies = new ArrayList<>();
        ArrayList<JavaPlot> plotsies = new ArrayList<>();

        PlotStyle myStyle = new PlotStyle(Style.LINES);

        for (SelectionInfo entry : info) {
            if (!lazies.contains(entry.selectedLaser)) {
                lazies.add(entry.selectedLaser);

                JavaPlot newPlot = new JavaPlot();
                newPlot.setTitle(entry.selectedLaser.getName());
                newPlot.getAxis("x").setLabel("Wavelength (nm)");
                newPlot.getAxis("y").setLabel("Intensity (%)");
                newPlot.getAxis("y").setBoundaries(0, 125);

                plotsies.add(newPlot);

                Fluorophore fp = entry.rankedFluorophores.get(entry.selectedIndex);
                System.out.println(fp.getName() + " : " + fp.express(entry.selectedLaser, entry.selectedDetector));

                //Graph continuous line & attach name in legend
                PointDataSet EMDataSet = (fp.makeEMDataSet(entry.selectedLaser));
                AbstractPlot emPlot = new DataSetPlot(EMDataSet);
                emPlot.setTitle(fp.getName());
                emPlot.setPlotStyle(myStyle);

                newPlot.addPlot(emPlot);

                //Graph filter bounds
                PointDataSet bounds = entry.selectedDetector.drawBounds();
                AbstractPlot boundsPlot = new DataSetPlot(bounds);
                boundsPlot.setPlotStyle(myStyle);

                newPlot.addPlot(boundsPlot);

            } else {
                int index = lazies.indexOf(entry.selectedLaser);
                JavaPlot plot = plotsies.get(index);

                Fluorophore fp = entry.rankedFluorophores.get(entry.selectedIndex);
                System.out.println(fp.getName() + " SNR : " + entry.SNR);

                //Graph continuous line & attach name in legend
                PointDataSet EMDataSet = (fp.makeEMDataSet(entry.selectedLaser));
                AbstractPlot emPlot = new DataSetPlot(EMDataSet);
                emPlot.setTitle(fp.getName());
                emPlot.setPlotStyle(myStyle);

                plot.addPlot(emPlot);

                //Graph filter bounds
                PointDataSet bounds = entry.selectedDetector.drawBounds();
                AbstractPlot boundsPlot = new DataSetPlot(bounds);
                boundsPlot.setPlotStyle(myStyle);

                plot.addPlot(boundsPlot);

            }
        }

        for (JavaPlot plot : plotsies) {
            //Throw up in JFrame onto screen
            JPlot graph = new JPlot(plot);
            graph.plot();
            graph.repaint();
            JFrame frame = new JFrame("FP Spectrum");
            frame.getContentPane()
                    .add(graph);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }

    }

    public static ArrayList<SelectionInfo> mishMashCombinatorics(ArrayList<SelectionInfo> suggestions, int n) {
        //Build list of things to check.

        double sumSNR = 0;
        ArrayList<SelectionInfo> allInfo = new ArrayList<>();
        ArrayList<SelectionInfo> iterateInfo = new ArrayList<>();

        //Test each FP with the other lasers based on simple SNR, keep the best.
        for (SelectionInfo info : suggestions) {
            allInfo.add(info);
            iterateInfo.add(info);
        }

        sumSNR = calcSumSNR(allInfo);

        //Start with all of the proteins, clip one by one and record how the SNR changes.
        //After each loop of clipping, whichever had the most positive change stays clipped.
        //Positive change = sumSNR++
        double SNR;

        //Pick the N best proteins.
        while (allInfo.size() > n) {
            SelectionInfo highestScore = iterateInfo.get(0);

            for (SelectionInfo info : iterateInfo) {

                //Remove the protein in question from the arraylist
                allInfo.remove(info);

                //Calculate the total SNR from that removal
                SNR = calcSumSNR(allInfo);

                //Add the protein back into the arraylist
                allInfo.add(info);

                //if removal positive, score should be positive since SNR should have increased
                //The more positive the impact, the higher the score.
                info.score = SNR - sumSNR;

                if (info.score > highestScore.score) {
                    highestScore = info;
                }

            }
            iterateInfo.remove(highestScore);
            allInfo.remove(highestScore);
            sumSNR += highestScore.score;

        }

        return allInfo;
    }

    static double calcSumSNR(ArrayList<SelectionInfo> allInfo) {
        double sumSNR = 0;

        for (SelectionInfo info : allInfo) {

            Fluorophore fp = info.rankedFluorophores.get(info.selectedIndex);

            //signal is info expressing in it's own channel with it's own laser.
            double signal = fp.express(info.selectedLaser, info.selectedDetector);
            double noise = 0;

            for (SelectionInfo otherInfo : allInfo) {
                if (info == otherInfo) {
                    continue;
                } else {

                    //noise is otherInfo's fluorophore expressing in info's channel with info's laser
                    noise += otherInfo.rankedFluorophores.get(otherInfo.selectedIndex).express(info.selectedLaser, info.selectedDetector);

                }
            }
            info.SNR = signal/noise;
            sumSNR += info.SNR;
        }

        return sumSNR;
    }
}
