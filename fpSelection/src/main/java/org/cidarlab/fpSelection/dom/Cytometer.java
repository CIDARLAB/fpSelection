/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.dom;

import java.util.List;
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
    private int nozSize;
    
    @Getter
    @Setter
    private int winExtension;
    
    @Getter
    @Setter
    private List<Laser> lasers;
    
    public void addLaser(Laser newLaser)
    {
        lasers.add(newLaser);
        return;
    }
}
