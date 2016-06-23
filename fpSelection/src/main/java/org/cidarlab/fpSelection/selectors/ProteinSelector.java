/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import org.cidarlab.fpSelection.dom.ProteinComparator;
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
import java.util.LinkedList;
import java.util.Map;
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
public class ProteinSelector {

    //Given a Laser & Filters, Suggest List of Proteins that works optimally for each filter
    //Suggest proteins based on a laser & filters
    //Works best for n >= 2;
    
    public static ArrayList<SelectionInfo> chooseFPs(HashMap<String, Fluorophore> masterList, Cytometer cyto, int n)
    {
        ArrayList<SelectionInfo> total = new ArrayList<>();
        for (Laser lase : cyto.lasers) {
            total.addAll(laserFiltersToFPs(masterList, lase));

        }
        
        //Prune the arrayList of the worst FPs until the size of the ArrayList is equal to 'n'
        return hillClimber(total, n);
    }
    
    
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

            tempList = new ArrayList<>();
            int threshold = 80;

            while (threshold > 40 && tempList.size() < (.1 * masterList.size() + 5)) {
                threshold--;

                for (Map.Entry<String, Fluorophore> entry : masterList.entrySet()) {
                    Fluorophore value = entry.getValue();

                    if (value.express(theLaser, aDetector) < threshold) {
                        continue;
                    } else {

                        tempList.add(value);
                    }
                }
            }
            if (tempList.isEmpty()) {
                continue;
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
        boolean second = false;
        
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
                Fluorophore fp = entry.rankedFluorophores.get(entry.selectedIndex);
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
                if(first)
                {
                    newPlot.addPlot(noisePlot);
                    newPlot.addPlot(emPlot);
                    newPlot.addPlot(boundsPlot);
                    newPlot.getAxis("x").setLabel("Wavelength (nm)'\r\nset title '" + entry.selectedLaser.name);
                    newPlot.getAxis("x").setBoundaries(300, 900);
                    newPlot.getAxis("y").setLabel("Intensity (%)");
                    newPlot.getAxis("y").setBoundaries(0, 125);
                    first = false;
                    second = true;
                }
                //otherwise add to graph object that is added to javaplot
                else
                {
                    g = new Graph();
                    g.addPlot(noisePlot);
                    g.addPlot(emPlot);
                    g.addPlot(boundsPlot);
                    g.getAxis("x").setLabel("Wavelength (nm)'\r\nset title '" + entry.selectedLaser.name);
                    g.getAxis("x").setBoundaries(300, 900);
                    g.getAxis("y").setLabel("Intensity (%)");
                    g.getAxis("y").setBoundaries(0, 125);
                    newPlot.addGraph(g);
                    second = false;
                }
                
            } else {

                //add emission plot
                Fluorophore fp = entry.rankedFluorophores.get(entry.selectedIndex);
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
                if (second)
                {
                    newPlot.addPlot(emPlot);
                    newPlot.addPlot(boundsPlot);
                    second = false;
                }
                else
                {
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
    
    public static ArrayList<SelectionInfo> hillClimber(ArrayList<SelectionInfo> suggestions, int n) {
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

                        //if true, keep info. False, keep otherInfo
                        if (ProteinComparator.dupeCompare(info, otherInfo, ProteinComparator.compareTypes.Brightness, false)) {
                            if (otherInfo.rankedFluorophores.size() - 1 == otherInfo.selectedIndex) {
                                if (!removes.contains(otherInfo)) {

                                    removes.add(otherInfo);
                                }
                                continue;
                            } else {

                                otherInfo.selectedIndex++;
                            }
                        } else if (info.rankedFluorophores.size() - 1 == info.selectedIndex) {
                            if (!removes.contains(info)) {

                                removes.add(info);
                            }
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

        sumSNR = calcSumSigNoise(allInfo);

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

                //Calculate the total Signal - Noise from that removal
                SNR = calcSumSigNoise(allInfo);

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

        }
        generateNoise(allInfo);
        sumSNR = calcSumSigNoise(allInfo);

        return allInfo;
    }

    public static double calcSumSigNoise(ArrayList<SelectionInfo> allInfo) {
//        double sumSNR = 0;
        double sumDiff = 0;

        for (SelectionInfo info : allInfo) {

            Fluorophore fp = info.rankedFluorophores.get(info.selectedIndex);

            //signal is info expressing in it's own channel with it's own laser.
            double signal = fp.express(info.selectedLaser, info.selectedDetector);
            double noise = 1;

            for (SelectionInfo otherInfo : allInfo) {
                if (info == otherInfo) {
                    continue;
                }

                //noise is otherInfo's fluorophore expressing in info's channel with info's laser
                Fluorophore noiseFP = otherInfo.rankedFluorophores.get(otherInfo.selectedIndex);
                noise += noiseFP.express(info.selectedLaser, info.selectedDetector);

            }
            info.SNR = signal / noise;
            info.SNDiff = signal - noise;
//            sumSNR += info.SNR;
            sumDiff += info.SNDiff;
        }

        //SOOOOOOOOO:
        
        //SNR provides higher average but not everything will be readable.
        //Signal - Noise provides lower average, but all signals are readable.
        //Ex: SNR yields 9 proteins, 1 has <1 SNR
        //    Signal - Noise yeilds 9, minimum has SNR = 3.
        
        
        return sumDiff;
    }

    public static void generateNoise(ArrayList<SelectionInfo> selected) {
        //noise is otherInfo's fluorophore expressing in info's channel with info's laser
        for (SelectionInfo info : selected) {
            //in case something got in there
            if(info.noise != null) info.noise.clear();
            else info.noise = new TreeMap<>();

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
