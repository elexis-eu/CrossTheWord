package it.uniroma1.lcl.crucyservlet.servlets.crossword;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import it.uniroma1.lcl.crucyservlet.mongodb.CrosswordDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.crossword.Crossword;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.CrosswordDoesnExistException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdDoesntExistException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.ParsingExeption;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import it.uniroma1.lcl.crucyservlet.utils.Parameters.*;
import it.uniroma1.lcl.crucyservlet.utils.Utils;
import org.apache.http.HttpStatus;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;

import static com.mongodb.client.model.Filters.*;
import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

/**
 * Servlet per la richiesta di un cruciverba, sia da giocatore esterno che da utente, 
 * attraverso il parametro CROSSWORD_ID (solo utenti registrati e loggati) oppure attraverso DOMAIN, DIFFICULT e LANGUAGE. 
 * Da definire, per gli utenti loggati, la possibilità di eliminare dalle possibili risposte cruciverba già giocati (anche se già al momento è altamente impobabile che accada)
 * @author paolo
 *
 */

//@WebServlet("/crossword")
public class NewCrossword extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession userSession = request.getSession();

		String email = (String) userSession.getAttribute(EMAIL);
		String difficult = request.getParameter(DIFFICULT);
		String language = request.getParameter(LANGUAGE);
		String domain = request.getParameter(DOMAIN);
		String number = request.getParameter(NUMBER_OF_CROSSWORDS);
		String crosswordId = request.getParameter(CROSSWORD_ID);
		String level = request.getParameter(LEVEL);

		UserDatabase userDB = UserDatabase.getInstance();

        Integer lev = null;
        if (level != null) {
            try {
                lev = Integer.parseInt(level);
                if (lev <= 0) lev = -1;
            } catch (NumberFormatException exp) {
                log("InvalidArgumentException the " + LEVEL + " must be an Integer by user: " + email);
                response.setStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY);
                response.getWriter().println("InvalidArgumentException the " + NUMBER_OF_CROSSWORDS + " must be an Integer");
                return;
            }
        }

		FindIterable<Document> crosswords = userDB.getUserCrosswords(email);

		CrosswordDatabase crossDB = CrosswordDatabase.getInstance();
		Bson query  = crosswordId != null
				? eq(CROSSWORD_ID, new ObjectId(crosswordId))
				: nin(CROSSWORD_ID, crosswords.map((doc) -> doc.getObjectId(CROSSWORD)));

		if (difficult != null) query = and(query, eq(DIFFICULT, difficult));
		if (language != null) query = and(query, eq(LANGUAGE, language));
		if (domain != null) query = and(query, eq(DOMAIN, domain));
		if (lev != null) query = and(query, eq(LEVEL, lev));

        Integer numb = null;
        if (number != null) {
			try {
				numb = Integer.parseInt(number);
			} catch (NumberFormatException exp) {
				log("InvalidArgumentException the " + NUMBER_OF_CROSSWORDS + " must be an Integer by user: " + email);
				response.setStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY);
				response.getWriter().println("InvalidArgumentException the " + NUMBER_OF_CROSSWORDS + " must be an Integer");
				return;
			}
		}

		List<Crossword> list =  crossDB.getCrosswords(query, numb != null ? numb : 5);
        System.out.println("Retrieved crosswords : "+ list.size());
        list.forEach(System.out::println);

		JsonArray jsonArr = new JsonArray();
		list.stream().map((cross) ->  {
			JsonObject out = new JsonObject();
			out.addProperty(CROSSWORD_ID, cross.getId().toString());
			out.addProperty(CROSSWORD_LANGUAGE, cross.getLanguage().toString());
			out.addProperty(SIZE, cross.getSize().toString());
			out.addProperty(DIFFICULT, cross.getDifficulty().toString());
			out.addProperty(LEVEL, cross.getLevel());
			out.addProperty(CROSSWORD_STRING, cross.toString());
			return out;
		}).forEach(jsonArr::add);

		response.setHeader("Content-Type", "application/json");
		response.setStatus(HttpStatus.SC_OK);
		response.getWriter().write(jsonArr.toString());
	}
}