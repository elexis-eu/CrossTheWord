package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.Casella;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaCarattere;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaPortale;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaSpeciale;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaSpeciale.TipoCasella;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.definizione.Definizione;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.definizione.Definizione.DifficultyDef;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.definizione.DefinizioneImmagine;

public class CaricaCruciverba
{
	/**
     * Metodo che data una rappresentazione toString di un Cruciverba ne
     * ricostruisce l'oggetto restituendolo.
     * @param path
     * @return Cruciverba
     */
    public Cruciverba carica(String toString)
    {
        String[] righe = toString.split("\n");
        int nRiga = 0;
        String primaRiga = righe[0];
        char primoId = primaRiga.charAt(0); // id dello schema principale
        Map<Character, Cruciverba> map = new HashMap<Character, Cruciverba>();
        while (nRiga < righe.length && !(primaRiga = righe[nRiga]).equals(""))
        {
            Map<Character, CasellaPortale> casellePortali = new HashMap<Character, CasellaPortale>();
            char id = primaRiga.charAt(0); // id del cruciverba
            int indexX = primaRiga.indexOf('x');
            int altezza = Integer.parseInt(primaRiga.substring(2, indexX));
            int larghezza = Integer.parseInt(primaRiga.substring(indexX + 1, primaRiga.length()));
            // Crea la matrice
            Casella[][] matrice = new Casella[altezza][larghezza];
            for (int i = 0; i < altezza; i++)
            {
                nRiga++;
                String[] riga = righe[nRiga].split(",");
                for (int j = 0; j < larghezza; j++)
                {
                    char carattere = riga[j].charAt(0);
                    if (riga[j].length() == 1)
                    {
                        if (carattere == ' ')
                            matrice[i][j] = new CasellaSpeciale(j, i, TipoCasella.INVISIBLE);
                        else if (carattere == '*')
                            matrice[i][j] = new CasellaSpeciale(j, i, TipoCasella.BLACK);
                        else
                            matrice[i][j] = new CasellaCarattere(j, i, carattere);
                    } else // allora e' una CasellaPortale
                    {
                        // toglie le due parentesi e applica lo split con il "."
                        String[] portale = riga[j].substring(1, riga[j].length() - 1).split("\\.");
                        matrice[i][j] = new CasellaPortale(j, i, portale[0].charAt(0));
                        if (portale.length != 4)
                            continue;
                        int x = Integer.parseInt(portale[2]);
                        int y = Integer.parseInt(portale[3]);
                        ((CasellaPortale) matrice[i][j]).setCasella(new CasellaPortale(x, y, portale[1].charAt(0)));
                        casellePortali.put(portale[1].charAt(0), ((CasellaPortale) matrice[i][j]));
                    }
                }
            }
            nRiga += 2;
            String riga;
            // inserisce le definizioni orizzontali
            while (Character.isDigit((riga = righe[nRiga]).charAt(0))) // Orizzontali
            {
                aggiungiDefinizione(riga, matrice, true);
                nRiga++;
            }
            nRiga++;
            // inserisce le definizioni verticali
            while (nRiga < righe.length && Character.isDigit((riga = righe[nRiga]).charAt(0)))
            {
                aggiungiDefinizione(riga, matrice, false);
                nRiga++;
            }
            Cruciverba crucy = new Cruciverba(matrice.length, matrice[0].length, matrice, id, casellePortali);
            map.put(id, crucy);
        }
        for (Cruciverba c : map.values())
            for (Character cId : c.getCasellePortali().keySet())
                c.getCasellePortali().get(cId).setCruciverba(map.get(cId));
        return map.get(primoId);
    }

