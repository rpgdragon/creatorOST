/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.creatorost.main;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import jmcastellano.eu.creatorost.exceptions.CreatorOSTException;
import jmcastellano.eu.creatorost.html.PeticionDeleteNombreDisco;
import jmcastellano.eu.creatorost.html.PeticionImagen;
import jmcastellano.eu.creatorost.html.PeticionPython;
import jmcastellano.eu.creatorost.html.PeticionNombreDisco;
import jmcastellano.eu.creatorost.modelo.Cancion;
import jmcastellano.eu.creatorost.modelo.Constantes;
import jmcastellano.eu.creatorost.utils.Logger;
import jmcastellano.eu.creatorost.utils.Utils;

/**
 *
 * @author rpgdragon
 */
public class ReproductorCanciones {
    
    private JFrame ventana;
    private JLabel textocancion;
    private JLabel fondo;
     
    public void iniciar(){
        try{
            comprobarCarpetaTemporal();
            comprobarCarpetaSave();
            comprobarCarpetaScripts();
            //en primer lugar vemos si tenemos que bajar el script o no.
            PeticionPython kh = new PeticionPython(Constantes.URL_SCRIPT_KHINSIDER,Constantes.NOMBRE_SCRIPT_KHINSIDER);
            PeticionPython des = new PeticionPython(Constantes.URL_SCRIPT_DESCRIPCION,Constantes.NOMBRE_SCRIPT_DESCRIPCION);
            kh.descargarSiNoExiste();
            des.descargarSiNoExiste();
            //buscamos el Token utilizado para las peticiones
            String token = leerToken();
            //mientras en paralelo ejecutamos la petición del nombre del disco
            PeticionNombreDisco d1 = new PeticionNombreDisco(token);
            int indice = 0;
            d1.realizarPeticion();
            do{   
                Thread.sleep(1000);
                indice++;
            } while(indice<10 && d1.isEnEjecucion());
            if(d1.getNombreDisco()==null || d1.getNombreDisco().isEmpty() || d1.getNombreDisco().startsWith("ERROR:")){
               Logger.getInstance().outString("Finalizamos programa por no tener el nombre del disco a mirar");
            }

            Logger.getInstance().outString("OST a grabar:" + d1.getNombreDisco());
            //descargamos la imagen del disco que vamos a visualizar
            PeticionImagen dimg = new PeticionImagen(d1.getNombreDisco());
            dimg.realizarPeticion();
            indice = 0;
            do{
                Thread.sleep(1000);
                indice++;
            } while(indice < 10 && dimg.isEnEjecucion());
            
            //vamos a comprobar si la imagen se ha bajado
            String ruta = Utils.dameRutaTemp();
            File imagen = new File(ruta + d1.getNombreDisco() + ".jpg");
            if(!imagen.exists()){
               throw new CreatorOSTException("Imagen OST no descargada");
            }
            //abrimos el OBS
            inicializarOBS();
            //abrimos la ventana
            inicializarVentana(d1.getNombreDisco());
            //ok, hora de descargar el disco
            //para ello tenemos que invocar el script de Python descargado
            ejecutarScriptKhinsider(d1.getNombreDisco());
            //tambien ejecutamos el de descripcion para generar las pistas de audio
            ejecutarScriptDescripcion(d1.getNombreDisco());
        
            //ok si llega aqui, esta todo lo necesario para grabar el disco
            //Primero buscamos todos los mp3 que hay
            File f = new File(Utils.dameRutaTemp());
            File l[]=f.listFiles();
            List<Cancion> lcancion = new ArrayList<>();
            for(File x:l){
                if(x.isHidden()||!x.canRead() || x.isDirectory()){
                   continue;
                }
                if(x.getName().endsWith(".mp3")){
                   int pos = x.getName().lastIndexOf(".mp3");
                   String nombreCancion = x.getName().substring(0, pos);
                   Cancion c = new Cancion(nombreCancion);
                   lcancion.add(c);
                }             
            }

           //ok todo listo, ahora es hora de iniciar la reproducción
           iniciarGrabacion(d1.getNombreDisco());
           //ahora empezamos a reproducir
           for(Cancion c: lcancion){
               reproducirCancion(c);
           }

           detenerGrabacion(d1.getNombreDisco());

           //miramos si es produccion por que hay que hacer más cosas
           if(Main.isIsProduction()){
            //antes de apagar el equipo hay que proceder a borrar el registro
            PeticionDeleteNombreDisco d2 = new PeticionDeleteNombreDisco(token,d1.getNombreDisco());
            d2.realizarPeticion();
            //esperams 1 seg a que se propague
            Thread.sleep(1000);
            //apagarEquipo();
           }
           else{
              System.exit(0);
           }
        }
        catch(InterruptedException | IOException | CreatorOSTException | AWTException   e){
            Logger.getInstance().outString(e.getMessage());
            try{
                if(Main.isIsProduction()){
                    apagarEquipo();
                }
            }
            catch(IOException | InterruptedException e1){
                Logger.getInstance().outString(e.getMessage());
            }
        } 
    }

