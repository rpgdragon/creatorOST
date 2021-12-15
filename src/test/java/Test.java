
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
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
public class Test {
    public static void main(String[] args){
        try{
            String token = leerToken();
            PeticionDeleteNombreDisco d1 = new PeticionDeleteNombreDisco(token,"teenage-mutant-ninja-turtles-fall-of-the-foot-clan-gamebo");
            d1.realizarPeticion();
        }
        catch(FileNotFoundException | CreatorOSTException e){
            System.out.println("Error Test:" + e.getMessage());
        }
    }

    private static String leerToken() throws FileNotFoundException, CreatorOSTException{
       //el fichero esta en la raiz de donde se ejecuta el programa
       File f = new File("./token.txt");
       //Si llega aqui es que al menos existe el fichero
       Scanner lector = new Scanner(f);
       if(!lector.hasNextLine()){
          throw new CreatorOSTException("No hay token en el fichero");
       }
       return lector.nextLine();
       
    }
}
