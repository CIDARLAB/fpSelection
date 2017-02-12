/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.selectors;

import java.util.Map;
import org.cidarlab.fpSelection.Utilities;
import org.cidarlab.fpSelection.dom.Fluorophore;
import org.cidarlab.fpSelection.parsers.MetadataParser;
import org.junit.Test;

/**
 *
 * @author prash
 */
public class ParserTest {
    
    
    @Test
    public void testMetadataParser(){
        String filepath = Utilities.getResourcesFilepath();
        filepath += "fluorophore_meta_data.csv";
        Map<String, Fluorophore> masterlist = MetadataParser.parse(filepath);
        
        System.out.println("Total Fluorophores :: " + masterlist.size());
        
        System.out.println("All Fluorophores ::\n" + masterlist.keySet());
        
    }
    
}
