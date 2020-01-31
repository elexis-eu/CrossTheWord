package it.uniroma1.lcl.crucy.cache;

/**
 * Classe utilizzata per ricevere una risposta dal server in modo asincrono.
 * @author dario
 *
 */
public interface CallbackVoid
{
	/**
	 * Metodo invocato se la richiesta ha avuto successo.
	 */
	void onSuccess();
	
	/**
	 * Metodo invocato se la richiesta non ha avuto successo.
	 * @param cause la causa per cui e' fallita la richiesta.
	 */
	void onFailure(Throwable cause);
}
