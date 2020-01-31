package it.uniroma1.lcl.crucyservlet.ClassiBuilder.template;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Converte una immagine raffigurante un tamplate, e ne crea un file con il
 * toString del template ulizzabile nel Builder.
 * Vanno settati i colori delle caselle dei vari mini-schemi, compreso quelle nere.
 * Le caselle Portale vanno aggiornate a mano per evitare l'uso di troppi
 * colori diversi.
 * 
 * @author Papi Suadoni
 *
 */
public class Template2
{    
    public String readImage2(String filename, int pixelSize) throws IOException
    {
    	String name = filename.substring(0, filename.length()-4);
        BufferedImage image = javax.imageio.ImageIO.read(new File(filename));
        Color color;
        StringBuffer sb = new StringBuffer();
        //ArrayList<String> righe = new ArrayList<>();
        String[][] matrice = new String[image.getHeight()][image.getWidth()];
        
        System.out.println(image.getWidth());
        System.out.println(image.getHeight());
        
        for (int i = 0; i < image.getHeight(); i += pixelSize)
        {
            int j = 0;
            color = new Color(image.getRGB(j, i));
            String str = "" + color.getRed() + color.getGreen() + color.getBlue();
           
            while (j < image.getWidth())
            {
                color = new Color(image.getRGB(j, i));
                str = "" + color.getRed() + color.getGreen() + color.getBlue();
                if (!str.equals("46204112") && (!str.equals("46204113")))
                    break;
                j += pixelSize;
            }
            while (j < image.getWidth())
            {
                color = new Color(image.getRGB(j, i));
                str = "" + color.getRed() + color.getGreen() + color.getBlue();
                String toAdd=null;
                
                if (str.equals("000"))
                	toAdd= "a*,"; // black
                else if (str.equals("46204112") || (str.equals("46204113")))
                	toAdd="--,"; // verde(sfondo)
                else if (str.equals("52152219"))
                	toAdd="a0,"; // blu (invisibile)
                else if (str.equals("25500")) //rosso fuoco
                	toAdd="a*,";
                else if (str.equals("527394")) //blu brutto
                	toAdd="b0,";
                else if (str.equals("0255230")) //celestino chiaro
                	toAdd="e*,";
                else if (str.equals("23012634")) //arancione scuro
                	toAdd="e0,";
                else if (str.equals("00255")) //blu standard
                	toAdd="b*,";
                else if (str.equals("24119615")) //giallo scuro
                	toAdd="c0,";
                else if (str.equals("2552380"))  // giallo standard
                	toAdd="c*,";
                else if (str.equals("22160133") || (str.equals("22160132")))  //verde(blu) slavato
                	toAdd="d0,";
                else if (str.equals("2080255"))   //viola mio
                	toAdd="d*,";
                else if (str.equals("02550"))   //verde fosforescente
                	toAdd="f*,";
                else if (str.equals("15589182")  || (str.equals("15689182")))   //viola originale
                	toAdd="f0,";
                else if (str.equals("0150135"))    //teal google
                	toAdd="h*,";
                else if (str.equals("2333098"))    //pink google
                	toAdd="h0,";
                else if (str.equals("1218572"))    //marrone
                	toAdd="g*,";
                else if (str.equals("255255255"))  // bianco
                	toAdd="g0,";
                
                else if (str.equals("2317760") || (str.equals("2317660")))
                {
                	toAdd="PP,"; // rosso (portal)
                }
                else
                {
                	toAdd= "";
                    System.out.println("valore: " + str + ", color: " + color);
                    return ""; // errore con l'immagine (presenza di caselle
                               // sconosciute)
                }
                
                matrice[i][j] = toAdd;
                j += pixelSize;
                
            }
           
        }
        
        
        for (int i = 0; i < image.getHeight(); i += pixelSize)
        {
        	for (int j = 0; j < image.getWidth(); j += pixelSize)
            {
        		System.out.print(matrice[i][j]);
            }
    		System.out.println("");
        }
        
        
        for (int i = 0; i < image.getHeight(); i += pixelSize)
        {
        	for (int j = 0; j < image.getWidth(); j += pixelSize)
            {
        		System.out.println(matrice[i][j]);
        		if(!matrice[i][j].equals("PP")) sb.append(matrice[i][j]);
        		else
        		{
        			 String top,bot,left, right;
        			 top = bot = left = right="";
     
        			
        			if(j > 0) left = matrice[i][j-1];
        			if(j+1 < image.getWidth())  right = matrice[i][j+1];
        			if(i > 0 ) top = matrice[i-1][j];
        			if(i+1 < image.getHeight() ) bot = matrice[i+1][j];
        			
        			String miniOne, miniTwo = null;
        			System.out.println( "---");
        			System.out.println( "..."+top+ "...");
        			System.out.println(left+matrice[i][j]+right);
        			System.out.println( "..."+bot+ "...");

        			System.out.println( "---");
      

        		}
            }
        }
        System.out.println(name);
        BufferedWriter bw = Files.newBufferedWriter(Paths.get(name));
        bw.write(sb.toString());
        bw.close();
        return sb.toString();
    }

    /**
     * Modifica la stringa togliendo la parte finale composta da sole caselle
     * trasparenti, e ne aggiunge all'inizio di ogni stringa, se necessario, in
     * modo da allineare tutte le righe
     * 
     * @param riga che bisogna aggiustare
     * @param lunghezza indica la lunghezza massima tra tutte le righe
     * @return stringa modificata
     */
    private int conta = 1;
    
    private String fixTheString(String riga, int lunghezza) 
    {
        int len = riga.length(), count = 0, i = len +1;
        while ((i -=3) >= 0 && riga.charAt(i) == '-')
            count+=3;
        String[] spazi = new String[(lunghezza -len) / 3];
        Arrays.fill(spazi, "--,");
        StringBuilder builder = new StringBuilder();
        for(String s : spazi) {
            builder.append(s);
        }
        //System.out.println(conta++ +". count: " + count);
        return  builder.toString()+ riga.substring(0, len -count);
    }

    public static void main(String[] args) throws IOException
    {
        System.out.print(new Template2().readImage2("resources/templatesCreate/1cbeebae6fdde1a.png", 4));
    }
}
