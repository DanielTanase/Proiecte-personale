/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.sql.Timestamp;

/**
 *
 * @author JAMAL
 */

//Creata pentru a stoca date din BD, intr-un array de obiecte
public class Product3 {
    
    private int id;
    private String pretIncepere;
    private String pretDirecta;
    private Timestamp dataIncepere;
    private Timestamp dataIncheiere;
    private String status;
    
    private byte[] img;
    private String producator;
    private String modelTelefon;
    
    private int bestBid;
    
    public Product3(){}
    
    public Product3 (int id, String pretIncepere, String pretDirecta, Timestamp dataIncepere, Timestamp dataIncheiere, String status, byte[] img, String producator, String modelTelefon, int bestBuy){
        
        this.id = id;
        this.pretIncepere = pretIncepere;
        this.pretDirecta = pretDirecta;
        this.dataIncepere = dataIncepere;
        this.dataIncheiere = dataIncheiere;
        this.status = status;
        this.img = img;
        this.producator = producator;
        this.modelTelefon = modelTelefon;
        this.bestBid = bestBuy;
    }
        public int getID(){
            return id;
        }
        
        public String getPretIncepere(){
            return pretIncepere;
        }
        
        public String getPretDirecta(){
            return pretDirecta;
        }
        
        public String getStatus(){
            return status;
        }
        
        public Timestamp getDataIncepere(){
            return dataIncepere;
        }
        
        public Timestamp getDataIncheiere(){
            return dataIncheiere;
        }
        
        public byte[] getImagine(){
            return img;
        }
        
        public String getProducator(){
            return producator;
        }
        
        public String getModelTelefon(){
            return modelTelefon;
        }
        
        public int getBestBid(){
            return bestBid;
        } 
}

