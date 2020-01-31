package it.uniroma1.lcl.crucy.cache;

import com.badlogic.gdx.Gdx;

/**
 * Classe utilizzata per prendere i dati in modo asincrono, nel thread principale.
 * @author dario
 *
 * @param <T> Risultato della richiesta di tipo generico T.
 */
public abstract class MainThreadCallback<T> implements Callback<T>
{
	/**
	 * Metodo invocato se la richiesta ha successo.
	 * @param result il risultato della richiesta.
	 */
	public abstract void onSuccessInMainThread(T result);
	
	/**
	 * Metodo invocato se la richiesta non ha successo.
	 * @param cause la causa per cui e' fallita la richiesta.
	 */
	public abstract void onFailureInMainThread(Throwable cause);
	
	@Override
	public void onSuccess(final T result)
	{
		Gdx.app.postRunnable(new Runnable()
				{
					@Override
					public void run()
					{
						onSuccessInMainThread(result);
					}
				});
	}
	
	@Override
	public void onFailure(final Throwable cause)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				onFailureInMainThread(cause);
			}
		});
	}
}
