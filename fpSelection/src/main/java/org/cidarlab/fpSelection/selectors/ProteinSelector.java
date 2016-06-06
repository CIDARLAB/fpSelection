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
import java.util.PriorityQueue;
import org.cidarlab.fpSelection.dom.Cytometer;
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
    void selectFPs(HashMap<String, Fluorophore> masterList, Laser theLaser)
    {
        //Pull Detector objects out.
        LinkedList<Detector> listDetectors = theLaser.getDetectors();
        
        //Each Detector has a list of ranked fluorophores:
        HashMap<Detector,PriorityQueue<Fluorophore>> rankedProteins = new HashMap<>();
        //Keep track of each detector's "best" protein
        HashMap<Detector,Fluorophore> optimals = new HashMap<>();
        
        
        PriorityQueue<Fluorophore> tempList;
        
        //For each filter, create list of proteins ranked in terms of expression.  O(nFilters * nProteins * PriorityQAdds).
        for(Detector theDetector : listDetectors)
        {
            ProteinComparator qCompare = new ProteinComparator();
            qCompare.laser = theLaser;
            qCompare.detect = theDetector;
            
            tempList = new PriorityQueue<>(qCompare);
            
            for(Fluorophore fp : masterList.values())
            {
                tempList.add(fp);
            }
            //Place biggest priority in our hashmap of bests.
            optimals.put(theDetector, tempList.poll());
            
            //Put the rest of priority queue into ranked hashmap
            rankedProteins.put(theDetector, tempList);
        }
        
        //After all of that, check noise intensity in other filters. If too large, move to next protein in filter's list. O(nFilters * nProteins)
        //
        //
        //
        //      FOR NOW, let's not. We'll figure it out after the simplest case.
        //
        //
        //
        
        
        
        //After all of that is done, create txt file or spreadsheet or whatever with protein suggestions.
        for(Map.Entry<Detector, Fluorophore> entry: optimals.entrySet())
        {
            System.out.println(entry.getKey().getFilterMidpoint() + " : " + entry.getValue().getName());
        }
        
        return;
    }
    
    
}
