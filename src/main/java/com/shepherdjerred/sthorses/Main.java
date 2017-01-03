
package com.shepherdjerred.sthorses;

import com.shepherdjerred.sthorses.files.ConfigHelper;
import com.shepherdjerred.sthorses.listeners.PlaceListener;
import com.shepherdjerred.sthorses.listeners.StoreListener;
import com.shepherdjerred.sthorses.metrics.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;


public class Main extends JavaPlugin {

	// Provide instance of Main class
	private static Main instance;

	public Main() {
		instance = this;
	}

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {

		ConfigHelper.loadConfigs();

		// Register events
		getServer().getPluginManager().registerEvents(new StoreListener(), this);
		getServer().getPluginManager().registerEvents(new PlaceListener(), this);

		// Setup Metrics
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
