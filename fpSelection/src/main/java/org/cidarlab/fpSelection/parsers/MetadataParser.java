/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.parsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cidarlab.fpSelection.Utilities;
import org.cidarlab.fpSelection.dom.Fluorophore;

/**
 *
 * @author prash
 */
public class MetadataParser {
    
    public static Map<String, Fluorophore> parse(String filepath){
        
        List<String[]> lines = Utilities.getCSVFileContentAsList(filepath);
        Map<String, Fluorophore> masterlist = new HashMap<String, Fluorophore>();
  
        
        
        for(int i=1;i<lines.size();i++){
            String[] pieces = lines.get(i);
            Fluorophore fp = new Fluorophore();
            
            //0 - Name
            fp.name = pieces[0].trim();
            
            //1 - Type
            if(pieces[1].trim().toLowerCase().contains("protein")){
                fp.isProtein = true;
            }
            
            //2 - Subtype?
            
            //3 - Oligomerization
            if(!pieces[3].trim().isEmpty() && !pieces[3].trim().equals("NA")){
                int oligo = Integer.valueOf(pieces[3].trim());
                fp.oligomerization = oligo;
            }
            
            //4 - Brightness. Translation?
            fp.setBrightness(Double.valueOf(pieces[4].trim()));
            
            //5 - Brightness %GFP
            //fp.brightness = Double.valueOf(pieces[5].trim())/100;
            
            //6 - Source. Not so important?
            
            //7 - Price
            if (pieces.length >= 7) {
                if (!pieces[7].trim().isEmpty()) {
                    String price = pieces[7].trim();
                    if (price.startsWith("$")) {
                        price = price.substring(1);
                    }
                    int priceVal;
                    if (price.contains("/")) {
                        String[] pricePieces = price.split("/");
                        priceVal = Integer.valueOf(pricePieces[0].trim());
                        if (pricePieces[1].equals("g") || pricePieces[1].equals("gram")) {
                            fp.unit = Fluorophore.PriceUnit.PER_GRAM;
                        } else if (pricePieces[1].equals("nM")) {
                            fp.unit = Fluorophore.PriceUnit.PER_NANOMOLAR;
                        }
                    } else {
                        priceVal = Integer.valueOf(price.trim());
                    }
                    fp.price = priceVal;
                }
            }
            
            
            masterlist.put(fp.name, fp);
        }
        
        
        return masterlist;
        
    }
    
}
