package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Difficulty;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Domains;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Language;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.LimitedDomains;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.Casella;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaPortale;

//import it.uniroma1.lcl.crucy.MongoDB.CrucyDB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * @author Matteo Costantini
 * @author Federico de Lellis
 */
public class UnisciSchemi
{

    /**
     * Mappa con chiave l'ID del minischema e come valore il set di dei minischemi associati a quell'ID
     */
	private Map<Character, Set<CruciverbaServer>> characterSetMapCrucy = new HashMap<>();

    /**
     * Lista contenente la rappresentazione formato String di ogni maxischema generato
     */
    private List<String> maxiCrucy = new ArrayList<>();

    private int counter = 0;
    
    /**
     * Minischema iniziale
     */
    private CruciverbaServer starterCrucy;


    /**
     * Istanza della classe CrucyDB
     */
    //TODO private CrucyDB crosswordDB = CrucyDB.getInstance();

    /**
     * Percorso in cui si trovano i minischemi da unire
     */
    private String path;

    /**
     * ID del minischema di partenza per la stampa del maxischema
     */
    private Character idStarterCrucy;
    
    /**
     * Grafo dei tipi
     */
    private Map<Character,Set<Character>> grafoTipi;
    
    /**
     * Contiene la soluzione. MOMENTANEO finche' non iniziamo a mettere in MongoDB
     */
    private Set<Set<CruciverbaServer>> sol = new HashSet<>();
    
    
    /**
     * Viene generato l'oggetto unisci schemi e viene memorizzato il path in cui si trovano i minischemi che deve unire
     * @param path percorso locale all'interno del progetto in cui si trovano i minischemi da unire
     */
    public UnisciSchemi(String path)
    {
        this.path = path;
    }

    /**
     * @return crosswordDB.
     */
    //TODO public CrucyDB getCrosswordDB() { return crosswordDB; }

    /**
     * @return la mappa associata al costruttore.

     */
    public Map<Character, Set<CruciverbaServer>> getCharacterSetMapCrucy() { return characterSetMapCrucy; }

    public void generateAll(String pathTemplate,String pathMinischemi) throws IOException
	{
		ArrayList<Character> a = new ArrayList<>();
		HashMap<Character, Character> temp;
		Character node1,node2;
		
		for(File tipoSchema : new File(pathTemplate).listFiles())
		{
			//System.out.println("PATH MINISCHEMA "+pathMinischemi);
			//System.out.println("TIPO SCHEMA "+tipoSchema + "   "+tipoSchema.getName()+   "  "+tipoSchema.getAbsolutePath());
			
			if(tipoSchema.getName().startsWith(".")) 
			{
				//System.out.println( "....continue..."); 
				continue;
			} 
			
			grafoTipi = new HashMap<>();
			
			String myPath = pathMinischemi+"/"+tipoSchema.getName();
			
			//System.out.println("mypath "+myPath);
			//System.out.println(Paths.get(pathTemplate+"/"+tipoSchema.getName()));
			BufferedReader br = Files.newBufferedReader(Paths.get(pathTemplate+"/"+tipoSchema.getName()));
			
			HashMap<Character,HashMap<Character,Character>> casellePortali= new HashMap<>();
			
			HashMap<Character,ArrayList<Character>> m = new HashMap<>();
			
			for(String s = br.readLine();s!=null;s = br.readLine())
			{ 
				
				for(String casella : s.split(","))
				{
					//System.out.println("isP() "+casella.charAt(0));
					if(casella.charAt(0) == 'P') 	// Se incontro una casella portale
					{
						//System.out.println("ENTRO SI");
						node1 = casella.charAt(1);
						node2 = casella.charAt(2);
						//System.out.println(casella+"  "+node1+"  "+node2);		//Stampa le caselle portali che trova nel template
						//Collegamento bidirezionale node1->node2
						if(grafoTipi.containsKey(node1)) grafoTipi.get(node1).add(node2);
						else grafoTipi.put(node1, new HashSet<>(Arrays.asList(node2)));
						//collgamento bidirezionale node2->node1
						if(grafoTipi.containsKey(node2)) grafoTipi.get(node2).add(node1);
						else grafoTipi.put(node2, new HashSet<>(Arrays.asList(node1)));
						
						//Creo struttura da riempire durante la ricorsione
						temp = casellePortali.getOrDefault(node1, new HashMap<Character,Character>());
						temp.put(node2, '-');
						casellePortali.put(node1, temp);
						
						temp = casellePortali.getOrDefault(node2, new HashMap<Character,Character>());
						temp.put(node1, '-');
						casellePortali.put(node2, temp);
					
						//Memorizzo l'ordine in cui sono messe le caselle portali come nomi di folder
						a = m.getOrDefault(node1, new ArrayList<>());
						a.add(node2);
						m.put(node1, a);
						
						a = m.getOrDefault(node2, new ArrayList<>());
						a.add(node1);
						m.put(node2, a);

					
					}}
			}
	
			ArrayList<Character> tipi = new ArrayList<>();
			for(Character c : grafoTipi.keySet())tipi.add(c);

			generaConfigCasellePortali(tipi,0,casellePortali,myPath,m);
		}
		
	}		
	
