package it.uniroma1.lcl.crucy.cache.exceptions;

/**
 * Exception lanciata se l'email inserita non e' valida.
 * @author dario
 *
 */
public class InvalidEmailException extends IllegalArgumentException
{
	public InvalidEmailException(){super();}
	public InvalidEmailException(String email){super(email);}
}
