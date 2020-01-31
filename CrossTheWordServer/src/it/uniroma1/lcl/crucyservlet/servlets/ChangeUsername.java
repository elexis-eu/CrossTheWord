package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;

/**
 * Servlet implementation class ChangeUsername
 * Utilizzabile esclusivamente da utente loggato dal momento che utilizza l'email conservata nella sessione richiesta
 * Da la possibilità settare o cambiare l'username del giocatore e, in caso di successo, ritorna il nuovo username come conferma
 */

//@WebServlet("/changeUsername")
public class ChangeUsername extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession currentSession = request.getSession();
    	String email = (String) currentSession.getAttribute(Parameters.EMAIL);


    	String username = request.getParameter(Parameters.USERNAME);
    	
    	/**
    	 * Da modificare il connectionManager se l'email non è nella sessione
    	 */
    	if (email != null) {
			try {
				UserDatabase.getInstance().setUsername(email, username);
			} catch (UserDoesntExistException e) {
				e.printStackTrace();
			}
			try {
				username = UserDatabase.getInstance().getUsername(email);
			} catch (UserDoesntExistException e) {
				System.out.println("Catch");
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	    		return;
			}
    	} else {
    		System.out.println("Here");
    		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    		return;
    	}
    	
    	PrintWriter pw = response.getWriter();
    	JsonObject json = new JsonObject();
    	json.addProperty(Parameters.USERNAME, username);
    	//json.addProperty(Parameters.SESSION_ID, currentSession.getId());		// verificare se è necessario
    	pw.println(new Gson().toJson(json));
    	pw.close();
	}
}