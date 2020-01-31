package it.uniroma1.lcl.crucyservlet.servlets.crossword;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.apache.http.HttpStatus;

/**
 * Servlet per lo spostamento di un cruciverba iniziato nella lista dei cruciverba completati associati ad un utente
 * attraverso il metodo setCrosswordDone. Ritorna true in caso di successo, false altrimenti
 */

//@WebServlet("/finishedCrossword")
public class FinishedCrossword extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException {

	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession sessionId = request.getSession(false);
		
		String crosswordId = request.getParameter(Parameters.CROSSWORD_ID);
		String email = (String) sessionId.getAttribute(Parameters.EMAIL);

		PrintWriter pw = response.getWriter();
		/*try {
			UserDatabase.getInstance().setCrosswordDone(email, crosswordId);
			pw.print("true");
		} catch (UserDoesntExistException e) {
			pw.print("false");
			pw.close();
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;	
		}
		pw.close();*/
	}
}