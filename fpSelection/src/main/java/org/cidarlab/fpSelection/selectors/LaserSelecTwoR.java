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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.cidarlab.fpSelection.adaptors.ScrapedCSVParse;
import org.cidarlab.fpSelection.adaptors.fpFortessaParse;
import org.cidarlab.fpSelection.adaptors.fpSpectraParse;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.EXPeakComparator;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;
import org.cidarlab.fpSelection.dom.ListHelper;
import org.cidarlab.fpSelection.dom.SelectionInfo;
import static org.cidarlab.fpSelection.selectors.LaserSelector.FilterFPtoLasers;

/**
 *
 * @author David
 */
public class LaserSelecTwoR {

    
     public static void main(String[] args) throws IOException {
        File input = new File("src/main/resources/Fluorophores.org/");
        HashMap<String, Fluorophore> spectralMaps = ScrapedCSVParse.parse(input);

//        File input = new File("src/main/resources/fp_spectra.csv");
//        HashMap<String, Fluorophore> spectralMaps = fpSpectraParse.parse(input);

        HashMap<String, Fluorophore> choose = new HashMap<>();
        Random next = new Random();

        File cyto = new File("src/main/resources/ex_fortessa.csv");
        Cytometer testCyto = fpFortessaParse.parse(cyto);

        ArrayList<Detector> detect = new ArrayList();

        for (int i = 0; i < 5; i++) {
            Laser get = testCyto.lasers.get(next.nextInt(testCyto.lasers.size()));
            detect.add(get.detectors.get(next.nextInt(get.detectors.size())));
        }

        ArrayList<SelectionInfo> pls = FilterFPtoLasers(spectralMaps, detect, 3);

        ProteinSelector.calcSumSigNoise(pls);
        ProteinSelector.generateNoise(pls);
        ProteinSelector.plotSelection(pls);

    }
    ///////////////////////////////////
    ///         OLD PROCESS         ///
    ///////////////////////////////////
    /*
        1. Group the proteins by adding them to n lists for equally spaced n lasers.
        2. Separate each laser's list into wide generic emission ranges, skip empty emission ranges.
        3. Fit each given detector to the most responsive ranges.
                i.e.: Between two 400-500 nm spaces on different lasers, FPs in one respond more than the other. You fit the detector onto that laser's range.
        4. Cull the herd. Any ranges that still don't have a proper detector are deleted.
        5. For each laser, scan over the space given (defined by generalSpace) to find the laser wavelength that produces the most response from all proteins that were placed in that range.
        6. Sort the protein lists based on expression and leakage in it's laser detector combo.
    
     */
    //////////////////////////////////////////
    ///         BETTER OLD PROCESS         ///
    //////////////////////////////////////////
    /*
        1. Organize proteins by duplicating them into n lists for n equally spaced lasers.
        2. Separate each laser's list into wide generic emission ranges, skip empty emission ranges.
        3. Fit each given detector to the most responsive ranges.
                i.e.: Between two 400-500 nm spaces on different lasers, FPs in one respond more than the other. You fit the detector onto that laser's range.
        4. Any laser with overlapping filters is duplicated, and filters are distributed between duplicates until there are no filter overlaps on any given laser.
        5. Any ranges that still don't have a proper detector are deleted.
        6. For each laser, scan over the space given (defined by generalSpace) to find the laser wavelength that produces the most response from all proteins that were placed in that laser.
        7. Sort the protein lists based on expression and leakage in it's laser detector combo.
    
     */
    ///////////////////////////////////
    ///         NEW PROCESS         ///
    ///////////////////////////////////
    /*
        -possible preprocess-> Trim list of FPs based on emission into filters. IF below threshold for all filters, remove from list.
    
        1. Sort FPs based on excitation peak.
        2. Split sorted list into n sections based on standard deviations, create a laser with wavelength equal to the mean excitation peak of each section.
        3. For each filter provided, fit it to the laser with the most responsive fps in that region.
        4. Run an fp selection algorithm on the setup to optimize it.
     */
    public static ArrayList<SelectionInfo> FilterFPtoLasers(HashMap<String, Fluorophore> fpList, List<Detector> selectedDetectors, int nLasers) {

        ArrayList<SelectionInfo> generalList = new ArrayList<>();

        /////////////////////////////////////////////////////
        // 1.   Sort FPs based on excitation peak.
        ArrayList<Fluorophore> sortFPs = new ArrayList<>();
        for (Fluorophore each : fpList.values()) {
            sortFPs.add(each);
        }
        sortFPs.sort(new EXPeakComparator());

        //////////////////////////////////////////////////////
        // 2.   Split sorted list based on standard deviations into n lasers.
        ListHelper groups = new ListHelper(sortFPs, nLasers);

        double avgStdDev = 0;
        double nextAvgDev;

        nextAvgDev = calcAvgStdDeviation(groups);
        do {
            
            groups.debugGrouping();
            avgStdDev = nextAvgDev;
            
            double testUp = avgStdDev, testDown = avgStdDev;
            //start at i = 1 because the marker at 0 shouldn't move.
            for (int i = 1; i < groups.markers.size(); i++) {
                int modOG = groups.markers.get(i);
                int modUp = modOG + 1;
                int modDown = modOG - 1;
                
                //Increment a marker, make sure markers don't go outside of bound
                if (modUp < groups.list.size()) {
                    //if it's the last marker, test it
                    if (i == groups.markers.size()-1) {
                        groups.markers.set(i,modUp);
                        testUp = calcAvgStdDeviation(groups);
                    }
                    else
                    {
                        //if it's not the last marker, make sure it doesn't reach into the next group.
                        if(modUp < groups.markers.get(i+1))
                        {
                            groups.markers.set(i, modUp);
                            testUp = calcAvgStdDeviation(groups);
                        }
                    }
                }
                
                //Decrement a marker, make sure markers don't go outside of bound
                if(modDown >= 0)
                {
                    //if it's the first marker, test it
                    if(i == 0)
                    {
                        groups.markers.set(i, modDown);
                        testDown = calcAvgStdDeviation(groups);
                    }
                    else
                    {
                        //if not, then just make sure it doesn't reach into the previous group
                        if(modDown > groups.markers.get(i-1))
                        {
                            groups.markers.set(i, modDown);
                            testDown = calcAvgStdDeviation(groups);
                        }
                    
                    }
                }
                //Compare the smaller avg std dev with the current avg std dev.
                int consider;
                double considerDev;
                if(testUp < testDown)
                {
                    consider = modUp;
                    considerDev = testUp;
                }
                else
                {
                    consider = modDown;
                    considerDev = testDown;
                }
                //set it only if it's smaller.
                if(considerDev < nextAvgDev)
                {
                    nextAvgDev = considerDev;
                    groups.markers.set(i, consider);
                }
                else
                {
                    //otherwise reset and move on.
                    groups.markers.set(i, modOG);
                }
            }

            //keep going until you go through all ups and downs and your std deviation only increases.
        } while (nextAvgDev < avgStdDev);

        ArrayList<SelectionInfo> selections = new ArrayList<>();
        //Create lasers based on avg wavelengths of each group.
        for(int i = 0; i < nLasers; i++)
        {
            SelectionInfo info = new SelectionInfo();
            info.selectedLaser = new Laser();
            info.selectedLaser.detectors = new LinkedList<>();
            info.selectedLaser.wavelength = (int) ((double)groups.avgWavelengths.get(i));
            info.rankedFluorophores = groups.groupToList(i);
            
            selections.add(info);
            
        }
        
        /////////////////////////////////
        // 3.   Fit the filters to the laser + fp groups that are most responsive in filter region

        
        for(Detector each : selectedDetectors)
        {
            SelectionInfo bestResponse = new SelectionInfo();
            
            //make a test laser function
            
            //save the laser with fps that respond the best in that filter.
            
            bestResponse.selectedLaser.detectors.add(each);
        }
        
        
        
        /////////////////////////////////
        // 4.   Run Hill Climber or BW Search on the result set to optimize it.
        
        

        return generalList;
    }

    private static double calcAvgStdDeviation(ListHelper groups) {
        double avgStdDev = 0;

        //for each group created, calculate std dev
        for (int i = 0; i < groups.markers.size(); i++) {
            double mean = 0;
            double variance = 0;
            int groupSize;
            
            if(i != groups.markers.size() - 1)
            {
                groupSize = groups.markers.get(i+1) - groups.markers.get(i);
            }
            else
            {
                groupSize = groups.list.size() - groups.markers.get(i);
            }

            for (int j = groups.markers.get(i); j < groups.markers.get(i) + groupSize; j++) {
                //first, calculate mean
                mean += groups.list.get(j).EXPeak();
            }
            mean /= groupSize;
            groups.avgWavelengths.set(i, mean);
            for (int k = groups.markers.get(i); k < groups.markers.get(i) + groupSize; k++) {
                //then calculate variance
                variance += Math.pow((groups.list.get(k).EXPeak() - mean), 2);
            }
            variance /= groupSize;
            //std dev = sqrt(variance)
            avgStdDev += Math.sqrt(variance);
        }

        avgStdDev /= groups.markers.size();
        return avgStdDev;
    }
}
