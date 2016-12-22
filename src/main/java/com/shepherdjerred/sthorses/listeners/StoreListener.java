package com.shepherdjerred.sthorses.listeners;

import com.shepherdjerred.sthorses.Main;
import com.shepherdjerred.sthorses.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
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
        if (!event.getWhoClicked().hasPermission("stHorses.store")) {
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

        // Be sure we're clicking on the saddle slot, and that a saddle was clicked
        if (event.getSlot() != 0 || event.getCurrentItem().getType() != Material.SADDLE) {
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

        ItemStack saddle = new ItemStack(Material.SADDLE, 1);
        ItemMeta saddleMeta = saddle.getItemMeta();
        List<String> lore = createAbstractHorseLore(abstractHorse);

        if (abstractHorse instanceof Horse) {
            lore.addAll(createHorseLore((Horse) abstractHorse));
        } else if (abstractHorse instanceof Llama) {
            lore.addAll(createLlamaLore((Llama) abstractHorse));
        } else if (abstractHorse instanceof ChestedHorse) {
            lore.addAll(createChestedHorseLore((ChestedHorse) abstractHorse));
        } else if (abstractHorse instanceof ZombieHorse) {
            lore.addAll(createZombieHorseLore((ZombieHorse) abstractHorse));
        } else if (abstractHorse instanceof SkeletonHorse) {
            lore.addAll(createSkeletonHorseLore((SkeletonHorse) abstractHorse));
        } else {
            lore.add("ERROR CREATING LORE");
        }

        // Let's not continue if there was an error creating the lore
        // This shouldn't happen, unless Mojang adds a new horse in a future update
        if (lore.stream().anyMatch(str -> str.trim().equals("ERROR CREATING LORE"))) {
            player.sendMessage("Error creating saddle!");
            return;
        }

        event.setCancelled(true);

        saddleMeta.setLore(lore);
        saddle.setItemMeta(saddleMeta);

        ItemUtils.addGlow(saddle);

        // Check for full inventory; Drop item or give item to player
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), saddle);
        } else {
            player.getInventory().addItem(saddle);
        }


        // Remove the saddle
        event.getClickedInventory().setItem(0, new ItemStack(Material.AIR));

        // Drop the horses inventory
        abstractHorse.getInventory().forEach(item -> {
            if (item != null) {
                abstractHorse.getWorld().dropItem(abstractHorse.getLocation(), item);
                item.setType(Material.AIR);
            }
        });

        // Remove the horse
        abstractHorse.remove();

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

        String jump = "Jump: " + String.valueOf(abstractHorse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).getValue());
        String speed = "Speed: " + String.valueOf(abstractHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue());
        String domestication = "Domestication: " + String.valueOf(abstractHorse.getDomestication() + "/" + String.valueOf(abstractHorse.getMaxDomestication()));
        String health = "Health: " + String.valueOf(abstractHorse.getHealth()) + "/" + String.valueOf(abstractHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        String age = "Age: " + String.valueOf(abstractHorse.getAge());

        lore.addAll(Arrays.asList(
                name, owner, ownerUuid, jump, speed, domestication, health, age
        ));

        return lore;
    }

    private List<String> createHorseLore(Horse horse) {
        List<String> lore = new ArrayList<>();

        lore.add("Variant: Horse");
        lore.add("Color: " + horse.getColor().toString());
        lore.add("Style: " + horse.getStyle().toString());

        return lore;
    }

    private List<String> createLlamaLore(Llama llama) {
        List<String> lore = new ArrayList<>();

        lore.add("Variant: Llama");
        lore.add("Color: " + llama.getColor().toString());

        return lore;
    }

    private List<String> createChestedHorseLore(ChestedHorse chestedHorse) {
        List<String> lore = new ArrayList<>();

        String variant = "Variant: ";

        if (chestedHorse instanceof Donkey) {
            variant = variant.concat("Donkey");
        } else if (chestedHorse instanceof Mule) {
            variant = variant.concat("Mule");
        } else {
            variant = variant.concat("ERROR CREATING LORE");
        }

        lore.add(variant);

        return lore;
    }

    private List<String> createSkeletonHorseLore(SkeletonHorse skeletonHorse) {
        List<String> lore = new ArrayList<>();

        lore.add("Variant: Skeleton");

        return lore;
    }

    private List<String> createZombieHorseLore(ZombieHorse zombieHorse) {
        List<String> lore = new ArrayList<>();

        lore.add("Variant: Zombie");

        return lore;
    }

}
