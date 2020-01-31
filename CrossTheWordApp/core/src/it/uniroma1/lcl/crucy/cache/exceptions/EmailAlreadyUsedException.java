package it.uniroma1.lcl.crucy.cache.exceptions;

/**
 * Created by bejgu on 28/09/2016.
 */

public class EmailAlreadyUsedException extends Exception
{
	public EmailAlreadyUsedException(){super();}
	public EmailAlreadyUsedException(String email){super(email);}
}
