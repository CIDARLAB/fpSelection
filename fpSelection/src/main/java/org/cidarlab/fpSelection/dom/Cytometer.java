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
    private String software;
    
    @Getter
    @Setter
    private String cytometerName;
    
    @Getter
    @Setter
    private String serial;
    
    @Getter
    @Setter
    private int sheathPressure;
    
    @Getter
    @Setter
    private int nozzleSize;
    
    @Getter
    @Setter
    private int winExtension;
    
    @Getter
    @Setter
    private LinkedList<Laser> lasers;
    
    public Cytometer()
    {
        software = null;
        cytometerName = null;
        serial = null;
        sheathPressure = 0;
        nozzleSize = 0;
        winExtension = 0;
        lasers = new LinkedList<>();
    }
    
    public Cytometer(String software, String name, String ser, int sheathPress, int nozSize, int winExt, LinkedList<Laser> laserList)
    {
        this.software = software;
        cytometerName = name;
        serial = ser;
        sheathPressure = sheathPress;
        nozzleSize = nozSize;
        winExtension = winExt;
        lasers = laserList;
    }
    
    public void addLaser(Laser newLaser)
    {
        lasers.add(newLaser);
        return;
    }
}
