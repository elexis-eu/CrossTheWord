package it.uniroma1.lcl.crucyservlet.mongodb;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.mail.MessagingException;

import it.uniroma1.lcl.crucyservlet.mongodb.exception.PasswordWrongException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserDoesntExistException;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.UserExistException;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.CODE_AUTH;
import static it.uniroma1.lcl.crucyservlet.utils.Parameters.TIME_CODE_AUTH;

/**
 * This class contains static methods for user's login
 * @author gio
 *
 */
public class Login 
{
	/**
	 * Email instance
	 */
	private static Email emailInstance = Email.getInstance();
	/**
	 * UserDB instance
	 */
	//private static UserDB userDBInstance = UserDB.getInstance();
	private static UserDatabase userDBInstance = UserDatabase.getInstance();

	/**
	 * iteration counter for hashed password
	 */
	private static final int ITERATION_COUNT = 70000;
	/**
	 * length of key used to crypted key
	 */
	private static final int KEY_LENGHT = 512;
	/**
	 * length of the authentication code
	 */
	private static final int AUTH_CODE_LENGTH = 8;
	/**
	 * validity time of authentication code 
	 */
	private static final long TIME_CODE_LIFE = 20l;

	/**
	 * Generate a random String authentication code
	 * @return authentication code
	 */
	private static String generateAuthCode()
	{
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		StringBuilder sb = new StringBuilder(AUTH_CODE_LENGTH);
		Random random = new Random();
		for (int i = 0; i < AUTH_CODE_LENGTH; i++)  
		    sb.append(chars[random.nextInt(chars.length)]);
	    return sb.toString();
	}
	
	/**
	 * Change password of user, checking if the authentication code is valid
	 * @param email email of user
	 * @param authcode authentication code sent to email
	 * @param password new password
	 * @return true if password has been changed
	 * @throws UserDoesntExistException if user doesn't exist
	 */
	public static boolean changePassword(String email, String authcode, String password) throws UserDoesntExistException
	{
		String userName = userDBInstance.getUsername(email);
		Map<String, String> authDB = userDBInstance.getAuthCode(email);
		long timeCode = new Date().getTime() / 60000; //after 20 minutes the code not work
		if(Math.abs(timeCode - Long.parseLong(authDB.get(TIME_CODE_AUTH))) <= TIME_CODE_LIFE && authcode.equals(authDB.get(CODE_AUTH)))
		{
			final byte[] SALT = salt();
			userDBInstance.setPassword(userName, Base64.getEncoder().encodeToString(cryptKey(password, SALT).getBytes()), Base64.getEncoder().encodeToString(SALT));
			return true;
		}
		return false;
	}
	
	/**
	 * check if the user exist and recovers information that will be sent in email's user{@code} sendMail}
	 * @param email email of the user
	 * @return true if the operation has been performed
	 * @throws UserDoesntExistException
	 * @throws MessagingException if the email has not been sent
	 */
	public static boolean forgotPassword(String email) throws UserDoesntExistException, MessagingException {
		String userName = userDBInstance.getUsername(email);
		String code = generateAuthCode();
		emailInstance.sendMail(userName, email, code);
		String dBTimeCode = "" + new Date().getTime() / 60000;
		userDBInstance.setAuthCode(email, code, dBTimeCode);
		return true;
	}
	
	/**
	 * connect to crucydatabase and checking if the email and the password are correct
	 * @param email email of the user
	 * @param password password of the user
	 * @return idSession that identifies the last access of the device
	 * @throws UserDoesntExistException if the user doesn't exist
	 * @throws PasswordWrongException if the password isnt' correct
	 */
	public static String login(String email, String password) throws UserDoesntExistException, PasswordWrongException, UnsupportedEncodingException {
		String[] credentials = userDBInstance.login(email.toLowerCase());

		//The password sent with the request is salted and ciphered
		byte[] salt = Base64.getDecoder().decode(credentials[1].getBytes("UTF-8"));
		String checkedPassword = cryptKey(password, salt);
		System.out.println("Client password: " + checkedPassword);

		//The password saved on the User Database is decoded
		credentials[0] = new String(Base64.getDecoder().decode(credentials[0].getBytes("UTF-8")));
		System.out.println("Server password: " + credentials[0]);

		if (!credentials[0].equals(checkedPassword)) //If the passwords don't match the sent one is wrong
			throw new PasswordWrongException();
		return credentials[2]; 						//If the password match I send back the Current Session
	}

	/**
	 * Add user on crucydatabase
	 * @param username
	 * @param password
	 * @param email
	 * @param idSession
	 * @throws UserExistException if the user doesn't exist
	 */
	public static void addUser(String email, String username, String password, String idSession) throws UserExistException {
		final byte[] SALT = salt();
		userDBInstance.addUser(email.toLowerCase(), username, Base64.getEncoder().encodeToString(cryptKey(password, SALT).getBytes()), Base64.getEncoder().encodeToString(SALT),idSession);
	}

	public static void addFacebookUser(String userName, String eMail, String idSession) throws UserExistException {
		addUser(eMail.toLowerCase(), userName, null, idSession);
	}

	public static String loginFacebook(String eMail) throws UserDoesntExistException {
		return userDBInstance.login(eMail.toLowerCase())[2];
	}

	/**
	 * Crypt the password
	 * @param PLAIN_TEXT plain password
	 * @param SALT salt
	 * @return Crypted String
	 */
	private static String cryptKey(final String PLAIN_TEXT, final byte[] SALT) {
		try 
		{
			return new String(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
					.generateSecret(new PBEKeySpec(PLAIN_TEXT.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGHT))
					.getEncoded());
		} 
		catch (InvalidKeySpecException | NoSuchAlgorithmException e) 
		{
			try(BufferedWriter bw = Files.newBufferedWriter(Paths.get("loginKey.log"), StandardOpenOption.APPEND))
			{
				String error = new Date() + "\tERROR->\t" + e.getMessage() + "\n";
				bw.write(error);
			} catch (IOException e1) {  }
			return "";
		}
	}
	
	/**
	 * generate salt used for encrypting password
	 * @return random array of byte
	 */
	private static byte[] salt()
	{
		SecureRandom random = new SecureRandom();
        byte[] salt = new byte[24];
        random.nextBytes(salt);
		return salt;
	}
}
