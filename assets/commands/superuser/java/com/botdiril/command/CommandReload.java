package com.botdiril.command;

import com.botdiril.command.context.DiscordCommandContext;

@Command("reload")
public class CommandReload
{
    public static void reload(DiscordCommandContext dcc)
    {
        var textChannel = dcc.textChannel;
        CommandManager.unload(() -> {
            CommandManager.load();
            textChannel.sendMessage("**Reload complete.**").queue();
        });
    }
}