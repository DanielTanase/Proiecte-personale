/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author JAMAL
 */
//Creata pentru a stoca date din BD intr-un array de obiecte
public class Product2 {

    private int id;
    private String producator;
    private String model;
    private String stare;
    private int an;
    private String descriere;
    private byte[] Image;
    
    public Product2(){}
    
    public Product2(int Id, String Producator, String Model, String Stare, int An, String Descriere, byte[] image){
    
        this.id = Id;
        this.producator = Producator;
        this.model = Model;
        this.stare = Stare;
        this.an = An;
        this.descriere = Descriere;
        this.Image = image;
       
    }
    
    
    public int getID(){
      return id;
    }
    
    public void setID(int ID){
        this.id = ID;
    }
    
    public String getProducator(){
        return producator;
    }
    
    public void setProducator(String Name){
        this.producator = Name;
    }
    
    public int getAn(){
        return an;
    }
    
    public void setAn(int Qte){
        this.an = Qte;
    }
   
    public String getModel(){
        return model;
    }
    
    public void setModel(String Name){
        this.model = Name;
    }
    
    public String getStare(){
        return stare;
    }
    
    public void setStare(String Name){
        this.stare = Name;
    }
    
    public String getDescriere(){
        return descriere;
    }
    
    public void setDescriere(String Name){
        this.descriere = Name;
    }
   
    public byte[] getMyImage(){
        return Image;
    }
}