    private void generaConfigCasellePortali(ArrayList<Character> tipi, int index, 
			HashMap<Character, HashMap<Character, Character>> map, String myPath, HashMap<Character, ArrayList<Character>> m) 
			throws IOException
	{
		
    	//System.out.println("tipiiiii  " + index + " "+tipi);
    	if(index == tipi.size())
		{

			for(Character c1 :tipi)
			{
				String folder = "";
				//System.out.println( "yooo  "+ m.toString() + " " +c1);
				for(Character c2 : m.get(c1))
				{
					folder += map.get(c1).get(c2);
					//System.out.println("folderVal "+folder);
					//System.out.println("map" +map.toString());

				}
				String crosswordsPath = myPath+"/"+c1+"";
				//System.out.println(crosswordsPath);

				File dir = new File(crosswordsPath);

				//System.out.println("PRE- ENTRATO");
				for(String f2 : dir.list())
				{
					//System.out.println("YEPPPP");
					//System.out.println(f2);
					File dir2 = new File(crosswordsPath+"/"+f2);
					//System.out.println(dir2.getPath());

					for(String f : dir2.list())
					{				
						

						//System.out.println(f2+"/"+ f);
					BufferedReader br = Files.newBufferedReader(Paths.get(crosswordsPath+"/"+f2+"/"+f));//Files.newBufferedReader(Paths.get(crosswordsPath+"/"+f));
					StringBuilder sb = new StringBuilder();
					for(String s = br.readLine();s!= null;s=br.readLine())sb.append(s).append("\n");
					br.close();
						
					Set<CruciverbaServer> setTemp = characterSetMapCrucy.getOrDefault(c1, new HashSet<>());
					CruciverbaServer c = CaricaCruciverba.caricaMinischema(sb.toString(),m.get(c1));
					CaricaCruciverba.caricaParoleMinischema(c, crosswordsPath+"/"+f+"-word");
					setTemp.add(c);
					characterSetMapCrucy.put(c1, setTemp);
					
					//System.out.println(c1);
					//System.out.println(setTemp );
					//System.out.println("YEPPPP333");

					
					}
				}
			} 
			
			//System.out.println("trovato");
			return ;
		}
		Character tipo = tipi.get(index);
		String p = myPath+"/"+tipo;
    	//System.out.println( "bbbbbbbbbbbbBBBBBBBBBBB" + p);

		File dir = new File(p);
		for(File f : dir.listFiles())
		{
			if(f.getName().startsWith("."))continue;
			String nameFile = f.getName();
			
			ArrayList<Character> a = m.get(tipo);
			boolean noGood = false;
			for(int i = 0; i<a.size();i++)
				if(map.get(tipo).get(a.get(i)) != '-' && map.get(tipo).get(a.get(i)) != nameFile.charAt(i))
					noGood = true;
			if(noGood)continue;
			
			HashSet<Integer> modificati = new HashSet<>();
			
			//Imposto i portali per una possibile soluzione
			for(int i = 0; i<a.size();i++)
			{
				if(map.get(tipo).get(a.get(i)) == '-')
				{
					modificati.add(i);
					Character portale = nameFile.charAt(i);
					
					HashMap<Character,Character> temp = map.get(tipo);
					temp.put(a.get(i), portale);
					map.put(tipo, temp);
					
					temp = map.get(a.get(i));
					temp.put(tipo,portale);
					map.put(a.get(i), temp);
				}	
			}
			//System.out.println("booo "+tipi.get(index));
			
			//Terminata la ricorsione su questo ramo risetto i porali che ho inizializzato io
			for(Integer i : modificati)
			{
				Character portale = '-';
					
				HashMap<Character,Character> temp = map.get(tipo);
				temp.put(a.get(i), portale);
				map.put(tipo, temp);
					
				temp = map.get(a.get(i));
				temp.put(tipo,portale);
				map.put(a.get(i), temp);		
			}			
			
		}
		generaConfigCasellePortali(tipi,index+1,map,myPath,m);
		
		createAll(map);

	}

    
    /**
     * Crea tutte le possibili unioni tra i minischemi presenti nella mappa di minischemi.
     * @throws IOException 
     */
    public void createAll(HashMap<Character, HashMap<Character, Character>> map) throws IOException 
    {
    	//map.get()
    	Stack<Character> allTypes = new Stack<>(); //TODO spostare: lo faccio solo quando leggo da template lo schema
    	for(Character tipo : grafoTipi.keySet())allTypes.push(tipo);
    	
    	HashMap<CruciverbaServer,Set<CruciverbaServer>> vic = new HashMap<>();
    	for(Character tipo1 : grafoTipi.keySet())
    	{	
    		//System.out.println("grafotipiiii "+ grafoTipi);
        	//System.out.println("tipo1 "+tipo1);

    		for(Character tipo2 : grafoTipi.get(tipo1))
    		{
    			//////////System.out.println("grafotipi "+grafoTipi);
            	//System.out.println("tipo2 "+tipo2);
            	//System.out.println("mappa caratteri x "+characterSetMapCrucy);
    			
    			for(CruciverbaServer cs1 : characterSetMapCrucy.get(tipo1))
    			{

    				for(CruciverbaServer cs2 : characterSetMapCrucy.get(tipo2))
    				{
                    	//System.out.println("cs2 "+cs2);

    					if( checkWordInCrucy(cs1,cs2))
    					{
                        	//System.out.println("entro checkWordInCrucy");

    						if(vic.containsKey(cs1) ) vic.get(cs1).add(cs2);
    						else vic.put(cs1, new HashSet<>(Arrays.asList(cs2)));
    						//System.out.println(cs1.hashCode()+ "   "+cs2.hashCode());
    					}
    				}
    			}
    			
    		}
    	}
    	createAll(allTypes,vic,new HashSet<CruciverbaServer>());
		
    }
    
