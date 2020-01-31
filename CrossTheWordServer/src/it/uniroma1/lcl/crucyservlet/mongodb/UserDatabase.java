package it.uniroma1.lcl.crucyservlet.mongodb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.DefinitionsNotInCrosswordException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdCrosswordDoesntExistEception;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserExistException;
import it.uniroma1.lcl.crucyservlet.servlets.crossword.StatusCrossword;
import org.apache.catalina.User;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;


public class UserDatabase {

    private CrucyDatabase crucyDB;
    private MongoCollection<Document> users;
    private MongoCollection<Document> userCrossword;
    private static UserDatabase instance;

    private UserDatabase() {
        this.crucyDB = CrucyDatabase.getInstance();
        this.users = crucyDB.getDB().getCollection("users");
        this.userCrossword = crucyDB.getDB().getCollection("userCrossword");
        // Setting the couple EMAIL, CROSSWORD id unique
        IndexOptions indexOptions = new IndexOptions().unique(true);
        this.userCrossword.createIndex(new Document(EMAIL, 1).append(CROSSWORD, 1), indexOptions);
    }

    public static UserDatabase getInstance() {
        if(instance == null) instance = new UserDatabase();
        return instance;
    }

    private boolean existUser(String key, String value)
    {
        return users.find(eq(key, value)).iterator().hasNext();
    }

    public void addUser(String email, String username, String password, String salt, String idSession) throws UserExistException {
        if(existUser(USERID, email)) throw new UserExistException();
        users.insertOne(new Document().append(USERID, email)
                .append(USERNAME, username)
                .append(PASSWORD, password)
                .append(ID_SESSION, idSession)
                .append(SALT, salt)
                .append(MONEY, 0)
                .append(EXPERIENCE, 0)
                .append(DIAMONDS, 0)
                .append(LEVEL, 1)
                .append(WORDNET_CORRECT, 0)
                .append(WORDNET_TOTAL, 0)
        );
    }

    public void deleteUser(String email) throws UserDoesntExistException {
        if (existUser(USERID, email)) users.deleteOne(new BasicDBObject(USERID, email));
        else throw new UserDoesntExistException();
    }

