package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.uniroma1.lcl.crucyservlet.mongodb.Login;
import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.PasswordWrongException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.sessionId;

/**
 * Servlet implementation class LoginServlet
 * Parametri: EMAIL (necessario). PASSWORD (necessario), SESSION_ID (opzionale)  
 * Al primo login da un device synchronized sarà false (i dati utente sul dispositivo devono essere aggiornati)
 * Agli auto-login successivi farà il confronto con il sessionId client e quello server per verificare se l'utente ha giocato da un disposivo diverso 
 * oppure ha fatto un logout.
 * Crea la sessione per l'utente e imposta come unico attributo l'email.
 * Verifica se l'utente è già loggato da un secondo dispositivo e, in caso sia così, invalida la sessione precedente/concorrente
 */

//@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		PrintWriter out = response.getWriter();

		String email = request.getParameter(Parameters.EMAIL);
		String password = request.getParameter(Parameters.PASSWORD);
		HttpSession session = request.getSession(true);

		if (session.getAttribute(Parameters.EMAIL) != null) {
			System.out.println("Relogged");
			JsonObject json = new JsonObject();
			json.addProperty(Parameters.EMAIL, (String) session.getAttribute(Parameters.EMAIL));
			json.addProperty(Parameters.USERNAME, (String) session.getAttribute(Parameters.USERNAME));
			json.addProperty(Parameters.SESSION_ID, session.getId());
			json.addProperty(Parameters.SYNCHRONIZED, true);
			response.setStatus(HttpStatus.SC_OK);
			out.println(new Gson().toJson(json));
			out.close();
			return;
		}

		try {
			String old_session = Login.login(email, password);
			System.out.println("Logged-in!");
		} catch (UserDoesntExistException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			//out.println("User doesn't exist.");
			System.out.println("User doesn't exist.");
			return;
		} catch (PasswordWrongException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			//out.println("Wrong password.");
			System.out.println("Wrong password.");
			return;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//TODO: Controlla, perché dovrebbe essere già esistente, quindi non è necessario lanciare nessuna eccezione
		try {
			UserDatabase.getInstance().setIdSession(email,session.getId());
		} catch (UserDoesntExistException e) {
			e.printStackTrace();
		}

		//Se currentSession e sessionId sono differenti significa che la sessione è nuova e quindi non ha alcun attributo assegnato
		JsonObject json = new JsonObject();
		String username = null;
		email = email.toLowerCase();
		try {
			username = UserDatabase.getInstance().getUsername(email);
			json.addProperty(Parameters.USERNAME, username);
		} catch (UserDoesntExistException e) {
			out.print(new Gson().toJson(new HashMap<String, String>()));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		request.getSession().setAttribute(Parameters.EMAIL, email);
		request.getSession().setAttribute(Parameters.USERNAME, username);
		request.getSession().setMaxInactiveInterval(60 * 60 * 24 * 4);

		json.addProperty(Parameters.EMAIL, email);
		json.addProperty(Parameters.SESSION_ID, request.getSession().getId());
		json.addProperty(Parameters.SYNCHRONIZED, false);

		out.println(new Gson().toJson(json));
		out.close();
	}
}
