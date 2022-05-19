package com.botdiril.command;


import com.botdiril.BotdirilStatic;
import com.botdiril.command.context.GuildMessageCommandContext;
import com.botdiril.command.invoke.CommandException;
import com.botdiril.command.invoke.CommandParam;
import com.botdiril.util.CommandAssert;

@Command("prefix")
public class CommandPrefix extends CommandBase
{
    public static void setPrefix(GuildMessageCommandContext co, @CommandParam(value = "prefix", ordinal = 0) String prefix)
    {
        CommandAssert.stringNotTooLong(prefix, 8, "The prefix is too long.");

        if (prefix.contains("@"))
        {
            throw new CommandException("The prefix can't contain @.");
        }

        var botdiril = BotdirilStatic.getBotdiril();

        var db = co.getDatabase();

        var guild = co.getGuild();

        db.simpleUpdate("""
            INSERT INTO `b50_discord`.`server_config` (`sc_id`, `sc_prefix`)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE `sc_prefix` = ?
            """, guild.getIdLong(), prefix, prefix);

        guild.retrieveMember(co.getBot())
              .queue(member -> member.modifyNickname("[%s] %s".formatted(prefix, botdiril.getBranding())).queue(a -> {
                  co.respond("Prefix set to: `%s`".formatted(prefix));
              }));

    }
}
