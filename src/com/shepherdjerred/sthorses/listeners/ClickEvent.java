
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

		if (event.getClickedInventory().getHolder() instanceof Horse) {

			if (event.getAction() == InventoryAction.PICKUP_ALL) {

				Player player = (Player) event.getWhoClicked();

				Horse horse = (Horse) event.getClickedInventory().getHolder();

				if (event.getSlot() == 0 && event.getCurrentItem().getType() == Material.SADDLE) {

					event.setCurrentItem(new ItemStack(Material.AIR, 1));

					String horseVariant = horse.getVariant().toString();
					String horseColor = horse.getColor().toString();
					String horseStyle = horse.getStyle().toString();
					String horseDomestication = String.valueOf(horse.getDomestication());
					String horseMaxDomestication = String.valueOf(horse.getMaxDomestication());
					String horseJump = String.valueOf(horse.getJumpStrength());
					String horseName = horse.getCustomName();
					String horseMaxHealth = String.valueOf(horse.getMaxHealth());
					String horseHealth = String.valueOf(horse.getHealth());
					String horseAge = String.valueOf(horse.getAge());
					String horseOwner = horse.getOwner().getName();
					String horseUUID = horse.getOwner().getUniqueId().toString();

					for (ItemStack horseItem : horse.getInventory()) {

						if (horseItem != null) {

							horse.getWorld().dropItem(horse.getLocation(), horseItem);

							horseItem.setType(Material.AIR);
						}

					}

					horse.remove();

					ItemStack saddle = new ItemStack(Material.SADDLE, 1);
					ItemMeta saddleMeta = saddle.getItemMeta();

					List<String> saddleLore = new ArrayList<String>();

					saddleLore.add("Type: " + horseVariant);
					saddleLore.add("Color: " + horseColor);
					saddleLore.add("Style: " + horseStyle);
					saddleLore.add("Dom: " + horseDomestication);
					saddleLore.add("MaxDom: " + horseMaxDomestication);
					saddleLore.add("Jump: " + horseJump);
					saddleLore.add("Name: " + horseName);
					saddleLore.add("Health: " + horseHealth);
					saddleLore.add("MaxHealth: " + horseMaxHealth);
					saddleLore.add("Age: " + horseAge);
					saddleLore.add("Owner: " + horseOwner);
					saddleLore.add("UUID: " + horseUUID);

					saddleMeta.setLore(saddleLore);

					saddle.setItemMeta(saddleMeta);

					player.getInventory().addItem(saddle);

				}

			}
		}

	}

}
