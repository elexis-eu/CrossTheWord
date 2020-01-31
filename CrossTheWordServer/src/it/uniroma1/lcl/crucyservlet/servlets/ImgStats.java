package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;


/**
 * Servlet implementation class ImgStats
 */

//@WebServlet("/imgStats")
public class ImgStats extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private MongoClient client;
	/**
	 * Object MongoDatabae
	 */
	private MongoDatabase db;
	/**
	 * Crossword collection
	 */
	private MongoCollection<Document> stats;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImgStats() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() {
		client = new MongoClient(SERVER_ADDRESS, MONGODB_PORT);
		db = client.getDatabase("crucy");
		stats = db.getCollection("stats2");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		response.setContentType("text/html");
		if (name == null || value == null) {
				MongoCursor<Document> iterator = stats.find().iterator();
				while (iterator.hasNext()) {
					Document doc = iterator.next();
					response.getWriter().print("<p> <a href=\"" + (String) doc.get("url") + "\" >" + (String) doc.get("url") +
												"</a> " +  doc.get("value") + "</p>");
				}
				
		} else {
			if (stats.count(new Document().append("url", name)) != 0) {
				Integer temp = stats.find(new Document().append("url", name)).first().getInteger("value");
				Integer updated = temp + Integer.parseInt(value);
				stats.updateOne(new Document("url", name), new Document("$set", new Document("value", updated)));
			} else {
				stats.insertOne(new Document().append("url", name).append("value", Integer.parseInt(value)));
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
