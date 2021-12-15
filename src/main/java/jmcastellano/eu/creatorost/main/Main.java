/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.creatorost.main;

import jmcastellano.eu.creatorost.utils.Logger;

/**
 *
 * @author rpgdragon
 */
public class Main {
    
    private static boolean isProduction = true;
    /**
     * Main del programa OST
     * @param args Si es false, no se contabiliza la cancion de los mensajes, si es true se contabiliza
     */
    public static void main(String[] args){
        if(args!=null && args.length > 0 && args[0].equals("false")){
            isProduction = false;
        }
       
       Logger.getInstance().outString("Iniciando creatorost:" + isProduction);
       ReproductorCanciones rc = new ReproductorCanciones();
       rc.iniciar();
    }

    public static boolean isIsProduction() {
        return isProduction;
    }

    public static void setIsProduction(boolean isProduction) {
        Main.isProduction = isProduction;
    }

}
