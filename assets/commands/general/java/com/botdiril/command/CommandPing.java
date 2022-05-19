package com.botdiril.command;

import com.botdiril.command.context.CommandContext;
import com.botdiril.response.ResponseEmbed;

@Command("ping")
public class CommandPing extends CommandBase
{
    public static void ping(CommandContext co)
    {
        var jda = co.getJDA();

        var eb = new ResponseEmbed();
        eb.setAuthor("Botdiril Debug Commands", null, co.getBotIconURL());
        eb.setTitle("Pong.");
        eb.setColor(0x008080);
        eb.setDescription(jda.getGatewayPing() + " ms");

        co.respond(eb);
    }
}
