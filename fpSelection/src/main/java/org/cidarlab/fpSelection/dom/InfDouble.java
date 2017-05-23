/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import lombok.Getter;


/**
 *
 * @author prash
 */
public class InfDouble {

    int infCount = 0;
    
    @Getter
    double snr = 0;
    
    InfDoubleMode mode;

    public InfDouble(InfDoubleMode _mode) {
        if (_mode.equals(InfDoubleMode.multiply)) {
            snr = 1;
        } else {
            snr = 0;
        }
        infCount = 0;
        mode = _mode;
    }

    public void includeSNR(double val) {
        if (val == Double.POSITIVE_INFINITY) {
            infCount++;
        } else {
            if (mode.equals(InfDoubleMode.add)) {
                snr += val;
            } else {
                snr *= val;
            }
        }
    }

    public int compare(InfDouble other) {
        int comp = 0;
        if (infCount == other.infCount) {
            if (snr == other.snr) {
                comp = 0; // Both equal
            } else if (snr > other.snr) {
                comp = 1; //Current is bigger
            } else if (snr < other.snr) {
                comp = -1; //Comp is bigger
            }
        } else if (infCount < other.infCount) {
            if (other.snr == 0) {
                if (snr == 0) {
                    comp = -1; //Comp is bigger
                } else {
                    comp = 1;  //Current is bigger
                }
            } else {
                comp = -1; //Comp is bigger
            }
        } else { //infCount > other.infCount
            if (snr == 0) {
                if (other.snr == 0) {
                    comp = 1; //Current is bigger
                } else {
                    comp = -1; //Comp is bigger
                }
            } else {
                comp = 1; //Current is bigger
            }
        }

        return comp;
    }
    public enum InfDoubleMode {

        multiply,
        add
    }

}


