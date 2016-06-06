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
    public static void selectFPs(HashMap<String, Fluorophore> masterList, Laser theLaser)
    {
        //Pull Detector objects out.
        LinkedList<Detector> listDetectors = theLaser.getDetectors();
        
        //Each Detector has a list of ranked fluorophores:
        HashMap<Detector,ArrayList<Fluorophore>> rankedProteins = new HashMap<>();
        
        //Keep track of each detector's "best" protein
        HashMap<Detector,Integer> optimals = new HashMap<>();
        
        
        ArrayList<Fluorophore> tempList;
        
        //  For each filter, create list of proteins ranked in terms of expression.  
        //  O(nFilters * nProteins * PriorityQAdd).
        
        for(Detector theDetector : listDetectors)
        {
            //Priority Queue comparator is based on expression in filter - need laser & filter references
            ProteinComparator qCompare = new ProteinComparator();
            qCompare.laser = theLaser;
            qCompare.detect = theDetector;
            
            tempList = new ArrayList<>();
            
            for(Fluorophore fp : masterList.values())
            {
                tempList.add(fp);
            }
            tempList.sort(qCompare);
            
            //Put into ranked hashmap
            rankedProteins.put(theDetector, tempList);
            
            //The first element should be the best thanks to qCompare
            optimals.put(theDetector, 0);
        }
        
        //After all of that, check noise intensity in other filters. If too large, move to next protein in filter's list. O(nFilters * nProteins)
        //
        //
        //
        //      FOR NOW, let's not. We'll figure it out after the simplest case.
        //
        //
        //
        
        //After all of that is done, print line, create txt file or spreadsheet, or do whatever with protein suggestions.
        for(Map.Entry<Detector, Integer> entry: optimals.entrySet())
        {
            //                      Filter Midpoint as identifier               Fluorophore Name as suggestion
            System.out.println(entry.getKey().getFilterMidpoint() + " : " + rankedProteins.get(entry.getKey()).get(entry.getValue()).getName());
        }   
    }
    
    //Try to revise list for better SNR. 
    void checkOptimals(HashMap<Detector, ArrayList<Fluorophore>> ranked, HashMap<Detector, Integer> optimals)
    {
        //I don't want it running to infinity, but I do want to go through the lists to see if there are better SNR decisions to be made.
        int tolerance = 5;
        
        for(int i = 0; i < tolerance; i++)
        {
            //Get SNR for each channel, adjust
        }
        
        
        
        return;
    }
    
    
}
