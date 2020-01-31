package it.uniroma1.lcl.crucy.cache;

import com.badlogic.gdx.Gdx;

/**
 * Classe utilizzata per rivere una risposta dal server in modo asincrono.
 * @author dario
 *
 */
public abstract class MainThreadCallbackVoid implements CallbackVoid
{
	/**
	 * Metodo invocato se la richiesta ha successo.
	 */
	public abstract void onSuccessInMainThread();
	
	/**
	 * Metodo invocato se la richiesta non ha successo.
	 * @param cause la causa per cui e' fallita la richiesta.
	 */
	public abstract void onFailureInMainThread(Throwable cause);
	
	@Override
	public void onSuccess()
	{
		Gdx.app.postRunnable(new Runnable()
				{
					@Override
					public void run()
					{
						onSuccessInMainThread();
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
