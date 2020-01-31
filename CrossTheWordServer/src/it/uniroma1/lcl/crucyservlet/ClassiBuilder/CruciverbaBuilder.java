package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.Casella;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaCarattere;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaPortale;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaSpeciale;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.template.TemplateErrorException;


import static it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaSpeciale.TipoCasella.*;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.*;
import it.uniroma1.lcl.crucyservlet.mongodb.DefinitionDatabase;


/**
 * Generatore di Cruciverba secondo il file template che viene passato
 * in input.
 * Gli schemi generati non contengono le definizioni, ma gli
 * indici. Prima di scaricare un Cruciverba dal server gli indici
 * saranno convertiti.
 * 
 * @author Ele1955 edited by Papi Suadoni
 *
 */
public class CruciverbaBuilder
{
    private static String macroName = "" ;
    private static final String NAME_WORDS = "-word";

    private static String pathOutputDirectory = "";
    private Integer altezza;
    private Integer larghezza;
    private int countMinischema = 0;
    private static int maxGenerate = 0;
    private static final int MAX_WRITE = 1000;
    /**
     * Inizializzazione delle matrice, utilizzata quando viene utilizzato
     * per tenere traccia delle caselle BLACK e INVISIBLE
     */
    private Casella[][] matrice;
    /**
     * Carattere che indentifica il mini-schema, per essere differenziato da
     * tutti gli altri che compongono lo schema finale
     */
    private Character id;
    /**
     * Matrice di interi che memorizza in posizione (x,y) quante caselle bianche
     * ci sono a destra di questa casella, compresa la casella.
     */
    private int[][] destra;
    /**
     * Matrice di interi che memorizza in posizione (x,y) quante caselle bianche
     * ci sono sotto questa casella, compresa la casella stessa.
     */
    private int[][] basso;
    /**
     * Memorizza dinamicamente in fase di costruzione quale prefisso si è
     * composto sopra la casella (x,y).
     */
    private Trie[][] alto;
    /**
     * Set che contiene le caselle nere che devono essere inserite nel
     * cruciverba.
     */
    private Set<Punto> caselleNere = new HashSet<>();
    /**
     * Dizionario che ha come chiave un char e come valore una Stringa che 
     * rappresenta il template di un mini-schema.
     */
    private Map<Character, String> templates;
    /**
     * Dizionario che contiene le coordinate delle CasellePortale. Come chiave
     * viene utilizzata un Character che identifica in maniera univoca la casella
     * e il mini-schema a cui si può collegare.
     */
    private Map<Character, Punto> puntiCasellePortali = new HashMap<>();
    /**
     * Dizionario che contiene come chiavi tutte le possibili parole che si possono
     * inserire nel cruciverba. A ciascuna parola viene associato il suo indice.
     */
    private Map<String,List< String>> paroleIndicizzate;
    /**
     * Dizionario che rappresenta il dizionario di tutte le possibili parole che si
     * possono inserire nel cruciverba.
     */
    private Map<Integer, Trie> dizionario;
    
    private final int PERCENTUALE_REINSERIMENTO = 99;
    //difficola' delle definizioni:
    private final int DIFFICOLTA_FACILE = 3; //inferiore a 0.30
    private final int DIFFICOLTA_MEDIA = 6; //compresa tra 0.30 e 0.60
    //difficlta' DIFFICILE oltre 0.60

    /**
     * Classe interna che rappresenta un punto all'interno della matrice del
     * cruciverba.
     */
    private class Punto
    {
        private int colonna;
        private int riga;

        /**
         * Costruttore che prende come parametri la posizione del punto.
         * @param colonna Colonna della tabella su cui si trova il punto
         * individuato.
         * @param riga Riga della tabella su cui si trova il punto individuato.
         */
        private Punto(int riga, int colonna)
        {
            setColonnaRiga(riga, colonna);
        }

        /**
         * Imposta le coordinate del punto ai parametri passati dalla funzione.
         * @param colonna Colonna della tabella su cui si trova il punto
         * individuato.
         * @param riga Riga della tabella su cui si trova il punto individuato.
         */
        public void setColonnaRiga(int riga, int colonna)
        {
            this.colonna = colonna;
            this.riga = riga;
        }

        /**
         * Confronta l'oggetto su cui viene chiamato il metodo con l'oggetto
         * passato come parametro. Restituisce true se appartengono alla
         * medesima classe e i campi dei due oggetti hanno a due a due gli
         * stessi valori.
         */
        @Override
        public boolean equals(Object a)
        {
            if (a == null)
                return false;
            if (a.getClass() != this.getClass())
                return false;
            Punto c = (Punto) a;
            return this.colonna == c.colonna && this.riga == c.riga;
        }

        /**
         * Restituisce un intero che rapppresenta univocamente il punto
         * all'interno della tabella del cruciverba.
         */
        @Override
        public int hashCode()
        {
            return 31 * colonna + riga;
        }
    }

