package it.uniroma1.lcl.crucy.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.Dimension;
import java.awt.Toolkit;

import it.uniroma1.lcl.crucy.MainCrucy;

public class DesktopLauncher {
	final static double SCALE_FACTOR = 0.5625;
	final static int EPSILON = 40;

	public static void main (String[] arg) {
		// Get Screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Subtract from screen height screen EPSILON
		int height = screenSize.height - EPSILON;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// Set window size in proportion with screen size
		config.width = (int)(height * SCALE_FACTOR);
		config.height = height;
		config.title = "Crucy";
        config.resizable = false;
		config.vSyncEnabled = false; // Setting to false disables vertical sync
		config.samples =  3;
		config.foregroundFPS = 120; // Setting to 0 disables foreground fps throttling
		config.backgroundFPS = 120;

		new LwjglApplication(new MainCrucy(), config);

	}
}
