/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import java.util.ArrayList;

/**
 *
 * @author David
 */
public class ListHelper {

    public ArrayList<Fluorophore> list;

    //each marker marks the beginning of a section in a list.
    //
    // num = 5
    // |   |   |   |   |
    // 1 2 3 4 5 6 7 8 9 10
    public ArrayList<Integer> markers;
    public ArrayList<Double> avgWavelengths;

    public ListHelper(ArrayList<Fluorophore> list, int numMarkers) {
        this.list = list;
        markers = new ArrayList<>(numMarkers);
        avgWavelengths = new ArrayList<>(numMarkers);
        for (int i = 0; i < numMarkers; i++) {
            markers.add(i * list.size() / numMarkers);
        }
    }

    public ArrayList<Fluorophore> groupToList(int groupNum) {
        ArrayList<Fluorophore> returnGroup = new ArrayList<>();
        int bound;

        if (groupNum == markers.size() - 1) {
            bound = list.size();
        } else {
            bound = markers.get(groupNum + 1);
        }

        for (int i = markers.get(groupNum); i < bound; i++) {
            returnGroup.add(list.get(i));
        }

        return returnGroup;

    }

    public void debugGrouping() {
        for (int i = 0; i < markers.size(); i++) {
            double mean = 0, variance = 0;
            int groupSize;
            int markStart;
            int markEnd;

            if (i != markers.size() - 1) {
                groupSize = markers.get(i + 1) - markers.get(i);
                markStart = markers.get(i);
                markEnd = markers.get(i + 1);
            } else {
                groupSize = list.size() - markers.get(i);
                markStart = markers.get(i);
                markEnd = list.size();
            }
            for (int j = markers.get(i); j < markers.get(i) + groupSize; j++) {
                mean += list.get(j).EXPeak();
            }
            mean /= groupSize;
            System.out.println("Group " + i + " :");
            System.out.println("Mean: " + mean + " nm");

            for (int j = markers.get(i); j < markers.get(i) + groupSize; j++) {
                variance += Math.pow(list.get(j).EXPeak() - mean, 2);
            }
            variance /= groupSize;
            System.out.println("Variance: " + variance + " nm");
            System.out.println("Std Dev: " + Math.sqrt(variance) + " nm");
            System.out.println("Group Size: " + groupSize);
            System.out.println("Bounding Markers: " + markStart + " and " + markEnd);
            System.out.println();

        }
    }
}
