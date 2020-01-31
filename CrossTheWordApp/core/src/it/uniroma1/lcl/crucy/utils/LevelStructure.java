package it.uniroma1.lcl.crucy.utils;


public class LevelStructure
{
    private Triple<String,String,String>[] structure;

    private static LevelStructure instance;

    public static LevelStructure getInstance()
    {
        if(instance==null)instance = new LevelStructure();
        return instance;
    }

    private LevelStructure()
    {
        structure = new Triple[50];
        for(int i=0;  i<50; i++)
        {
            structure[i]=new Triple<String, String, String>(null,null,null);
        }
    }

    public String getID(int lvl, int n)
    {
        if (n==1) return structure[lvl-1].getFirst();
        else if (n==2) return structure[lvl-1].getSecond();
        else if (n==3) return structure[lvl-1].getThird();
        return null;
    }

    public void setCross(String s, int lvl, int n)
    {
        if (n==1) structure[lvl-1].setFirst(s);
        else if (n==2) structure[lvl-1].setSecond(s);
        else if (n==3) structure[lvl-1].setThird(s);
    }

    public Tuple<Integer,Integer> search (String s)
    {
        Tuple<Integer,Integer> ret=null;
        for(int i=0;  i<50; i++)
        {
            if(structure[i].getThird().equals(s)) ret=new Tuple<Integer, Integer>(i+1, 3);
            else if(structure[i].getSecond().equals(s)) ret=new Tuple<Integer, Integer>(i+1, 2);
            else if(structure[i].getFirst().equals(s)) ret=new Tuple<Integer, Integer>(i+1, 1);
            continue;
        }
        return ret;
    }

    public String idPresent(int lvl, int n)
    {
        for(int i=0;  i<50; i++)
        {
            if(n==3)
                return structure[lvl-1].getThird();
            else if (n==2)
                return structure[lvl-1].getSecond();
            else if (n==1)
                return structure[lvl-1].getFirst();
        }

        return null;
    }

    public void print()
    {
        System.out.println("============");
        System.out.println("============");
        for(int i=0;  i<50; i++)
        {
            System.out.print(i + ")  ");
            System.out.println(structure[i].getFirst() +"  "+ structure[i].getSecond()+"  "+structure[i].getThird());
        }
        System.out.println("============");
        System.out.println("============");

    }


}
