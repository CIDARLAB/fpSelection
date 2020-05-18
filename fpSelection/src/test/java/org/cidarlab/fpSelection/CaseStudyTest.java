/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cidarlab.fpSelection.algorithms.ExhaustiveSelection;
import org.cidarlab.fpSelection.algorithms.HillClimbingSelection;
import org.cidarlab.fpSelection.algorithms.RandomWalk;
import org.cidarlab.fpSelection.algorithms.SimulatedAnnealing;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SNR;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.parsers.fpSpectraParse;
import org.junit.Test;

/**
 * @author prash
 */
public class CaseStudyTest {

    static LinkedList<int[]> filterCombinations;
    static LinkedList<int[]> fluorophorePermutations;

    private static String basefp = Utilities.getCaseStudyFilepath();

    private static String setfp = "Set13";
    private static String set = "set13";

    private static String harvardFortessafp = basefp + "inputFiles" + Utilities.getSeparater() + "HarvardFortessa.csv";
    private static String harvardSonyfp = basefp + "inputFiles" + Utilities.getSeparater() + "HarvardSony.csv";
    private static String harvardMacsquantfp = basefp + "inputFiles" + Utilities.getSeparater() + "HarvardMacsquant.csv";
    private static String harvardCytoFlexfp = basefp + "inputFiles" + Utilities.getSeparater() + "HarvardCytoFlex.csv";

    private static String figure1Sonyfp = basefp + "inputFiles" + Utilities.getSeparater() + "figure1Sony.csv";

    private static String largerSpectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "largerSpectra.csv";
    private static String largerBrightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + "largerBrightness.csv";
    private static String smallerSpectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "smallerSpectra.csv";
    private static String smallerBrightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + "smallerBrightness.csv";

    private static String caseSpectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "finalSpectra.csv";
    //private static String caseSpectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "caseStudySpectra.csv";

    private static String caseSpectraMScarletfp = basefp + "inputFiles" + Utilities.getSeparater() + "caseStudySpectra_mScarlet.csv";
    private static String caseBrightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + "caseStudyBrightness.csv";

    private static String caseSpectraMScarletfig1fp = basefp + "inputFiles" + Utilities.getSeparater() + "caseStudySpectrafig1.csv";
    private static String caseBrightnessfig1fp = basefp + "inputFiles" + Utilities.getSeparater() + "caseStudyBrightnessfig1.csv";


    private static String figure1Spectrafp = basefp + "inputFiles" + Utilities.getSeparater() + "figure1Spectra.csv";
    private static String figure1Brightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + "figure1Brightness.csv";

    private static String runExptfp = basefp + setfp + Utilities.getSeparater() + "comp" + Utilities.getSeparater();

    private static String plotfp = basefp + setfp + Utilities.getSeparater() + "plots" + Utilities.getSeparater();

    private static String fig1plots = basefp + "fig1plots" + Utilities.getSeparater();


    private static int exhaustiveLim = 1000;


    // These tests need to be rewritten based on the latest refactor.
    // @Test
    public void testCaseStudy() throws IOException, InterruptedException {

        Cytometer harvardFortessa = fpFortessaParse.parse(harvardFortessafp, false);
        Cytometer harvardMacsquant = fpFortessaParse.parse(harvardMacsquantfp, false);
        Cytometer harvardCytoflex = fpFortessaParse.parse(harvardCytoFlexfp, false);


        String spectrafp = basefp + "inputFiles" + Utilities.getSeparater() + set + Utilities.getSeparater() + "spectra.csv";
        String brightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + set + Utilities.getSeparater() + "brightness.csv";


        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(spectrafp);
        fpSpectraParse.addBrightness(new File(brightnessfp), caseStudySpectralMap);


        //Run Analysis        
        
        
        
        /*
        System.out.println("==================Fortessa===================");
        exhaustiveTests(caseStudySpectralMap, harvardFortessa, "EX_HarvFort");
        
        
        System.out.println("==================Macsquant==================");
        exhaustiveTests(caseStudySpectralMap, harvardMacsquant, "EX_HarvMacs");
        
        System.out.println("==================CytoFlex===================");
        exhaustiveTests(caseStudySpectralMap, harvardCytoflex, "EX_HarvFlex");
        */
        
        
        /*
        System.out.println("Stochastic Test - Fortessa===================");
        stochasticTests(caseStudySpectralMap,harvardFortessa, "HarvFort");
        
        
        System.out.println("Stochastic Test - Macsquant==================");
        stochasticTests(caseStudySpectralMap,harvardMacsquant, "HarvMacs");
        
        System.out.println("Stochastic Test - CytoFlex===================");
        stochasticTests(caseStudySpectralMap,harvardCytoflex, "HarvFlex");
        */


        Map<String, Cytometer> cytomap = new HashMap<String, Cytometer>();
        cytomap.put("HarvFort", harvardFortessa);
        cytomap.put("HarvMacs", harvardMacsquant);
        cytomap.put("HarvFlex", harvardCytoflex);


        //Normalize results to 1.                 
        //String runfp = basefp + setfp + Utilities.getSeparater() +  "fprun" + Utilities.getSeparater();
        //analyseRuns(runfp,caseStudySpectralMap, cytomap);


        //plotExpComp();


        analyzeResults(basefp + setfp + Utilities.getSeparater());


        //compareResults();

        //String methodsfp = basefp + "methods" + Utilities.getSeparater();

        //for(String s:caseStudySpectralMap.keySet()){
        //createImage(methodsfp,true,caseStudySpectralMap.get(s),250,900,0,0,0);
        //createImage(methodsfp,false,caseStudySpectralMap.get(s),250,900,0,0,0);
        //}


        //"DsRed",250,900,561,590,630
        //createEXImage(methodsfp,caseStudySpectralMap.get("DsRed"),250,900,561,590,630);
        //"DsRed",250,900,488,650,720        
        //createEXImage(methodsfp,caseStudySpectralMap.get("DsRed"),250,900,488,650,720);

        //getFig3CorrectedResult();

        //getFig1();
    }


    public static void analyzeResults(String folderfp) {

        List<String> cytometers = new ArrayList<>();
        List<Integer> nfps = new ArrayList<>();

        cytometers.add("HarvFlex");
        cytometers.add("HarvFort");
        cytometers.add("HarvMacs");

        nfps.add(2);
        nfps.add(3);
        nfps.add(4);
        nfps.add(5);

        List<String> lines = new ArrayList<>();
        lines.add("Cytometer,n,No NA,Panel matches,Percentage,All Panels match,Percentage,Within 5%,Percentage,All Within 5%,Percentage,Within 10%,Percentage,All Within 10%,Percentage,Within 20%,Percentage,All Within 20%,Percentage");
        for (String cytometer : cytometers) {
            for (Integer nfp : nfps) {
                lines.add(analyzeSpecificConfig(folderfp, cytometer, nfp));
            }
        }
        Utilities.writeToFile(folderfp + "analysis.csv", lines);

    }

