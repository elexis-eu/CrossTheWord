package it.uniroma1.lcl.crucy.cache;

/**
 * Classe utilizzata per prendere i dati dal server in modo asincrono.
 * @author dario
 *
 * @param <T> Risultato della richiesta di tipo generico T.
 */
public interface Callback<T>
{
	/**
	 * Metodo invocato se la richiesta ha avuto successo.
	 * @param result il risultato della richiesta.
	 */
	void onSuccess(T result);
	
	/**
	 * Metodo invocato se la richiesta non ha avuto successo.
	 * @param cause la causa per cui e' fallita la richiesta.
	 */
	void onFailure(Throwable cause);
}
