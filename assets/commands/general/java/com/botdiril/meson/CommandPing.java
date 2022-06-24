package com.botdiril.meson;

import com.botdiril.command.Command;
import com.botdiril.command.behavior.CommandBase;
import com.botdiril.command.behavior.CommandBehavior;
import com.botdiril.command.behavior.EnumVolatility;
import com.botdiril.command.context.CommandContext;
import com.botdiril.response.ResponseEmbed;

@Command("ping")
public class CommandPing extends CommandBase<CommandContext>
{
    @Override
    public Runnable declareBehavior(CommandBehavior behavior)
    {
        behavior.setVolatility(EnumVolatility.STATELESS);

        return () -> {
            var jda = this.co.getJDA();

            var eb = new ResponseEmbed();
            eb.setAuthor("Botdiril Debug Commands", null, this.co.getBotIconURL());
            eb.setTitle("Pong.");
            eb.setColor(0x008080);
            eb.setDescription(jda.getGatewayPing() + " ms");

            this.co.respond(eb);
        };
    }
}
