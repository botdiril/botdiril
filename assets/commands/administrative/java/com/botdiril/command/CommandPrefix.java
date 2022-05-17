package com.botdiril.command;


import com.botdiril.Botdiril;
import com.botdiril.command.context.DiscordCommandContext;
import com.botdiril.command.invoke.CommandParam;
import com.botdiril.command.invoke.CommandException;
import com.botdiril.util.CommandAssert;

@Command("prefix")
public class CommandPrefix
{
    public static void setPrefix(DiscordCommandContext co, @CommandParam("prefix") String prefix)
    {
        CommandAssert.stringNotTooLong(prefix, 8, "The prefix is too long.");

        if (prefix.contains("@"))
        {
            throw new CommandException("The prefix can't contain @.");
        }

        co.sc.setPrefix(co.db, prefix);

        co.guild.retrieveMember(co.bot).queue(member -> member.modifyNickname("[%s] %s".formatted(prefix, Botdiril.BRANDING)).complete());
        co.respond("Prefix set to: `%s`".formatted(prefix));
    }
}
