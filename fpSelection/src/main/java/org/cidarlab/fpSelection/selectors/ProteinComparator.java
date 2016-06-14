/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.util.ArrayList;
import java.util.Comparator;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class ProteinComparator implements Comparator<Fluorophore> {

    public enum compareTypes {
        Brightness, Leakage
    }

    //Populate these fields before use!!! - basis for calculations
    public Laser laser;
    public Detector detect;

    //Determines order of significance
    public ArrayList<compareTypes> compareTerms;

    //Determines the falloff weight of later entries in the compareTerms list.
    public double weight;

    //If you want only one compare type to have dominance over the ranking.
    public boolean absolute;
    
    public void setDefaults()
    {
        weight = .75;
        absolute = false;
        compareTerms = new ArrayList<>();
        compareTerms.add(compareTypes.Leakage);
        compareTerms.add(compareTypes.Brightness);
        return;
    }

    @Override
    public int compare(Fluorophore o1, Fluorophore o2) {

        //All comparison variables have been normalized to a (0-100) range
        //Comparison in terms of brightness in desired filter.
        double num1Express = o1.express(laser, detect);
        double num2Express = o2.express(laser, detect);

        //Comparison in terms of % not leaked.
        double num1Leak = 100 - o1.leakageCalc(detect);
        double num2Leak = 100 - o2.leakageCalc(detect);

        for (int i = 0; i < compareTerms.size(); i++) {
            //Each variable we're accounting for should be weighted in terms of importance.
            
            switch (compareTerms.get(i)) {
                case Brightness:
                    num1Express *= Math.pow(weight, i);
                    num2Express *= Math.pow(weight, i);
                    break;

                case Leakage:
                    num1Leak *= Math.pow(weight, i);
                    num2Leak *= Math.pow(weight, i);
                    break;

                default:
                    break;

            }

        }

        //If absolute is true, we just return a comparison based on single attributes.
        if (absolute == true) {
            
            switch (compareTerms.get(0)) {
                case Brightness:
                    //Descending order - brightest first
                    return (int) (num2Express - num1Express);

                case Leakage:
                    //Descending order - most area efficient first
                    return (int) (num2Leak - num1Leak);

                default:
                    return 0;
            }
        } else {        //Brightness + % not leaked
            double sum1 = num1Express + num1Leak;
            double sum2 = num2Express + num2Leak;

            return (int) (sum2 - sum1);
        }

    }
}
