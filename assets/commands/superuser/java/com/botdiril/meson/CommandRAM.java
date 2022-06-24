package com.botdiril.meson;

import com.botdiril.command.Command;
import com.botdiril.command.behavior.CommandBase;
import com.botdiril.command.behavior.CommandBehavior;
import com.botdiril.command.behavior.EnumVolatility;
import com.botdiril.command.context.CommandContext;
import com.botdiril.response.ResponseEmbed;

@Command("memory")
public class CommandRAM extends CommandBase<CommandContext>
{
    @Override
    public Runnable declareBehavior(CommandBehavior behavior)
    {
        behavior.setVolatility(EnumVolatility.STATELESS);

        return () -> {
            var eb = new ResponseEmbed();
            eb.setAuthor("Botdiril Debug Commands", null, this.co.getBotIconURL());
            eb.setTitle("Memory information.");
            eb.setColor(0x008080);

            var rt = Runtime.getRuntime();

            eb.addField("Maximum memory", rt.maxMemory() / 1_048_576 + " MiB", false);
            eb.addField("Used memory", rt.totalMemory() / 1_048_576 + " MiB", false);
            eb.addField("Free memory", rt.freeMemory() / 1_048_576 + " MiB", false);

            this.co.respond(eb);
        };
    }
}
