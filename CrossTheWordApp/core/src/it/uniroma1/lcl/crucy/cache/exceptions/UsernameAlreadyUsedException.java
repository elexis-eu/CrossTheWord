package it.uniroma1.lcl.crucy.cache.exceptions;

public class UsernameAlreadyUsedException extends IllegalArgumentException
{

	public UsernameAlreadyUsedException(){super();}
	public UsernameAlreadyUsedException(String username){super(username);}
}
