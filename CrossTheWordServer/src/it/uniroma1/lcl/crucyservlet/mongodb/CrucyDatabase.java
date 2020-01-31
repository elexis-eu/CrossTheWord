package it.uniroma1.lcl.crucyservlet.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

public class CrucyDatabase {

    private MongoClient client;
    private MongoDatabase db;
    private static CrucyDatabase instance;

    private CrucyDatabase() {
        /*MongoCredential credential = MongoCredential.createCredential(MONGODB_USER,
                MONGODB_AUTHDB,
                MONGODB_PASSWORD.toCharArray());*/
        this.client = new MongoClient(new ServerAddress(MONGODB_ADDRESS, MONGODB_PORT));
        System.out.println("CLIENT MONGO: " + client);
        this.db = client.getDatabase("crucy");
    }

    public static CrucyDatabase getInstance() {
        if(instance == null) instance = new CrucyDatabase();
        return instance;
    }

    public MongoClient getClient() {return client;}
    public MongoDatabase getDB() {return db;}


}
