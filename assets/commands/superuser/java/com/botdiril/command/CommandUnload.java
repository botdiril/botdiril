package com.botdiril.command;

import com.botdiril.command.context.DiscordCommandContext;

@Command("unload")
public class CommandUnload
{
    public static void unload(DiscordCommandContext dcc)
    {
        var textChannel = dcc.textChannel;
        CommandManager.unload(() -> textChannel.sendMessage("**Unload complete.**").queue());
    }
}