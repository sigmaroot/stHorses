package com.shepherdjerred.sthorses.listeners;

import com.shepherdjerred.sthorses.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PlaceListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteractEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check permission
        if (!player.hasPermission("stHorses.place")) {
            return;
        }

        // Ensure the player is clicking a block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Null checks!
        if (item == null) {
            return;
        }

        // Check that it's a saddle or carpet
        if (item.getType() != Material.SADDLE && item.getType() != Material.CARPET) {
            return;
        }

        // Check that it has a lore
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();

        if (!itemMeta.getLore().stream().anyMatch(lore -> lore.contains("Name: "))) {
            return;
        }

        // TODO Check if placing on interactable block

        Location spawnLocation = new Location(event.getClickedBlock().getWorld(),
                event.getClickedBlock().getX(),
                event.getClickedBlock().getY() + 1,
                event.getClickedBlock().getZ());

        AbstractHorse abstractHorse = createAbstractHorse(itemMeta, spawnLocation);

        // Something went wrong, let's stop
        if (abstractHorse == null) {
            return;
        }

        player.getInventory().removeItem(item);

    }

    private AbstractHorse createAbstractHorse(ItemMeta itemMeta, Location location) {

        List<String> lore = itemMeta.getLore();

        // TODO Check for valid lines/lore first, throw exceptions if neeeded

        String variant = lore.stream().filter(str -> str.contains("Variant: ")).findFirst().get().replace("Variant: ", "");
        String name = lore.stream().filter(str -> str.contains("Name: ")).findFirst().get().replace("Name: ", "");
        
        String ownerUuid = "";
        try {
            ownerUuid = lore.stream().filter(str -> str.contains("Owner UUID: ")).findFirst().get().replace("Owner UUID: ", "");
        } catch (NoSuchElementException e) {
            ownerUuid = lore.stream().filter(str -> str.contains("UUID: ")).findFirst().get().replace("UUID: ", "");
        }        

        String domestication = lore.stream().filter(str -> str.contains("Domestication: ")).findFirst().get().replace("Domestication: ", "");
        String health = lore.stream().filter(str -> str.contains("Health: ")).findFirst().get().replace("Health: ", "");

        double jump = 0.0D;
        try {
            jump = Double.valueOf(lore.stream().filter(str -> str.contains("Real Jump: ")).findFirst().get().replace("Real Jump: ", ""));
        } catch (NoSuchElementException e) {
            jump = Double.valueOf(lore.stream().filter(str -> str.contains("Jump: ")).findFirst().get().replace("Jump: ", ""));
        }
        
        double speed = 0.0D;
        try {
            speed = Double.valueOf(lore.stream().filter(str -> str.contains("Real Speed: ")).findFirst().get().replace("Real Speed: ", ""));
        } catch (NoSuchElementException e) {
            speed = Double.valueOf(lore.stream().filter(str -> str.contains("Speed: ")).findFirst().get().replace("Speed: ", ""));
        }

        int age = Integer.valueOf(lore.stream().filter(str -> str.contains("Age: ")).findFirst().get().replace("Age: ", ""));
        
        String carpetColor = "";
        try {
            carpetColor = lore.stream().filter(str -> str.contains("Carpet: ")).findFirst().get().replace("Carpet: ", "");
        } catch (NoSuchElementException e) {
            carpetColor = "WHITE";
        }

        AbstractHorse abstractHorse;

        try {
            Class<?> clazz = Class.forName("org.bukkit.entity." + translateOldVariants(variant));
            Class<? extends AbstractHorse> subclass = clazz.asSubclass(AbstractHorse.class);
            abstractHorse = location.getWorld().spawn(location, subclass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        if (!name.equals("None")) {
            abstractHorse.setCustomName(name);
        }

        if (!ownerUuid.equals("None")) {
            abstractHorse.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(ownerUuid)));
        }

        String[] horseHealth = health.replace("Health: ", "").split("/");
        String[] horseDom = domestication.replace("Domestication: ", "").split("/");

        double horseCurrentHealth = Double.valueOf(horseHealth[0]);
        double horseMaxHealth = Double.valueOf(horseHealth[1]);
        int horseCurrentDom = Integer.valueOf(horseDom[0]);
        int horseMaxDom = Integer.valueOf(horseDom[1]);

        abstractHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(horseMaxHealth);
        abstractHorse.setHealth(horseCurrentHealth);
        abstractHorse.setMaxDomestication(horseMaxDom);
        abstractHorse.setDomestication(horseCurrentDom);

        abstractHorse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(jump);
        abstractHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
        abstractHorse.setAge(age);

        if (abstractHorse instanceof Horse) {
            createHorse((Horse) abstractHorse, lore);
        } else if (abstractHorse instanceof Llama) {
            createLlama((Llama) abstractHorse, lore);
        }

        if (abstractHorse instanceof Horse) {
            giveSaddle(abstractHorse);
        } else if (abstractHorse instanceof Llama) {
            giveCarpet(abstractHorse, carpetColor);
        } else {
            abstractHorse.getInventory().setItem(0, new ItemStack(Material.SADDLE, 1));
        }

        return abstractHorse;
    }
    
    private String translateOldVariants(String variant) {
        switch (variant) {
        case "DONKEY":
            return "Horse";
        case "HORSE":
            return "Horse";
        case "MULE":
            return "Mule";
        case "SKELETON_HORSE":
            return "SkeletonHorse";
        default:
            return variant;
        }
    }

    private void createHorse(Horse horse, List<String> lore) {
        String color = lore.stream().filter(str -> str.contains("Color: ")).findFirst().get().replace("Color: ", "");
        String style = lore.stream().filter(str -> str.contains("Style: ")).findFirst().get().replace("Style: ", "");
        horse.setColor(Horse.Color.valueOf(color));
        horse.setStyle(Horse.Style.valueOf(style));
    }

    private void createLlama(Llama llama, List<String> lore) {
        String color = lore.stream().filter(str -> str.contains("Color: ")).findFirst().get().replace("Color: ", "");
        llama.setColor(Llama.Color.valueOf(color));
    }

    private void giveSaddle (AbstractHorse abstractHorse) {
        ((Horse) abstractHorse).getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
    }
    
    private void giveCarpet (AbstractHorse abstractHorse, String carpetColor) {
        ItemStack carpet = new ItemStack(Material.CARPET, 1);
        carpet.setDurability(ItemUtils.getCarpetColorAsShort(carpetColor));
        abstractHorse.getInventory().setItem(1, carpet);
    }

}