    /**
     * Metodo che permette d'impostare l'altezza del cruciverba al valore del
     * parametro.
     * @param altezza Altezza del futuro cruciverba.
     * @return l'oggetto stesso secondo il pattern del builder
     */
    private void setAltezza(int altezza)
    {
        this.altezza = altezza;
        destra = null;
    }
    public CruciverbaBuilder setMaxGenerate(int maxGenerate)
    {
        this.maxGenerate = maxGenerate;
        return this;
    }

    /**
     * Metodo che permette d'impostare la larghezza del cruciverba al valore del
     * parametro.
     * 
     * @param larghezza Larghezza del futuro cruciverba.
     * @return l'oggetto stesso secondo il pattern del builder
     */
    private void setLarghezza(int larghezza)
    {
        this.larghezza = larghezza;
        destra = null;
    }

    /**
     * Imposta le parole che possono essere inserite nel cruciverba e le
     * relative indicizzazioni
     * @param parole mappa con chiave parola e valore l'indice (long)
     * @return
     */
    private void setParoleIndicizzate(Map<String, List<String>> parole)
    {
        this.paroleIndicizzate = parole;
    }	
    
    public Map<String,List<String>>getParoleIndicizzate()
    {
    	return paroleIndicizzate;
    }
    
    /**
     * Metodo che prende in input una stringa indicante la path  di un file.
     * Il file contiene una sequenza di caratteri indicanti uno schema (template)
     * del Cruciverba che si vuole costruire.
     * Il file contiene tutte le informazioni per poter dividere il Template 
     * in vari mini-schemi, e viene settato il campo Map<Character, String> templates.
     * @param path percorso del file
     * @return this CruciverbaBuilder
     * @throws IOException si solleva in caso di problemi con il percorso
     */
    public CruciverbaBuilder setTemplateFromFile(String path) throws IOException
    {
        this.caselleNere = new HashSet<>(); // svuota tutte le caselle nere
        Map<Character, StringBuilder> templateSB = new HashMap<>();
        this.templates = new HashMap<>();
        BufferedReader br = Files.newBufferedReader(Paths.get(path));
        String line = br.readLine();
        while (line != null)
        {
            Map<Character, String> mappa = new HashMap<>();
            String[] chars = line.split(",");
            char precedente = 0;
            String temp = "";
            String spazi = "";
            for (int i = 0; i < chars.length; i++)
            {
                String str = chars[i];
                char primo = str.charAt(0);
                char carattere = str.charAt(1);
                if (primo == 'P')
                {
                    char t = 0;
                    boolean b = (i == 0 || precedente == str.charAt(1));
                    primo = str.charAt((b) ? 1 : 2);
                    carattere = 'P';
                    t = str.charAt((b) ? 2 : 1);
                    aggiungiRigaAlTemplate(mappa, t, spazi + "(" + str.substring(1).replace(t + "", "") + ")");
                }
                if (precedente == 0 || primo == precedente || primo == '-')
                {
                    precedente = (primo == '-') ? precedente : primo;
                    temp += (carattere == 'P') ? "(" + str.substring(1).replace(precedente + "", "") + ")" : carattere;
                } else
                {
                    aggiungiRigaAlTemplate(mappa, precedente, temp);
                    precedente = primo;
                    temp = spazi + carattere;
                }
                if (i == chars.length - 1)
                    aggiungiRigaAlTemplate(mappa, precedente, temp);
                spazi += "-";
            }
            for (Character key : mappa.keySet())
                if (templateSB.containsKey(key))
                    templateSB.get(key).append("\n").append(mappa.get(key));
                else
                    templateSB.put(key, new StringBuilder(mappa.get(key)));
            line = br.readLine();
        }
        for(Character c : templateSB.keySet())
            this.templates.put(c, templateSB.get(c).toString());
        
        return this;
    }

    /**
     * Metodo di supporto che aggiunge la riga del template che si sta creando
     * controllando prima che non ci fossero pezzi di riga attualmente in
     * costruzione, in tal caso fa una somma delle due.
     * @param mappa
     * @param prec
     * @param str
     */
    private void aggiungiRigaAlTemplate(Map<Character, String> mappa, char prec, String str)
    {
        if (mappa.containsKey(prec))
        {
            String riga = mappa.get(prec);
            int count = 0;
            int i = 0;
            while(i < riga.length())
            {
                if (riga.charAt(i) == '(') 
                    do{
                        count++;
                    }while(riga.charAt(++i) != ')' && i < riga.length());
                i++;
            }
            int len = riga.length() - count;
            mappa.put(prec, mappa.get(prec) + str.substring(len));
        } else
            mappa.put(prec, str);
    }
    
