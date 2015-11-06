
package com.shepherdjerred.sthorses;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.shepherdjerred.sthorses.commands.MainCommand;
import com.shepherdjerred.sthorses.listeners.ClickEvent;
import com.shepherdjerred.sthorses.listeners.InteractEvent;


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
		Config.getInstance().loadFiles();

		// Register events
		getServer().getPluginManager().registerEvents(new ClickEvent(), this);
		getServer().getPluginManager().registerEvents(new InteractEvent(), this);

		// Register Commands
		this.getCommand("sth").setExecutor(new MainCommand());

		// Setup Metrics
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {

		}
	}

	public String getMessagesString(String input) {
		return ChatColor.translateAlternateColorCodes('&', Config.getInstance().messages.getString(input));
	}
}
