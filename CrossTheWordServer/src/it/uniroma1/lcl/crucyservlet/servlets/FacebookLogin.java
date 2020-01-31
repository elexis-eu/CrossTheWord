package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

/**
 * Servlet implementation class FacebookLogin
 */

//@WebServlet("/facebook")
public class FacebookLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    private static final String clientID = "2424972070929191";
    private static final String clientSecret = "8990abfcd75e33b763ff864af168d296";
    private static final String redirectURI = "http://2.236.83.85/crucy2/facebook";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Request Arrived");
		String accessToken = request.getParameter("token");
		//String accessToken = "EAAbxCDguEs8BAJ2uNDTerjjFjZCFxfucUvp9qQzrj3uOQdHehHH9VeH3SgFB5Wp3o2YQx9mHEGBbrvTkxb51KGrBZCNjB7sQRKx5j7gQrXeBZC6vCGfRxZACyf7L0JyBoJO8yYvIfA4sHAn5F09ZCK51xzYK7A7jTEER6A8mHuVLZA7RKBDGbwuKRalFH2ZCxxLbgRDpnCu7J6RWKAOfyHY";
		String line, outputString = "";
		URL url = new URL("https://graph.facebook.com/me?access_token="
				+ accessToken);
		URLConnection conn1 = url.openConnection();
		PrintWriter out = response.getWriter();


		outputString = "";
		BufferedReader reader = null;

		// Parameters to extract
		String userId = null;
		String name = null;
		String email = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					conn1.getInputStream()));
			while ((line = reader.readLine()) != null) {
				outputString += line;
			}
			reader.close();

			HashMap json = new Gson().fromJson(outputString, HashMap.class);

			userId = (String) json.get("id");
			name = ((String) json.get("name")).replaceAll("\\s+","");

		} catch(Exception ex) {
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			System.out.println(Arrays.toString(ex.getStackTrace()));
			return;
		}


		if (userId == null) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			out.print("Token isn't valid");
			System.out.println("Token isn't valid");
			return;
		}

		url = new URL("https://graph.facebook.com/"+ userId + "?fields=email&access_token=" + accessToken);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		conn1 = url.openConnection();

		outputString = "";
		reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					conn1.getInputStream()));
			while ((line = reader.readLine()) != null) {
				outputString += line;
			}
			reader.close();

			HashMap json = new Gson().fromJson(outputString, HashMap.class);

			email = (String) json.get("email");

		} catch(Exception ex) {
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			System.out.println(Arrays.toString(ex.getStackTrace()));
			return;
		}

		System.out.println(email);

		UserDatabase userDatabase = UserDatabase.getInstance();
		email = userDatabase.facebookLogin(userId, email, name);

		HttpSession currentSession = request.getSession(true);

		currentSession.setAttribute(EMAIL, email);
		currentSession.setAttribute(USERNAME, name);

		JsonObject json = new JsonObject();

		json.addProperty(Parameters.EMAIL, email);
		json.addProperty(Parameters.SESSION_ID, request.getSession().getId());
		json.addProperty(Parameters.SYNCHRONIZED, false);


		response.setStatus(HttpStatus.SC_OK);
		out.println(new Gson().toJson(json));
		out.close();

	}

}