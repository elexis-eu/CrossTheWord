package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import it.uniroma1.lcl.crucy.utils.Tuple;

public class Sense {

    private String _id;
    private String synsetID;
    private String lemma;
    private ArrayList<SenseInfo> senses;


    public Sense(){}

    public Sense(String lemma, String synsetID){
        this.lemma = lemma;
        this.synsetID = synsetID;
    }

    public Sense(String lemma, String synsetID, ArrayList<SenseInfo> senses){
        this.lemma = lemma;
        this.synsetID = synsetID;
        this.senses = senses;
    }

    public String get_id() { return _id; }

    public String getSynsetID() { return synsetID; }

    public String getLemma() { return lemma; }

    public ArrayList<SenseInfo> getSenses() { return senses; }

    public Set<String> getSources() {
        Set<String> sources = new HashSet<String>();
        for (SenseInfo si : senses)
            sources.add(si.getSource());
        return sources;
    }
}