    private void createAll(Stack<Character> allTypes,HashMap<CruciverbaServer,Set<CruciverbaServer>> vic,HashSet<CruciverbaServer> yetIn) throws IOException
    {
    	if(counter>=30000)return;

    	if(allTypes.isEmpty())
    	{

    		@SuppressWarnings("unchecked") //TODO controllare clone delle varie classi
			HashSet<CruciverbaServer> solution = (HashSet<CruciverbaServer>) yetIn.clone();
    		
    		HashMap<Character,CruciverbaServer> insertedByType = new HashMap<>();
    		
    		for(CruciverbaServer k : solution) insertedByType.put(k.getId(),k);
    		
    		for(CruciverbaServer cs : solution)
    		{
    			Map<Character,CasellaPortale> temp = cs.getCasellePortali();
    			for(Character k : temp.keySet())
    			{
    				CasellaPortale cas = temp.get(k);
    				cas.setCruciverba(insertedByType.get(k));
    				cas.setCasella(insertedByType.get(k).getCasellePortali().get(cas.getCharacter()));
    			}
    		}
    		
    		/*crosswordDB.insertCrossword(domains.name(), difficult.name(), language.name(), 
    				insertedByType.get(idStarterCrucy).toString());*/
    		
    		//System.out.println("scrivo: "+ "generati/"+counter);
    		if(counter%1000 == 0 && counter<30000)
    		{

    			String sw = "generati/"+counter;
	    		File ff = new File("generati");
	    		ff.mkdirs();
	    		File f = new File(sw);
	    		f.createNewFile();
	    		BufferedWriter bw = Files.newBufferedWriter(Paths.get(sw));
	    		for(CruciverbaServer qwe : solution)
	    		{
	    			bw.write(qwe.toString());
	    			bw.close();
	    			break;
	    		}
	    		
    		}
    		counter++;
    		//sol.add(solution);
    		return ;
    	}
    	Character u = allTypes.peek();
    	allTypes.pop();
    	boolean flag;
    	for(CruciverbaServer cs : characterSetMapCrucy.get(u))
    	{
    		flag = true;
    		for(CruciverbaServer k : yetIn)
    		{
    			if(grafoTipi.get(cs.getId()).contains(k.getId()))
    				{if(vic.containsKey(cs) && !vic.get(cs).contains(k))flag = false;}
    			else if(!checkWordInCrucy(cs, k))flag= false;
    		}
    		
    		if(flag)
    		{
    			yetIn.add(cs);
    			createAll(allTypes,vic,yetIn);
    			yetIn.remove(cs);
    		}
    	}
    	
    	allTypes.push(u);
    }
    
