package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Difficulty;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Domains;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Language;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.RunnableFuture;

/**
 * Created by stefan on 08/12/16.
 */
public class GeneraMinischemi
{
	
	
	 
    public static void main(String[] args) throws Exception
    {
    	
    	/*
    	Map<Character, Set<CruciverbaServer>> set = new CruciverbaBuilder().build(Domains.RANDOM, null, args[1], Language.ITALIAN, 10 );
        for (Map.Entry<Character, Set<CruciverbaServer>> entry : set.entrySet())
        {
            try(PrintWriter writer = new PrintWriter(args[0] + "/" + entry.getKey() + ".txt"))
            {
                for (CruciverbaServer cruciverba : entry.getValue())
                {
                    writer.println(cruciverba.toString());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    

    	*/
    	
    	System.out.println("STARTTTTT");
    	 Domains domain = Domains.RANDOM;
         Language language = Language.ITALIAN;
         Difficulty difficult = null;
         String template = "";
         String macroName = "" ;

         String pathOutputDirectory = "";
         int maxGenerate = 100;

         for(int k = 0; k< args.length; k++)
         {
             if(args[k].equals("-d"))
                 domain = Domains.valueOf(args[++k]);
             else if(args[k].equals("-l"))
                 language = Language.valueOf(args[++k]);
             else if(args[k].equals("-D"))
                 difficult = Difficulty.valueOf(args[++k]);
             else if(args[k].equals("-t"))
             {
                 template = args[++k];
                 macroName = template.substring(template.lastIndexOf(File.separator));
             }
             else if(args[k].equals("-p"))
                 pathOutputDirectory = args[++k];
             else if(args[k].equals("-m"))
                 maxGenerate = Integer.parseInt(args[++k]);
             else
                 throw new InvalidPropertiesFormatException("Invalid arguments" + args[k]);

         }
         final int max = maxGenerate;
         
         ///////////////////////
         //macroName="tCerchio";
         //template= "templates/tCerchio";
         //maxGenerate=20;
        // pathOutputDirectory=  "minicherchio";
         
         //macroName="tVortici";
         //template= "testing/templates/tVortici";
         //maxGenerate=20;
         //pathOutputDirectory=  "testing/out";
         
         ///////////////////////
         new CruciverbaBuilder().setMacroName(macroName).setMaxGenerate(maxGenerate).setPathOutput(pathOutputDirectory);

         Map<Character, String> map = new CruciverbaBuilder().getTemplates(template);
         for(Map.Entry<Character, String> entry : map.entrySet())
         {         System.out.println(entry.getKey());

         	//new CruciverbaBuilder().buildOnThread(entry.getKey(), entry.getValue());
             Thread t = new Thread(new Runnable()
             {
                 @Override
                 public void run()
                 {
                     try
                     {
                         new CruciverbaBuilder().buildOnThread(entry.getKey(), entry.getValue());
                     }
                     catch (Exception e)
                     {
                         e.printStackTrace();
                     }
                 }
             });
             t.start();
             
         }

    /*
        Domains domain = Domains.RANDOM;
        Language language = Language.ITALIAN;
        Difficulty difficult = null;
        String template = "";
         String macroName = "" ;

        String pathOutputDirectory = "";
        int maxGenerate = 100;

        for(int k = 0; k< args.length; k++)
        {
            if(args[k].equals("-d"))
                domain = Domains.valueOf(args[++k]);
            else if(args[k].equals("-l"))
                language = Language.valueOf(args[++k]);
            else if(args[k].equals("-D"))
                difficult = Difficulty.valueOf(args[++k]);
            else if(args[k].equals("-t"))
            {
                template = args[++k];
                macroName = template.substring(template.lastIndexOf(File.separator));
            }
            else if(args[k].equals("-p"))
                pathOutputDirectory = args[++k];
            else if(args[k].equals("-m"))
                maxGenerate = Integer.parseInt(args[++k]);
            else
                throw new InvalidPropertiesFormatException("Invalid arguments" + args[k]);

        }
       
        final int max = maxGenerate;
        
        System.out.println("yoyo1 "+ domain+" yoyo2 "+ language+" yoyo3 "+ difficult+" yoyo4 " +template+" yoyo5 " +macroName+" yoyo6 "+ max +" yoyo7 " + pathOutputDirectory );
        CruciverbaBuilder cb = new CruciverbaBuilder();
        cb.setMacroName(macroName).setMaxGenerate(maxGenerate).setParhOutput(pathOutputDirectory);

        
        final String mac=macroName;
        final String pathOut = pathOutputDirectory;
        final Domains dom = domain;
        final String ttt = template;
        final Language lll= language; 
        final Difficulty ddd=difficult; 
        final int maxg=maxGenerate; 

        Map<Character, String> map = new CruciverbaBuilder().getTemplates(ttt);
        for(Map.Entry<Character, String> entry : map.entrySet())
        {	
        	System.out.println("TEMPLATEX");
            Thread t = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        new CruciverbaBuilder().buildOnThread(entry.getKey(), entry.getValue());
                        /////
                        
                        System.out.println("ROFLMAO1111");
                        Map<Character, Set<CruciverbaServer>> set = new CruciverbaBuilder().setMacroName(mac).setParhOutput(pathOut).build(dom, null, ttt, lll, maxg);
            	        for (Map.Entry<Character, Set<CruciverbaServer>> entry : set.entrySet())
            	        {
            	           for (CruciverbaServer cruciverba : entry.getValue())
            	            {
            	                String stringPortals = cruciverba.getOrderedPortals();
            	                String fileName ;
            	                File folder =new File( pathOut + File.separator + mac + File.separator + entry.getKey() + File.separator + stringPortals );

            	                if(!folder.exists())
            	                {
            	                    folder.mkdirs();
            	                    fileName = File.separator + 0 + ".txt";
            	                }
            	                else
            	                {
            	                   String[] list = folder.list();
            	                   Arrays.sort(list);
            	                    fileName = File.separator + (Integer.parseInt(list[list.length -1].substring(0, list[list.length -1].length() -4))+1) + ".txt";
            	                }
            	                File file = new File(folder.getAbsolutePath() + fileName);
            	                file.createNewFile();
            	                try (PrintWriter writer = new PrintWriter(file))
            	                {
            	                    writer.println(cruciverba.toString());
            	                } catch (Exception e)
            	                {
            	                    e.printStackTrace();
            	                }
            	           }
            	        }
                        System.out.println("ROFLMAO22222222222222");

                        
                        /////

                    }
                    catch (Exception e)
                    {
                        System.out.println("fin2");

                        
                    }
                }
            });
            
            System.out.println("test1ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
            t.start();
            System.out.println("test2ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");

       
        }
        System.out.println(map);
        System.out.println(cb.getParoleIndicizzate());
        
        
    
        
        */
    }   
}
