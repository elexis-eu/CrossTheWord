package it.uniroma1.lcl.crucyservlet.servlets.popup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.FindIterable;
import it.uniroma1.lcl.crucyservlet.mongodb.PopupDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;
import static it.uniroma1.lcl.crucyservlet.utils.Parameters.CROSSWORD_STRING;


//@WebServlet("/popupSenses")
public class PopupSenses extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int MAX_SENSES = 6;

    @Override
    public void init() throws ServletException {
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String synsetID = request.getParameter(Parameters.SYNSET_ID);

        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            ArrayList<Document> senses = PopupDatabase.getInstance().getSenses(synsetID);

            if (senses.size() > MAX_SENSES)
                senses = getRandomSenses(senses);

            JsonArray sensesArray = new JsonArray();
            senses.stream().map((sense) ->  {
                JsonObject jo = new JsonObject();
                jo.addProperty("_id", sense.getObjectId("_id").toString());
                jo.addProperty(SYNSET_ID, sense.getString(SYNSET_ID));
                jo.addProperty(LEMMA, sense.getString(LEMMA));

                ArrayList<Document> sources = (ArrayList<Document>) sense.get(SENSES);
                JsonArray jo_sources= new JsonArray();
                sources.stream().map((source) -> {
                    JsonObject jo_source = new JsonObject();
                    jo_source.addProperty(SENSE_ID, source.getString(SENSE_ID));
                    jo_source.addProperty(SOURCE, source.getString(SOURCE));
                    return jo_source;
                }).forEach(jo_sources::add);
                jo.add(SENSES, jo_sources);

                return jo;
            }).forEach(sensesArray::add);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(sensesArray.toString());
            response.getWriter().close();

            System.out.println("Senses retrieved!");
        } catch (IdDoesntExistException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Word id doesn't exist");
        } finally {
            response.getWriter().close();
        }
    }


    public ArrayList<Document> getRandomSenses(ArrayList<Document> senses) {
        ArrayList<Document> randomSenses = new ArrayList<Document>();

        ArrayList<String> upperSenses = new ArrayList<String>();
        Random rand = new Random(System.currentTimeMillis());

        int chosen = 0;
        while (chosen < MAX_SENSES) {
            Integer idx = rand.nextInt(senses.size());

            Document sense = senses.get(idx);
            String upperLemma = sense.getString(Parameters.LEMMA).toUpperCase();
            if (!upperSenses.contains(upperLemma)) {
                randomSenses.add(sense);
                upperSenses.add(upperLemma);
                chosen++;
            }
        }

        return randomSenses;
    }
}
