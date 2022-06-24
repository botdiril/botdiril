package com.botdiril.meson;


import com.botdiril.command.Command;
import com.botdiril.command.behavior.CommandBase;
import com.botdiril.command.behavior.CommandBehavior;
import com.botdiril.command.behavior.EnumVolatility;
import com.botdiril.command.context.GuildMessageCommandContext;
import com.botdiril.data.IGuildConfiguration;

@Command("prefix")
public class CommandPrefix extends CommandBase<GuildMessageCommandContext>
{
    @Override
    public Runnable declareBehavior(CommandBehavior behavior)
    {
        behavior.setVolatility(EnumVolatility.WRITE);

        var prefix = behavior.defineArgument("prefix", String.class, config -> {});

        return () -> {
            this.assertion.stringNotTooLong(prefix, 8, "The prefix is too long.");
            this.assertion.assertTrue(!prefix.contains("@"), "The prefix cannot contain the @ symbol.");

            var cfg = this.ds.get(IGuildConfiguration.class);

            var guild = this.co.getGuild();

            cfg.setPrefix(guild, prefix);

            guild.retrieveMember(this.co.getBot())
                 .queue(member -> member.modifyNickname("[%s] %s".formatted(prefix, this.botdiril.getBranding())).queue(a -> {
                     this.co.respond("Prefix set to: `%s`".formatted(prefix));
                 }));
        };
    }
}
