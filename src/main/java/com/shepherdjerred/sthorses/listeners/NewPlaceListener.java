package com.shepherdjerred.sthorses.listeners;

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
import java.util.UUID;

public class NewPlaceListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteractEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check permission
        if (!player.hasPermission("stHorses.place")) {
            Bukkit.broadcastMessage("1");
            return;
        }

        // Ensure the player is clicking a block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            Bukkit.broadcastMessage("2");
            return;
        }

        // Null checks!
        if (item == null) {
            Bukkit.broadcastMessage("3");
            return;
        }

        // Check that it's a saddle or carpet
        if (item.getType() != Material.SADDLE && item.getType() != Material.CARPET) {
            Bukkit.broadcastMessage("4");
            return;
        }

        // Check that it has a display name and lore
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) {
            Bukkit.broadcastMessage("5");
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();

        // Check that the saddle is ours
        if (!itemMeta.getDisplayName().contains("stHorses Saddle")) {
            Bukkit.broadcastMessage("6");
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

    }

    private AbstractHorse createAbstractHorse(ItemMeta itemMeta, Location location) {

        List<String> lore = itemMeta.getLore();

        String variant = lore.stream().filter(str -> str.contains("Variant: ")).findFirst().get().replace("Variant: ", "");
        String name = lore.stream().filter(str -> str.contains("Name: ")).findFirst().get().replace("Name: ", "");
        String ownerUuid = lore.stream().filter(str -> str.contains("Owner UUID: ")).findFirst().get().replace("Owner UUID: ", "");

        String domestication = lore.stream().filter(str -> str.contains("Domestication: ")).findFirst().get().replace("Domestication: ", "");
        String health = lore.stream().filter(str -> str.contains("Health: ")).findFirst().get().replace("Health: ", "");

        double jump = Double.valueOf(lore.stream().filter(str -> str.contains("Real Jump: ")).findFirst().get().replace("Real Jump: ", ""));
        double speed = Double.valueOf(lore.stream().filter(str -> str.contains("Real Speed: ")).findFirst().get().replace("Real Speed: ", ""));

        int age = Integer.valueOf(lore.stream().filter(str -> str.contains("Age: ")).findFirst().get().replace("Age: ", ""));

        AbstractHorse abstractHorse;

        try {
            Class<?> clazz = Class.forName("org.bukkit.entity." + variant);
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

        giveSaddle(abstractHorse);

        return abstractHorse;
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
        if (abstractHorse instanceof Horse) {
            ((Horse) abstractHorse).getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        } else if (abstractHorse instanceof Llama) {
            // TODO Set the carpet to the original item used to store the llama
            abstractHorse.getInventory().setItem(0, new ItemStack(Material.CARPET, 1));
        } else {
            abstractHorse.getInventory().setItem(0, new ItemStack(Material.SADDLE, 1));
        }
    }

}
