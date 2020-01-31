package it.uniroma1.lcl.crucyservlet.servlets.popup;

import it.uniroma1.lcl.crucyservlet.mongodb.StatisticsDatabase;
import it.uniroma1.lcl.crucyservlet.utils.Parameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebServlet("/popupStatistics")
public class PopupStatistics extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String lemma = request.getParameter(Parameters.LEMMA);
        System.out.println("Lemma delle statistiche: " + lemma);
        String synsetID = request.getParameter(Parameters.SYNSET_ID);
        System.out.println("Synset delle statistiche: " + synsetID);
        Boolean action = Boolean.valueOf(request.getParameter(Parameters.RIGHT));

        if (action) {
            System.out.println("Right statistic received");
            StatisticsDatabase.getInstance().updateWrongStatistic(lemma, synsetID);
        } else {
            System.out.println("Wrong statistic received");
            StatisticsDatabase.getInstance().updateRightStatistic(lemma, synsetID);
        }
    }
}