    @Deprecated
    private Stack<Character> generaGrafoDeiTipi()
    {
    	grafoTipi = new HashMap<Character,Set<Character>>();
    	Stack<Character> visited = new Stack<Character>();
    	generaGrafoDeiTipi(idStarterCrucy, visited);
    	return visited;
    }
    @Deprecated
    private void generaGrafoDeiTipi(Character u,Stack<Character> visited)
    {
	    for(CruciverbaServer cs : characterSetMapCrucy.get(u))
	    {
    		for(Character v : cs.getCasellePortali().keySet())
	    	{
	    		if (grafoTipi.containsKey(u))grafoTipi.get(u).add(v) ;
	    		else grafoTipi.put(u,new HashSet<Character>(Arrays.asList(v)));
	    		if(grafoTipi.containsKey(v)) grafoTipi.get(v).add(u);
	    		else grafoTipi.put(v,new HashSet<Character>(Arrays.asList(u)));
	     		if(visited.search(v) == -1)
	     		{
	    			visited.push(v);
	     			generaGrafoDeiTipi(v,visited);	
	     		}
	    	}
    		break;
	    }
    }

    
	
    /**
     * Controlla le parole interne del minischema c1 con il minischema c2.
     * @param c1 minischema
     * @param c2 minischema
     * @return false se i minischemi hanno almeno una parola in comune, true altrimenti
     */
    private boolean checkWordInCrucy(CruciverbaServer c1, CruciverbaServer c2)		//TODO Mettere le parole in CruciverbaServer
    {
    	for(String prefix : c1.getInternalPrefixWords())
    		if(c2.getInternalPrefixWords().contains(prefix))return false;
        for (String parola : c1.getInternalWords())
            if (c2.getInternalPrefixWords().contains(parola)) return false;
        return true;
    }
    
    /**
     * Metodo per stampare il risultato del createAll come stringa
     * Serve per il debug
     * @return stringa contenente tutte le soluzioni
     */
	public String getResult() 
	{
		//System.out.println("qwerty amica" + sol.size());
		StringBuilder sb = new StringBuilder();
		int i =0;
		for(Set<CruciverbaServer> m : sol)
		{
			i++;
			sb.append("soluzione "+i+"\n");
			for(CruciverbaServer c : m)
				sb.append(c.hashCode()+"\n");
			sb.append("\n\n");
		}
		return sb.toString();
	}
	
