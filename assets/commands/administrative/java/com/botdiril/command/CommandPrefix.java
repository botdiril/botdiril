package com.botdiril.command;


import com.botdiril.command.context.GuildMessageCommandContext;
import com.botdiril.command.invoke.CommandException;
import com.botdiril.command.invoke.CommandParam;
import com.botdiril.test.GuildConfiguration;
import com.botdiril.util.CommandAssertions;

@Command("prefix")
public class CommandPrefix extends CommandBase
{
    public static void setPrefix(GuildMessageCommandContext co, @CommandParam(value = "prefix", ordinal = 0) String prefix)
    {
        CommandAssertions.stringNotTooLong(prefix, 8, "The prefix is too long.");

        if (prefix.contains("@"))
        {
            throw new CommandException("The prefix can't contain @.");
        }

        var cfg = co.getDataSource(GuildConfiguration.class);

        var guild = co.getGuild();

        cfg.setPrefix(guild, prefix);

        guild.retrieveMember(co.getBot())
              .queue(member -> member.modifyNickname("[%s] %s".formatted(prefix, this.botdiril.getBranding())).queue(a -> {
                  co.respond("Prefix set to: `%s`".formatted(prefix));
              }));
    }
}
