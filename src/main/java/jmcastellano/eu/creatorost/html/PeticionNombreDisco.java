/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jmcastellano.eu.creatorost.html;

import java.net.URL;
import java.util.Scanner;
import jmcastellano.eu.creatorost.modelo.Constantes;

/**
 *
 * @author rpgdragon
 */
public class PeticionNombreDisco extends PeticionWeb {

   private String nombreDisco = null;

   public PeticionNombreDisco(String token){
      //antes de hacer nada, hay que leer el token que hay en el directorio ue se ejecuta
      super(Constantes.URL_DISCOGRAFIA + "index.php?token=" + token);
   }

   @Override
   protected void realizarAccion(URL url) throws Exception{
        Scanner s = new Scanner(url.openStream(),"UTF-8");
        nombreDisco = s.nextLine();
        if(nombreDisco==null || nombreDisco.isEmpty()){
           throw new Exception("Nombre de disco vacio");
        }
        if(nombreDisco.startsWith("ERROR:")){
          String aux = nombreDisco;
          nombreDisco=null;
          throw new Exception(aux);
        }
        
    }

    public String getNombreDisco() {
        return nombreDisco;
    }

    public void setNombreDisco(String nombreDisco) {
        this.nombreDisco = nombreDisco;
    }

   
}
