package it.uniroma1.lcl.crucy.cache.retrievers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import it.uniroma1.lcl.crucy.cache.utils.Parameters;

/**
 * Classe Factory, costruisce l'oggetto di tipo InformationRetriever.
 * @author dario
 *
 */
public class InformationRetrieverFactory
{
	public static InformationRetriever create()
	{
		return new InformationRetriever()
		{

			private String serverUrl;
			private String exsternalSession;
			private String connectionLanguage;
			
			@Override
			public void init()
			{
				FileHandle f = Gdx.files.internal("conf"+File.separator+"server.conf");
				Map<String,String> value = new Json().fromJson(new HashMap<String,String>().getClass(),f.readString());
				serverUrl = value.get(Parameters.SERVER_URL);
				exsternalSession = value.get(Parameters.EXTERNAL_SESSION);
				connectionLanguage = value.get(Parameters.CONNECTION_LANGUAGE);
			}

			@Override
			public String getServerUrl(){return serverUrl;}

			@Override
			public String getExsternalSession(){return exsternalSession;}

			@Override
			public String getConnLanguage(){return connectionLanguage;}		
		};
		
	}
}