    public static String analyzeSpecificConfig(String folderfp, String cytometer, Integer n) {
        String exptfp = folderfp + "expt" + Utilities.getSeparater();
        String compfp = folderfp + "comp" + Utilities.getSeparater();
        File[] folders = (new File(compfp)).listFiles();

        int signalsMatch = 0;
        int allSignalsMatch = 0;
        int nona = 0;
        int within05 = 0;
        int within10 = 0;
        int within20 = 0;

        int all05 = 0;
        int all10 = 0;
        int all20 = 0;

        String nfp = n + "fp";
        for (File folder : folders) {

            String foldername = folder.getName();
            if (foldername.contains(cytometer)) {
                if (foldername.endsWith(nfp)) {
                    String compbasefp = folder.getAbsolutePath() + Utilities.getSeparater();
                    String exptbasefp = exptfp + folder.getName() + Utilities.getSeparater();
                    for (int i = 1; i <= 100; i++) {
                        boolean allSignalsMatchFlag = true;

                        String compfilefp = compbasefp + folder.getName() + "_row" + i + ".csv";
                        String exptfilefp = exptbasefp + folder.getName() + "_spMEDIAN_row" + i + ".csv";
                        List<String[]> complines = Utilities.getCSVFileContentAsList(compfilefp);
                        List<String[]> exptlines = Utilities.getCSVFileContentAsList(exptfilefp);
                        boolean hasna = false;

                        for (String[] exptline : exptlines) {
                            for (String strval : exptline) {
                                if (strval.equals("NA")) {
                                    hasna = true;
                                    break;
                                }
                            }
                            if (hasna) {
                                break;
                            }
                        }

                        if (!hasna) {
                            for (int j = 1; j < complines.size(); j++) {

                                String[] compline = complines.get(j);
                                String[] exptline = exptlines.get(j);
                                int comp1index = 0;
                                int expt1index = 0;

                                int count05 = 0;
                                int count10 = 0;
                                int count20 = 0;


                                for (int k = 1; k < compline.length; k++) {
                                    double compvalue = Double.parseDouble(compline[k]);
                                    double exptvalue = Double.parseDouble(exptline[k]);
                                    double diff = Math.abs(compvalue - exptvalue);

                                    if (diff < 0.05) {
                                        within05++;
                                        count05++;
                                    }
                                    if (diff < 0.1) {
                                        within10++;
                                        count10++;
                                    }
                                    if (diff < 0.2) {
                                        within20++;
                                        count20++;
                                    }

                                    if (compvalue == 1.0) {
                                        comp1index = k;
                                    }
                                    if (exptvalue == 1.0) {
                                        expt1index = k;
                                    }
                                }

                                if (count05 == n) {
                                    all05++;
                                }
                                if (count10 == n) {
                                    all10++;
                                }
                                if (count20 == n) {
                                    all20++;
                                }
                                if (comp1index == expt1index) {
                                    signalsMatch++;
                                } else {
                                    allSignalsMatchFlag = false;
                                }
                            }
                            nona++;
                            if (allSignalsMatchFlag) {
                                allSignalsMatch++;
                            }

                        }

                    }
                }
            }
        }


        String line =
                cytometer
                        + "," + n
                        + "," + nona
                        + "," + signalsMatch
                        + "," + (signalsMatch / (n * (double) nona))
                        + "," + allSignalsMatch
                        + "," + (allSignalsMatch / ((double) nona))
                        + "," + within05
                        + "," + (within05 / ((double) nona * n * n))
                        + "," + all05
                        + "," + (all05 / ((double) nona * n))
                        + "," + within10
                        + "," + (within10 / ((double) nona * n * n))
                        + "," + all10
                        + "," + (all10 / ((double) nona * n))
                        + "," + within20
                        + "," + (within20 / ((double) nona * n * n))
                        + "," + all20
                        + "," + (all20 / ((double) nona * n));
        System.out.println("For " + cytometer + " with " + nfp + ":");
        System.out.println("Number of configurations with no NA in expt: " + nona);
        System.out.println("Number of signals that matched for each configuration: " + signalsMatch + ". Percentage = " + (signalsMatch / (n * (double) nona)));
        System.out.println("Number of signals that matched for all configurations: " + allSignalsMatch + ". Percentage = " + (allSignalsMatch / ((double) nona)));
        System.out.println("-------------------------------------------------------");
        System.out.println("Total number within  5%: " + within05 + ". Percentage = " + (within05 / ((double) nona * n * n)));
        System.out.println("Total configurations within  5%: " + all05 + ". Percentage = " + (all05 / ((double) nona * n)));
        System.out.println("Total number within 10%: " + within10 + ". Percentage = " + (within10 / ((double) nona * n * n)));
        System.out.println("Total configurations within 10%: " + all10 + ". Percentage = " + (all10 / ((double) nona * n)));
        System.out.println("Total number within 20%: " + within20 + ". Percentage = " + (within20 / ((double) nona * n * n)));
        System.out.println("Total configurations within 20%: " + all20 + ". Percentage = " + (all20 / ((double) nona * n)));
        System.out.println("=======================================================");

        return line;
    }

    public static void getFig1() throws IOException, InterruptedException {
        Cytometer harvardCytoflex = fpFortessaParse.parse(harvardCytoFlexfp, false);
        Cytometer harvardFortessa = fpFortessaParse.parse(harvardFortessafp, false);
        Cytometer harvardMacsquant = fpFortessaParse.parse(harvardMacsquantfp, false);

        String spectrafp = basefp + "inputFiles" + Utilities.getSeparater() + set + Utilities.getSeparater() + "spectra.csv";
        String brightnessfp = basefp + "inputFiles" + Utilities.getSeparater() + set + Utilities.getSeparater() + "brightness.csv";

        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(spectrafp);
        fpSpectraParse.addBrightness(new File(brightnessfp), caseStudySpectralMap);
        exhaustiveRun(4, caseStudySpectralMap, harvardFortessa);

    }


    private static void exhaustiveRun(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer) throws IOException, InterruptedException {

        int numFluorophores = spectralMaps.size();

        //count filters
        int numFilters = 0;
        for (Laser laser : cytometer.lasers) {
            numFilters += laser.detectors.size();
        }

        //fluorophore index --> fluorophore object
        Fluorophore[] fluorophores = new Fluorophore[numFluorophores];
        int fpi = 0;
        for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
            Fluorophore fluorophore = entry.getValue();
            fluorophores[fpi] = fluorophore;
            fpi++;
        }

        Laser[] lasers = new Laser[numFilters];
        Detector[] detectors = new Detector[numFilters];
        int filterIndex = 0;
        for (Laser laser : cytometer.lasers) {
            for (Detector detector : laser.detectors) {
                lasers[filterIndex] = laser;
                detectors[filterIndex] = detector;
                filterIndex++;
            }
        }

        //get all combinations of filters (order not important)
        filterCombinations = new LinkedList<>();
        int tempData[] = new int[n];
        getCombinations(tempData, 0, numFilters - 1, 0, n);

        //get all permutations of fluorophores to match to filters (order is important)
        fluorophorePermutations = new LinkedList<>();
        tempData = new int[n];
        getPermutations(tempData, numFluorophores, n);

        long totalComputations = filterCombinations.size() * fluorophorePermutations.size();
        System.out.println("Filter Combinations :: " + filterCombinations.size());
        System.out.println("FP Permutations     :: " + fluorophorePermutations.size());
        System.out.println("Total Computations : " + totalComputations);

        List<Map.Entry<List<SelectionInfo>, SNR>> results = new ArrayList<>();
        int count = 0;
        for (int[] filterCombo : filterCombinations) {
            for (int[] fluorophorePerm : fluorophorePermutations) {
                List<SelectionInfo> currentSelection = ExhaustiveSelection.getSelection(n, fluorophorePerm, filterCombo, fluorophores, lasers, detectors);
                SNR snr = new SNR(currentSelection);
                results.add(new AbstractMap.SimpleEntry<>(currentSelection, snr));

                //ProteinSelector.generateNoise(currentSelection);
                //JavaPlot plot = ProteinSelector.getJavaPlot(currentSelection);
                //Utilities.plotToFile(plot, fp + count + ".png");
                //count++;
            }
        }

