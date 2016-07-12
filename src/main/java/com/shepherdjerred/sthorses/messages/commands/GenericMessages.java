package com.shepherdjerred.sthorses.messages.commands;

import com.shepherdjerred.sthorses.messages.MessageHelper;

public class GenericMessages {

    public static String getReloadMessage() {
        return MessageHelper.getMessagePrefix() + MessageHelper.colorMessagesString("messages.generic.reload");
    }

    public static String getNoPermsMessage() {
        return MessageHelper.getMessagePrefix() + MessageHelper.colorMessagesString("messages.generic.noPerms");
    }

    public static String getNoConsoleMessage() {
        return MessageHelper.getMessagePrefix() + MessageHelper.colorMessagesString("messages.generic.noConsole");
    }

    public static String getNoArgsMessage(String correctArgs) {
        return MessageHelper.getMessagePrefix() + MessageHelper.colorMessagesString("messages.generic.noArgs.correct")
                .replace("%args%", correctArgs);
    }

    public static String getInvalidArgsMessage(String givenArg, String correctArgs) {
        return MessageHelper.getMessagePrefix() + MessageHelper.colorMessagesString("messages.generic.invalidArg.correct")
                .replace("%arg%", givenArg)
                .replace("%args%", correctArgs);
    }

}
