
package com.shepherdjerred.sthorses.listeners;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_9_R1.GenericAttributes;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
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

		// Get the player who took the item
		Player player = (Player) event.getWhoClicked();

		// Check if player is allowed to store a horse
		if (event.getWhoClicked().hasPermission("stHorses.store")) {

			if (event.getClickedInventory() != null) {

				if (event.getClickedInventory().getHolder() != null) {

					// Check that the inventory belongs to a horse
					if (event.getClickedInventory().getHolder() instanceof Horse) {

						// Check that an item was picked up, this allows shift left-clicking to take the saddle off without removing the horse
						if (event.getAction() == InventoryAction.PICKUP_ALL) {

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

								// Get horse NMS
								CraftLivingEntity horseNMS = (CraftLivingEntity) horse;

								// Create variables to store the horses data
								ItemStack saddle = new ItemStack(Material.SADDLE, 1);
								ItemMeta saddleMeta = saddle.getItemMeta();

								// Create list to hold the lore
								List<String> saddleLore = new ArrayList<String>();

								// Add the horses data to the lore variable

								if (horse.getCustomName() != null) {
									saddleLore.add("Name: " + horse.getCustomName());
								} else {
									saddleLore.add("Name: None");
								}

								if (horse.getOwner() != null) {
									saddleLore.add("Owner: " + horse.getOwner().getName());
								} else {
									saddleLore.add("Owner: " + player.getName());
								}

								saddleLore.add("Variant: " + horse.getVariant().toString());
								saddleLore.add("Color: " + horse.getColor().toString());
								saddleLore.add("Style: " + horse.getStyle().toString());
								saddleLore.add("Jump: " + String.valueOf(horse.getJumpStrength()));
								saddleLore.add("Speed: " + String.valueOf(horseNMS.getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
								saddleLore.add("Health: " + String.valueOf(horse.getHealth() + "/" + String.valueOf(horse.getMaxHealth())));
								saddleLore.add("Domestication: " + String.valueOf(horse.getDomestication() + "/" + String.valueOf(horse.getMaxDomestication())));
								saddleLore.add("Age: " + String.valueOf(horse.getAge()));

								if (horse.getOwner() != null) {
									saddleLore.add("UUID: " + horse.getOwner().getUniqueId().toString());
								} else {
									saddleLore.add("UUID: " + player.getUniqueId().toString());
								}

								// Set the lore
								saddleMeta.setLore(saddleLore);

								// Save the lore
								saddle.setItemMeta(saddleMeta);

								// Give a saddle
								player.getInventory().addItem(addGlow(saddle));

								// Remove the horse
								horse.remove();

							}

						}
					}
				}
			}
		}

	}

	private ItemStack addGlow(ItemStack item) {
		net.minecraft.server.v1_9_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null)
			tag = nmsStack.getTag();
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}

}