	 public void generateAll2(String pathMinischemi,String pathTemplate) throws IOException
	 {
			ArrayList<Character> a = new ArrayList<>();
			HashMap<Character, Character> temp;
			Character node1,node2;
			
			//System.out.println(new File(pathTemplate));
			for(File tipoSchema : new File(pathTemplate).listFiles())
			{
				
				if(tipoSchema.getName().startsWith(".")) continue;
				
				grafoTipi = new HashMap<>();
				String myPath = pathMinischemi+"/"+tipoSchema.getName();
								
				BufferedReader br = Files.newBufferedReader(Paths.get(pathTemplate+"/"+tipoSchema.getName()));
				
				HashMap<Character,HashMap<Character,Character>> casellePortali= new HashMap<>();
				HashMap<Character,ArrayList<Character>> m = new HashMap<>();
				
				for(String s = br.readLine();s!=null;s = br.readLine())
				{ 
					for(String casella : s.split(","))
					{
						if(casella.charAt(0) == 'P') 	// Se incontro una casella portale
						{
							node1 = casella.charAt(1);
							node2 = casella.charAt(2);
							
							//Collegamento bidirezionale node1->node2
							if(grafoTipi.containsKey(node1)) grafoTipi.get(node1).add(node2);
							else grafoTipi.put(node1, new HashSet<>(Arrays.asList(node2)));
							//collgamento bidirezionale node2->node1
							if(grafoTipi.containsKey(node2)) grafoTipi.get(node2).add(node1);
							else grafoTipi.put(node2, new HashSet<>(Arrays.asList(node1)));
							
							//Creo struttura da riempire durante la ricorsione
							temp = casellePortali.getOrDefault(node1, new HashMap<Character,Character>());
							System.out.println(temp);

							temp.put(node2, '-');////////////
							casellePortali.put(node1, temp);
							
							temp = casellePortali.getOrDefault(node2, new HashMap<Character,Character>());
							temp.put(node1, '-');/////////////
							casellePortali.put(node2, temp);
						
							//Memorizzo l'ordine in cui sono messe le caselle portali come nomi di folder
							a = m.getOrDefault(node1, new ArrayList<>());
							a.add(node2);
							m.put(node1, a);
							
							a = m.getOrDefault(node2, new ArrayList<>());
							a.add(node1);
							m.put(node2, a);

						}}
				}
				System.out.println(m);
				ArrayList<Character> tipi = new ArrayList<>();
				for(Character c : grafoTipi.keySet())tipi.add(c);  
				
				/*
				 *  tipi = minischemi e.g. [a,b,c...]
				 *  m = con chi e' collegato chi  e.g {a=[b], b=[a, c, d, e], c=[b], d=[b], e=[b]}
				 *  myPath = path minischema  e.g "tCerchio"
				 *  casellePortali e.g. {a={b=-}, b={a=-, c=-, d=-, e=-}, c={b=-}, d={b=-}, e={b=-}}
				 */ 
				generaConfigCasellePortali2(tipi,0,casellePortali,myPath,m);
			}
			
	}		
		
	 private void generaConfigCasellePortali2(ArrayList<Character> tipi, int index, 
				HashMap<Character, HashMap<Character, Character>> map, String myPath, HashMap<Character, ArrayList<Character>> m) 
				throws IOException
	{
			
		 System.out.println("---------");
		 System.out.println("m    "+m);
		 System.out.println("map    "+map);
		 System.out.println("tipi    "+tipi);
		 System.out.println("path    "+myPath);
		 System.out.println("index    "+index);
		 System.out.println("---------");

	    if(index == tipi.size())
		{
	    	
			for(Character c1 :tipi)
			{
				String folder = "";
				for(Character c2 : m.get(c1)) folder += map.get(c1).get(c2);
				System.out.println(folder);
					
				String crosswordsPath = myPath+"/"+c1+"";

				File dir = new File(crosswordsPath);

				for(String f2 : dir.list())
				{
						
					File dir2 = new File(crosswordsPath+"/"+f2);

					for(String f : dir2.list())
					{				
							
/*
						BufferedReader br = Files.newBufferedReader(Paths.get(crosswordsPath+"/"+f2+"/"+f));//Files.newBufferedReader(Paths.get(crosswordsPath+"/"+f));
						StringBuilder sb = new StringBuilder();
						for(String s = br.readLine();s!= null;s=br.readLine())sb.append(s).append("\n");
						br.close();
							
						Set<CruciverbaServer> setTemp = characterSetMapCrucy.getOrDefault(c1, new HashSet<>());
						CruciverbaServer c = CaricaCruciverba.caricaMinischema(sb.toString(),m.get(c1));
						CaricaCruciverba.caricaParoleMinischema(c, crosswordsPath+"/"+f+"-word");
						setTemp.add(c);
						characterSetMapCrucy.put(c1, setTemp);
*/						

						
					}
				}
			} 
				
			return ;
		}
		generaConfigCasellePortali2(tipi,index+1,map,myPath,m);

		Character tipo = tipi.get(index);
		String p = myPath+"/"+tipo;

		File dir = new File(p);
		for(File f : dir.listFiles())
		{
			if(f.getName().startsWith("."))continue;
			String nameFile = f.getName();
				
			ArrayList<Character> a = m.get(tipo);
			boolean noGood = false;
			for(int i = 0; i<a.size();i++)
				if(map.get(tipo).get(a.get(i)) != '-' && map.get(tipo).get(a.get(i)) != nameFile.charAt(i))
					noGood = true;
			if(noGood)continue;
				
			HashSet<Integer> modificati = new HashSet<>();
				
			//Imposto i portali per una possibile soluzione
			for(int i = 0; i<a.size();i++)
			{
				if(map.get(tipo).get(a.get(i)) == '-')
				{
					modificati.add(i);
					Character portale = nameFile.charAt(i);
					
					HashMap<Character,Character> temp = map.get(tipo);
					temp.put(a.get(i), portale);
					map.put(tipo, temp);
						
					temp = map.get(a.get(i));
					temp.put(tipo,portale);
					map.put(a.get(i), temp);
				}	
			}
				
			//Terminata la ricorsione su questo ramo risetto i porali che ho inizializzato io
			for(Integer i : modificati)
			{
				Character portale = '-';
						
				HashMap<Character,Character> temp = map.get(tipo);
				temp.put(a.get(i), portale);
				map.put(tipo, temp);
						
				temp = map.get(a.get(i));
				temp.put(tipo,portale);
				map.put(a.get(i), temp);		
			}			
			
		}
		createAll(map);
		

		}
	 

	
	
	
	
	
	
