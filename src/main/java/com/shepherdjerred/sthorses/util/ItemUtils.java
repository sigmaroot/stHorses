package com.shepherdjerred.sthorses.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static void addGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    public static String getCarpetColorAsString(short color) {
        List<String> colorNames = new ArrayList<>();
        colorNames.add(0, "WHITE");
        colorNames.add(1, "ORANGE");
        colorNames.add(2, "MAGENTA");
        colorNames.add(3, "LIGHT_BLUE");
        colorNames.add(4, "YELLOW");
        colorNames.add(5, "LIME");
        colorNames.add(6, "PINK");
        colorNames.add(7, "GRAY");
        colorNames.add(8, "SILVER");
        colorNames.add(9, "CYAN");
        colorNames.add(10, "PURPLE");
        colorNames.add(11, "BLUE");
        colorNames.add(12, "BROWN");
        colorNames.add(13, "GREEN");
        colorNames.add(14, "RED");
        colorNames.add(15, "BLACK");
        
        return colorNames.get(color);
    }

    public static short getCarpetColorAsShort(String color) {
        for (short i = 0; i < 16; i++) {
            if (getCarpetColorAsString(i).equalsIgnoreCase(color)) {
                return i;
            }
        }

        return 0;
    }

}