    /**
     * Search user into crucydatabase and recovery the password, salt and session id
     * @param email email of user
     * @return array of string that contains password, salt, idSession
     * @throws UserDoesntExistException if user doesn't exist
     */
    public String[] login(String email) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) {
            String[] data = new String[3];
            data[0] = user.getString(PASSWORD);
            data[1] = user.getString(SALT);
            data[2] = user.getString(ID_SESSION);
            return data;
        } else throw new UserDoesntExistException();
    }

    /**
     *
     * @param facebookID
     * @param email
     * @return the email when he did the registration
     */
    public String facebookLogin(String facebookID, String email, String name) {
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER);
        Document obj = new Document("$setOnInsert", new Document(USERID, email).append(USERNAME, name)).append("$set", new Document(FACEBOOK_ID, facebookID));
        Document out = users.findOneAndUpdate(or(eq(FACEBOOK_ID, facebookID), eq(USERID, email)), obj, options);
        return out.getString(USERID);
    }

    // USERNAME METHODS
    public void setUsername(String email, String username) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) users.updateOne(new Document(USERID, email), new Document("$set", new Document(USERNAME, username)));
        else throw new UserDoesntExistException();
    }

    public String getUsername(String eMail) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, eMail.toLowerCase())).first();
        if (user!=null) return user.getString(USERNAME);
        else throw new UserDoesntExistException();
    }

    // EXPERIENCE METHODS
    public void setExperience(String email, int experience) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) users.updateOne(new Document(USERID, email), new Document("$set", new Document(EXPERIENCE, experience)));
        else throw new UserDoesntExistException();
    }

    public int getExperience(String email) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if(user!=null)  return user.getInteger(EXPERIENCE);
        else throw new UserDoesntExistException();
    }

    // MONEY METHODS
    public void setMoney(String email, int money) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) users.updateOne(new Document(USERID, email), new Document("$set", new Document(MONEY, money)));
        else throw new UserDoesntExistException();
    }

    public int getMoney(String email) throws UserDoesntExistException{
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) return user.getInteger(MONEY);
        else throw new UserDoesntExistException();
    }

    // DIAMONDS METHODS
    public void setDiamonds(String email, int diamonds) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) users.updateOne(new Document(USERID, email), new Document("$set", new Document(DIAMONDS, diamonds)));
        else throw new UserDoesntExistException();
    }

    public int getDiamonds(String email) throws UserDoesntExistException{
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) return user.getInteger(DIAMONDS);
        else throw new UserDoesntExistException();
    }

    // LEVEL METHODS
    public void setLevel(String email, int level) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) users.updateOne(new Document(USERID, email), new Document("$set", new Document(LEVEL, level)));
        else throw new UserDoesntExistException();
    }

    public int getLevel(String email) throws UserDoesntExistException{
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) return user.getInteger(LEVEL);
        else throw new UserDoesntExistException();
    }


    public void setWordnetStatistics(String email, int wordnetCorrect, int wordnetTotal) throws UserDoesntExistException{
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) users.updateOne(new Document(USERID, email),
                new Document("$inc", new Document(WORDNET_CORRECT, wordnetCorrect).append(WORDNET_TOTAL, wordnetTotal)));
        else throw new UserDoesntExistException();
    }

    // SESSION ID METHODS
    public void setIdSession(String email, String idSession) throws UserDoesntExistException{
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) users.updateOne(new Document(USERID, email), new Document("$set", new Document(ID_SESSION, idSession)));
        else throw new UserDoesntExistException();
    }

    public String getIdSession(String email) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) return user.getString(ID_SESSION);
        else throw new UserDoesntExistException();
    }

    // AUTHENTICATION CODE METHODS
    public void setAuthCode(String email, String authcode, String timeAuthCode) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!= null) users.updateOne(new Document(USERID, email), new BasicDBObject().append("$set", new BasicDBObject()
                .append(CODE_AUTH, authcode).append(TIME_CODE_AUTH, timeAuthCode)));
        else throw new UserDoesntExistException();
    }

    public Map<String, String> getAuthCode(String email) throws UserDoesntExistException {
        Document user = users.find(new Document(USERID, email)).first();
        if (user!= null){
            Map<String, String> m = new HashMap<>();
            m.put(CODE_AUTH, user.getString(CODE_AUTH));
            m.put(TIME_CODE_AUTH, user.getString(TIME_CODE_AUTH));
            return m;
        } else throw new UserDoesntExistException();
    }


    protected void setPassword(String email, String password, String salt) throws UserDoesntExistException {
        Document user = users.find(eq(USERID, email)).first();
        if (user!=null) users.updateOne(new Document(USERID, email), new Document("$set", new Document(PASSWORD, password).append(SALT, salt).append(TIME_CODE_AUTH, "0")));
        else throw new UserDoesntExistException();
    }

    public FindIterable<Document> getUserCrosswords(String email) {
        return userCrossword.find(new Document(EMAIL, email)).projection(new Document(CROSSWORD, 1).append("_id", 0));
    }

    public void setCrosswordStarted(String email, ObjectId crosswordID) {
        userCrossword.insertOne(new Document(EMAIL, email)
                .append(CROSSWORD, crosswordID)
                .append(DEFINITIONS,new ArrayList<>()));
    }

    public Document getCrosswordStatus(String email, ObjectId crosswordId) throws IdCrosswordDoesntExistEception {

        Document query = new Document();
        query.append(EMAIL, email)
                .append(CROSSWORD, crosswordId);

        Document projection = new Document();
        projection.append(CROSSWORD, 1).append("_id", 0).append(DEFINITIONS, 1);

        FindIterable<Document> project = userCrossword.find(query).projection(projection);

        Document doc;
        try {
            doc = project.iterator().next();
        } catch (NoSuchElementException e) {
            throw new IdCrosswordDoesntExistEception();
        }
        doc.replace(CROSSWORD, doc.getObjectId(CROSSWORD).toString());
        return doc;
    }

    public void addWordsToStartedCrossword(String email, ObjectId crosswordID, List<Document> definitions) throws DefinitionsNotInCrosswordException {

        // Check that all definitions are in the crossword
        CrosswordDatabase crossDB = CrosswordDatabase.getInstance();
        if (!crossDB.areDefinitionsInCrossword(crosswordID, definitions)) {
            throw new DefinitionsNotInCrosswordException();
        }

        // Insert the definitions
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true);
        userCrossword.findOneAndUpdate(new Document(EMAIL, email).append(CROSSWORD,crosswordID),
                Updates.addEachToSet(DEFINITIONS, definitions),
                options);
    }

    public List<ObjectId> getCrosswordsStarted(String email){
        CrosswordDatabase crosswordDatabase = CrosswordDatabase.getInstance();
        List<Document> aggregators = new ArrayList<>();

        aggregators.add(new Document("$match", new Document(EMAIL, email)));

        aggregators.add(new Document().append("$project", new Document()
                .append(CROSSWORD, 1)
                .append("_id", 0)
                .append(SIZE, new Document()
                        .append("$size", "$"+DEFINITIONS))));

        aggregators.add(new Document("$lookup",
                new Document()
                        .append("from", "crosswords")
                        .append("localField", CROSSWORD)
                        .append("foreignField", CROSSWORD_ID)
                        .append("as", "join")));

        aggregators.add(new Document().append("$project", new Document()
                .append(CROSSWORD, 1)
                .append(SIZE, 1)
                .append("val", new Document()
                                .append("$arrayElemAt", Arrays.asList("$join", 0)))));

        aggregators.add(new Document().append("$project", new Document()
                .append(CROSSWORD, 1)
                .append(SIZE, 1)
                .append(SIZE+"2",
                        new Document()
                                .append("$size","$val."+DEFINITIONS))));

        aggregators.add(new Document().append("$match", new Document("$expr",
                new Document("$ne", Arrays.asList("$"+SIZE,"$"+SIZE+"2")))));

        AggregateIterable<Document> list = userCrossword.aggregate(aggregators);

        List<ObjectId> out = new ArrayList<>();
        for (Document cross : list) out.add(cross.getObjectId(CROSSWORD));
        return out;
    }

    public ArrayList<String> getTopRanking(int top_number) {
        ArrayList<String> userRanking = new ArrayList<>();
        Iterator<Document> userIterator = users.find().sort(new Document(MONEY, -1)).limit(top_number).iterator();
        while(userIterator.hasNext()){
            Document user = userIterator.next();
            userRanking.add(user.getString(USERNAME)+"\t"+user.getInteger(MONEY));
        }
        return userRanking;
    }


    public static void main(String[] args){
        try {
            PopupDatabase popupDB = PopupDatabase.getInstance();
            List<String> lines = Files.lines(Paths.get("C:\\Users\\marzia\\Documents\\workspace\\crucy-babelnet\\synset-senses-wn5.txt")).collect(Collectors.toList());
            for (String line : lines)
                popupDB.addElement(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Popup elements inserted");
    }
}
