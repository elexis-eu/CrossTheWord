package it.uniroma1.lcl.crucyservlet.servlets.crossword;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.uniroma1.lcl.crucyservlet.mongodb.exception.IdCrosswordDoesntExistEception;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.apache.http.HttpStatus;

/**
 * Servlet per la rimozione di un cruciverba iniziato dalla lista limitata a 8 associata ad un utente
 * Da chiamare in caso si voglia semplicemnte rinunciare a un cruciverba oppure prima di spostare un cruciverba 
 * tra i cruciverba finiti attraverso /finishedCrossword
 */

//@WebServlet("/removeStatus")
public class RemoveStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void init() throws ServletException {

	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession sessionId = request.getSession(false);
		
		String crosswordId = request.getParameter(Parameters.CROSSWORD_ID);
		String email = (String) sessionId.getAttribute(Parameters.EMAIL);


		PrintWriter pw = response.getWriter();
		
		/*try {
			UserDB.getInstance().removeCrosswordState(email, crosswordId);
			pw.print("true");
		} catch (IdCrosswordDoesntExistEception | UserDoesntExistException e) {
			pw.print("false");
			pw.close();
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;	
		}
		pw.close();*/
	}
}