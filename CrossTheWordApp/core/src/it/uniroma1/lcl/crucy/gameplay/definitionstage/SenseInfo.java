package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.io.Serializable;

public class SenseInfo implements Serializable {
    private String senseID;
    private String source;

    public SenseInfo() {}

    public SenseInfo(String senseID, String source){
        this.senseID = senseID;
        this.source = source;
    }

    public String getSenseID() {return  senseID;}

    public String getSource() {return  source;}

}
