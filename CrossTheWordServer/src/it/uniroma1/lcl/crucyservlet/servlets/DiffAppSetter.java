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

//@WebServlet("/diffSetter")
public class DiffAppSetter extends HttpServlet
{
	private static final long serialVersionUID = 1L;
    private static final String check = "3421689";
    
	public void init() throws ServletException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	String pars = request.getParameter(Parameters.DEFINITIONBATCH);
    	String checkFromRequest = request.getParameter(Parameters.CHECK);
    	
    	PrintWriter out = response.getWriter(); 
    	
    	if(checkFromRequest.equals(check))
    	{
    		DefinitionDatabase.getInstance().setDifficulty(pars);
        	out.println(new Gson().toJson(pars));
    	}
    	else out.println(new Gson().toJson("FAILEDCHECK_DIFFSETTER"));

    	out.close();
    	
    	/**
    	//DefinitionsDB.getInstance().setDifficulty(pars);
    	PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter("/home/student/crucyDef/testWrite4.txt", true)));
    	outFile.println(pars);   
    	for(String row : pars.split("\n"))
  		{
  			String[] splitted= row.split("\t");
  		    //definition.updateOne(new Document(), new Document("$set", new Document(MONEY, money)));
  			outFile.println(splitted[0]+ "+" +splitted[1]);//pars);
  		}
    	
    	//outFile.println("AAAAAARGH " + pars);
    	outFile.close();
    		
    	PrintWriter o = response.getWriter();
    	o.println(this.getServletConfig().getServletContext().getRealPath("/"));
    	o.close();
*/
    	/*
	   
	    	//
	    
		
		
    		*/
    	//}
	   
	    //PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter("/home/student/crucyDef/testWrite.txt", true)));
	   // outFile.write(pars);
	
	    //outFile.close();
	   /*
	    String filename= "/home/student/crucyDef/testWrite.txt";
	    FileWriter fw = new FileWriter(filename,false); //the true will append the new data
	    fw.write(pars);//appends the string to the file
	    fw.close();
    	*/
    	/**
    	 * Da modificare il connectionManager se l'email non è nella sessione
    	 
    	if (email != null) {
        	UserDB.getInstance().setUserName(email, username);   
        	try {
				username = UserDB.getInstance().getUserName(email);
			} catch (UserDoesntExistException e) {
	    		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	    		return;
			}
    	} else {
    		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    		return;
    	}*/
    	
	}
}
