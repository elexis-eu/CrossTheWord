package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class TestCruciverba
{
//
//
//    public static void salva(List<String> lista, String cartella) throws IOException
//    {
//        int count = 1;
//        for(String s : lista)
//        {
//            BufferedWriter br = Files.newBufferedWriter(
//                    Paths.get(cartella, "CrucyString" + cartella.charAt(0) + count++));
//            br.write(s);
//            br.close();
//        }
//    }
//
//
//    public static void main(String[] args) throws IOException, JSONException, TemplateErrorException, CompressorException
//    {
//        Map<Character, Set<CruciverbaServer>> mappa = new CruciverbaBuilder()
//                .build(null, Difficult.MEDIUM, "tEsagono", null, 50);
//        UnisciSchemi unisciSchemi = new UnisciSchemi(mappa, null, null, null, null);
//        unisciSchemi.createAll();
//        salva(unisciSchemi.getResult(), "Esagono");
//        System.out.println("Schemi generati: " + unisciSchemi.getResult().size());
//    }
//
//    public static void mainTestLibgdx() throws IOException
//    {
//      //return dummy;
//        BufferedReader br = Files.newBufferedReader(Paths.get("toStr7.txt"));
//        String linea;
//        StringBuilder stringa = new StringBuilder();
//        while((linea = br.readLine()) != null  )
//            stringa.append(linea).append("\n");
//        Cruciverba cc = new CaricaCruciverba().carica(stringa.toString());
//        System.out.println("dummy\n" + cc);
//        for(Casella[] riga : cc.getCasellePortali().get('b').getCruciverba().getCasellePortali().get('d').getCruciverba().getBoard())
//        {
//            for(Casella c : riga)
//            {
//                if (c == null)
//                    System.out.println("null");
//                System.out.print(c.toString() + ", ");
//            }
//
//            System.out.println();
//
//        }
//    }

}
