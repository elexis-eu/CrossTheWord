package it.uniroma1.lcl.crucy.cache.retrievers;

/**
 * Classe che si occupa di restituire le informazioni del server.
 * @author dario
 *
 */
public interface InformationRetriever
{
	void init();
	String getServerUrl();
	String getExsternalSession();
	String getConnLanguage();
}
