package it.uniroma1.lcl.crucyservlet.servlets.crossword;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.apache.http.HttpStatus;

/**
 * Servlet per la richiesta degli id dei cruciverba iniziati associati ad uno specifico utente
 */

//@WebServlet("/startedCrossword")
public class StartedCrosswords extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void init() throws ServletException {

	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = (String) request.getSession(false).getAttribute(Parameters.EMAIL);
		Set<String> output = new HashSet<>();

		PrintWriter pw = response.getWriter();
		
		/*try {
			output = UserDB.getInstance().getIncompleteCrossoword(email);
		} catch (UserDoesntExistException e) {
			pw.print(new Gson().toJson(new HashMap<String,String>()));
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;	
		}*/
		
		pw.print(new Gson().toJson(output));
		pw.close();
	}
}