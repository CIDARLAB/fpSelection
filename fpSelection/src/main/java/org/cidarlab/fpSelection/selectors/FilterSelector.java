/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import org.cidarlab.fpSelection.parsers.fpFortessaParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import static org.cidarlab.fpSelection.selectors.LaserSelector.FilterFPtoLasers;
import static org.cidarlab.fpSelection.parsers.fpSpectraParse.parse;

/**
 *
 * @author david
 */
public class FilterSelector {

    //took all filter widths from guides at bdbiosciences, left out ones that seemed niche like 28 nm wide, 44 nm wide, etc
    //https://www.bdbiosciences.com/documents/BD_Accuri_Optical_Filter_Guide.pdf
    //https://www.bdbiosciences.com/documents/multicolor_fluorochrome_laser_chart.pdf
    //:D
    private static int[] filterWidths = {15, 20, 25, 30, 40, 45, 50};

    public static void main(String[] args) throws IOException {
        File input = new File("src/main/resources/fp_spectra.csv");
        HashMap<String, Fluorophore> spectralMaps = parse(input);

//        File input = new File("src/main/resources/Fluorophores.org/");
//        HashMap<String, Fluorophore> spectralMaps = generateFPs(input);
        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parse(cyto);

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Give an integer n for the number of you would like: ");
//        int n = scanner.nextInt();
        ArrayList<SelectionInfo> pls = LFPtoFilter(spectralMaps, testCyto.lasers, 7);

        ProteinSelector.calcSumSigNoise(pls);
        ProteinSelector.generateNoise(pls);
        ProteinSelector.plotSelection(pls);

    }

    public static ArrayList<SelectionInfo> LFPtoFilter(HashMap<String, Fluorophore> FPList, List<Laser> lasers, int nDetectors) {
        ArrayList<SelectionInfo> skeleton = new ArrayList();

        //At least the 30% expressed fps pls
        double threshold = .1;

        //Generate skeleton of returnList.
        for (Laser each : lasers) {
            SelectionInfo newInfo = new SelectionInfo();
            newInfo.selectedLaser = each;
            newInfo.noise = new TreeMap<>();
            newInfo.rankedFluorophores = new ArrayList();
            newInfo.selectedIndex = 0;

            for (Fluorophore fp : FPList.values()) {
                //if an fp gets excited at all, let's add it to the list for now.
                if (fp.EXspectrum.containsKey((double) each.wavelength)) {
                    if (fp.EXspectrum.get((double) each.wavelength) > threshold) {
                        newInfo.rankedFluorophores.add(fp);
                    }
                }
            }
            skeleton.add(newInfo);
        }
        //
        //Find EM peaks of each protein
        //Fit a 60 wide filter on each one
        ArrayList<SelectionInfo> individuals = new ArrayList<>();

        for (SelectionInfo each : skeleton) {
            for (Fluorophore fp : each.rankedFluorophores) {
                SelectionInfo fpSpecific = new SelectionInfo();
                fpSpecific.selectedLaser = each.selectedLaser;
                fpSpecific.rankedFluorophores = new ArrayList<>();
                fpSpecific.rankedFluorophores.add(fp);
                fpSpecific.selectedIndex = 0;

                Detector dumDetect = new Detector();
                dumDetect.filterWidth = 60;
                dumDetect.filterMidpoint = (int) fp.EMPeak();

                fpSpecific.selectedDetector = dumDetect;
                fpSpecific.score = fp.express(fpSpecific.selectedLaser, fpSpecific.selectedDetector);
                individuals.add(fpSpecific);
            }
        }

        ArrayList<SelectionInfo> goodbye = new ArrayList<>();

        //If filter widths overlap, cull the underperformers.
        //
        for (SelectionInfo each : individuals) {
            for (SelectionInfo eachOther : individuals) {
                //If we're looking at the same thing or filters that aren't on the same laser, skip
                if (each == eachOther || each.selectedLaser != eachOther.selectedLaser) {
                    continue;
                } else {
                    checkOverlap(each, eachOther, goodbye);
                }
            }
        }

        //the dearly departed
        for (SelectionInfo dying : goodbye) {
            individuals.remove(dying);
        }
        //Narrow the filter widths and check effect on individual SNR
        ArrayList<SelectionInfo> returnList = new ArrayList();
        for (SelectionInfo each : individuals) {
            returnList.add(each);
        }

        while (returnList.size() > nDetectors) {
            double sumSNR = ProteinSelector.calcSumSigNoise(individuals);
            double newSNR;
            SelectionInfo bestRemove = individuals.get(0);
            for (SelectionInfo iter : returnList) {
                individuals.remove(iter);

                newSNR = ProteinSelector.calcSumSigNoise(individuals);
                iter.score = newSNR - sumSNR;

                if (iter.score > bestRemove.score) {
                    bestRemove = iter;
                }

                individuals.add(iter);

            }

            returnList.remove(bestRemove);
            individuals.remove(bestRemove);
        }

        for (SelectionInfo reduce : returnList) {
            Detector narrow = reduce.selectedDetector;
            int bestWidth = narrow.filterWidth;
            double SNR = ProteinSelector.calcSumSigNoise(returnList);
            double newSNR;
            for (int i = filterWidths.length - 1; i >= 0; i--) {
                narrow.filterWidth = filterWidths[i];
                newSNR = ProteinSelector.calcSumSigNoise(returnList);
                if (newSNR > SNR) {
                    bestWidth = narrow.filterWidth;
                    SNR = newSNR;
                }
            }
            narrow.filterWidth = bestWidth;

            System.out.println(reduce.selectedLaser.name + " Detector: " + narrow.filterMidpoint + "/" + narrow.filterWidth + " or similar");

        }

        return returnList;
    }

    static void checkOverlap(SelectionInfo each, SelectionInfo eachOther, ArrayList<SelectionInfo> goodbye) {

        Detector first = each.selectedDetector;
        Detector second = eachOther.selectedDetector;

        double firstMin = first.filterMidpoint - first.filterWidth / 2;
        double secondMin = second.filterMidpoint - second.filterWidth / 2;

        //if they are partially overlapping or inside of eachother
        if ((firstMin < (secondMin + second.filterWidth) && firstMin > secondMin)) {

            //cull the lesser scoring fp
            if (each.score < eachOther.score) {
                if (!goodbye.contains(each)) {
                    goodbye.add(each);
                }
            } else if (!goodbye.contains(eachOther)) {
                goodbye.add(eachOther);
            }

        } else if ((secondMin < (firstMin + first.filterWidth) && secondMin > firstMin)) {
            //cull the lesser scoring fp
            if (each.score < eachOther.score) {
                if (!goodbye.contains(each)) {
                    goodbye.add(each);
                }
            } else if (!goodbye.contains(eachOther)) {
                goodbye.add(eachOther);
            }

        } else if ((firstMin > secondMin && (firstMin + first.filterWidth) < (secondMin + second.filterWidth))) {

            //cull the lesser scoring fp
            if (each.score < eachOther.score) {
                if (!goodbye.contains(each)) {
                    goodbye.add(each);
                }
            } else if (!goodbye.contains(eachOther)) {
                goodbye.add(eachOther);
            }

        } else if ((secondMin > firstMin && (secondMin + second.filterWidth) < (firstMin + first.filterWidth))) {

            //cull the lesser scoring fp
            if (each.score < eachOther.score) {
                if (!goodbye.contains(each)) {
                    goodbye.add(each);
                }
            } else if (!goodbye.contains(eachOther)) {
                goodbye.add(eachOther);
            }
        }
    }

}