    private void inicializarVentana(String nombreDisco){
        ventana = new JFrame();
        ventana.setSize(1280, 720);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());
        ventana.setTitle("Creación de OST");
        cambiarFondo(nombreDisco);
        ventana.add(getTextocancion());
        ventana.setVisible(true);
    }

    private void cambiarFondo(String nombreDisco){
        fondo = new JLabel(new ImageIcon(Utils.dameRutaTemp() + nombreDisco + ".jpg"));
        ventana.setContentPane(fondo);
    }

    private void apagarEquipo() throws IOException, InterruptedException {
        Logger.getInstance().outString("Se procede apagar equipo");
        if(!Main.isIsProduction()){
            System.exit(0);
        }
        Process p;
        if(Utils.windowsOrLinux()){
            p = Runtime.getRuntime().exec("shutdown /s");
        }
        else{
            p = Runtime.getRuntime().exec("sudo shutdown -h now");
        }
        p.waitFor(10, TimeUnit.SECONDS);
    }

    private JLabel getTextocancion(){
        if(textocancion==null){
            textocancion=new JLabel("", SwingConstants.CENTER);
            Font font = new Font("SansSerif", Font.BOLD, 22);
            textocancion.setFont(font);
            textocancion.setBackground(Color.WHITE);
            textocancion.setOpaque(true);
            textocancion.setBounds(0, 598, 1262, 75);
            Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
            textocancion.setBorder(border);
        }
        return textocancion;
    }
    
    
    private void reproducirCancion(Cancion cancionactual){
        //iniciamos la reproduccion
        String ruta = Utils.dameRutaTemp();
        FileInputStream stream;
        try {
            stream = new FileInputStream(ruta + cancionactual.getNombre_cancion() + ".mp3");
            Player player = new Player(stream);
            //vamos a cambiar el Label para mostrar el nombre de la cancion
            actualizarCancionVentana(cancionactual);
            player.play();
            player.close();
        } catch (FileNotFoundException | JavaLayerException ex) {
            System.out.println(ex.getMessage());
        }
    }
 
    private void actualizarCancionVentana(Cancion cancionactual) {
        Font font = null;
        if(cancionactual.getNombre_cancion().length()>100){
            font = new Font("SansSerif", Font.BOLD, 22);
        }
        if(cancionactual.getNombre_cancion().length()>50 && cancionactual.getNombre_cancion().length()<= 100){
            font = new Font("SansSerif", Font.BOLD, 26);
        }
        if(cancionactual.getNombre_cancion().length()<=50){
            font = new Font("SansSerif", Font.BOLD, 30);
        }        
        textocancion.setFont(font);
        getTextocancion().setText(cancionactual.getNombre_cancion());
    }

    
  

    /**
     * Metodo que se encarga de que exista una carpeta temporal donde depositar las canciones
     * y los ficheros que se descarguen 
     */
    private void comprobarCarpetaTemporal() {
        //nos aseguramos de que el directorio tmp existe para el SO actual
        String rutatmp;
        if(Utils.windowsOrLinux()){
            rutatmp = Utils.dameRutaWindowsTemp();
        }
        else{
            rutatmp = Constantes.RUTA_UNIX_TEMPORAL;
        }
        File f = new File(rutatmp);
        if(!f.exists() || !f.isDirectory()){
            f.mkdir();
        }
        //ahora vamos a proceder a borrar todos los ficheros que haya en la carpeta temporal
        for(File file: f.listFiles()){
            if(!file.isDirectory()){
                file.delete();
            }
        }
    }
    
    /**
     * Metodo que se encarga de que exista una carpeta temporal donde depositar las canciones
     * y los ficheros que se descarguen 
     */
    private void comprobarCarpetaSave() {
        //nos aseguramos de que el directorio ost existe para el SO actual
        String rutatmp;
        if(Utils.windowsOrLinux()){
            rutatmp = Utils.dameRutaWindowsOST();
        }
        else{
            rutatmp = Constantes.RUTA_UNIX;
        }
        File f = new File(rutatmp);
        if(!f.exists() || !f.isDirectory()){
            f.mkdir();
        }
    }

