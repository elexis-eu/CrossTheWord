package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.uniroma1.lcl.crucyservlet.mongodb.DefinitionDatabase;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;

/**
 * Servlet per la richiesta della classifica giocatori:
 * - attraverso il parametro EMAIL per quella nell'intorno del giocatore specificato
 * - attraverso il parametro NUM_USERS per quella delle top posizione specificate
 * 
 */

//@WebServlet("/diffBatch")
public class Current extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final String check = "3421689";


	public void init() throws ServletException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String checkFromRequest = request.getParameter(Parameters.CHECK);
    	PrintWriter out = response.getWriter(); 
    	if(checkFromRequest.equals(check))
    	{
    		out.println(new Gson().toJson(DefinitionDatabase.getInstance().getBatch()));
    	}
    	else out.println(new Gson().toJson("FAILEDCHECK_DIFFBATCH"));

    	out.close();
        	
       
	}
}