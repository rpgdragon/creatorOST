/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.creatorost.utils;

import jmcastellano.eu.creatorost.modelo.Constantes;

/**
 *
 * @author pi
 */
public class Utils {
    
    public static String dameRutaTemp() {
        String ruta;
        if(Utils.windowsOrLinux()){
            ruta = Utils.dameRutaWindowsTemp();
        }
        else{
            ruta = Constantes.RUTA_UNIX_TEMPORAL;
        }
        return ruta;
    }

    public static String dameRutaOST() {
        String ruta;
        if(Utils.windowsOrLinux()){
            ruta = Utils.dameRutaWindowsOST();
        }
        else{
            ruta = Constantes.RUTA_UNIX;
        }
        return ruta;
    }

    public static String dameRutaScripts() {
        String ruta;
        if(Utils.windowsOrLinux()){
            ruta = Utils.dameRutaWindowsScripts();
        }
        else{
            ruta = Constantes.RUTA_UNIX_SCRIPTS;
        }
        return ruta;
    }

    public static boolean windowsOrLinux(){
        String os = System.getProperty("os.name");
        return os.toLowerCase().contains("windows");
    }
    
    public static void esperar(int i) {
        try{
            Thread.sleep(i);
        }
        catch(InterruptedException e){}
    }
    
    public static String dameRutaWindowsOST(){
        return Constantes.RUTA_WINDOWS.replaceAll("%%usuario%%",  System.getProperty("user.name"));
    }

    public static String dameRutaWindowsTemp(){
        return Constantes.RUTA_WINDOWS_TEMPORAL.replaceAll("%%usuario%%",  System.getProperty("user.name"));
    }

    public static String dameRutaWindowsScripts(){
        return Constantes.RUTA_WINDOWS_SCRIPTS.replaceAll("%%usuario%%",  System.getProperty("user.name"));
    }
}