    /**
     * Metodo che prende in input una stringa rappresentante un mini-schema
     * e si occupa di impostare tutto il CruciverbaBuilder.
     * Imposta l'altezza, larghezza, caselle nere, caselle trasparenti e 
     * caselle portali.
     * @param template
     * @param c
     * @throws TemplateErrorException
     */
    private void impostaBuilder(String template, char c) throws TemplateErrorException
    {
        String[] lines = template.split("\n");
        int min = lines[0].length();
        int max = 0;
        for(int k = 0; k < lines.length ; k++) //calcola altezza e larghezza del cruciverba
        {
            String s = lines[k];
            int j = s.length()-1;
            while(j >= 0 && s.charAt(j) == '-') //calcola gli spazi bianchi finali
                j--;
            s = s.substring(0, j+1);
            lines[k] = s;
            int i = 0;
            while(i < s.length() && s.charAt(i) == '-') //calcola gli spazi bianchi iniziali
                i++;
            min = Math.min(min, i); 
            int count = 0;
            while(i < s.length())
            {
                if (s.charAt(i) == '(') 
                    do{
                        count++;
                    }while(s.charAt(++i) != ')');
                else i++;
            }
            max = Math.max(max, i - count);
        }
        this.setAltezza(lines.length);
        this.setLarghezza(max-min);
        this.id = c;
        this.caselleNere = new HashSet<>();
        this.puntiCasellePortali = new HashMap<>();
        this.matrice = new Casella[altezza][larghezza];
        if ((larghezza == 1 && altezza == 1) || altezza < 1 || larghezza < 1)
            throw new TemplateErrorException("Template non valido");
        for(int i=0; i<lines.length; i++)
        {
            String str = lines[i].substring(min, lines[i].length());
            int portal = 0;
            int meno = 0;
            int j2 = 0;
            for(int j=0; j<str.length(); j++){
                char car = str.charAt(j);
                j2 = j-meno;
                if(car == '(')
                {
                    portal = j;
                    while(str.charAt(++j) != ')');
                    //da sistemare!
                    puntiCasellePortali.put(str.substring(portal + 1, j).charAt(0), new Punto(i, j2));
                    meno += j-portal;
                    matrice[i][j2] = new CasellaPortale(j2, i, 'P');
                }
                else if (car == '-' || car == ' ')
                {
                    matrice[i][j2] = new CasellaSpeciale(j2, i, INVISIBLE);
                    this.caselleNere.add(new Punto(i, j2));
                } else if (car == '0')
                {
                    matrice[i][j2] = new CasellaCarattere(j2, i, '0');
                }
                else if (car == '*')
                {
                    matrice[i][j2] = new CasellaSpeciale(j2, i, BLACK);
                    this.caselleNere.add(new Punto(i, j2));
                }
                if(j + 1 >= str.length())
                {
                    int var = j-meno;
                    while(++var < larghezza)
                    {
                        matrice[i][var] = new CasellaSpeciale(var, i, INVISIBLE);
                        this.caselleNere.add(new Punto(i, var));
                    }
                }
            }
        }
    }
    public CruciverbaBuilder setPathOutput (String parhOutput)
    {
        this.pathOutputDirectory = parhOutput;
        return  this;
    }
    public CruciverbaBuilder setMacroName (String macroName)
    {
        this.macroName = macroName;
        return this;
    }
    /**
     * Metodo che genera una mappa che ha come chiave una Character e come valore 
     * un set di mini-schemi dello stesso pezzo del Macro-schema. La chiave differenzia
     * i vari mini-schemi, e servirà all' UNIONE per unire i pezzi giusti per creare
     * un Cruiciverba completo.
     * Prende in input gli enum che settano la tipologia dei cruciverba generati.
     * Tutti i cruciverba della mappa hanno lo stesso dominio e difficolta'.
     * Prende in input anche un intero (maxGenerate) che setta il massimo dei mini-schemi per set che si
     * voglio creare.
     * @param template path di un file che rappresenta il template dello schema
     * @param maxGenerate numero massimo di mini-schemi per set che si vogliono generare
     * @return
     * @throws IOException 
     * @throws JSONException 
     * @throws TemplateErrorException 
     */
    public Map<Character, Set<CruciverbaServer>> build(Difficulty difficulty, String template, Integer maxGenerate) throws IOException, TemplateErrorException
    {
       
        Map<String, String> mappa ;
        
        if(difficulty == null)  // get all words
        {
            mappa = DefinitionDatabase.getInstance().getDefinitions(Difficulty.EASY.name()); //modifica, fatta a mano, del prelievo delle definizioni fatta da giov, potrebbe non andare
            mappa.putAll(DefinitionDatabase.getInstance().getDefinitions(Difficulty.MEDIUM.name()));
            System.out.println(mappa.size());
            mappa.putAll(DefinitionDatabase.getInstance().getDefinitions(Difficulty.HARD.name()));
        }
        else
        {
            mappa = DefinitionDatabase.getInstance().getDefinitions(difficulty.name());
        }
        Map<String, List<String>> mappaParoleIndici = new HashMap<>();
        for(Map.Entry<String,String> s: mappa.entrySet())
        {
            List<String> set = mappaParoleIndici.getOrDefault(s.getValue(), new ArrayList<>());
            set.add(s.getKey());
            mappaParoleIndici.put(s.getValue(), set);
        }
        System.out.println(mappaParoleIndici.size());
       
        System.out.println("LA DIMENSIONE e'"+ mappaParoleIndici.size());

        this.setParoleIndicizzate(mappaParoleIndici);
        this.setTemplateFromFile(template);
        Map<Character, Set<CruciverbaServer>> map = new HashMap<>();
      

        for (Character key : templates.keySet()){
            impostaBuilder(templates.get(key), key.charValue());
            countMinischema = 0;
            Set<CruciverbaServer> set = buildAll(mappaParoleIndici, maxGenerate, key.charValue());
            System.out.println("==Finito il set: '" + key + "' che contiene : " + set.size() + "mini-schemi");
            map.put(key, set);
        } 
        return map;
    }

