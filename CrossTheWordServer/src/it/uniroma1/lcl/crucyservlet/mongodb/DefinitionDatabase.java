package it.uniroma1.lcl.crucyservlet.mongodb;

import com.mongodb.client.MongoCollection;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdDoesntExistException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

public class DefinitionDatabase {

    private CrucyDatabase crucyDB;
    private MongoCollection<Document> definitions;
    private static DefinitionDatabase instance;

    private DefinitionDatabase() {
        this.crucyDB = CrucyDatabase.getInstance();
        this.definitions = crucyDB.getDB().getCollection("definitions");
    }

    public static DefinitionDatabase getInstance() {
        if(instance == null) instance = new DefinitionDatabase();
        return instance;
    }

    private void addDefinition(String word, String id, String definition, Double difficulty) {
        definitions.insertOne(new Document(WORD, word)
                .append(SYNSET_ID, id)
                .append(DEFINITION, definition)
                .append(DIFFICULT, difficulty));
    }

    public String getDefinition(String definitionID) throws IdDoesntExistException {
        Document def = definitions.find(eq(SYNSET_ID , definitionID)).first();
        if (def == null) throw new IdDoesntExistException();
        else return String.join("::", definitionID, def.getString(LEMMA), def.getString(SYNSET_ID),
                def.getString(DEFINITION), Difficulty.valueOfDifficulty(def.getDouble(DIFFICULT)).name());
    }


    public Map<String, String> getDefinitions(String difficulty) {
        Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
        Map<String, String> def = new HashMap<>();

        Document query = new Document().append(DIFFICULT, new Document("$gte", diff.lower).append("$lt", diff.upper));
        Iterator<Document> docIter = definitions.find(query).iterator();

        while (docIter.hasNext()) {
            Document definition = docIter.next();
            def.put(definition.getString(SYNSET_ID), definition.getString(WORD));
        }
        return def;
    }

    public Map<String, String> getDefinitions() {
        Map<String, String> def = new HashMap<>();
        Iterator<Document> docIter = definitions.find().iterator();

        while (docIter.hasNext()) {
            Document definition = docIter.next();
            def.put(definition.getString(SYNSET_ID), definition.getString(WORD));
        }
        return def;
    }

    public void setDifficulty(String batch) { }

    public String getBatch() {  return ""; }

    public static void main(String[] args){

        try {
            DefinitionDatabase defDB = DefinitionDatabase.getInstance();
            Random rd = new Random();
            List<String> lines = Files.lines(Paths.get(args[0])).collect(Collectors.toList());
            for (String line : lines){
                String[] tks = line.split("\t");
                defDB.addDefinition(tks[0], tks[1], tks[2], rd.nextDouble());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Definition Inserted");
    }


}
