package com.shepherdjerred.sthorses.listeners;

import com.shepherdjerred.sthorses.Main;
import com.shepherdjerred.sthorses.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreListener implements Listener {

    private final List<InventoryAction> ALLOWED_ACTIONS = Arrays.asList(
            InventoryAction.PICKUP_ALL
    );

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        // Check permission
        if (!player.hasPermission("stHorses.store")) {
            return;
        }

        // Null checks
        if (event.getClickedInventory() == null) {
            return;
        }

        // Check that the inventory belongs to a horse
        if (!(event.getClickedInventory().getHolder() instanceof AbstractHorse)) {
            return;
        }

        if (event.getSlot() != 0 && event.getSlot() != 1) {
            return;
        }

        if (event.getCurrentItem().getType() != Material.SADDLE && event.getCurrentItem().getType() != Material.CARPET) {
            return;
        }

        // Don't continue if we're ignoring shift-clicks
        if (Main.getInstance().getConfig().getBoolean("store.ShiftClickIgnored")) {
            if (!ALLOWED_ACTIONS.contains(event.getAction())) {
                return;
            }
        }

        // Don't continue if the clicked saddle has lore
        if (event.getCurrentItem().getItemMeta().hasLore()) {
            return;
        }

        AbstractHorse abstractHorse = (AbstractHorse) event.getClickedInventory().getHolder();

        ItemStack saddle = new ItemStack(event.getCurrentItem().getType(), 1);
        ItemMeta saddleMeta = saddle.getItemMeta();
        List<String> lore = createAbstractHorseLore(abstractHorse);

        if (abstractHorse instanceof Horse) {
            lore.addAll(createHorseLore((Horse) abstractHorse));
        } else if (abstractHorse instanceof Llama) {
            lore.addAll(createLlamaLore((Llama) abstractHorse));
        }

        saddleMeta.setDisplayName("stHorses Saddle");
        saddleMeta.setLore(lore);
        saddle.setItemMeta(saddleMeta);

        ItemUtils.addGlow(saddle);

        event.setCurrentItem(new ItemStack(Material.AIR));

        // Drop the horses inventory
        abstractHorse.getInventory().forEach(item -> {
            if (item != null) {
                abstractHorse.getWorld().dropItem(abstractHorse.getLocation(), item);
                item.setType(Material.AIR);
            }
        });

        // Remove the horse
        abstractHorse.remove();

        // Check for full inventory; Drop item or give item to player
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), saddle);
        } else {
            player.getInventory().addItem(saddle);
        }

        event.setCancelled(true);

    }

    private List<String> createAbstractHorseLore(AbstractHorse abstractHorse) {
        List<String> lore = new ArrayList<>();

        String name = "Name: ";
        String owner = "Owner: ";
        String ownerUuid = "Owner UUID: ";

        if (abstractHorse.getCustomName() != null) {
            name = name.concat(abstractHorse.getCustomName());
        } else {
            name = name.concat("None");
        }

        if (abstractHorse.getOwner() != null) {
            owner = owner.concat(String.valueOf(abstractHorse.getOwner().getName()));
            ownerUuid = ownerUuid.concat(String.valueOf(abstractHorse.getOwner().getUniqueId()));
        } else {
            owner = owner.concat("None");
            ownerUuid = ownerUuid.concat("None");
        }

        double jumpValue = abstractHorse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).getValue();

        // Formula from http://minecraft.gamepedia.com/Horse#Statistics
        double jumpValueInBlocks = -0.1817584952 * Math.pow(jumpValue, 3) + 3.689713992 * Math.pow(jumpValue, 2) + 2.128599134 * jumpValue - 0.343930367;
        jumpValueInBlocks = (double) Math.round(jumpValueInBlocks * 1d) / 1d;

        double speedValue = abstractHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
        double speedValueInBlocks = 43.178 * speedValue - 0.0214;
        speedValueInBlocks = (double) Math.round(speedValueInBlocks * 1d) / 1d;

        String variant = "Variant: " + abstractHorse.getClass().getSimpleName().replace("Craft", "");
        String jump = "Jump: ~"  + String.valueOf(jumpValueInBlocks) + " blocks";
        String speed = "Speed: ~" + String.valueOf(speedValueInBlocks) + " blocks";
        String health = "Health: " + String.valueOf(abstractHorse.getHealth()) + "/" + String.valueOf(abstractHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        String domestication = "Domestication: " + String.valueOf(abstractHorse.getDomestication() + "/" + String.valueOf(abstractHorse.getMaxDomestication()));
        String age = "Age: " + String.valueOf(abstractHorse.getAge());
        String realJump = "Real Jump: " + String.valueOf(jumpValue);
        String realSpeed = "Real Speed: " + String.valueOf(speedValue);

        lore.addAll(Arrays.asList(
                name, owner, variant, jump, speed, health, domestication, age, ownerUuid, realJump, realSpeed
        ));

        return lore;
    }

    private List<String> createHorseLore(Horse horse) {
        List<String> lore = new ArrayList<>();
        lore.add("Color: " + horse.getColor().toString());
        lore.add("Style: " + horse.getStyle().toString());
        return lore;
    }

    private List<String> createLlamaLore(Llama llama) {
        List<String> lore = new ArrayList<>();
        lore.add("Color: " + llama.getColor().toString());
        return lore;
    }

}
