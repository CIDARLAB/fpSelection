/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.cidarlab.fpSelection.dom.Cytometer;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class RestrictedAnneal {

    //IT LIVES!!!!! It actually works well!    
    
    public static ArrayList<SelectionInfo> AnnealMeBaby(HashMap<String, Fluorophore> masterList, Cytometer cyto, int n)
    {
        ArrayList<SelectionInfo> total = new ArrayList<>();
        for (Laser lase : cyto.lasers) {
            total.addAll(sortTop20P(masterList, lase));

        }
        
        
        ArrayList<ArrayList<SelectionInfo>> runs = new ArrayList<>();
        ArrayList<Double> runScores = new ArrayList();

        for (int i = 0; i < 10; i++) {

            ArrayList<SelectionInfo> selected = simAnneal(total, n);
            double score = ProteinSelector.calcSumSigNoise(selected);

            runs.add(selected);
            runScores.add(score);
        }
        double bestScore = 0;
        int best = 0;
        for( double each : runScores)
        {
            if(each > bestScore)
            {
                bestScore = each;
                best = runScores.indexOf(each);
            }
        }
        
        ArrayList<SelectionInfo> bestRun = runs.get(best);
        return bestRun;
    }
    
    
    
    public static ArrayList<SelectionInfo> sortTop20P(HashMap<String, Fluorophore> masterList, Laser theLaser) {

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
            int threshold = 90;

            while (threshold > 40 && tempList.size() < .2 * masterList.size()) {
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

    public static ArrayList<SelectionInfo> simAnneal(ArrayList<SelectionInfo> suggesteds, int n) {
        ArrayList<SelectionInfo> returnList = new ArrayList<>();
        Random choice = new Random();
        
        ArrayList<Fluorophore> chosen = new ArrayList();

        for (int i = 0; i < n; i++) {
            SelectionInfo toAdd = suggesteds.get(choice.nextInt(suggesteds.size()));
            if(!chosen.contains(toAdd.getFP()))
            {
                returnList.add(toAdd);
                chosen.add(toAdd.getFP());
            }
            else
            {
                i--;
            }
        }

        double temperature = Math.pow(700, 10);
        double tempEnd = Math.pow(10, -100);
        double coolFactor = .99;

        double bestSNR = ProteinSelector.calcSumSigNoise(returnList);
        double prevSNR = bestSNR;
        double newSNR;
        double diff;

        int prevNum;
        int newNum;


        SelectionInfo toModify;

        while (temperature > tempEnd) {
            toModify = returnList.get(choice.nextInt(returnList.size()));

            prevNum = toModify.selectedIndex;
            do {
                newNum = choice.nextInt(toModify.rankedFluorophores.size());
            } while(chosen.contains(toModify.getFP(newNum)));
            
            
            toModify.selectedIndex = newNum;
            newSNR = ProteinSelector.calcSumSigNoise(returnList);

            diff = newSNR - prevSNR;
            if (diff > 0) {
                chosen.remove(chosen.indexOf(toModify.getFP(prevNum)));
                chosen.add((toModify.getFP(newNum)));                
                prevSNR = newSNR;
                
            } else if (Math.exp(diff / (temperature * 100)) > Math.random()) {
                chosen.remove(chosen.indexOf(toModify.getFP(prevNum)));
                chosen.add(toModify.getFP(newNum));
                prevSNR = newSNR;
                
            } else {
                toModify.selectedIndex = prevNum;
            }
            

            if (prevSNR > bestSNR) {
                bestSNR = prevSNR;
            }

            temperature *= coolFactor;

        }

        return returnList;
    }

}
