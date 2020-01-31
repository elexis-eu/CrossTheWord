package it.uniroma1.lcl.crucy.cache.utils;

import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.LimitedDomains;

public class NeededCrossword {

    private Language language;
    private LimitedDomains domain;
    private int level;
    private GameType gametype;
    private Difficult difficulty;

    public NeededCrossword(Language language, LimitedDomains domain, GameType gametype, int level, Difficult difficulty){
        this.language = language;
        this.domain = domain;
        this.level = level;
        this.gametype = gametype;
        this.difficulty = difficulty;
    }

    public NeededCrossword(Language language, GameType gametype, Difficult difficulty){
        this.language = language;
        this.domain = LimitedDomains.MIXED;
        this.level = -1;
        this.gametype = gametype;
        this.difficulty = difficulty;

    }

    public Language getLanguage() {return language;}
    public LimitedDomains getDomain() {return domain;}
    public int getLevel() {return level;}
    public Difficult getDifficulty() {return difficulty;}
    public GameType getGametype() {return gametype;}
}