    public Map<Character, String> getTemplates(String templatePath) throws IOException
    {
        this.setTemplateFromFile(templatePath);
        return templates;
    }
    public void buildOnThread(Character key , String templateMinischmea) throws TemplateErrorException
    {
    	System.out.println(templateMinischmea);
        Map<String, String> mappa ;
        
        System.out.println("wow");
       // if(true)mappa = testCaricaDaFile();
        //else{
        mappa = DefinitionDatabase.getInstance().getDefinitions(Difficulty.EASY.name()); //modifica, fatta a mano, del prelievo delle definizioni fatta da giov, potrebbe non andare
        System.out.println("wow2");
      
        System.out.println("MAPPA DEF "+ mappa.size());
        mappa.putAll(DefinitionDatabase.getInstance().getDefinitions( Difficulty.MEDIUM.name()));
       
        mappa.putAll(DefinitionDatabase.getInstance().getDefinitions( Difficulty.HARD.name()));
        System.out.println("preciclo");
       // }
        
        Map<String, List<String>> mappaParoleIndici = new HashMap<>();

        for(Map.Entry<String,String> s: mappa.entrySet())
        {
            List<String> set = mappaParoleIndici.getOrDefault(s.getValue(), new ArrayList<>());
            set.add(s.getKey());
            mappaParoleIndici.put(s.getValue(), set);
//            if (mappaParoleIndici.size() >10000 )
//                break;
        }
        	
       
        this.setParoleIndicizzate(mappaParoleIndici);

        impostaBuilder(templateMinischmea, key.charValue());

        countMinischema = 0;
        Set<CruciverbaServer> set = buildAll(mappaParoleIndici, maxGenerate, key.charValue());


        System.out.println("Finito il set: '" + key + "' che contiene : " + set.size() + "mini-schemi");
    }
    /**
     * Metodo che genera un set di CruiciverbaServer.
     * Questi CruciverbaServer sono tutti mini-schemi dello stesso pezzo.
     * Una volta generato un Cruciverba rimuove le parole utilizzate dalla
     * Map da cui vengono prese le parole per la generazione. Una percentuale
     * indicata dalla costante final PERCENTUALE_REINSERIMENTO viene riutilizzata.
     * @param mappa
     * @param maxGenerate
     * @return Set set di mini-schemi dello stesso pezzo
     */
    private Set<CruciverbaServer> buildAll(Map<String, List<String>> mappa, Integer maxGenerate, char codMinischema)
    {
    	System.out.println("CODICE "+codMinischema);
        int countCruciverba = 1;
        System.out.println(countCruciverba);
        Map<String, List<String>> map = new HashMap<>(mappa);
        String[][] curr = new String[altezza][larghezza];
        Set<CruciverbaServer> set = new HashSet<>();
        generaDestra();
        generaBasso();
        if (!isValido())
            return set;
        //while(true){
        System.out.println("======" );
        System.out.println( countCruciverba +" > " + maxGenerate);
        if(countCruciverba >= maxGenerate)//////////////////////
            return set;//////////////////////
        //break;
        this.setParoleIndicizzate(map);
        generaParole();
        if (generaAlto())
            return new HashSet<>();
        Set<String[][]> sol = new HashSet<>();
//            Long time = System.currentTimeMillis();
       
        System.out.println(maxGenerate);
        System.out.println("======" );
        buildCruci(sol, curr, 0, 0,0);
        if(sol.size() != 0)
        {
            System.out.println("++generati:\t" + id + "\t" + countMinischema);
            for(String[][] crucy : sol)
                generaDefinizioni(crucy, id);
            sol.clear();
        }
        //break;
        //CruciverbaServer crucy = null;
//            for (String[][] cruci : sol)
//            {
//                crucy = generaDefinizioni(cruci, codMinischema);
//                System.out.println("Creati: " + countCruciverba++);
//                //set.add(crucy); //todo delete when want output
//                //calcola la percentuale (PERCENTUALE_REINSERIMENTO) delle parole interne
////                int percentuale = (int) ((PERCENTUALE_REINSERIMENTO/ 100.0) * crucy.getInternalWords().size());
////                Set<Integer> interi = new HashSet<>();
////                for(int i = 0; i < percentuale; i++)
////                    interi.add((int) (Math.random()*crucy.getInternalWords().size()));
////                int i = 0;
////                //toglie le parole dalla mappa tranne un PERCENTUALE_REINSERIMENTO per cento
////                for(String word : crucy.getInternalWords())
////                {
////                    if(interi.contains(i++))
////                        continue;
////                    map.remove(word);
////                }
////                if ((System.currentTimeMillis() - time)/1000 > 120)// se impiega più di due minuti a generare
////                    break;
//            }
//            if (crucy == null)
//                break;
//            map.remove(crucy.getInternalWords().get(new Random().nextInt(crucy.getInternalWords().size())));
//        }
        return set;
    }

