package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;

/**
 * Servlet per la richiesta della classifica giocatori:
 * - attraverso il parametro EMAIL per quella nell'intorno del giocatore specificato
 * - attraverso il parametro NUM_USERS per quella delle top posizione specificate
 * 
 */

//@WebServlet("/highscore")
public class HighScore extends HttpServlet {
	private static final long serialVersionUID = 1L;


	public void init() throws ServletException {

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String users = request.getParameter(Parameters.NUM_USERS);
		PrintWriter out = response.getWriter();

		ArrayList<String> ranking = UserDatabase.getInstance().getTopRanking(Integer.parseInt(users));
		out.println(new Gson().toJson(ranking));
		out.close();
	}
}