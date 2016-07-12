
package com.shepherdjerred.sthorses.listeners;

import java.util.ArrayList;
import java.util.List;


import com.shepherdjerred.sthorses.Main;
import net.minecraft.server.v1_10_R1.GenericAttributes;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class StoreListener implements Listener {

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != null
                && event.getClickedInventory().getHolder() != null
                && event.getClickedInventory().getHolder() instanceof Horse
                && event.getSlot() == 0
                && event.getCurrentItem().getType() == Material.SADDLE
                && event.getCurrentItem().getItemMeta() != null) {

            if ((!Main.getInstance().getConfig().getBoolean("store.ShiftClickIgnored")
                    && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                    || event.getAction() == InventoryAction.PICKUP_ALL) {

                ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

                // Check if a horse is already stored in this saddle
                if (itemMeta.getLore() != null)
                    if (itemMeta.getLore().get(0).contains("Name:"))
                        return;

                if (!event.getWhoClicked().hasPermission("stHorses.store"))
                    return;

                // Set the picked up item to air so that no saddle will drop
                event.setCurrentItem(new ItemStack(Material.AIR, 1));

                Horse horse = (Horse) event.getClickedInventory().getHolder();

                horse.getInventory().forEach(item -> {
                    if (item != null) {
                        horse.getWorld().dropItem(horse.getLocation(), item);
                        item.setType(Material.AIR);
                    }
                });

                CraftLivingEntity horseNMS = (CraftLivingEntity) horse;
                ItemStack saddle = new ItemStack(Material.SADDLE, 1);
                List<String> saddleLore = new ArrayList<>();

                if (horse.getCustomName() != null)
                    saddleLore.add("Name: " + horse.getCustomName());
                else
                    saddleLore.add("Name: None");

                if (horse.getOwner() != null)
                    saddleLore.add("Owner: " + horse.getOwner().getName());
                else
                    saddleLore.add("Owner: " + player.getName());

                saddleLore.add("Variant: " + horse.getVariant().toString());
                saddleLore.add("Color: " + horse.getColor().toString());
                saddleLore.add("Style: " + horse.getStyle().toString());
                saddleLore.add("Jump: " + String.valueOf(horse.getJumpStrength()));
                saddleLore.add("Speed: " + String.valueOf(horseNMS.getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                saddleLore.add("Health: " + String.valueOf(horse.getHealth() + "/" + String.valueOf(horse.getMaxHealth())));
                saddleLore.add("Domestication: " + String.valueOf(horse.getDomestication() + "/" + String.valueOf(horse.getMaxDomestication())));
                saddleLore.add("Age: " + String.valueOf(horse.getAge()));

                if (horse.getOwner() != null)
                    saddleLore.add("UUID: " + horse.getOwner().getUniqueId().toString());
                else
                    saddleLore.add("UUID: " + player.getUniqueId().toString());

                ItemMeta saddleMeta = saddle.getItemMeta();

                saddleMeta.setLore(saddleLore);
                saddle.setItemMeta(saddleMeta);

                player.getInventory().addItem(addGlow(saddle));
                horse.remove();

            }
        }
    }

    private ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_10_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
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
