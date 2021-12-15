/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jmcastellano.eu.creatorost.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import jmcastellano.eu.creatorost.modelo.Constantes;
import jmcastellano.eu.creatorost.utils.Utils;

/**
 *
 * @author rpgdragon
 */
public class PeticionImagen extends PeticionWeb {

   private String nombreImagen = null;

   public PeticionImagen(String nombreImagen){
      super(Constantes.URL_DISCOGRAFIA + nombreImagen + ".jpg");
      this.nombreImagen = nombreImagen;
   }

   @Override
   protected void realizarAccion(URL url) throws Exception{
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        
        String ruta = Utils.dameRutaTemp();
        try (OutputStream outstream = new FileOutputStream(new File(ruta + nombreImagen + ".jpg"))) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
        }
    }

    public String getNombreImagen() {
        return nombreImagen;
    }

    public void setNombreImagen(String nombreImagen) {
        this.nombreImagen = nombreImagen;
    }

   
}
