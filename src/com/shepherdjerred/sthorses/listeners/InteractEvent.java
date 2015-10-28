
package com.shepherdjerred.sthorses.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.shepherdjerred.sthorses.Main;


public class InteractEvent implements Listener {

	@EventHandler
	public void onInteractEvent(PlayerInteractEvent event) {

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			Player player = event.getPlayer();

			if (player.getItemInHand() != null) {

				ItemStack item = player.getItemInHand();

				if (item.getType() == Material.SADDLE) {

					if (item.getItemMeta() != null) {

						ItemMeta itemMeta = item.getItemMeta();

						if (itemMeta.getLore() != null) {

							List<String> itemLore = itemMeta.getLore();

							if (itemLore.get(0).contains("Type:") && itemLore.get(1).contains("Color:")
									&& itemLore.get(2).contains("Style:") && itemLore.get(3).contains("Dom:")
									&& itemLore.get(4).contains("MaxDom:") && itemLore.get(5).contains("Jump:")
									&& itemLore.get(6).contains("Name") && itemLore.get(7).contains("Health:")
									&& itemLore.get(8).contains("MaxHealth:") && itemLore.get(9).contains("Age:")
									&& itemLore.get(10).contains("Owner:") && itemLore.get(11).contains("UUID:")) {

								Horse horse = event.getClickedBlock().getWorld()
										.spawn(event.getClickedBlock().getLocation(), Horse.class);
								horse.setVariant(Variant.valueOf(itemLore.get(0).replace("Type: ", "")));
								horse.setColor(Color.valueOf(itemLore.get(1).replace("Color: ", "")));
								horse.setStyle(Style.valueOf(itemLore.get(2).replace("Style: ", "")));
								horse.setDomestication(Integer.parseInt(itemLore.get(3).replace("Dom: ", "")));
								horse.setMaxDomestication(Integer.parseInt(itemLore.get(4).replace("MaxDom: ", "")));
								horse.setJumpStrength(Double.parseDouble(itemLore.get(5).replace("Jump: ", "")));

								if (itemLore.get(6).replace("Name: ", "") != "null") {
									horse.setCustomName(itemLore.get(6).replace("Name: ", ""));
								}

								horse.setHealth(Double.parseDouble(itemLore.get(7).replace("Health: ", "")));
								horse.setMaxHealth(Double.parseDouble(itemLore.get(8).replace("MaxHealth: ", "")));
								horse.setAge(Integer.parseInt(itemLore.get(9).replace("Age: ", "")));
								horse.setOwner((AnimalTamer) Main.getInstance().getServer()
										.getPlayer(itemLore.get(10).replace("Owner: ", "")));

							}

						}

					}

				}

			}

		}

	}

}
