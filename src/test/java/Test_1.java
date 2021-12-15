
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmcastellano.eu.creatorost.exceptions.CreatorOSTException;
import jmcastellano.eu.creatorost.html.PeticionDeleteNombreDisco;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rpgdragon
 */
public class Test_1  {
    public static void main(String[] args){
               Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_SUBTRACT);
            robot.delay(500);
            robot.keyRelease(KeyEvent.VK_SUBTRACT);
        } catch (AWTException ex) {
            Logger.getLogger(Test_1.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    
}