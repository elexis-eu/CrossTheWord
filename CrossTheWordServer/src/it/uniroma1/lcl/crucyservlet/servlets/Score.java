package it.uniroma1.lcl.crucyservlet.servlets;

import com.google.gson.Gson;
import it.uniroma1.lcl.crucyservlet.mongodb.UserDatabase;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;
import org.apache.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

//@WebServlet("/score")
public class Score extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sessionId = request.getSession(false);
        String email = (String) sessionId.getAttribute(Parameters.EMAIL);

        PrintWriter out = response.getWriter();
        try {
            Map<String, Integer> userScore = new HashMap<>();
            userScore.put(Parameters.MONEY, UserDatabase.getInstance().getMoney(email));
            userScore.put(Parameters.DIAMONDS, UserDatabase.getInstance().getDiamonds(email));
            userScore.put(Parameters.EXPERIENCE, UserDatabase.getInstance().getExperience(email));
            userScore.put(Parameters.LEVEL, UserDatabase.getInstance().getLevel(email));
            out.println(new Gson().toJson(userScore));
        } catch (UserDoesntExistException e) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().println("User doesn't exist.");
            out.close();
            return;
        }
        response.setStatus(HttpStatus.SC_OK);
        out.close();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sessionId = request.getSession(false);
        String email = (String) sessionId.getAttribute(Parameters.EMAIL);

        String money = request.getParameter(Parameters.MONEY);
        String diamonds = request.getParameter(Parameters.DIAMONDS);
        String experience = request.getParameter(Parameters.EXPERIENCE);
        String level = request.getParameter(Parameters.LEVEL);

        try{
            if (money != null)  UserDatabase.getInstance().setMoney(email, Integer.parseInt(money));
            if (diamonds != null)  UserDatabase.getInstance().setDiamonds(email, Integer.parseInt(diamonds));
            if (experience != null)  UserDatabase.getInstance().setExperience(email, Integer.parseInt(experience));
            if (level != null)  UserDatabase.getInstance().setLevel(email, Integer.parseInt(level));
        } catch (UserDoesntExistException e) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().println("User doesn't exist.");
            return;
        }
        response.setStatus(HttpStatus.SC_OK);
    }
}
