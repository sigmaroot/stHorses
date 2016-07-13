package com.shepherdjerred.sthorses.messages;

import com.shepherdjerred.sthorses.files.FileManager;
import org.bukkit.ChatColor;

public class MessageHelper {


    public static String colorString(String input) {

        return ChatColor.translateAlternateColorCodes('&', input);

    }

    public static String colorMessagesString(String input) {

        if (FileManager.getInstance().messages.getString(input) != null)
            return colorString(FileManager.getInstance().messages.getString(input));
        else
            return "MESSAGE MISSING";

    }

    public static String getMessagePrefix() {

        return colorMessagesString("prefix");

    }


}
