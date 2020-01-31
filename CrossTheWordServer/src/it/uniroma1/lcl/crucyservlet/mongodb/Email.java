package it.uniroma1.lcl.crucyservlet.mongodb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email 
{
	/**
	 * Reference to Email instance
	 */
	private static Email instance;
	/**
	 * password of email sender
	 */
	private String password;
	/**
	 * email sender
	 */
	private String from;
	/**
	 * Constructor.
	 * Read password and email sender
	 */
	private Email() 
	{
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("login"))))
		{
			password = br.readLine();
			from = br.readLine();
		}
		catch (IOException e)
		{
			try(BufferedWriter bw = Files.newBufferedWriter(Paths.get("emailError.log"), StandardOpenOption.APPEND))
			{
				String error = new Date() + "\tERROR->\t" + e.getMessage() + "\n";
				bw.write(error);
			} catch (IOException e1) {  }
		}
	}
	
	public static Email getInstance()
	{
		if (instance == null) instance = new Email();
		return instance;
	}
	
	/**
	 * Send an authentication's code to user's email for chancing password
	 * @param userName username of the user
	 * @param to email of the user
	 * @param code authentication code
	 * @throws MessagingException if the email has not been sent
	 */
	protected void sendMail(String userName, String to, String code) throws MessagingException
	{
		Properties proprieties = new Properties(); //Set proprieties
		proprieties.put("mail.smtp.auth", "true");
		proprieties.put("mail.smtp.starttls.enable", "true");
		proprieties.put("mail.smtp.host", "smtp.gmail.com");
		proprieties.put("mail.smtp.port", "587");
        proprieties.put("mail.smtp.user", from);
        proprieties.put("mail.smtp.password", password);
		
		Session session = Session.getInstance(proprieties, new Authenticator(){ //Set Session
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}});
		
		Message message = new MimeMessage(session); //Set Message
		message.setFrom(new InternetAddress(from));
		message.setRecipients(Message.RecipientType.TO,
			InternetAddress.parse(to));
		message.setSubject("Crucy");
		message.setText("Dear " + userName + " ,\n\nWe've received a request to reset the password for this email address. \n\n" 
				+"To reset your password please insert this code:\n" + code + "\n\n"
				+ "If you don't want to reset your password, please ignore this message. Your password will not be reset.\n" 
				+ "For further informations, please email crucy.crossword@gmail.com");
		Transport.send(message);
	}
}