	ArrayList<TreeMap<Character, String>> perm = new ArrayList<TreeMap<Character, String>>();
	private TreeMap<Character, String> schemaToString; 

	
	public TreeMap<Character,ArrayList<Character>> getSchema(String pathTemplate) throws IOException
	{
		
		ArrayList<Character> a = new ArrayList<>();
		Character node1,node2;
		
		BufferedReader br = Files.newBufferedReader(Paths.get(pathTemplate));
		TreeMap<Character,ArrayList<Character>> m = new TreeMap<>();
			
		for(String s = br.readLine();s!=null;s = br.readLine())
		{ 
			for(String casella : s.split(","))
			{
				if(casella.charAt(0) == 'P') 	
				{
					node1 = casella.charAt(1);
					node2 = casella.charAt(2);
					a = m.getOrDefault(node1, new ArrayList<>());
					a.add(node2);
					m.put(node1, a);
						
					a = m.getOrDefault(node2, new ArrayList<>());
					a.add(node1);
					m.put(node2, a);
				}
			}
		}
		return m; 
	}	
	
	private TreeMap<Character,ArrayList<String>> getAvailable(String pathMinischemi, TreeMap<Character,ArrayList<Character>> schema)
	{
		 TreeMap<Character,ArrayList<String>> available = new  TreeMap<Character,ArrayList<String>>();
		
		 for(Character c : schema.keySet())
		 {
			 File f1 = new File (pathMinischemi+"/"+c);
			 for(String lettere : f1.list())
			 {				
				ArrayList<String> toPut = available.getOrDefault(c, new ArrayList<String>());
				toPut.add(lettere);
				
				available.put(c, toPut);
			 }
		 }
		 return available;
	}
	

	private ArrayList<TreeMap<Character, String>> getSchemi(String pathMinischemi,String pathTemplate) throws IOException 
	{
		 TreeMap<Character,ArrayList<Character>> schema = getSchema(pathTemplate);
		 schemaToString = schemaToSting(schema); 
		 TreeMap<Character,ArrayList<String>> available = getAvailable(pathMinischemi, schema);
		 
		 System.out.println(schema);
		 System.out.println(available);

		 permutation((Character)schema.keySet().toArray()[0] ,new  TreeMap<Character,String>(), null, null, schema, available);
		 
		 int j=0;
		 ArrayList<TreeMap<Character, String>> joinable = new ArrayList<TreeMap<Character, String>>();
		 for(TreeMap<Character, String> t: perm) 
		 {		

		 	 if(perm.get(j).size()==schemaToString.size()) check(available, j, joinable);
		 	 j++;
		}	
		return joinable;
		
	}
	
	
	public void permutation(Character c, TreeMap<Character, String> current, String previous, Character cPrev, TreeMap<Character,ArrayList<Character>> schema,TreeMap<Character,ArrayList<String>> available)
	{
		for(String s : available.get(c))
		{							
			if(!matches(s,previous,c, cPrev, available)) continue;
			current.put(c, s);
			if(schema.get(c).size()==0) perm.add(new TreeMap<Character, String>(current));
			
			for(Character cc : schema.get(c))
			{		
				schema.get(cc).remove(c);
				permutation(cc,current, s, c, schema, available);
			}	
		}		
	} 
	
	
	private boolean matches(String s, String previous, Character c, Character cPrev, TreeMap<Character,ArrayList<String>> available)
	{
		if(previous==null) return true;
		int index = schemaToString.get(c).indexOf(cPrev); 
		if(previous.toLowerCase().contains(( s.toLowerCase().charAt(index)+"")))  return true;
		return false;
	}
	
