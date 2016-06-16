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
import com.panayotis.gnuplot.style.FillStyle;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.swing.JPlot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
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
        for (Detector eachDetector : theLaser.detectors) {

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
            choiceInfo.noise = new TreeMap<>();

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
        JavaPlot newPlot = new JavaPlot();
        boolean first = true;
        for (SelectionInfo entry : info) {
            if (!lazies.contains(entry.selectedLaser)) {
                lazies.add(entry.selectedLaser);

                if (first) first = false;
                else newPlot.newGraph();
                
                newPlot.setTitle(entry.selectedLaser.name);
                newPlot.getAxis("x").setLabel("Wavelength (nm)");
                newPlot.getAxis("y").setLabel("Intensity (%)");
                newPlot.getAxis("y").setBoundaries(0, 125);
                newPlot.set("terminal","png transparent truecolor nocrop enhanced size 1200,600 font 'arial,7'");
                newPlot.set("style fill", "transparent solid 0.3");
                newPlot.set("style data", "lines");
                newPlot.set("style data filledcurves", "x1");
                newPlot.set("key","font ',8'");

                Fluorophore fp = entry.rankedFluorophores.get(entry.selectedIndex);
                System.out.println(fp.name + " SNR : " + String.format("%.3f", entry.SNR));

                //Graph continuous line & attach name in legend
                PointDataSet EMDataSet = (fp.makeEMDataSet(entry.selectedLaser));
                AbstractPlot emPlot = new DataSetPlot(EMDataSet);
                emPlot.setTitle(fp.name);

                newPlot.addPlot(emPlot);

                PointDataSet noiseDataSet = (entry.makeDataSet());
                AbstractPlot noisePlot = new DataSetPlot(noiseDataSet);
                noisePlot.setTitle("Noise from other Laser FPs");
                noisePlot.set("fs","transparent solid 0.1 noborder");

                newPlot.addPlot(noisePlot);

                //Graph filter bounds
                PlotStyle ps = new PlotStyle(Style.LINES);
                PointDataSet bounds = entry.selectedDetector.drawBounds();
                AbstractPlot boundsPlot = new DataSetPlot(bounds);
                boundsPlot.setPlotStyle(ps);
                boundsPlot.setTitle("");

                newPlot.addPlot(boundsPlot);

            } else {

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

        boolean duplicates = true;
        ArrayList<SelectionInfo> removes = new ArrayList<>();
        while (duplicates) {
            duplicates = false;

            for (SelectionInfo info : iterateInfo) {

                Fluorophore fp1 = info.rankedFluorophores.get(info.selectedIndex);
                for (SelectionInfo otherInfo : allInfo) {
                    Fluorophore fp2 = otherInfo.rankedFluorophores.get(otherInfo.selectedIndex);

                    //if the same FP is chosen 
                    if (fp1 == fp2 && info.selectedDetector != otherInfo.selectedDetector) {

                        if (fp1.express(info.selectedLaser, info.selectedDetector) > fp2.express(otherInfo.selectedLaser, otherInfo.selectedDetector)) {
                            if (otherInfo.rankedFluorophores.size() - 1 == otherInfo.selectedIndex) {
                                removes.add(otherInfo);
                                continue;
                            } else {

                                otherInfo.selectedIndex++;
                            }
                        } else if (info.rankedFluorophores.size() - 1 == info.selectedIndex) {
                            removes.add(info);
                            continue;
                        } else {

                            info.selectedIndex++;
                        }

                        duplicates = true;
                    }

                }

            }
        }
        for (SelectionInfo info : removes) {
            if (iterateInfo.contains(info)) {
                iterateInfo.remove(info);
            }
            if (allInfo.contains(info)) {
                allInfo.remove(info);
            }
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
        generateNoise(allInfo);
        calcSumSNR(allInfo);

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
                }

                //noise is otherInfo's fluorophore expressing in info's channel with info's laser
                Fluorophore noiseFP = otherInfo.rankedFluorophores.get(otherInfo.selectedIndex);
                noise += noiseFP.express(info.selectedLaser, info.selectedDetector);
                

            }
            info.SNR = signal / noise;
            sumSNR += info.SNR;
        }

        return sumSNR;
    }

    static void generateNoise(ArrayList<SelectionInfo> selected) {
        //noise is otherInfo's fluorophore expressing in info's channel with info's laser
        for (SelectionInfo info : selected) {
            //in case something got in there
            info.noise.clear();

            for (SelectionInfo otherInfo : selected) {
                if (info.selectedLaser == otherInfo.selectedLaser) {
                    continue;
                }

                Fluorophore noiseFp = otherInfo.rankedFluorophores.get(otherInfo.selectedIndex);

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

}