        Collections.sort(results, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
            SNR s1 = o1.getValue();
            SNR s2 = o2.getValue();
            return s1.compare(s2);
        });
        Collections.reverse(results);

        List<String> top = new ArrayList<>();
        List<String> middle = new ArrayList<>();
        List<String> bottom = new ArrayList<>();

        String line = "";

        List<Map.Entry<List<SelectionInfo>, SNR>> topresult = new ArrayList<>();
        List<Map.Entry<List<SelectionInfo>, SNR>> middleresult = new ArrayList<>();
        List<Map.Entry<List<SelectionInfo>, SNR>> bottomresult = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            topresult.add(results.get(i));
        }

        for (int i = 4000; i < 4020; i++) {
            middleresult.add(results.get(i));
        }

        for (int i = (results.size() - 1); i >= (results.size() - 20); i--) {
            bottomresult.add(results.get(i));
        }

        for (Map.Entry<List<SelectionInfo>, SNR> entry : topresult) {
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            top.add(line);
        }

        for (Map.Entry<List<SelectionInfo>, SNR> entry : middleresult) {
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            middle.add(line);
        }

        for (Map.Entry<List<SelectionInfo>, SNR> entry : bottomresult) {
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            bottom.add(line);
        }

        Utilities.writeToFile(fig1plots + "top.csv", top);
        Utilities.writeToFile(fig1plots + "middle.csv", middle);
        Utilities.writeToFile(fig1plots + "bottom.csv", bottom);

    }


    public static void getFig3CorrectedResult() throws IOException {
        Cytometer harvardCytoflex = fpFortessaParse.parse(harvardCytoFlexfp, false);

        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(caseSpectraMScarletfp);
        fpSpectraParse.addBrightness(new File(caseBrightnessfp), caseStudySpectralMap);

        Laser uv = null;
        Detector uvc = null;
        Detector uva = null;

        Laser violet = null;
        Detector violetb = null;

        Laser yelgrn = null;
        Detector yelgrnc = null;

        Laser red = null;
        Detector redc = null;

        for (Laser laser : harvardCytoflex.lasers) {
            if (laser.getName().equals("UV")) {
                uv = laser;
                for (Detector d : laser.detectors) {
                    if (d.identifier.equals("C"))
                        uvc = d;
                    else if (d.identifier.equals("A"))
                        uva = d;
                }

            } else if (laser.getName().equals("Violet")) {
                violet = laser;
                for (Detector d : laser.detectors) {
                    if (d.identifier.equals("B"))
                        violetb = d;
                }
            } else if (laser.getName().equals("Yel/Grn")) {
                yelgrn = laser;
                for (Detector d : laser.detectors) {
                    if (d.identifier.equals("C"))
                        yelgrnc = d;
                }
            } else if (laser.getName().equals("Red")) {
                red = laser;
                for (Detector d : laser.detectors) {
                    if (d.identifier.equals("C"))
                        redc = d;
                }
            }
        }

        System.out.println("\n\n");

        List<Fluorophore> fps = new ArrayList<Fluorophore>();
        fps.add(caseStudySpectralMap.get("mCherry"));
        fps.add(caseStudySpectralMap.get("Cerulean"));
        fps.add(caseStudySpectralMap.get("Sirius"));
        fps.add(caseStudySpectralMap.get("mScarlet"));
        fps.add(caseStudySpectralMap.get("iRFP713"));


        SelectionInfo si0 = new SelectionInfo();
        si0.selectedFluorophore = caseStudySpectralMap.get("mCherry");
        si0.selectedLaser = uv;
        si0.selectedDetector = uvc;

        SelectionInfo si1 = new SelectionInfo();
        si1.selectedFluorophore = caseStudySpectralMap.get("Cerul'ean");
        si1.selectedLaser = violet;
        si1.selectedDetector = violetb;

        SelectionInfo si2 = new SelectionInfo();
        si2.selectedFluorophore = caseStudySpectralMap.get("Sirius");
        si2.selectedLaser = uv;
        si2.selectedDetector = uva;

        SelectionInfo si3 = new SelectionInfo();
        si3.selectedFluorophore = caseStudySpectralMap.get("mScarlet");
        si3.selectedLaser = yelgrn;
        si3.selectedDetector = yelgrnc;

        SelectionInfo si4 = new SelectionInfo();
        si4.selectedFluorophore = caseStudySpectralMap.get("iRFP713");
        si4.selectedLaser = red;
        si4.selectedDetector = redc;

        List<SelectionInfo> selection = new ArrayList<SelectionInfo>();
        selection.add(si0);
        selection.add(si1);
        selection.add(si2);
        selection.add(si3);
        selection.add(si4);
        
        /*
        SNR snr = new SNR(selection);
        String string = toString(selection.size(),selection,snr);
        System.out.println(string);
        */

        System.out.println("----------------------------------");
        for (SelectionInfo si : selection) {
            double max = fps.get(0).express(si.getSelectedLaser(), si.getSelectedDetector());
            List<Double> signals = new ArrayList<Double>();
            for (Fluorophore fp : fps) {
                double signal = fp.express(si.getSelectedLaser(), si.getSelectedDetector());
                if (signal > max) {
                    max = signal;
                }
                signals.add(signal);
            }
            for (Double d : signals) {
                System.out.println(d / max);
            }
            System.out.println("----------------------------------");

        }


    }

    public static void createEXImage(String filepath, Fluorophore fp, double low, double high, double laserwl, double detectorlow, double detectorhigh) throws InterruptedException, IOException {

        String color = "g";

        String picname = "";
        String pyname = "";

        picname = filepath + fp.name + "_laser" + laserwl + "rsum.png";
        pyname = filepath + fp.name + "_laser" + laserwl + ".py";

        List<String> lines = new ArrayList<String>();
        lines.add("import matplotlib\n" +
                "import math\n" +
                "import numpy\n" +
                "from math import e\n" +
                "import seaborn as sns\n" +
                "matplotlib.use('agg',warn=False, force=True)\n" +
                "from matplotlib import pyplot as plt\n" +
                "from matplotlib import patches as patches\n" +
                "\n" +
                "fig = plt.figure()\n" +
                "sns.set(font_scale=1)\n" +
                "sns.set_style(\"white\")\n" +
                "sns.despine()\n");


        double mult = fp.EXspectrum.get(laserwl) / 100;

        String xsig = "xsig = [";
        String ysig = "ysig = [";

        String xdetec = "xdet = [";
        String ydetec = "ydet = [";


        for (double i = detectorlow; i < detectorhigh; i += 5) {
            if (fp.EMspectrum.containsKey(i)) {
                xdetec += i + ",";
                double val = ((fp.EMspectrum.get(i) / 100) * mult);
                ydetec += val + ",";
                lines.add("plt.plot([" + i + "," + i + "," + (i + 5) + "," + (i + 5) + "],[0," + val + "," + val + ",0],color='black', linewidth=1)");
                //plt.plot(xfilt,yfilt, color='black', linewidth=1.5)

            }
        }


        for (double i = low; i < high; i++) {
            if (fp.EMspectrum.containsKey(i)) {
                xsig += i + ",";
                ysig += ((fp.EMspectrum.get(i) / 100) * mult) + ",";
            }
        }
        if (fp.EMspectrum.containsKey(high)) {
            xsig += high;
            ysig += ((fp.EMspectrum.get(high) / 100) * mult);
        }

        if (xsig.endsWith(",")) {
            xsig = xsig.substring(0, xsig.lastIndexOf(","));
            ysig = ysig.substring(0, ysig.lastIndexOf(","));
        }


        xdetec += detectorhigh + "," + detectorlow + "]";
        ydetec += "0.0,0.0]";


        xsig += "]";
        ysig += "]";

        lines.add(xdetec);
        lines.add(ydetec);


        lines.add(xsig);
        lines.add(ysig);

        lines.add("plt.fill_between(xsig,ysig,facecolor='" + color + "',alpha=0.6)");
        lines.add("plt.plot(xsig,ysig,'-',color='" + color + "')");


        lines.add("plt.xlabel(\"Wavelength\")");
        lines.add("plt.ylabel(\"Intensity\")");
        lines.add("plt.xlim(" + low + "," + high + ")");
        lines.add("plt.ylim(0,1.001)");
        lines.add("plt.legend(frameon=False)\n" +
                "plt.tight_layout()");
        lines.add("fig.savefig('" + picname + "', dpi=180)");
        Utilities.writeToFile(pyname, lines);
        Utilities.runPythonScript(pyname);
    }

    public static void createImage(String filepath, boolean ex, Fluorophore fp, double low, double high, double laserwl, double detectorlow, double detectorhigh) throws InterruptedException, IOException {

        String color = "g";

        String picname = "";
        String pyname = "";

        if (ex) {
            color = "b";
            picname = filepath + fp.name + "_ex" + ".png";
            pyname = filepath + fp.name + "_ex" + ".py";
        } else {
            color = "g";
            picname = filepath + fp.name + "_em" + ".png";
            pyname = filepath + fp.name + "_em" + ".py";
        }

        List<String> lines = new ArrayList<String>();
        lines.add("import matplotlib\n" +
                "import math\n" +
                "import numpy\n" +
                "from math import e\n" +
                "import seaborn as sns\n" +
                "matplotlib.use('agg',warn=False, force=True)\n" +
                "from matplotlib import pyplot as plt\n" +
                "from matplotlib import patches as patches\n" +
                "\n" +
                "fig = plt.figure()\n" +
                "sns.set(font_scale=1)\n" +
                "sns.set_style(\"white\")\n" +
                "sns.despine()\n");
        
        
        
/*
        plt.fill(xsig,ysig,facecolor='g',alpha=0.6)
plt.fill(xnoi,ynoi,facecolor='r',alpha=0.6)
        */

        String xsig = "xsig = [";
        String ysig = "ysig = [";


        if (ex) {
            for (double i = low; i < high; i++) {
                if (fp.EXspectrum.containsKey(i)) {
                    xsig += i + ",";
                    ysig += (fp.EXspectrum.get(i) / 100) + ",";
                }
            }
            if (fp.EXspectrum.containsKey(high)) {
                xsig += high;
                ysig += (fp.EXspectrum.get(high) / 100);
            }

            if (xsig.endsWith(",")) {
                xsig = xsig.substring(0, xsig.lastIndexOf(","));
                ysig = ysig.substring(0, ysig.lastIndexOf(","));
            }
        } else {
            for (double i = low; i < high; i++) {
                if (fp.EMspectrum.containsKey(i)) {
                    xsig += i + ",";
                    ysig += (fp.EMspectrum.get(i) / 100) + ",";
                }
            }
            if (fp.EMspectrum.containsKey(high)) {
                xsig += high;
                ysig += (fp.EMspectrum.get(high) / 100);
            }

            if (xsig.endsWith(",")) {
                xsig = xsig.substring(0, xsig.lastIndexOf(","));
                ysig = ysig.substring(0, ysig.lastIndexOf(","));
            }
        }


        xsig += "]";
        ysig += "]";

        lines.add(xsig);
        lines.add(ysig);

        lines.add("plt.fill_between(xsig,ysig,facecolor='" + color + "',alpha=0.6)");
        lines.add("plt.plot(xsig,ysig,'-',color='" + color + "')");


        lines.add("plt.xlabel(\"Wavelength\")");
        lines.add("plt.ylabel(\"Intensity\")");
        lines.add("plt.xlim(" + low + "," + high + ")");
        lines.add("plt.ylim(0,1.001)");
        lines.add("plt.legend(frameon=False)\n" +
                "plt.tight_layout()");
        lines.add("fig.savefig('" + picname + "', dpi=180)");
        Utilities.writeToFile(pyname, lines);
        Utilities.runPythonScript(pyname);
    }

    public static boolean thresh(List<String[]> comp, List<String[]> exp) {


        return false;
    }

    public static int matches(List<String[]> comp, List<String[]> exp) {

        int n = 0;
        for (int i = 1; i < comp.size(); i++) {
            double compval = Double.valueOf(comp.get(i)[i]);
            double expval = Double.valueOf(exp.get(i)[i]);
            if ((compval == 1.00) && (expval == 1.00)) {
                n++;
            } else if ((compval != 1.00) && (expval != 1.00)) {
                n++;
            } else {
                //return false;
            }
        }

        return n;
    }

    public static void compareResults() {
        String compfp = basefp + "run1" + Utilities.getSeparater();
        String expfp = basefp + "expt1" + Utilities.getSeparater();
        File[] folders = (new File(compfp)).listFiles();

        int fp2tot = 0;
        int fp3tot = 0;
        int fp4tot = 0;
        int fp5tot = 0;
        int fp6tot = 0;

        int sonytot = 0;
        int macstot = 0;
        int forttot = 0;

        int fp2match = 0;
        int fp3match = 0;
        int fp4match = 0;
        int fp5match = 0;
        int fp6match = 0;

        int fp2thresh = 0;
        int fp3thresh = 0;
        int fp4thresh = 0;
        int fp5thresh = 0;
        int fp6thresh = 0;

        int sonymatch = 0;
        int macsmatch = 0;
        int fortmatch = 0;

        int sonythresh = 0;
        int macsthresh = 0;
        int fortthresh = 0;

        for (File folder : folders) {
            if (!folder.getName().startsWith("Stoch_")) {
                continue;
            }
            File[] rowfiles = folder.listFiles();
            for (File rowfile : rowfiles) {
                String compfilename = rowfile.getName();
                String rowname = compfilename.substring(compfilename.lastIndexOf("_") + 1, compfilename.lastIndexOf(".csv"));
                String expfilename = expfp + folder.getName() + Utilities.getSeparater() + folder.getName() + "_spMEAN_" + rowname + ".csv";
                File expfile = new File(expfilename);

                List<String[]> complines = Utilities.getCSVFileContentAsList(rowfile);
                List<String[]> explines = Utilities.getCSVFileContentAsList(expfile);
                int n = complines.size() - 1;

                if (rowfile.getName().contains("2fp")) {
                    fp2tot++;
                } else if (rowfile.getName().contains("3fp")) {
                    fp3tot++;
                } else if (rowfile.getName().contains("4fp")) {
                    fp4tot++;
                } else if (rowfile.getName().contains("5fp")) {
                    fp5tot++;
                } else if (rowfile.getName().contains("6fp")) {
                    fp6tot++;
                }

                if (rowfile.getName().contains("Fort")) {
                    forttot++;
                } else if (rowfile.getName().contains("Sony")) {
                    sonytot++;
                } else if (rowfile.getName().contains("Macs")) {
                    macstot++;
                }
                //System.out.println(rowfile.getName());

                if (matches(complines, explines) == n) {
                    if (rowfile.getName().contains("2fp")) {
                        fp2match++;
                    } else if (rowfile.getName().contains("3fp")) {
                        fp3match++;
                    } else if (rowfile.getName().contains("4fp")) {
                        fp4match++;
                    } else if (rowfile.getName().contains("5fp")) {
                        fp5match++;
                    } else if (rowfile.getName().contains("6fp")) {
                        fp6match++;
                    }

                    if (rowfile.getName().contains("Fort")) {
                        fortmatch++;
                    } else if (rowfile.getName().contains("Sony")) {
                        sonymatch++;
                    } else if (rowfile.getName().contains("Macs")) {
                        macsmatch++;
                    }
                }

            }
        }

        List<String> lines = new ArrayList<String>();

        lines.add("Category,Matches Count, Threshold Count,Total");
        lines.add("2fp," + fp2match + "," + fp2thresh + "," + fp2tot);
        lines.add("3fp," + fp3match + "," + fp3thresh + "," + fp3tot);
        lines.add("4fp," + fp4match + "," + fp4thresh + "," + fp4tot);
        lines.add("5fp," + fp5match + "," + fp5thresh + "," + fp5tot);
        lines.add("6fp," + fp6match + "," + fp6thresh + "," + fp6tot);
        lines.add("Fort," + fortmatch + "," + fortthresh + "," + forttot);
        lines.add("Macs," + macsmatch + "," + macsthresh + "," + macstot);
        lines.add("Sony," + sonymatch + "," + sonythresh + "," + sonytot);

        Utilities.writeToFile(basefp + "analysis.csv", lines);
    }

    public static void plotExpComp() throws InterruptedException, IOException {
        String compfp = basefp + setfp + Utilities.getSeparater() + "comp" + Utilities.getSeparater();
        String expfp = basefp + setfp + Utilities.getSeparater() + "expt" + Utilities.getSeparater();
        File[] files = (new File(compfp)).listFiles();
        for (File folderf : files) {
            if (folderf.getName().contains("HarvSony")) continue;
            //if (folderf.getName().contains("HC_HarvFlex_5fp")) continue;

            File[] rowfiles = folderf.listFiles();
            for (File rowfile : rowfiles) {
                String filename = rowfile.getName();
                filename = filename.substring(0, filename.lastIndexOf(".csv"));
                String row = filename.substring(filename.lastIndexOf("_row"));
                String prefix = filename.substring(0, filename.lastIndexOf("_row"));

                //String expMeanfile = prefix + "_spMEAN" + row;
                //String expMeanfp = expfp + folderf.getName() + Utilities.getSeparater() + expMeanfile + ".csv";
                String expMedianfile = prefix + "_spMEDIAN" + row;
                String expMedianfp = expfp + folderf.getName() + Utilities.getSeparater() + expMedianfile + ".csv";


                //getAnalyzePlotLines(rowfile.getAbsolutePath(),expMeanfp,expMeanfile);
                getAnalyzePlotLines(rowfile.getAbsolutePath(), expMedianfp, expMedianfile);
                //System.out.println(filename + " : " + prefix + " : " + row);                
            }
        }

    }

    public static void getAnalyzePlotLines(String compfp, String expfp, String filename) throws InterruptedException, IOException {
        List<String> lines = new ArrayList<String>();

        String pyfp = plotfp + filename + ".py";
        String figfp = plotfp + filename + ".png";


        if (!Utilities.validFilepath(expfp)) {
            return;
        }


        List<String[]> complines = Utilities.getCSVFileContentAsList(compfp);
        List<String[]> explines = Utilities.getCSVFileContentAsList(expfp);

        int n = complines.size() - 1;
        List<String> fps = new ArrayList<String>();
        List<String> detectors = new ArrayList<String>();
        for (int i = 1; i <= n; i++) {
            detectors.add(complines.get(0)[i]);
        }

        List<List<Double>> compvalues = new ArrayList<List<Double>>();
        List<List<Double>> expvalues = new ArrayList<List<Double>>();


        boolean foundna = false;
        System.out.println("Starting File Name : " + filename);
        for (int i = 1; i <= n; i++) {
            List<Double> compconfig = new ArrayList<Double>();
            List<Double> expconfig = new ArrayList<Double>();
            fps.add(complines.get(i)[0]);
            for (int j = 1; j <= n; j++) {
                compconfig.add(Double.valueOf(complines.get(i)[j]));

                if (explines.get(i)[j].toLowerCase().contains("na")) {
                    foundna = true;
                    break;
                } else {
                    expconfig.add(Double.valueOf(explines.get(i)[j]));
                }


            }
            if (foundna) {
                break;
            }
            compvalues.add(compconfig);
            expvalues.add(expconfig);

        }

        if (foundna) {
            return;
        }
        //Headers and Lib imports
        lines.add("import matplotlib\n" +
                "import seaborn as sns\n" +
                "import pandas as pd\n" +
                "matplotlib.use('agg',warn=False, force=True)\n" +
                "from matplotlib import pyplot as plt\n" +
                "from matplotlib import patches as patches\n\n");


        //Plot details 
        lines.add("fig = plt.figure()\n" +
                "sns.set(font_scale=1)\n" +
                "sns.set_style(\"white\")\n\n");


        List<Integer> sigX = new ArrayList<Integer>();
        List<Integer> btX = new ArrayList<Integer>();

        List<Double> sigExp = new ArrayList<Double>();
        List<Double> sigComp = new ArrayList<Double>();

        List<Double> btExp = new ArrayList<Double>();
        List<Double> btComp = new ArrayList<Double>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int index = ((n + 2) * (i)) + j + 1;

                String color = "g";
                if (i == j) {
                    //Sig
                    sigX.add(index);
                    sigExp.add(expvalues.get(i).get(j));
                    sigComp.add(compvalues.get(i).get(j));
                    color = "g";

                } else {
                    //bt
                    btX.add(index);
                    btExp.add(expvalues.get(i).get(j));
                    btComp.add(compvalues.get(i).get(j));
                    color = "r";
                }
                double lineval = expvalues.get(i).get(j);
                if (compvalues.get(i).get(j) > lineval) {
                    lineval = compvalues.get(i).get(j);
                }
                lines.add("plt.plot([" + index + "," + index + "],[0, " + lineval + "],'-',c=\"" + color + "\",alpha=0.6)");

            }
        }

        String sigXline = "sigX = [";
        String btXline = "btX = [";

        String sigExpline = "sigExp = [";
        String sigCompline = "sigComp = [";

        String btExpline = "btExp = [";
        String btCompline = "btComp = [";

        for (int i = 0; i < sigX.size(); i++) {
            sigXline += sigX.get(i);
            sigExpline += sigExp.get(i);
            sigCompline += sigComp.get(i);

            if (i < (sigX.size() - 1)) {
                sigXline += ",";
                sigExpline += ",";
                sigCompline += ",";
            }
        }

        for (int i = 0; i < btX.size(); i++) {
            btXline += btX.get(i);
            btExpline += btExp.get(i);
            btCompline += btComp.get(i);

            if (i < (btX.size() - 1)) {
                btXline += ",";
                btExpline += ",";
                btCompline += ",";
            }
        }

        sigXline += "]";
        btXline += "]";

        sigExpline += "]";
        sigCompline += "]";

        btExpline += "]";
        btCompline += "]";

        lines.add(sigXline);
        lines.add(btXline);
        lines.add(sigExpline);
        lines.add(sigCompline);

        lines.add(btExpline);
        lines.add(btCompline);
        //Add Scatter plots
        lines.add("plt.scatter(sigX, sigExp, marker='x', c=\"g\",alpha=0.6)\n" +
                "plt.scatter(sigX, sigComp, marker='o', c=\"g\",alpha=0.6)\n" +
                "\n" +
                "plt.scatter(btX, btExp, marker='x', c=\"r\",alpha=0.6)\n" +
                "plt.scatter(btX, btComp, marker='o', c=\"r\",alpha=0.6)\n\n");


        String xticks = "";
        String xtickVals = "";
        for (int i = 0; i < n; i++) {

            String xtick = "";
            String xtickVal = "";
            for (int j = 0; j < n; j++) {

                xtickVal += ("\"" + fps.get(j) + "\"");
                xtick += (((n + 2) * i) + j + 1);
                if (j < n - 1) {
                    xtick += ",";
                    xtickVal += ",";
                }
            }

            xticks += xtick;
            xtickVals += xtickVal;
            if (i < n - 1) {
                xticks += ",";
                xtickVals += ",";
            }
        }

        lines.add("plt.xticks([" + xticks + "],[" + xtickVals + "],rotation=90)");

        lines.add("ax1 = plt.subplot(111)");

        for (int i = 0; i < detectors.size(); i++) {
            float xpos = (((((n + 2) * i) + 1) * 2) + (n - 1)) / 2;
            lines.add("ax1.annotate('" + detectors.get(i) + "', xy=(" + xpos + ", 1.05), xytext=(" + xpos + ", 1.05), ha='center')");
        }

        int xlim = (n * n) + (2 * (n - 1)) + 1;

        String modifiedfp = figfp.replaceAll("\\\\", "/");

        lines.add("plt.xlim(0," + xlim + ")");
        lines.add("plt.ylim(0,1.1)");
        lines.add("plt.tight_layout()");
        lines.add("fig.savefig('" + modifiedfp + "', dpi=120)");

        Utilities.writeToFile(pyfp, lines);
        Utilities.runPythonScript(pyfp);


    }

    public static void analyseRuns(String runfp, Map<String, Fluorophore> fpmap, Map<String, Cytometer> cytomap) throws IOException {
        File[] files = (new File(runfp)).listFiles();
        for (File f : files) {
            if (f.getName().contains("_1fp")) continue;

            get10Rows(f.getAbsolutePath(), fpmap, cytomap);
        }

    }

    public static Laser getLaser(Cytometer c, String lname) {
        for (Laser l : c.lasers) {
            if (l.getName().equals(lname)) {
                return l;
            }
        }
        System.out.println("This shouldn't happen");
        return null;
    }

    public static Detector getDetector(Laser l, String dname) {
        for (Detector d : l.detectors) {
            if (d.identifier.equals(dname)) {
                return d;
            }
        }
        System.out.println("This shouldn't happen");
        return null;
    }

    public static List<SelectionInfo> getConfiguration(String[] row, Map<String, Fluorophore> fpmap, Cytometer c) {
        int n = row.length / 5;

        List<SelectionInfo> config = new ArrayList<SelectionInfo>();
        for (int i = 0; i < row.length; i += 5) {
            Fluorophore fp = fpmap.get(row[i]);
            Laser l = getLaser(c, row[i + 1]);
            Detector d = getDetector(l, row[i + 2]);
            SelectionInfo si = new SelectionInfo();
            si.setSelectedFluorophore(fp);
            si.setSelectedLaser(l);
            si.setSelectedDetector(d);
            config.add(si);
        }

        return config;
    }

    public static void get10Rows(String filepath, Map<String, Fluorophore> fpmap, Map<String, Cytometer> cytomap) throws IOException {


        File f = new File(filepath);

        String foldername = f.getName();
        foldername = foldername.substring(0, foldername.indexOf(".csv"));
        System.out.println(foldername);

        String rootfolderfp = runExptfp + foldername + Utilities.getSeparater();
        Utilities.makeDirectory(rootfolderfp);


        Cytometer c = null;
        if (f.getName().contains("HarvFort")) {
            c = cytomap.get("HarvFort");
        } else if (f.getName().contains("HarvMacs")) {
            c = cytomap.get("HarvMacs");
        } else if (f.getName().contains("HarvSony")) {
            c = cytomap.get("HarvSony");
        } else if (f.getName().contains("HarvFlex")) {
            c = cytomap.get("HarvFlex");
        }

        List<String[]> rows = Utilities.getCSVFileContentAsList(filepath);
        int lim = 101;
        if (lim > rows.size()) {
            lim = rows.size();
        }

        for (int i = 1; i < lim; i++) {
            List<SelectionInfo> config = getConfiguration(rows.get(i), fpmap, c);
            printConfig(config, rootfolderfp, foldername, i);
        }
    }

    public static void printConfig(List<SelectionInfo> config, String rootfp, String foldername, int row) {

        List<String> lines = new ArrayList<String>();

        List<Fluorophore> fps = new ArrayList<Fluorophore>();
        String header = "";
        for (SelectionInfo si : config) {
            fps.add(si.getSelectedFluorophore());
            header += ("," + si.getSelectedLaser().getName() + " (" + si.getSelectedDetector().identifier + ")");
        }
        lines.add(header);

        for (int i = 0; i < config.size(); i++) {
            SelectionInfo si = config.get(i);
            String configline = fps.get(i).name;

            List<Double> sigs = new ArrayList<Double>();
            for (Fluorophore fp : fps) {
                sigs.add(fp.express(si.getSelectedLaser(), si.getSelectedDetector()));
            }
            for (Double d : normalize(sigs)) {
                configline += ("," + d);
            }
            lines.add(configline);
        }

        String filefp = rootfp + foldername + "_row" + row + ".csv";
        Utilities.writeToFile(filefp, lines);

    }

    private static List<Double> normalize(List<Double> vals) {
        double max = vals.get(0);
        for (Double d : vals) {
            if (d > max) {
                max = d;
            }
        }
        List<Double> norm = new ArrayList<Double>();
        for (Double d : vals) {
            if (max != 0)
                norm.add(d / max);
            else
                norm.add(d);
        }
        return norm;
    }

    //@Test
    public void testFigure1() throws IOException, InterruptedException {
        Cytometer figure1cytometer = fpFortessaParse.parse(figure1Sonyfp, false);
        //Cytometer figure1cytometer = fpFortessaParse.parse(harvardFortessafp, false);
        Map<String, Fluorophore> spectralMap = fpSpectraParse.parse(figure1Spectrafp);
        fpSpectraParse.addBrightness(new File(figure1Brightnessfp), spectralMap);
        exhaustiveRun(2, spectralMap, figure1cytometer);


    }

    private static List<String> getFigure1PlotScript(Map<Double, Double> signal, Map<Double, Double> noise, Detector d, String filename) {
        //250-900

        List<String> lines = new ArrayList<>();
        lines.add("import matplotlib\n" +
                "import math\n" +
                "import numpy\n" +
                "from math import e\n" +
                "import seaborn as sns\n" +
                "matplotlib.use('agg',warn=False, force=True)\n" +
                "from matplotlib import pyplot as plt\n" +
                "from matplotlib import patches as patches\n");


        lines.add("fig = plt.figure()\n" +
                "sns.set(font_scale=1)\n" +
                "sns.set_style(\"white\")\n" +
                "sns.despine()");


        double filtLeft = d.filterMidpoint - (d.filterWidth / 2);
        double filtRight = d.filterMidpoint + (d.filterWidth / 2);

        String xfilt = "xfilt = [" + filtLeft + "," + filtLeft + "," + filtRight + "," + filtRight + "]";
        String yfilt = "yfilt = [" + 0.0 + "," + 100.0 + "," + 100.0 + "," + 0.0 + "]";

        lines.add(xfilt);
        lines.add(yfilt);

        lines.add("plt.plot(xfilt,yfilt, color='black', linewidth=1.5)");
        lines.add("plt.fill(xfilt,yfilt, facecolor=\"none\", hatch=\"/\", edgecolor=\"black\", linewidth=0.0)");


        String xsig = "xsig = [";
        String ysig = "ysig = [";
        String xnoi = "xnoi = [";
        String ynoi = "ynoi = [";

        List<Double> xsigval = new ArrayList<>();
        List<Double> ysigval = new ArrayList<>();
        List<Double> xnoival = new ArrayList<>();
        List<Double> ynoival = new ArrayList<>();

        for (int i = 250; i <= 900; i += 1) {
            double wl = (double) i;
            if (signal.containsKey(wl)) {
                xsigval.add(wl);
                ysigval.add(signal.get(wl));
            } else {
                xsigval.add(wl);
                ysigval.add(0.0);

            }

            if (noise.containsKey(wl)) {
                xnoival.add(wl);
                ynoival.add(noise.get(wl));
            } else {
                xnoival.add(wl);
                ynoival.add(0.0);
            }

        }

        xsig += xsigval.get(0);
        ysig += ysigval.get(0);
        xnoi += xnoival.get(0);
        ynoi += ynoival.get(0);

        for (int i = 1; i < xsigval.size(); i++) {
            xsig += "," + xsigval.get(i);
        }
        for (int i = 1; i < ysigval.size(); i++) {
            ysig += "," + ysigval.get(i);
        }
        for (int i = 1; i < xnoival.size(); i++) {
            xnoi += "," + xnoival.get(i);
        }
        for (int i = 1; i < ynoival.size(); i++) {
            ynoi += "," + ynoival.get(i);
        }

        xsig += "]";
        ysig += "]";
        xnoi += "]";
        ynoi += "]";

        lines.add(xsig);
        lines.add(ysig);
        lines.add(xnoi);
        lines.add(ynoi);


        lines.add("plt.fill(xsig,ysig,facecolor='g',alpha=0.6)");
        lines.add("plt.fill(xnoi,ynoi,facecolor='r',alpha=0.6)");


        lines.add("plt.xlabel(\"Wavelength\")\n" +
                "plt.ylabel(\"Intensity\")\n" +
                "plt.xlim(250,900)\n" +
                "plt.ylim(0,100.1)\n" +
                "plt.legend(frameon=False)\n" +
                "plt.tight_layout()\n" +
                "#plt.yscale('symlog')\n" +
                "fig.savefig('" + filename + ".png', dpi=180)");

        return lines;
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        Cytometer harvardFortessa = fpFortessaParse.parse(harvardFortessafp, false);
        Cytometer harvardSony = fpFortessaParse.parse(harvardSonyfp, false);
        Cytometer harvardMacsquant = fpFortessaParse.parse(harvardMacsquantfp, false);

        //Map<String, Fluorophore> smallerSpectralMap = fpSpectraParse.parse(smallerSpectrafp);
        //fpSpectraParse.addBrightness(new File(smallerBrightnessfp), smallerSpectralMap);

        //Map<String, Fluorophore> largerSpectralMap = fpSpectraParse.parse(largerSpectrafp);
        //fpSpectraParse.addBrightness(new File(largerBrightnessfp), largerSpectralMap);

        Map<String, Fluorophore> caseStudySpectralMap = fpSpectraParse.parse(caseSpectrafp);
        fpSpectraParse.addBrightness(new File(caseBrightnessfp), caseStudySpectralMap);

        //Map<String, Fluorophore> smallerSpectralMap = fpSpectraParse.parse(smallerSpectrafp);
        //fpSpectraParse.addBrightness(new File(smallerBrightnessfp), smallerSpectralMap);

        //Map<String, Fluorophore> largerSpectralMap = fpSpectraParse.parse(largerSpectrafp);
        //fpSpectraParse.addBrightness(new File(largerBrightnessfp), largerSpectralMap);


        //System.out.println("==================Fortessa===================");
        //exhaustiveTests(caseStudySpectralMap, harvardFortessa, "EX_HarvFort");
        //System.out.println("==================Sony=======================");
        //exhaustiveTests(caseStudySpectralMap, harvardSony, "EX_HarvSony");
        //System.out.println("==================Macsquant==================");
        //exhaustiveTests(caseStudySpectralMap, harvardMacsquant, "EX_HarvMacs");


    }

    private static void stochasticTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException {
        stochasticTest(1, spectralMaps, cytometer, prefix);
        stochasticTest(2, spectralMaps, cytometer, prefix);
        stochasticTest(3, spectralMaps, cytometer, prefix);
        stochasticTest(4, spectralMaps, cytometer, prefix);
        stochasticTest(5, spectralMaps, cytometer, prefix);
        if (!prefix.equals("HarvSony")) {
            stochasticTest(6, spectralMaps, cytometer, prefix);
        }
        //stochasticTest(7,spectralMaps,cytometer,prefix);
        //stochasticTest(8,spectralMaps,cytometer,prefix);
    }

    private static void stochasticTest(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException {

        List<Map.Entry<List<SelectionInfo>, SNR>> uniqueResults = new ArrayList<>();
        Set<String> uniquelines = new HashSet<>();
        List<String> salines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            salines.add(line);
            if (!uniquelines.contains(line)) {
                uniquelines.add(line);
                uniqueResults.add(new AbstractMap.SimpleEntry<>(selection, snr));
            }
        }
        Utilities.writeToFile(basefp + "SA_" + prefix + "_" + n + "fp.csv", salines);

        List<String> hclines = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = HillClimbingSelection.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            hclines.add(line);
            if (!uniquelines.contains(line)) {
                uniquelines.add(line);
                uniqueResults.add(new AbstractMap.SimpleEntry<>(selection, snr));
            }
        }
        Utilities.writeToFile(basefp + "HC_" + prefix + "_" + n + "fp.csv", hclines);

        List<String> rwlines = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = RandomWalk.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            rwlines.add(line);
            if (!uniquelines.contains(line)) {
                uniquelines.add(line);
                uniqueResults.add(new AbstractMap.SimpleEntry<>(selection, snr));
            }
        }
        Utilities.writeToFile(basefp + "RW_" + prefix + "_" + n + "fp.csv", rwlines);

        Collections.sort(uniqueResults, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
            SNR s1 = o1.getValue();
            SNR s2 = o2.getValue();
            return s1.compare(s2);
        });
        Collections.reverse(uniqueResults);

        List<String> lines = new ArrayList<>();
        List<String> top = new ArrayList<>();
        List<String> bottom = new ArrayList<>();

        String line = "";
        for (int i = 0; i < n - 1; i++) {
            line += "FP,Laser,Detector,Signal,Noise,";

        }

        line += "FP,Laser,Detector,Signal,Noise";
        lines.add(line);
        top.add(line);
        bottom.add(line);

        for (Map.Entry<List<SelectionInfo>, SNR> entry : uniqueResults) {
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            lines.add(line);
        }

        /*
        for (int i = 0; i < 10; i++) {
            Map.Entry<List<SelectionInfo>, SNR> entry = uniqueResults.get(i);
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            top.add(line);
        }

        for (int i = uniqueResults.size() - 1; i >= uniqueResults.size() - 10; i--) {
            Map.Entry<List<SelectionInfo>, SNR> entry = uniqueResults.get(i);
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            bottom.add(line);
        }
        */
        Utilities.writeToFile(basefp + "Stoch_" + prefix + "_" + n + "fp.csv", lines);
        //Utilities.writeToFile(basefp + "Stoch_" + prefix + "_" + n + "fp_top10.csv", top);
        //Utilities.writeToFile(basefp + "Stoch_" + prefix + "_" + n + "fp_bottom10.csv", bottom);


    }

    private static void exhaustiveTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException, InterruptedException {
        long current = 0;

        for (int i = 2; i <= 5; i++) {
            System.out.println("Starting Exhaustive with n = " + i);
            current = System.currentTimeMillis();
            exhaustivePlots(i, spectralMaps, cytometer, prefix);
            //exhaustiveCount(i,spectralMaps,cytometer,prefix);
            System.out.println("Time taken = " + ((System.currentTimeMillis() - current)) + " milliseconds.");
            System.out.println("------------------------------");
        }

    }


    private static void getSignalPlots(List<SelectionInfo> selection, String prefix) {
        String scriptsfp = basefp + "figure1" + Utilities.getSeparater();

        SelectionInfo selection0 = selection.get(0);
        SelectionInfo selection1 = selection.get(1);

        Fluorophore fp0 = selection0.getSelectedFluorophore();
        Detector d0 = selection0.getSelectedDetector();
        Laser l0 = selection0.getSelectedLaser();

        Fluorophore fp1 = selection1.getSelectedFluorophore();
        Detector d1 = selection1.getSelectedDetector();
        Laser l1 = selection1.getSelectedLaser();

        //First plot
        double multfp0l0 = getMultiplier(fp0, l0);
        double multfp1l0 = getMultiplier(fp1, l0);

        Map<Double, Double> sig0 = getEM(fp0.EMspectrum, multfp0l0);
        Map<Double, Double> noi0 = getEM(fp1.EMspectrum, multfp1l0);
        List<String> script0 = getFigure1PlotScript(sig0, noi0, d0, prefix + "plot0");

        Utilities.writeToFile(scriptsfp + prefix + "plot0.py", script0);

        //Second Plot
        double multfp0l1 = getMultiplier(fp0, l1);
        double multfp1l1 = getMultiplier(fp1, l1);

        Map<Double, Double> sig1 = getEM(fp1.EMspectrum, multfp1l1);
        Map<Double, Double> noi1 = getEM(fp0.EMspectrum, multfp0l1);
        List<String> script1 = getFigure1PlotScript(sig1, noi1, d1, prefix + "plot1");

        Utilities.writeToFile(scriptsfp + prefix + "plot1.py", script1);


    }

    private static Map<Double, Double> getEM(Map<Double, Double> em, double mult) {
        Map<Double, Double> adjustedEm = new HashMap<>();
        for (Double wl : em.keySet()) {
            double adjem = em.get(wl) * mult;
            adjustedEm.put(wl, adjem);
        }

        return adjustedEm;
    }

    private static double getMultiplier(Fluorophore fp, Laser laser) {
        if (!fp.EXspectrum.containsKey((double) laser.wavelength)) {
            return 0;
        }

        double multiplier = (fp.EXspectrum.get((double) laser.wavelength) / 100); //This is where laser power and brightness go
        if (fp.getBrightnessNormalizedTo() != null) {
            multiplier = (fp.EXspectrum.get((double) laser.wavelength) / 100) * fp.getBrightness(); //This is where laser power and brightness go
        }

        return multiplier;
    }


    private static Map<String, Map<String, Double>> getLims() {
        Map<String, Map<String, Double>> lims = new HashMap<>();
        Map<String, Double> mScarlet = new HashMap<>();
        mScarlet.put("exl", 384.0);
        mScarlet.put("exh", 644.0);
        mScarlet.put("eml", 538.0);
        mScarlet.put("emh", 773.0);

        Map<String, Double> mCherry = new HashMap<>();
        mCherry.put("exl", 300.0);
        mCherry.put("exh", 650.0);
        mCherry.put("eml", 550.0);
        mCherry.put("emh", 800.0);

        Map<String, Double> mOrange = new HashMap<>();
        mOrange.put("exl", 300.0);
        mOrange.put("exh", 600.0);
        mOrange.put("eml", 530.0);
        mOrange.put("emh", 700.0);

        Map<String, Double> dsRed2 = new HashMap<>();
        dsRed2.put("exl", 351.0);
        dsRed2.put("exh", 596.0);
        dsRed2.put("eml", 562.0);
        dsRed2.put("emh", 712.0);

        Map<String, Double> irfp713 = new HashMap<>();
        irfp713.put("exl", 535.0);
        irfp713.put("exh", 726.0);
        irfp713.put("eml", 650.0);
        irfp713.put("emh", 800.0);

        Map<String, Double> sirius = new HashMap<>();
        sirius.put("exl", 304.0);
        sirius.put("exh", 422.0);
        sirius.put("eml", 381.0);
        sirius.put("emh", 551.0);

        Map<String, Double> cerulean = new HashMap<>();
        cerulean.put("exl", 300.0);
        cerulean.put("exh", 700.0);
        cerulean.put("eml", 433.0);
        cerulean.put("emh", 700.0);

        Map<String, Double> ko = new HashMap<>();
        ko.put("exl", 300.0);
        ko.put("exh", 570.0);
        ko.put("eml", 520.0);
        ko.put("emh", 700.0);

        Map<String, Double> mPlum = new HashMap<>();
        mPlum.put("exl", 450.0);
        mPlum.put("exh", 630.0);
        mPlum.put("eml", 584.0);
        mPlum.put("emh", 800.0);

        Map<String, Double> tagRFP = new HashMap<>();
        tagRFP.put("exl", 320.0);
        tagRFP.put("exh", 600.0);
        tagRFP.put("eml", 535.0);
        tagRFP.put("emh", 750.0);

        Map<String, Double> tdTomato = new HashMap<>();
        tdTomato.put("exl", 300.0);
        tdTomato.put("exh", 630.0);
        tdTomato.put("eml", 550.0);
        tdTomato.put("emh", 700.0);

        Map<String, Double> irfp720 = new HashMap<>();
        irfp720.put("exl", 535.0);
        irfp720.put("exh", 729.0);
        irfp720.put("eml", 650.0);
        irfp720.put("emh", 800.0);

        lims.put("mScarlet", mScarlet);
        lims.put("mCherry", mCherry);
        lims.put("mOrange", mOrange);
        lims.put("DsRed2", dsRed2);
        lims.put("iRFP713", irfp713);
        lims.put("Sirius", sirius);
        lims.put("Cerulean", cerulean);
        lims.put("KO", ko);
        lims.put("mPlum", mPlum);
        lims.put("TagRFP", tagRFP);
        lims.put("tdTomato", tdTomato);
        lims.put("iRFP720", irfp720);

        return lims;


    }


    private static boolean weHaveData(List<SelectionInfo> selection) {

        Map<String, Map<String, Double>> lims = getLims();

        for (SelectionInfo info : selection) {
            int laserwl = info.getSelectedLaser().wavelength;
            double detectorl = info.getSelectedDetector().filterMidpoint - (info.getSelectedDetector().filterWidth / 2.0);
            double detectorh = info.getSelectedDetector().filterMidpoint + (info.getSelectedDetector().filterWidth / 2.0);


            for (SelectionInfo fpinfo : selection) {
                String fp = fpinfo.getSelectedFluorophore().name;
                if ((laserwl < lims.get(fp).get("exl")) || (laserwl > lims.get(fp).get("exh"))) {
                    return false;
                }

                if (detectorl < lims.get(fp).get("eml")) {
                    return false;
                }
                if (detectorh > lims.get(fp).get("emh")) {
                    return false;
                }
            }
        }


        return true;
    }


    private static boolean weHaveSignalData(List<SelectionInfo> selection) {

        Map<String, Map<String, Double>> lims = getLims();

        for (SelectionInfo info : selection) {
            int laserwl = info.getSelectedLaser().wavelength;
            double detectorl = info.getSelectedDetector().filterMidpoint - (info.getSelectedDetector().filterWidth / 2.0);
            double detectorh = info.getSelectedDetector().filterMidpoint + (info.getSelectedDetector().filterWidth / 2.0);

            String fp = info.getSelectedFluorophore().name;
            if ((laserwl < lims.get(fp).get("exl")) || (laserwl > lims.get(fp).get("exh"))) {
                return false;
            }

            if (detectorl < lims.get(fp).get("eml")) {
                return false;
            }
            if (detectorh > lims.get(fp).get("emh")) {
                return false;
            }


        }


        return true;
    }


    private static void exhaustiveCount(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException, InterruptedException {

        int numFluorophores = spectralMaps.size();

        //count filters
        int numFilters = 0;
        for (Laser laser : cytometer.lasers) {
            numFilters += laser.detectors.size();
        }

        //fluorophore index --> fluorophore object
        Fluorophore[] fluorophores = new Fluorophore[numFluorophores];
        int fpi = 0;
        for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
            Fluorophore fluorophore = entry.getValue();
            fluorophores[fpi] = fluorophore;
            fpi++;
        }

        Laser[] lasers = new Laser[numFilters];
        Detector[] detectors = new Detector[numFilters];
        int filterIndex = 0;
        for (Laser laser : cytometer.lasers) {
            for (Detector detector : laser.detectors) {
                lasers[filterIndex] = laser;
                detectors[filterIndex] = detector;
                filterIndex++;
            }
        }

        //get all combinations of filters (order not important)
        filterCombinations = new LinkedList<>();
        int tempData[] = new int[n];
        getCombinations(tempData, 0, numFilters - 1, 0, n);

        //get all permutations of fluorophores to match to filters (order is important)
        fluorophorePermutations = new LinkedList<>();
        tempData = new int[n];
        getPermutations(tempData, numFluorophores, n);

        long totalComputations = filterCombinations.size() * fluorophorePermutations.size();
        System.out.println("Filter Combinations :: " + filterCombinations.size());
        System.out.println("FP Permutations     :: " + fluorophorePermutations.size());
        System.out.println("Total Computations : " + totalComputations);

        int count = 0;
        for (int[] filterCombo : filterCombinations) {
            for (int[] fluorophorePerm : fluorophorePermutations) {
                List<SelectionInfo> currentSelection = ExhaustiveSelection.getSelection(n, fluorophorePerm, filterCombo, fluorophores, lasers, detectors);
                //if(weHaveData(currentSelection)){
                if (weHaveSignalData(currentSelection)) {
                    count++;
                } else {
                    continue;
                }
            }
        }

        System.out.println("Number of solutions for " + n + " fp are: " + count);


    }


    private static void exhaustivePlots(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) throws IOException, InterruptedException {

        //String exhaustivefp = basefp + "exhaustivePlots" + Utilities.getSeparater();
        //Utilities.makeDirectory(exhaustivefp);
        //String fp = exhaustivefp + n + Utilities.getSeparater();
        //Utilities.makeDirectory(fp);
        int numFluorophores = spectralMaps.size();

        //count filters
        int numFilters = 0;
        for (Laser laser : cytometer.lasers) {
            numFilters += laser.detectors.size();
        }

        //fluorophore index --> fluorophore object
        Fluorophore[] fluorophores = new Fluorophore[numFluorophores];
        int fpi = 0;
        for (Map.Entry<String, Fluorophore> entry : spectralMaps.entrySet()) {
            Fluorophore fluorophore = entry.getValue();
            fluorophores[fpi] = fluorophore;
            fpi++;
        }

        Laser[] lasers = new Laser[numFilters];
        Detector[] detectors = new Detector[numFilters];
        int filterIndex = 0;
        for (Laser laser : cytometer.lasers) {
            for (Detector detector : laser.detectors) {
                lasers[filterIndex] = laser;
                detectors[filterIndex] = detector;
                filterIndex++;
            }
        }

        //get all combinations of filters (order not important)
        filterCombinations = new LinkedList<>();
        int tempData[] = new int[n];
        getCombinations(tempData, 0, numFilters - 1, 0, n);

        //get all permutations of fluorophores to match to filters (order is important)
        fluorophorePermutations = new LinkedList<>();
        tempData = new int[n];
        getPermutations(tempData, numFluorophores, n);

        long totalComputations = filterCombinations.size() * fluorophorePermutations.size();
        System.out.println("Filter Combinations :: " + filterCombinations.size());
        System.out.println("FP Permutations     :: " + fluorophorePermutations.size());
        System.out.println("Total Computations : " + totalComputations);


        SNR smallest = null;
        List<Map.Entry<List<SelectionInfo>, SNR>> results = new ArrayList<>();
        int count = 0;
        for (int[] filterCombo : filterCombinations) {
            for (int[] fluorophorePerm : fluorophorePermutations) {
                List<SelectionInfo> currentSelection = ExhaustiveSelection.getSelection(n, fluorophorePerm, filterCombo, fluorophores, lasers, detectors);

                //if(weHaveData(currentSelection)){
                if (weHaveSignalData(currentSelection)) {
                    count++;
                } else {
                    continue;
                }

                SNR snr = new SNR(currentSelection);

                if (results.size() < (exhaustiveLim - 1)) {
                    results.add(new AbstractMap.SimpleEntry<>(currentSelection, snr));
                } else if (results.size() == (exhaustiveLim - 1)) {
                    results.add(new AbstractMap.SimpleEntry<>(currentSelection, snr));
                    Collections.sort(results, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
                        SNR s1 = o1.getValue();
                        SNR s2 = o2.getValue();
                        return s1.compare(s2);
                    });
                    Collections.reverse(results);
                } else {

                    if (snr.compare(results.get(exhaustiveLim - 1).getValue()) > 0) {
                        //If SNR is "Better"
                        int replaceAt = 0;
                        for (int i = 0; i < results.size(); i++) {
                            if (snr.compare(results.get(i).getValue()) > 0) {
                                replaceAt = i;
                                break;
                            } else {

                            }
                        }
                        results.add(replaceAt, new AbstractMap.SimpleEntry<>(currentSelection, snr));
                        results.remove(exhaustiveLim);
                    } else {
                        //Don't do anything right?
                    }
                }
                //ProteinSelector.generateNoise(currentSelection);
                //JavaPlot plot = ProteinSelector.getJavaPlot(currentSelection);
                //Utilities.plotToFile(plot, fp + count + ".png");
                //count++;
            }
        }

        Collections.sort(results, (Map.Entry<List<SelectionInfo>, SNR> o1, Map.Entry<List<SelectionInfo>, SNR> o2) -> {
            SNR s1 = o1.getValue();
            SNR s2 = o2.getValue();
            return s1.compare(s2);
        });
        Collections.reverse(results);

        System.out.println("Number of solutions for " + n + " fp are: " + count);

        //flattenImages(fp);
        List<String> lines = new ArrayList<>();
        //List<String> top = new ArrayList<>();
        //List<String> bottom = new ArrayList<>();

        String line = "";
        for (int i = 0; i < n - 1; i++) {
            line += "FP,Laser,Detector,Signal,Noise,";

        }

        line += "FP,Laser,Detector,Signal,Noise";
        lines.add(line);
        //top.add(line);
        //bottom.add(line);

        for (Map.Entry<List<SelectionInfo>, SNR> entry : results) {
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            lines.add(line);
        }

        /*
        for (int i = 0; i < 10; i++) {
            Map.Entry<List<SelectionInfo>, SNR> entry = results.get(i);
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            top.add(line);
        }

        for (int i = results.size() - 1; i >= results.size() - 10; i--) {
            Map.Entry<List<SelectionInfo>, SNR> entry = results.get(i);
            List<SelectionInfo> selection = entry.getKey();
            SNR snr = entry.getValue();
            line = toString(n, selection, snr);
            bottom.add(line);
        }*/

        Utilities.writeToFile(basefp + setfp + Utilities.getSeparater() + "fprun" + Utilities.getSeparater() + prefix + "_" + n + "fp.csv", lines);


        //Utilities.writeToFile(basefp + prefix + "_" + n + "fp_top10.csv", top);
        //Utilities.writeToFile(basefp + prefix + "_" + n + "fp_bottom10.csv", bottom);

    }

    private static String toString(int n, List<SelectionInfo> selection, SNR snr) {
        Map<String, Integer> maps = new HashMap<>();

        for (int i = 0; i < n; i++) {
            maps.put(selection.get(i).getSelectedFluorophore().name, i);
        }
        List<String> keys = new ArrayList<>(maps.keySet());

        String line = "";
        for (int i = 0; i < n - 1; i++) {
            String key = keys.get(i);
            line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
            line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue() + ",";
            //            "FP,Laser,Detector,Signal,Noise,";
        }
        String key = keys.get(n - 1);
        line += selection.get(maps.get(key)).getSelectedFluorophore().name + "," + selection.get(maps.get(key)).getSelectedLaser().getName() + "," + selection.get(maps.get(key)).getSelectedDetector().identifier + ",";
        line += snr.getSignalNoiseList().get(maps.get(key)).getKey() + "," + snr.getSignalNoiseList().get(maps.get(key)).getValue();
        return line;
    }

    private static void flattenImages(String fp) throws IOException, InterruptedException {
        File[] files = (new File(fp)).listFiles();
        for (File f : files) {
            String fname = f.getName();

            StringBuilder commandBuilder = null;
            commandBuilder = new StringBuilder("/usr/bin/convert " + f.getAbsolutePath() + " -background white -alpha remove " + fp + fname + "Flat.png");

            String command = commandBuilder.toString();
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
            proc.waitFor();
            InputStream is = proc.getInputStream();
            InputStream es = proc.getErrorStream();
            OutputStream os = proc.getOutputStream();
            is.close();
            es.close();
            os.close();
        }
        files = (new File(fp)).listFiles();
        for (File f : files) {
            if (!f.getName().endsWith("Flat.png")) {
                f.deleteOnExit();
            }

        }

    }

    public static void getCombinations(int data[], int start, int n, int index, int k) {
        if (index == k) {
            filterCombinations.add(data.clone());
            return;
        }
        for (int i = start; i <= n && n - i + 1 >= k - index; i++) {
            data[index] = i;
            getCombinations(data, i + 1, n, index + 1, k);
        }
    }

    public static void getPermutations(int data[], int n, int k) {
        if (k == 0) {
            fluorophorePermutations.add(data.clone());
            return;
        }
        outerloop:
        for (int i = 0; i < n; ++i) {
            for (int j = data.length - 1; j >= k; j--) {
                if (data[j] == i) {
                    continue outerloop;
                }
            }
            data[k - 1] = i;
            getPermutations(data, n, k - 1);
        }
    }

    public static void simAnnealingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        testSimAnnealing(1, spectralMaps, cytometer, prefix);
        testSimAnnealing(2, spectralMaps, cytometer, prefix);
        testSimAnnealing(3, spectralMaps, cytometer, prefix);
    }

    public static void testSimAnnealing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        String exhaustivefp = basefp + prefix + "_" + n + "fp.csv";
        List<String> exhaustive = Utilities.getFileContentAsStringList(exhaustivefp);

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            line += ("," + exhaustive.indexOf(line));
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "SA_" + prefix + "_" + n + "fp.csv", lines);

    }

    public static void hillClimbingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        testHillClimbing(1, spectralMaps, cytometer, prefix);
        testHillClimbing(2, spectralMaps, cytometer, prefix);
        testHillClimbing(3, spectralMaps, cytometer, prefix);
    }

    public static void testHillClimbing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        String exhaustivefp = basefp + prefix + "_" + n + "fp.csv";
        List<String> exhaustive = Utilities.getFileContentAsStringList(exhaustivefp);

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            line += ("," + exhaustive.indexOf(line));
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "HC_" + prefix + "_" + n + "fp.csv", lines);

    }

    public static void higherSimAnnealingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        testHigherSimAnnealing(4, spectralMaps, cytometer, prefix);
        testHigherSimAnnealing(5, spectralMaps, cytometer, prefix);
        testHigherSimAnnealing(6, spectralMaps, cytometer, prefix);
        testHigherSimAnnealing(7, spectralMaps, cytometer, prefix);
        testHigherSimAnnealing(8, spectralMaps, cytometer, prefix);
    }

    public static void testHigherSimAnnealing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = SimulatedAnnealing.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "SA_" + prefix + "_" + n + "fp.csv", lines);

    }

    public static void higherHillClimbingTests(Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {
        testHigherHillClimbing(4, spectralMaps, cytometer, prefix);
        testHigherHillClimbing(5, spectralMaps, cytometer, prefix);
        testHigherHillClimbing(6, spectralMaps, cytometer, prefix);
        testHigherHillClimbing(7, spectralMaps, cytometer, prefix);
        testHigherHillClimbing(8, spectralMaps, cytometer, prefix);
    }

    public static void testHigherHillClimbing(int n, Map<String, Fluorophore> spectralMaps, Cytometer cytometer, String prefix) {

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<SelectionInfo> selection = HillClimbingSelection.run(n, spectralMaps, cytometer);
            SNR snr = new SNR(selection);
            String line = toString(n, selection, snr);
            lines.add(line);
        }
        Utilities.writeToFile(basefp + "HC_" + prefix + "_" + n + "fp.csv", lines);

    }

}