	private void check(TreeMap<Character,ArrayList<String>> available, int j, ArrayList<TreeMap<Character,String>> ret)
	{
		
		TreeMap<Character, String> t= perm.get(j);
		
		HashSet<String> hash= new HashSet<String>();
		boolean b=false;
		for(Character c:schemaToString.keySet())
		{
			int i=0;
			for(Character cc: schemaToString.get(c).toCharArray())
			{
				if(!hash.contains((c+":"+cc).toLowerCase())) 
				{	if(t.get(c).toLowerCase().charAt(i)== t.get(cc).toLowerCase().charAt(0))  b=true; 
					else  return; 
				}
				hash.add((c+":"+cc).toLowerCase()); hash.add((cc+":"+c).toLowerCase());
				i++;
			}
		}
		if(b) ret.add(t);
	}
	

	
	
	public TreeMap<Character, String>  schemaToSting(TreeMap<Character, ArrayList<Character>> schema)
	{
		TreeMap<Character,String> ret =new TreeMap<Character,String>();
		for(Character c: schema.keySet())
		{
			String toPut="";
			for(Character cc : schema.get(c)) toPut+=cc; 
			ret.put(c, toPut);
		}
		return ret;
	}
	
	
	public void join(String pathMinischemi, String pathTemplate) throws IOException 
	{
		 ArrayList<TreeMap<Character, String>> joinable = getSchemi(pathMinischemi, pathTemplate);
		 
		  for(TreeMap<Character,String> entry : joinable)
			  System.out.println(entry);
		  
		  String p  = "testing/newmin/tVortici/a/r/0.txt";
		  	System.out.println(p);
			BufferedReader br = Files.newBufferedReader(Paths.get(p));
			StringBuilder sb = new StringBuilder();
			for(String s = br.readLine();s!=null;s=br.readLine())sb.append(s).append("\n");
			br.close();
			ArrayList<Character> cas = new ArrayList<>();
			cas.add('b');
			CruciverbaServer t1 = CaricaCruciverba.caricaMinischema(sb.toString(),cas);
			
	}
	
	public static void main(String args[]) throws  IOException
	{
		UnisciSchemi u = new UnisciSchemi("1");
		
		u.join("testing/newmin/tVortici", "testing/templates/tVortici");
		//u.join("/home/student/crucyDef/minischemi2/", "/home/student/crucyDef/templates/templates/", args[0], args[1]);

		//u.getSchemi("minischemi/tCerchio", "templates/tCerchio");
		
		//u.getSchemi("testing/newmin/tVortici", "testing/templates/tVortici");
		//u.getSchemi("minischemi/tCerchio", "templates/tCerchio");

		/*
		 File cartella = new File("A_MINISCHEMI_DUMP/newminischemi/");
		 for(File tipoSchema : cartella.listFiles())
		 {
			 if(tipoSchema.getName().startsWith("."))continue;
			 System.out.println(tipoSchema.getName());
			 for(File casellePortali : tipoSchema.listFiles())
			 {
				 for(File tipoMinischema : casellePortali.listFiles())
				 {
					 for(File mini : tipoMinischema.listFiles())
					 {
						 BufferedReader br = Files.newBufferedReader(Paths.get(mini.getPath()));
						 System.out.println(br.readLine());
					 }
				 }
			 }
			 break;
		 }
		 
		*/
		
	}
    
}