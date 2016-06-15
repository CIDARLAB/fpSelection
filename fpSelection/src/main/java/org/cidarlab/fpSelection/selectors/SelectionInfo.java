/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.util.ArrayList;
import org.cidarlab.fpSelection.dom.Detector;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.dom.Laser;

/**
 *
 * @author david
 */
public class SelectionInfo {

    public Laser selectedLaser;
    public Detector selectedDetector;
    public ArrayList<Fluorophore> rankedFluorophores;

    public int selectedIndex;
    public double score;
    public double SNR;
}
