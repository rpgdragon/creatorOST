/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.creatorost.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author pi
 */
public class Logger {
    
    private static Logger log;
    
    private Logger(){
        try{
            // Create a new file output stream en el directorio actual
            PrintStream fileOut = new PrintStream(new FileOutputStream("./outOST.txt", true));
            // Create a new file error stream en el directorio actual 
            PrintStream fileErr = new PrintStream(new FileOutputStream("./errOST.txt",true));
            // Redirect standard out to file.
            System.setOut(fileOut);
            // Redirect standard err to file.
            System.setErr(fileErr);
        }
        catch(FileNotFoundException e){}
    }
    
    public static Logger getInstance(){
        if(log==null){
            log = new Logger();
        }
        return log;
    }
    
    public void outString(String cadena){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
        System.out.println(sdf.format(d) + ": " + cadena);
    }
}