/**
     * Metodo que se encarga de que exista una carpeta temporal donde depositar las canciones
     * y los ficheros que se descarguen 
     */
    private void comprobarCarpetaScripts() {
        //nos aseguramos de que el directorio tmp existe para el SO actual
        String rutatmp;
        if(Utils.windowsOrLinux()){
            rutatmp = Utils.dameRutaWindowsScripts();
        }
        else{
            rutatmp = Constantes.RUTA_UNIX_SCRIPTS;
        }
        File f = new File(rutatmp);
        if(!f.exists() || !f.isDirectory()){
            f.mkdir();
        }
    }

    private String leerToken() throws FileNotFoundException, CreatorOSTException{
       //el fichero esta en la raiz de donde se ejecuta el programa
       File f = new File("./token.txt");
       //Si llega aqui es que al menos existe el fichero
       Scanner lector = new Scanner(f);
       if(!lector.hasNextLine()){
          throw new CreatorOSTException("No hay token en el fichero");
       }
       return lector.nextLine();
       
    }

    /**
    * Metodo que permite la ejecución del fichero Khinsider.py
    */
    private void ejecutarScriptKhinsider(String nombreDisco) throws CreatorOSTException {
        try{
            String ruta;
            String rutaTemporal;
            if(Utils.windowsOrLinux()){
                ruta = Utils.dameRutaWindowsScripts() + Constantes.NOMBRE_SCRIPT_KHINSIDER ;
                rutaTemporal = Utils.dameRutaWindowsTemp();
            }
            else{
                ruta = Constantes.RUTA_UNIX_SCRIPTS + Constantes.NOMBRE_SCRIPT_KHINSIDER;
                rutaTemporal = Constantes.RUTA_UNIX_TEMPORAL;
            }
            ProcessBuilder processBuilder = new ProcessBuilder("python3", ruta, nombreDisco, rutaTemporal);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if(exitCode!=0){
                 throw new CreatorOSTException("Error en Script de Descarga");
            }
        }
        catch(IOException | InterruptedException e){
            throw new CreatorOSTException(e.getMessage());
        }
    }

/**
    * Metodo que permite la ejecución del fichero Khinsider.py
    */
    private void ejecutarScriptDescripcion(String nombreDisco) throws CreatorOSTException {
        try{
            String ruta;
            String rutaTemporal;
            String rutaOST;
            if(Utils.windowsOrLinux()){
                ruta = Utils.dameRutaWindowsScripts() + Constantes.NOMBRE_SCRIPT_DESCRIPCION ;
                rutaTemporal = Utils.dameRutaWindowsTemp();
                rutaOST = Utils.dameRutaWindowsOST();
            }
            else{
                ruta = Constantes.RUTA_UNIX_SCRIPTS + Constantes.NOMBRE_SCRIPT_DESCRIPCION;
                rutaTemporal = Constantes.RUTA_UNIX_TEMPORAL;
                rutaOST = Constantes.RUTA_UNIX;
            }
            ProcessBuilder processBuilder = new ProcessBuilder("python3", ruta, rutaTemporal, "True");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if(exitCode!=0){
                 throw new CreatorOSTException("Error en Script de Descripcion");
            }
            //si llega aqui, hay que mover el fichero Out a la carpeta OST, renombrado al nombre del disco
            File f = new File(rutaTemporal + "out.txt");
            File f2 = new File(rutaOST + nombreDisco + ".txt");
            f.renameTo(f2);
        }
        catch(IOException | InterruptedException e){
            throw new CreatorOSTException(e.getMessage());
        }
    }

    private void inicializarOBS() throws IOException,InterruptedException {
        Process p;
        if(Utils.windowsOrLinux()){
            p = Runtime.getRuntime().exec("cmd /c start /d \"C:\\Program Files\\obs-studio\\bin\\64bit\\\" obs64.exe --profile \"OST\" --scene \"OST\" --minimize-to-tray");
        }
        else{
             //TODO revisar
            p = Runtime.getRuntime().exec("obs --profile \"OST\" --scene \"OST\" --minimize-to-tray");
        }
        p.waitFor(10, TimeUnit.SECONDS);
    }

    private void iniciarGrabacion(String nombreDisco) throws AWTException{
        Logger.getInstance().outString(Constantes.INICIANDO_TRANSMISION + " " + nombreDisco);  
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ADD);
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_ADD);
    }

    private void detenerGrabacion(String nombreDisco) throws AWTException{
        Logger.getInstance().outString(Constantes.DETENIENDO + " " + nombreDisco);  
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_SUBTRACT);
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_SUBTRACT);
    }
}