    /**
     * Genera dizionario: dividendo le parole per lunghezza ed inserendole nella
     * rispettiva Trie.
     */
    private void generaParole()
    {
        dizionario = new HashMap<Integer, Trie>();
        for (String i : this.paroleIndicizzate.keySet())
        {
            int len = i.length();
            if (len > altezza && len > larghezza)
                continue;
            if (dizionario.containsKey(len))
                dizionario.get(len).addWord(i);
            else
                dizionario.put(len, new Trie(i));
        }
    }

    /**
     * Data una matrice di stringhe che rappresenta il cruciverba associa alle
     * parole presenti nella matrice le relative definizioni.
     * @param a matrice di stringhe che contiene lo schema del cruciverba.
     * @return un'istanza della classe cruciverba corrispondente allo schema
     * passato come parametro.
     */
    private CruciverbaServer generaDefinizioni(String[][] a, char minischema)
    {
        Integer[][] nrDef = new Integer[altezza][larghezza];
        Casella[][] board = new Casella[matrice.length][];
        Set<String> internalWords = new HashSet<>();
        Set<String> internalPrefixWords = new HashSet<>();
        Map<Character, CasellaPortale> casellePortali = new HashMap<>();
        StringBuilder definizioni = new StringBuilder();
        
        //clona la matrice, salvando le posizioni della caselle BLACK e INVISIBLE
        for(int i = 0; i < matrice.length; i++)
            board[i] = matrice[i].clone();

        for (int i = 0; i < altezza; i++)
        {
            int j = 0;
            while (j < larghezza)
            {
                for (int k = 0; k < a[i][j].length(); k++)
                {
                    if (a[i][j].charAt(k) != '*')
                        board[i][j + k] = new CasellaCarattere(j+k, i, a[i][j].charAt(k));
                }
                j += a[i][j].length();
            }
        }
        //aggiungo le caselle portale
        for (Map.Entry<Character, Punto> entry : this.puntiCasellePortali.entrySet())
        {
            Punto p = entry.getValue();
            CasellaPortale cp= new  CasellaPortale(p.colonna, p.riga, 
                    ((CasellaCarattere)(board[p.riga][p.colonna])).getCharacter());
            board[p.riga][p.colonna] = cp;
            casellePortali.put(entry.getKey(), cp);
        }

        definizioni.append("Orizzontali:\n");
        int count = 0;
        for (int i = 0; i < altezza; i++)
            for (int j = 0; j < larghezza; j++)
            {
                // calcolo i numeri corrispondenti alle definizioni
                if ((a[i][j] != null && a[i][j].length() > 1)
                        || (basso[i][j] > 1 && (i == 0 || (i > 0 && basso[i - 1][j] == 0))))
                    nrDef[i][j] = ++count;

                // imposto la casella di partenza della parola e
                // inserisco la parolaDefinizione nella lista horizontaWords
                if (a[i][j] != null && a[i][j].length() > 1 && board[i][j] != null)
                {
                    String word = a[i][j];
                    internalPrefixWords.add( (word.length() > 5) ? word.substring(0, 5) : word);
                    internalWords.add(word);
                    definizioni.append(count).append("(").append(i).append(",").append(j).append("): ").append("_ID")
                        .append(getRandomIndex(word)).append("ID_").append("\n");
                }
            }
        StringBuilder s = new StringBuilder();
        definizioni.append("Verticali:\n");
        // associo alle parole verticali le rispettive definizioni
        for (int i = 0; i < altezza; i++)
        {
            for (int j = 0; j < larghezza; j++)
            {
                if (basso[i][j] > 1 && (i == 0 || (i > 0 && basso[i - 1][j] == 0)))
                {
                    int k = i;
                    while (k < altezza && basso[k][j] != 0)
                        s.append(((CasellaCarattere) board[k++][j]).getCharacter());
                    String word = s.toString();
                    internalWords.add(word);
                    internalPrefixWords.add( (word.length() > 5) ? word.substring(0, 5) : word);
                    definizioni.append(nrDef[i][j]).append("(").append(i).append(",").append(j).append("): ").append("_ID")
                        .append(getRandomIndex(word)).append("ID_").append("\n");
                    s = new StringBuilder();
                }
            }
            s = new StringBuilder();
        }
        CruciverbaServer cruciverbaServer = new CruciverbaServer(altezza, larghezza, board, this.id,
                definizioni.toString(), internalWords, internalPrefixWords, casellePortali);
        try {
            String stringPortals = cruciverbaServer.getOrderedPortals();
            String fileName;
            File folder = new File(pathOutputDirectory + File.separator + macroName + File.separator + minischema + File.separator + stringPortals);
            String filenameWord;
            if (!folder.exists())
            {
                folder.mkdirs();
                fileName = File.separator + 0 + ".txt";
                filenameWord =File.separator+  0+ NAME_WORDS;
            }
            else {
                String[] list = folder.list();
                Arrays.sort(list);
                int num = countMinischema++;
                fileName = File.separator + num + ".txt";
                filenameWord = File.separator + num+ NAME_WORDS;
            }
            File file = new File(folder.getAbsolutePath() + fileName);
            File fileWords = new File(folder.getAbsolutePath() + filenameWord);
            fileWords.createNewFile();
            file.createNewFile();
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file)))
            {
                bufferedWriter.append(cruciverbaServer.toString());
                bufferedWriter.flush();
            }
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileWords)))
            {
                for(String word : cruciverbaServer.getInternalWords())
                    bufferedWriter.append(word + "\t");
                bufferedWriter.flush();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cruciverbaServer;
    }

    private String getRandomIndex(String word)
    {
        List<String> list = paroleIndicizzate.get(word);
        Random random = new Random();

        return list.get(random.nextInt(list.size()));

    }

    /**
     * Genera matrice di interi che memorizza in posizione (x,y) quante caselle
     * bianche ci sono a destra di questa casella, compresa la casella.
     */
    private void generaDestra()
    {
        destra = new int[altezza][larghezza];
        int dim;
        for (int i = 0; i < altezza; i++)
        {
            dim = 0;
            for (int j = larghezza - 1; j >= 0; j--)
            {
                if (caselleNere.contains(new Punto(i, j)))
                    dim = 0;
                else
                    destra[i][j] = ++dim;
            }
        }
    }

    /**
     * Genera una matrice di interi che memorizza in posizione (x,y) quante
     * caselle bianche ci sono sotto questa casella, compresa la casella.
     */
    private void generaBasso()
    {
        basso = new int[altezza][larghezza];
        int dim;
        for (int i = 0; i < larghezza; i++)
        {
            dim = 0;
            for (int j = altezza - 1; j >= 0; j--)
            {
                if (caselleNere.contains(new Punto(j, i)))
                    dim = 0;
                else
                    basso[j][i] = ++dim;
            }
        }
    }

    /**
     * Genera la prima riga della matrice alto
     * @return true se non è possibile costruire un cruciverba, false se invece
     * potrebbe esserlo.
     */
    private boolean generaAlto()
    {
        alto = new Trie[altezza + 1][larghezza];
        for (int i = 0; i < larghezza; i++)
        {
            if (dizionario.containsKey(basso[0][i]))
                alto[0][i] = dizionario.get(basso[0][i]);
            else if (basso[0][i] == 1)
                alto[0][i] = null;
            else if (basso[0][i] == 0)
                alto[0][i] = new Trie();
            else
                return true;
        }
        return false;
    }

    /**
     * Controlla che il Cruciverba sia idoneo, ovvero
     * che non abbia caselle singole bianche isolate.
     * @return booleano
     */
    private boolean isValido()
    {
        for (int i = 0; i < altezza; i++)
            for (int j = 0; j < larghezza; j++)
            {
                if (i == 0 || basso[i - 1][j] == 0)
                    if (i == altezza - 1 || basso[i + 1][j] == 0)
                        if (j == larghezza - 1 || basso[i][j + 1] == 0)
                            if (j == 0 || basso[i][j - 1] == 0)
                                if (basso[i][j] != 0)
                                    return false;
            }
        return true;
    }

    /**
     * Metodo ricorsivo che inserisce fisicamente le parole nel Cruciverba.
     * @param sol insieme che contiene i cruciverba finora generati
     * @param curr Matrice di stringhe che contiene l'evoluzione dello schema
     * del cruciverba
     * @param h riga corrente.
     * @param l colonna corrente.
     * si vogliono costruire tutti i possibili cruciverba.
     * @return
     */
    private Set<String[][]> buildCruci(Set<String[][]> sol, String[][] curr, int h, int l, int k)
    {
    	//if( h==0 && l==0)System.out.println(altezza+ " "  + larghezza+ " "+ h);
    	//System.exit(0);
    	
    	if (h == altezza) // Caso base -> condizione di uscita dalla ricorsione
        {
            List<String> ss = new ArrayList<String>();
            // Controllo se con il completamento dell'ultima riga si sono
            // formate parole in verticale già presenti
            // nel cruciverba.
            for (int i = 0; i < larghezza; i++)
            {
                if (alto[h][i] != null)
                {
                    if (!dizionario.get(alto[h][i].getPrefix().length()).hideIfPossible(alto[h][i].getPrefix()))
                    {
                        for (String s : ss)
                            dizionario.get(s.length()).addWord(s);
                        return sol;
                    } else
                        ss.add(alto[h][i].getPrefix());
                }
            }
            // Memorizzo il cruciverba generato.
            String[][] temp= new String[altezza][larghezza];
            for(int i=0;i<altezza;i++)
                for (int j = 0; j < larghezza; j++)
                    temp[i][j] = curr[i][j];
            for (String s : ss)
                dizionario.get(s.length()).addWord(s);
            // Uscita dalla ricorsione.
            sol.add(temp);

            if(sol.size() == MAX_WRITE)
            {
            	System.out.println("generati:\t" + id + "\t" + countMinischema);
                for(String[][] crucy : sol)
                    generaDefinizioni(crucy, id);
                sol.clear();
                //if(countMinischema>MAX_WRITE) System.exit(0);
                if(countMinischema>MAX_WRITE) return sol;
                
                /////////////////////
            }
            return sol;
        }
        if (destra[h][l] == 0)// CASELLA NERA
        {
            curr[h][l] = "*";
            // Se si forma in verticale una parola già inserita mi blocco
            if (h - 2 > 0 && basso[h - 2][l] > 1)
                if (!dizionario.get(alto[h][l].getPrefix().length()).hideIfPossible(alto[h][l].getPrefix()))
                    return sol;
            // Se sotto la casella nera inizia una nuova parola inizializzo alto
            if (h + 1 < altezza && basso[h + 1][l] > 1)
                alto[h + 1][l] = dizionario.get(basso[h + 1][l]);
            // RICORSIONE:
            // Se sono a fine riga vado alla successiva
            if (l + 1 >= larghezza)
            {
            	if(k<MAX_WRITE+2)  buildCruci(sol, curr, h + 1, 0,k);
            	//buildCruci(sol, curr, h + 1, 0,k);
                // Riaggiungo la parola verticale se la avevo tolta
                if (h - 2 > 0 && basso[h - 2][l] > 1)
                    dizionario.get(alto[h][l].getPrefix().length()).addWord(alto[h][l].getPrefix());
            }
            // Se non sono a fine riga mi sposto avanti di un caattere
            else
            {
            	if(k<MAX_WRITE+2)buildCruci(sol, curr, h, l + 1,k); 
            	//buildCruci(sol, curr, h, l + 1,k); 
                // Riaggiungo la parola verticale se la avevo tolta
                if (h - 2 > 0 && basso[h - 2][l] > 1)
                    dizionario.get(alto[h][l].getPrefix().length()).addWord(alto[h][l].getPrefix());
            }
        } else if (destra[h][l] == 1) // SINGOLETTO ORIZZONTALE
        {
            // I possbili singoletti sono determinati dalle informazioni che ho
            // in verticale
            // N.B. Ho SICURAMENTE informazioni in verticali non le ho se e solo
            // se ho
            // 1) una casella nera sotto un'altra casella nera
            // 2) un singoletto verticale
            // Ma non trattandosi di una casella nera e non potendo essere
            // presente un singoletto
            // orizzontale e verticale allo stesso tempo per isValido() iniziale
            // Per tutti i possibili caratteri che posso inserire
            if(alto[h][l] != null) // correzione
                for (Character c : alto[h][l].getSons())
                {
                    // Inserisco il carattere nella matrice soluzione
                    curr[h][l] = "" + c;
                    // Modifico le informazioni in verticale poichè inserisco c
                    alto[h + 1][l] = alto[h][l].moveTo(c);
                    // RICORSIONE
                    if (l + 1 >= larghezza)
                    {   
                    	 if(k<MAX_WRITE+2) buildCruci(sol, curr, h + 1, 0,k);
                    	 //buildCruci(sol, curr, h + 1, 0,k);
                    }
                    else
                    {	
                    	 if(k<MAX_WRITE+2) buildCruci(sol, curr, h, l + 1,k);
                    	//buildCruci(sol, curr, h, l + 1,k);
                    }
                    if (sol.size() == 1)
                        return sol;
                }

        }
        else
        {
            // PAROLA ORIZZONTALE
            // Genero tutte le possibili parole orizzontali per questa posizione
            Set<String> possibiliParole = generaPossibiliParole(h, l);
            System.out.println(possibiliParole.size());
            // Per ogni possibile parola p
            for (String p : possibiliParole)
            {
                // Inserisco la parola nel cruciverba e modifico le informazioni
                // verticali
                inserisci(p, curr, h, l);
                // RICORSIONE
                if (l + p.length() >= larghezza)
                {	
                	 if(k<MAX_WRITE+2)buildCruci(sol, curr, h + 1, 0,k);
                	buildCruci(sol, curr, h + 1, 0,k);
                }
                else
                {	
                	 if(k<MAX_WRITE+2)buildCruci(sol, curr, h, l + p.length(),k);
                	buildCruci(sol, curr, h, l + p.length(),k);
                }
                dizionario.get(p.length()).addWord(p);
                if (sol.size() >= maxGenerate)
                    return sol;
            }
        }
        return sol;
    }

    /**
     * Genera le possibili parole orizzontali che si possono inserire a partire
     * dalla posizione (h,l) in base alle informazioni calcolate finora.
     * @param h altezza a cui inizia la nuuova parola orizzontale
     * @param l colonna a partire dalla quale inizia la parola
     * @return set contenente tutte le parole che si possono inserire in
     * orizzontale alle coordintate (h,l).
     */
    private Set<String> generaPossibiliParole(int h, int l)
    {
        int len = destra[h][l];
        HashSet<String> pp = new HashSet<String>();
        Map<Integer, Set<Character>> possibiliCaratteri = new HashMap<>();
        for (int i = l; i < l + len; i++)
            if (alto[h][i] != null)
                possibiliCaratteri.put(i, alto[h][i].getSons());
        return generaPossibiliParole(len, pp, dizionario.get(len), possibiliCaratteri, "", h, l);
    }

    /**
     * Genera le possibili parole in orizzontale, inserendo in posizione (h,l)
     * uno tra i possibili caratteri per quella posizione considerando sia il
     * prefisso orizzontale che verticale fino al completamento della parola.
     * @param len Lunghezza che deve avere la parola.
     * @param pp Set di parole possibili generate finora.
     * @param curr Posizione nella Trie del riferimento che tiene conto del
     * prefisso orizzontale.
     * @param possibiliCaratteri Mappa che associa all'intero 'l' i possibili
     * caratteri che possono essere inseriti su quella colonna
     * @param s Stringa che si è formata finora.
     * @param h Altezza della riga sulla quale lavoriamo.
     * @param l Posizione della colonna su cui proviamo a inserire i caratteri.
     * @return Un set contenente tutte le possibili parole che si possono
     * formare in orizzontale con i dati memorizzati nel dizionario.
     */
    private Set<String> generaPossibiliParole(int len, Set<String> pp, Trie curr,
            Map<Integer, Set<Character>> possibiliCaratteri, String s, int h, int l)
    {
        if (s.length() == len)
        {
            pp.add(s);
            return pp;
        }
        if(curr == null) return new HashSet<String>(); //correzione
        if (alto[h][l] == null)
            possibiliCaratteri.put(l, curr.getSons());
        for (Character c : possibiliCaratteri.get(l))
            if (curr.isSon(c))
                generaPossibiliParole(len, pp, curr.moveTo(c), possibiliCaratteri, s + c, h, l + 1);
        return pp;
    }

    /**
     * Memorizza che una certa parola è stata inserita nello schema del
     * cruciverba e la "nasconde" nel dizionario.
     * @param p Parola da inserire.
     * @param sol Matrice di stringhe che rappresenta lo stato del cruciverba.
     * @param h Altezza alla quale viene inserita la parola.
     * @param l Colonna a partire dalla quale viene inserita la parola
     * orizzontale.
     */
    private void inserisci(String p, String[][] sol, int h, int l)
    {
        sol[h][l] = p;
        dizionario.get(p.length()).hideWord(p);
        for (int i = 0; i < p.length(); i++)
            if (alto[h][l + i] != null)
                alto[h + 1][l + i] = alto[h][l + i].moveTo(p.charAt(i));
    }
    
    
    private  Map<String, String> testCaricaDaFile() 
    {
  		Map<String, String> map = new HashMap<String,String>();
  		String s=null;

    	FileReader f=null;
        BufferedReader b = null;

		try {
			f = new FileReader("0302SALVATI.txt");
			b = new BufferedReader(f);
			 while(true)
		        {
		          s=b.readLine();
		          if(s==null) break;
		          
		          String[] split = s.split("\t");
		          map.put(split[0], split[1].replace(" ", ""));
		         
		        }
		} catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		
		return map;
    }
    
    
}