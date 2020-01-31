package it.uniroma1.lcl.crucy.cache.exceptions;

/**
 * Exception lanciata se l'username inserito non e' valido.
 * @author dario
 *
 */
public class InvalidUsernameException extends IllegalArgumentException
{

	public InvalidUsernameException(){super();}
	public InvalidUsernameException(String username){super(username);}
}
