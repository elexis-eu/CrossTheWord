package it.uniroma1.lcl.crucy;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;


public class AndroidLauncher extends AndroidApplication {

    private final static String TAG = AndroidLauncher.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = false;
        config.useGyroscope = false;
        config.useCompass = false;
        config.useAccelerometer = false;
        config.numSamples = 2;

        initialize(new MainCrucy(), config);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart");
        //autologin();
    }

}