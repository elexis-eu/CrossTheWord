package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;


/**
 * Servlet per l'invio di statistiche riguardanti le definizioni nei cruciverba attraverso i parametri WORD e DEFINITION_ID
 */

//@WebServlet("/annotation")
public class AnnotationHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String definitionId = (String) request.getAttribute(DEFINITION_ID);
    	String word = (String) request.getAttribute(WORD);
    	
    	if (word == null || definitionId == null) {
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		return;
    	}
    	
    	//StatisticDefinitionsDB.getInstance().insertDef(definitionId, word);
    	response.getWriter().print("true");
	}
}