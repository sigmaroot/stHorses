
package com.shepherdjerred.sthorses;

import java.io.IOException;

import com.shepherdjerred.sthorses.files.ConfigHelper;
import com.shepherdjerred.sthorses.listeners.PlaceListener;
import com.shepherdjerred.sthorses.metrics.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;

import com.shepherdjerred.sthorses.commands.MainExecutor;
import com.shepherdjerred.sthorses.listeners.StoreListener;


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

		this.saveDefaultConfig();
		ConfigHelper.getInstance().loadFiles();

		// Register events
		getServer().getPluginManager().registerEvents(new StoreListener(), this);
		getServer().getPluginManager().registerEvents(new PlaceListener(), this);

		// Register Commands
		this.getCommand("sth").setExecutor(new MainExecutor());

		// Setup Metrics
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