    /**
     * Metodo di supporto che si occupa di decodificare una riga contenente la
     * definzione. Inserisce la Definizione in tutte le caselle che ne fanno
     * parte.
     * @param riga
     * @param board matrice che contiene le caselle
     * @param orizzontale indica se la Definizione e' orizzontale o verticale
     */
    private void aggiungiDefinizione(String riga, Casella[][] board, boolean orizzontale)
    {
        int i = 0;
        while (Character.isDigit(riga.charAt(i))) i++;
        int parentesi = riga.indexOf(")");
        int virgola = riga.indexOf(",");
        int y = Integer.parseInt(riga.substring(i + 1, virgola));
        int x = Integer.parseInt(riga.substring(virgola + 1, parentesi));
        i = Integer.parseInt(riga.substring(0, i)); // numberBox
        String definizione = riga.substring(parentesi + 3);
        String[] arrayDef = definizione.split("::");
        DifficultyDef dif  = DifficultyDef.H;
        if(arrayDef[4].charAt(0) == 'E')
            dif = DifficultyDef.E;
        else if(arrayDef[4].charAt(0) == 'M')
            dif = DifficultyDef.M;
        Definizione def = null;
        if (arrayDef.length == 5) //crea la definizione
            def = new Definizione(Long.parseLong(arrayDef[0]), arrayDef[1], arrayDef[2], arrayDef[3], dif);
        else if (arrayDef.length == 6)
            def = new DefinizioneImmagine(Long.parseLong(arrayDef[0]), arrayDef[1], arrayDef[2], arrayDef[3], dif, arrayDef[5]);
        //else
            //throw TemplateIncorectException  lancia eccezione perche' il template non e' corretto
        int len = arrayDef[1].length(); // lunghezza della parola
        CasellaCarattere c = (CasellaCarattere) board[y][x];
        c.setBoxNumber(i);
        if (orizzontale) // definizione orizzontale
        {
            int max = x + len;
            for (int j = x; j < max; j++)
            {
                CasellaCarattere cC = (CasellaCarattere) board[y][j];
                cC.setHorizontalDef(def);
            }
        } else // definizione verticale
        {
            int max = y + len;
            for (int j = y; j < max; j++)
            {
                CasellaCarattere cC = (CasellaCarattere) board[j][x];
                cC.setVerticalDef(def);
            }
        }
    }


    //--------------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------------
    //---------------------------------------  CARICA MINISCHEMI   -------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------------
    
    public static CruciverbaServer caricaMinischema(String s,ArrayList<Character> cas)
    {
		int countPort = 0;
		Map<Character, CasellaPortale> casellePortali = new HashMap<>();
    	String[] v = s.split("\n");
		String[] gen = v[0].split(" ");
		Character id = gen[0].charAt(0);
		String[] dim = gen[1].split("x");
		int altezza = Integer.parseInt(dim[0]);
		int larghezza = Integer.parseInt(dim[1]);
		Casella[][] board = new Casella[altezza][larghezza];	//controllato sempre cose'
		for(int i = 1;i<=altezza;i++)
		{
			String[] caratteriLinea = v[i].split(",");
			for(int j = 0;j<larghezza;j++)
			{
				Casella c;
				if(caratteriLinea[j].equals(" "))c = new CasellaSpeciale(j, i, TipoCasella.INVISIBLE);
				else if(caratteriLinea[j].equals("*")) c = new CasellaSpeciale(j, i, TipoCasella.BLACK);
				else if(caratteriLinea[j].startsWith("("))
				{
					c = new CasellaPortale(j, i, caratteriLinea[j].charAt(1));
					casellePortali.put(cas.get(countPort++), (CasellaPortale) c);	
				}
				else c = new CasellaCarattere(j,i,caratteriLinea[j].charAt(0));
				board[i-1][j] = c;
				
			}
			
		}
		StringBuilder sb = new StringBuilder();
		for(int i = altezza+1;i<v.length;i++)sb.append(v[i]).append("\n");
		//System.out.println(sb.toString());
		return new CruciverbaServer(altezza, larghezza, board, id,sb.toString() , new HashSet<String>(), new HashSet<String>(), casellePortali);
    	
    }
    
    public static void caricaParoleMinischema(CruciverbaServer cs,String s)
    {
    	String[] words = s.split("\t");
    	Set<String> internalWords = new HashSet<String>();
    	Set<String> internalPrefixWords = new HashSet<String>();
    	for(String w : words)
    	{
    		internalWords.add(w);
    		if(w.length()>4)internalPrefixWords.add(w.substring(0, 4));
    	}
    	cs.setInternalWords(internalWords);
    	cs.setInternalPrefixWords(internalPrefixWords);
    }
    
    
    
    
    
    
    
    public static void main(String[] args) throws IOException
    {
    	String p  = "/home/student/crucyDef/A_MINISCHEMI_DUMP/newminischemi/tEsa/a/a.txt";
		BufferedReader br = Files.newBufferedReader(Paths.get(p));
		StringBuilder sb = new StringBuilder();
		for(String s = br.readLine();s!=null;s=br.readLine())sb.append(s).append("\n");
		br.close();
		ArrayList<Character> cas = new ArrayList<>();
		cas.add('b');
		CaricaCruciverba.caricaMinischema(sb.toString(),cas);
		
    }


}
