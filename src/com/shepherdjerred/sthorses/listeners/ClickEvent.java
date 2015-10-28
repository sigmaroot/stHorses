
package com.shepherdjerred.sthorses.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ClickEvent implements Listener {

	@EventHandler
	public void onClickEvent(InventoryClickEvent event) {

		if (event.getWhoClicked().hasPermission("stHorses.save")) {

			// Check that the inventory belongs to a horse
			if (event.getClickedInventory().getHolder() instanceof Horse) {

				// Check that an item was picked up, this allows shift left-clicking to take the saddle off without removing the horse
				if (event.getAction() == InventoryAction.PICKUP_ALL) {

					// Get the player who took the item
					Player player = (Player) event.getWhoClicked();

					// Get the horse who the inventory belongs to
					Horse horse = (Horse) event.getClickedInventory().getHolder();

					// Check that the slot was 0, and the item was a saddle
					if (event.getSlot() == 0 && event.getCurrentItem().getType() == Material.SADDLE) {

						// Set the picked up item to air so that no saddle will drop
						event.setCurrentItem(new ItemStack(Material.AIR, 1));

						// Empty the horses inventory
						for (ItemStack horseItem : horse.getInventory()) {

							if (horseItem != null) {

								horse.getWorld().dropItem(horse.getLocation(), horseItem);

								horseItem.setType(Material.AIR);
							}

						}

						// Create variables to store the horses data
						ItemStack saddle = new ItemStack(Material.SADDLE, 1);
						ItemMeta saddleMeta = saddle.getItemMeta();

						List<String> saddleLore = new ArrayList<String>();

						// Add the horses data to the lore variable
						saddleLore.add("Type: " + horse.getVariant().toString());
						saddleLore.add("Color: " + horse.getColor().toString());
						saddleLore.add("Style: " + horse.getStyle().toString());
						saddleLore.add("Dom: " + String.valueOf(horse.getDomestication()));
						saddleLore.add("MaxDom: " + String.valueOf(horse.getMaxDomestication()));
						saddleLore.add("Jump: " + String.valueOf(horse.getJumpStrength()));
						saddleLore.add("Name: " + horse.getCustomName());
						saddleLore.add("Health: " + String.valueOf(horse.getMaxHealth()));
						saddleLore.add("MaxHealth: " + String.valueOf(horse.getHealth()));
						saddleLore.add("Age: " + String.valueOf(horse.getAge()));
						saddleLore.add("Owner: " + horse.getOwner().getName());
						saddleLore.add("UUID: " + horse.getOwner().getUniqueId().toString());

						// Set the lore
						saddleMeta.setLore(saddleLore);

						// Save the lore
						saddle.setItemMeta(saddleMeta);

						// Give a saddle
						player.getInventory().addItem(saddle);

						// Remove the horse
						horse.remove();

					}

				}
			}
		}

	}

}
