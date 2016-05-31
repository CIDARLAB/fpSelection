/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import java.util.LinkedList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author david
 */
public class Cytometer {
    
//    Lombok helps produce functions to minimize the excess code we write. If you look at the Navigator in netbeans, we now have a bunch of useful get/set functions.
    
    
    @Getter
    @Setter
    private int sheathPressure;
    
    @Getter
    @Setter
    private int nozzleSize;
    
    @Getter
    @Setter
    private int windowExt;
    
    @Getter
    @Setter
    private LinkedList<Laser> lasers;
    
    public Cytometer()
    {
        sheathPressure = 0;
        nozzleSize = 0;
        windowExt = 0;
        lasers = new LinkedList<>();
    }
    
    public Cytometer(int sheathPress, int nozSize, int winExt, LinkedList<Laser> laserList)
    {
        sheathPressure = sheathPress;
        nozzleSize = nozSize;
        windowExt = winExt;
        lasers = laserList;
    }
    
    public void addLaser(Laser newLaser)
    {
        lasers.add(newLaser);
        return;
    }
}
