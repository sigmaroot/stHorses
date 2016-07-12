package com.shepherdjerred.sthorses.messages;

import com.shepherdjerred.sthorses.Main;
import com.shepherdjerred.sthorses.files.FileManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class MessageHelper {


    public static String colorString(String input) {

        return ChatColor.translateAlternateColorCodes('&', input);

    }

    public static String colorConfigString(String input) {

        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString(input));

    }

    public static List<String> colorConfigListStrings(String input) {

        List<String> output = new ArrayList<>();

        Main.getInstance().getConfig().getStringList(input).forEach(s -> {
            output.add(ChatColor.translateAlternateColorCodes('&', s));
        });

        return output;

    }

    public static String colorMessagesString(String input) {

        return ChatColor.translateAlternateColorCodes('&', FileManager.getInstance().messages.getString(input));

    }

    public static List<String> colorMessagesListStrings(String input) {

        List<String> output = new ArrayList<>();

        Main.getInstance().getConfig().getStringList(input).forEach(s -> {
            output.add(ChatColor.translateAlternateColorCodes('&', s));
        });

        return output;

    }

    public static String getMessagePrefix() {

        return colorMessagesString("prefix");

    }


}
