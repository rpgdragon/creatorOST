/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jmcastellano.eu.creatorost.html;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import jmcastellano.eu.creatorost.modelo.Constantes;

/**
 *
 * @author rpgdragon
 */
public class PeticionDeleteNombreDisco extends PeticionWeb {

   public PeticionDeleteNombreDisco(String token, String disco){
      //antes de hacer nada, hay que leer el token que hay en el directorio ue se ejecuta
      super(Constantes.URL_DISCOGRAFIA + "index.php?token=" + token + "&disco=" + disco);
      System.out.println(Constantes.URL_DISCOGRAFIA + "index.php?token=" + token + "&disco=" + disco);
   }

   @Override
   protected void realizarAccion(URL url) throws Exception{
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("DELETE");
        httpCon.connect();
        BufferedReader br = new BufferedReader(new InputStreamReader((httpCon.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        if(sb.toString()!=null && !sb.toString().isEmpty() && sb.toString().startsWith(Constantes.ERROR)){
           throw new Exception("Se ha producido un error en la respuesta");
        }
   }
    
}
