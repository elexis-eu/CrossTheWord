package it.uniroma1.lcl.crucyservlet.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdDoesntExistException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

import static com.mongodb.client.model.Filters.eq;

public class StatisticsDatabase {

    private CrucyDatabase crucyDB;
    private MongoCollection<Document> statistics;
    private static StatisticsDatabase instance;

    private StatisticsDatabase() {
        this.crucyDB = CrucyDatabase.getInstance();
        this.statistics = crucyDB.getDB().getCollection("statistics");
    }

    public static StatisticsDatabase getInstance() {
        if(instance == null) instance = new StatisticsDatabase();
        return instance;
    }

    public Document existStatistic(String lemma, String synsetID) {
        return statistics.find(and(eq(LEMMA, lemma), eq(SYNSET_ID, synsetID))).first();
    }


    public void insertStatistic(String lemma, String synsetID) {
        statistics.insertOne(new Document()
                        .append(LEMMA, lemma).append(SYNSET_ID,synsetID)
                        .append(POPUP_RIGHT, 0).append(POPUP_TOTAL,0));
    }

    public void updateRightStatistic(String lemma, String synsetID) {
        if (existStatistic(lemma, synsetID) == null) insertStatistic(lemma, synsetID);
        statistics.updateOne(and(eq(LEMMA, lemma), eq(SYNSET_ID,synsetID)),
                new BasicDBObject().append("$inc", new BasicDBObject()
                .append(POPUP_RIGHT, 1).append(POPUP_TOTAL,1)));
    }

    public void updateWrongStatistic(String lemma, String synsetID) {
        if (existStatistic(lemma, synsetID) == null) insertStatistic(lemma, synsetID);
        statistics.updateOne(and(eq(LEMMA, lemma), eq(SYNSET_ID,synsetID)),
                new BasicDBObject().append("$inc", new BasicDBObject().append(POPUP_TOTAL,1)));
    }

    public static void main(String[] args){
        /*//getInstance().insertStatistic("great_grey_owl", "bn:00041577n", "greatgreyowl", "bn:00041577n");
        //getInstance().updateTotalStatistic("great_grey_owl", "bn:00041577n", "greatgreyowl", "bn:00041577n");
        getInstance().updateWrongStatistic("Strix_aluco", "bn:00074722n", "tawnyowl", "bn:00074722n");*/
    }

}
