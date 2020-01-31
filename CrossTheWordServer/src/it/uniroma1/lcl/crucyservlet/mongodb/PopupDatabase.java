package it.uniroma1.lcl.crucyservlet.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.bson.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

public class PopupDatabase {

    private CrucyDatabase crucyDB;
    private MongoCollection<Document> popup_data;
    private static PopupDatabase instance;

    private PopupDatabase() {
        this.crucyDB = CrucyDatabase.getInstance();
        this.popup_data = crucyDB.getDB().getCollection("popup_data");
        this.popup_data.createIndex(new Document(SYNSET_ID, 1).append(LEMMA, 1), new IndexOptions().unique(true));
    }

    public static PopupDatabase getInstance() {
        if (instance == null) instance = new PopupDatabase();
        return instance;
    }

    public void addElement(String element) {
        List<String> tks = Arrays.asList(element.split("\t"));
        ArrayList<Document> senses = new ArrayList<>();
        for (int i = 3; i< tks.size(); i+=2)
            senses.add(new Document(SENSE_ID, tks.get(i)).append(SOURCE, tks.get(i+1)));

        popup_data.insertOne(new Document().append(SYNSET_ID, tks.get(0))
            .append(LEMMA, tks.get(1)).append(SENSES, senses)
            .append(LANGUAGE, tks.get(2)));
    }

    public ArrayList<Document> getSenses(String synsetID) throws IdDoesntExistException {
        ArrayList<Document> sense = popup_data.find(eq(SYNSET_ID, synsetID)).into(new ArrayList<>());
        if (sense!= null) { return sense; }
        else throw new IdDoesntExistException();
    }

}
