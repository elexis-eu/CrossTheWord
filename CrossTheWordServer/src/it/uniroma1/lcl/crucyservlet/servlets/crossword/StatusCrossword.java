package it.uniroma1.lcl.crucyservlet.servlets.crossword;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.*;

import io.restassured.http.ContentType;
import it.uniroma1.lcl.crucyservlet.mongodb.CrosswordDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.crossword.Crossword;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.DefinitionsNotInCrosswordException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdCrosswordDoesntExistEception;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import it.uniroma1.lcl.crucyservlet.utils.Utils;
import org.apache.http.HttpStatus;
import org.bson.Document;
import org.bson.types.ObjectId;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

/**
 * Servlet per la gestione dello stato dei cruciverba iniziati da parte di uno specifico utente
 * In base ai parametri presenti nella richiesta può (attraverso l'email):
 * - richiedere lo stato di un singolo cruciverba con CROSSWORD_ID
 * - aggiungere una singola parola allo stato di un cruciverba iniziato con WORD oppure un set di parole con STATUS_CROSSWORD
 * 
 */

//@WebServlet("/statusCrossword")
public class StatusCrossword extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void init() throws ServletException {

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession sessionId = request.getSession();

		String email = (String) sessionId.getAttribute(Parameters.EMAIL);

		String body = Utils.getBody(request);

		JsonParser jsonParser = new JsonParser();
		JsonObject jsonBody = null;

		try {
			jsonBody = jsonParser.parse(body).getAsJsonObject();
			System.out.println(jsonBody);
		} catch (JsonSyntaxException ex) {
			log("Invalid json format by user: " + email);

            response.setStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY);
			response.getWriter().println("Invalid JSON");
			return;
		}

		UserDatabase userDB = UserDatabase.getInstance();
		ObjectId id = null;
		JsonArray jsonArray = null;
		try {
			id = new ObjectId(jsonBody.get(CROSSWORD_ID).getAsString());
			jsonArray = jsonBody.get(DEFINITIONS).getAsJsonArray();
		} catch (NullPointerException ex) {
			log("Invalid json format by user: " + email);

            response.setStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY);
			response.getWriter().println("Invalid JSON");
			return;
		}

		List<Document> definitions = new ArrayList<>();
		for (Iterator<JsonElement> it = jsonArray.iterator(); it.hasNext(); ) {
			JsonElement elem = it.next();
			JsonObject wordId = elem.getAsJsonObject();

			String def_id;
			String word;
			int row;
			int column;
			boolean isHorizontal;
			try {
				def_id = wordId.get(DEFINITION_ID).getAsString();
				word = wordId.get(WORD).getAsString();
				row = wordId.get(ROW).getAsInt();
				column = wordId.get(COLUMN).getAsInt();
				isHorizontal = wordId.get(HORIZONTAL).getAsBoolean();
			} catch (NullPointerException ex) {
				log("Invalid json format by user: " + email);
                response.setStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY);
				response.getWriter().println("Invalid JSON");
				return;
			}
			definitions.add(new Document().append(DEFINITION_ID, def_id)
					.append(WORD, word)
					.append(ROW, row)
					.append(COLUMN, column)
					.append(HORIZONTAL, isHorizontal));
		}

		try {
			userDB.addWordsToStartedCrossword(email, id, definitions);
			/*
				TODO: BISOGNA FARE IL TODO SOTTO!!!!!!
				TODO: Controllare se dopo l'inserimento il cruciverba è completato, e se lo è, aggiungerlo nel cruciverba completati
			    e rimuoverlo da quelli Started
			 */
		} catch (DefinitionsNotInCrosswordException ex) {
			log("Definitions are not inside the crosswords: " + email);
            System.out.println("Definitions are not inside the crosswords: " + email);

            response.setStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY);
			response.getWriter().println("Invalid Json");
			return;
		}
		response.setStatus(HttpStatus.SC_OK);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession sessionId = request.getSession(false);

		String email = (String) sessionId.getAttribute(Parameters.EMAIL);
		String crossId = request.getParameter(CROSSWORD_ID);

		UserDatabase userDB = UserDatabase.getInstance();

		if (crossId == null) {
			List<ObjectId> objectsId = userDB.getCrosswordsStarted(email);

			JsonArray jsonArr = new JsonArray();
			objectsId.stream().map(ObjectId::toString).forEach(jsonArr::add);

			response.setStatus(HttpStatus.SC_OK);
			response.setContentType("application/json");
			response.getWriter().println(jsonArr);
			return;
		}

		ObjectId crosswordId;
		try {
			crosswordId = new ObjectId(crossId);
		} catch (IllegalArgumentException ex) {
			log("String is not a objectID");
			response.setStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY);
			response.getWriter().println("Invalid ID format");
			return;
		}

		System.out.println("REQUESTING STATE FOR CROSSWORD: "+ crosswordId.toString());
		Document doc;
		try {
			doc = userDB.getCrosswordStatus(email, crosswordId);
			System.out.println(doc);
		} catch (IdCrosswordDoesntExistEception idCrosswordDoesntExistEception) {
			log("String is not a objectID");
			response.setStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY);
			response.getWriter().println("The crossword was not starts by the user");
			return;
		}

		response.getWriter().println(doc.toJson());
		response.setStatus(HttpStatus.SC_OK);
		response.setContentType("application/json");
	}
}