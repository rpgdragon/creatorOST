/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.creatorost.html;

import java.net.URL;
import jmcastellano.eu.creatorost.utils.Logger;

/**
 *
 * @author rpgdragon
 */
public abstract class PeticionWeb {
    
    private boolean enEjecucion = false;
    private String urlPeticion = null;
    public static final int MAX_ERRORES = 10;
    private boolean exito = false;

    public PeticionWeb(String urlPeticion){
       this.urlPeticion = urlPeticion;
    }
      
    public void realizarPeticion(){
        this.enEjecucion = true;
        Thread t = new Thread(() -> {
            int indice = 0;
            do{
                try{
                  URL url = new URL(urlPeticion);
                  realizarAccion(url);
                  exito = true;
                  break;       
                } catch(Exception e){
                  Logger.getInstance().outString(e.getMessage());
                  indice++;
                }
            }while(indice < MAX_ERRORES);
            this.enEjecucion = false;
        });
        t.start();
    }

    protected abstract void realizarAccion(URL url) throws Exception;    

    public boolean isEnEjecucion() {
        return enEjecucion;
    }

    public boolean isExito(){
        return exito;
    }
    
}
