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
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;

/**
 * Servlet implementation class FacebookUser
 */
//@WebServlet("/fbUser")
public class FacebookUser extends HttpServlet {
	private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public FacebookUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8");
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType("application/json");
		HttpSession currentSession = request.getSession();
    	String prevSession = null;
    	boolean synchro = false;

    	String email = request.getParameter(Parameters.EMAIL);
    	String username = request.getParameter("username"); 
    	String sessionId = request.getParameter(Parameters.SESSION_ID); 
    	

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
