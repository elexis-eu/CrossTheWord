package it.uniroma1.lcl.crucyservlet.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Language;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Size;
import it.uniroma1.lcl.crucyservlet.mongodb.crossword.Crossword;
import it.uniroma1.lcl.crucyservlet.mongodb.crossword.WordPosition;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.CrosswordDoesnExistException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdDoesntExistException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.ParsingExeption;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

public class CrosswordDatabase {

    private CrucyDatabase crucyDB;
    private MongoCollection<Document> crosswords;
    private static CrosswordDatabase instance;

    private CrosswordDatabase() {
        this.crucyDB = CrucyDatabase.getInstance();
        this.crosswords = crucyDB.getDB().getCollection("crosswords");
        CodecRegistry registry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromCodecs(new WordPosition.WordPositionCodec()));
        this.crosswords = this.crosswords.withCodecRegistry(registry);
        // Create uniques ID
        //IndexOptions indexOptions = new IndexOptions().unique(true);
        //this.crosswords.createIndex(Indexes.text(KEY), indexOptions);
    }

    public static CrosswordDatabase getInstance() {
        if(instance == null) instance = new CrosswordDatabase();
        return instance;
    }

    public void addCrossword(String crosswordString, Language language, Difficulty difficulty, int level) throws ParsingExeption {
        Crossword crossword = Crossword.parseCrossword(crosswordString, language);
        crossword.setLevel(level);
        crossword.setDifficulty(difficulty);
        crossword.saveInDB(crosswords);
    }

    public void addCrossword(String crosswordString, Language language, int level) throws ParsingExeption {
        assert (level > 0);
        Crossword crossword = Crossword.parseCrossword(crosswordString, language);
        crossword.setLevel(level);
        crossword.saveInDB(crosswords);
    }


    public List<Crossword> getCrosswords(Bson query) {
        return Crossword.getCrosswords(crosswords, query, 0);
    }

    public List<Crossword> getCrosswords(Bson query, int limit) {
        return Crossword.getCrosswords(crosswords, query, limit);
    }

    public boolean areDefinitionsInCrossword(ObjectId crosswordId, List<Document> definitions) {
        Document query = new Document();
        query.append(CROSSWORD_ID, crosswordId);
        query.append(DEFINITIONS, new Document()
                .append("$all", definitions.stream()
                        .map(doc -> new Document().append("$elemMatch", doc))
                        .collect(Collectors.toList())
                )
        );

        return getCrosswords(query).size() > 0;
    }

    public static void main(String[] args) {
        String path = "C:\\Users\\marzia\\Desktop\\crosswords_sampled\\hard";
        File file = new File(path);
        List<String> crossword_files = Arrays.asList(file.list());
        try {
            CrosswordDatabase crosswordDB = CrosswordDatabase.getInstance();

            for (String cf : crossword_files) {
                String crossword = Files.lines(Paths.get(path +"\\"+ cf)).collect(Collectors.joining("\n"));
                crosswordDB.addCrossword(crossword, Language.ENGLISH, Difficulty.HARD, -1);
            }

        } catch (IOException | ParsingExeption e) {
            e.printStackTrace();
        }
        System.out.println("Crossword Inserted");
    }
}
