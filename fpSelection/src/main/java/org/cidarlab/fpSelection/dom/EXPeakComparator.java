/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import java.util.Comparator;

/**
 *
 * @author David
 */
public class EXPeakComparator implements Comparator<Fluorophore> {

    @Override
    public int compare(Fluorophore o1, Fluorophore o2) {
        double comp = o1.EXPeak() - o2.EXPeak();
        if(comp > 0)
        {
            return 1;
        }
        else if ( comp < 0 )
        {
             return -1;
        }
        else
        {
            return 0;
        }
    }
    
}
