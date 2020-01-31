package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.uniroma1.lcl.crucyservlet.mongodb.Login;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.DataFormatException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Servlet implementation class RegisterServlet
 * necessaria per la creazione di un nuovo utente
 */

//@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	private static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*])(?=\\S+$).{8,}$");
	private static final Pattern VALID_USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9\\-\\_]{4,16}$");
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
	}

	/**
	 * Registrazione all'applicazione attraverso email, username e password
	 * Se la registrazione va a buon fine ritorna come messaggio al requester "true",
	 * altrimenti ritorna "false" e codice di stato 400 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		String email = request.getParameter(Parameters.EMAIL);
		String username = request.getParameter(Parameters.USERNAME);
		String password = request.getParameter(Parameters.PASSWORD);

		try {
			if (email == null ||  username == null || password == null)
				throw new NullPointerException();
			else if ((!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches())
					|| (!VALID_PASSWORD_REGEX.matcher(password).matches())
					|| (!VALID_USERNAME_REGEX.matcher(username).matches())
					|| username.length() < 2
					|| password.length() < 8
					|| password.length() > 128)
				throw new DataFormatException();
			Login.addUser(email, username, password, request.getSession().getId());
			response.setStatus(HttpServletResponse.SC_OK);
			out.print("Success (new DB)!");
			System.out.println("Success (new DB)!");
		} catch (UserExistException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.println("User already exists with these credentials");
			System.out.println("User already exists with these credentials");
		} catch (NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.println("Some fields are missing.");
			System.out.println("Some fields are missing.");
		} catch (DataFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.println("Data format is not valid.");
			System.out.println("Data format is not valid.");
		} finally {
			out.close();
			return;
		}
	}
}
