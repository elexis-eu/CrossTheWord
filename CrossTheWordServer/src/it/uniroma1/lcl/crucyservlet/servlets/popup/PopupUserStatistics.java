package it.uniroma1.lcl.crucyservlet.servlets.popup;

import it.uniroma1.lcl.crucyservlet.mongodb.StatisticsDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

//@WebServlet("/popupUserStatistics")
public class PopupUserStatistics extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = (String) request.getSession(false).getAttribute(Parameters.EMAIL);
        Integer wordnetCorrect = Integer.parseInt(request.getParameter(WORDNET_CORRECT));
        Integer wordnetTotal = Integer.parseInt(request.getParameter(WORDNET_TOTAL));

        System.out.println("Worndet Correct per user " + email + " : " + wordnetCorrect);
        System.out.println("Worndet Total per user " + email + " : " + wordnetCorrect);

        UserDatabase userDB = UserDatabase.getInstance();
        try {
            userDB.setWordnetStatistics(email, wordnetCorrect, wordnetTotal);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (UserDoesntExistException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("User ID doesn't exist");
        }
    }
}
