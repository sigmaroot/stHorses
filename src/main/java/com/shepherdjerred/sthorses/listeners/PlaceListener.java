package com.shepherdjerred.sthorses.listeners;

import com.shepherdjerred.sthorses.Main;
import com.shepherdjerred.sthorses.messages.MessageHelper;
import net.minecraft.server.v1_11_R1.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class PlaceListener implements Listener {

    private static List<Material> interactables = Arrays.asList(Material.CHEST, Material.ENDER_CHEST, Material.BREWING_STAND, Material.HOPPER, Material.DISPENSER, Material.FURNACE, Material.BURNING_FURNACE, Material.TRAPPED_CHEST, Material.ENCHANTMENT_TABLE, Material.WORKBENCH);

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteractEvent(PlayerInteractEvent event) {

        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand() != null) {

            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType() == Material.SADDLE && item.getItemMeta() != null) {

                ItemMeta itemMeta = item.getItemMeta();

                if (itemMeta.getLore() != null) {

                    List<String> itemLore = itemMeta.getLore();

                    // Check that the lore is ours
                    if (itemLore.get(0).contains("Name:")
                            && itemLore.get(1).contains("Owner:")
                            && itemLore.get(2).contains("Variant:")
                            && itemLore.get(3).contains("Color:")
                            && itemLore.get(4).contains("Style:")
                            && itemLore.get(5).contains("Jump:")
                            && itemLore.get(6).contains("Speed:")
                            && itemLore.get(7).contains("Health:")
                            && itemLore.get(8).contains("Domestication:")
                            && itemLore.get(9).contains("Age:")
                            && itemLore.get(10).contains("UUID:")) {

                        if (!player.hasPermission("stHorses.spawn")) {
                            player.sendMessage(MessageHelper.getMessagePrefix() + MessageHelper.colorMessagesString("place.noPermisson"));
                            return;
                        }


                        if (Main.getInstance().getConfig().getBoolean("store.notOnInteractable")
                                && interactables.contains(event.getClickedBlock().getType()))
                            return;

                        Location location = new Location(event.getClickedBlock().getWorld(),
                                event.getClickedBlock().getX(),
                                event.getClickedBlock().getY() + 1,
                                event.getClickedBlock().getZ());

                        Horse horse = event.getClickedBlock().getWorld().spawn(location, Horse.class);

                        if (Main.getInstance().getConfig().getBoolean("store.safeSpawning")
                                && horse.getEyeLocation().getBlock().getType() != Material.AIR
                                && horse.getLocation().getBlock().getType() != Material.AIR) {
                            horse.remove();
                            event.setCancelled(true);
                            player.sendMessage(MessageHelper.getMessagePrefix() + MessageHelper.colorMessagesString("store.cantSpawnThere"));
                            return;
                        }

                        player.getInventory().removeItem(item);

                        CraftLivingEntity horseNMS = (CraftLivingEntity) horse;
                        String[] horseHealth = itemLore.get(7).replace("Health: ", "").split("/");
                        String[] horseDom = itemLore.get(8).replace("Domestication: ", "").split("/");

                        if ((!itemLore.get(0).equals("Name: None")))
                            horse.setCustomName(itemLore.get(0).replace("Name: ", ""));

                        horse.setStyle(Style.valueOf(itemLore.get(2).replace("Style: ", "")));
                        horse.setColor(Color.valueOf(itemLore.get(3).replace("Color: ", "")));
                        horse.setStyle(Style.valueOf(itemLore.get(4).replace("Style: ", "")));
                        horse.setJumpStrength(Double.parseDouble(itemLore.get(5).replace("Jump: ", "")));
                        horseNMS.getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)
                                .setValue(Double.parseDouble(itemLore.get(6).replace("Speed: ", "")));
                        horse.setMaxHealth(Double.parseDouble(horseHealth[1]));
                        horse.setHealth(Double.parseDouble(horseHealth[0]));
                        horse.setDomestication(Integer.parseInt(horseDom[0]));
                        horse.setMaxDomestication(Integer.parseInt(horseDom[1]));
                        horse.setAge(Integer.parseInt(itemLore.get(9).replace("Age: ", "")));
                        horse.setOwner(Main.getInstance().getServer().getOfflinePlayer((UUID.fromString(itemLore.get(10).replace("UUID: ", "")))));

                        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));

                    }
                }
            }
        }
    }
}
