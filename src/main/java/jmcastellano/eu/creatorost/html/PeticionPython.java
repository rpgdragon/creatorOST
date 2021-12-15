
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
public class PeticionPython extends PeticionWeb {
    
    private File ficheroPhyton;
    private String name;

    public PeticionPython(String url, String name){
     super(url);
     this.name = name;
    }
    @Override
    public void realizarAccion(URL url) throws Exception{
       URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        try (OutputStream outstream = new FileOutputStream(ficheroPhyton)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
        }
       catch(Exception e){
           throw new Exception("No se ha podido descargar el script de Python");
       }
    }

    public void descargarSiNoExiste(){
        if(Utils.windowsOrLinux()){
             ficheroPhyton = new File(Utils.dameRutaWindowsScripts() + name);
        }
        else{
             ficheroPhyton = new File(Constantes.RUTA_UNIX_SCRIPTS + name);
        }
        if(!ficheroPhyton.exists()){
          this.realizarPeticion();
       }
    }

    public File getFicheroPhyton() {
        return ficheroPhyton;
    }

    public void setFicheroPhyton(File ficheroPhyton) {
        this.ficheroPhyton = ficheroPhyton;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
