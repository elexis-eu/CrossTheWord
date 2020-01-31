package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.apache.catalina.Server;

//@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		HttpSession session = request.getSession();
		if (session.getAttribute(Parameters.EMAIL) != null) {
			response.getWriter().println(session.getAttribute(Parameters.EMAIL) + " Logged-out!");
			response.setStatus(HttpServletResponse.SC_OK);
			session.invalidate();
		}
	}
}