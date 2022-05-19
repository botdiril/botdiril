package com.botdiril.command;

import net.dv8tion.jda.api.entities.User;

import java.util.Comparator;

import com.botdiril.BotdirilStatic;
import com.botdiril.command.context.CommandContext;
import com.botdiril.command.invoke.CommandParam;
import com.botdiril.permission.AbstractPowerLevelManager;
import com.botdiril.permission.PowerLevel;
import com.botdiril.response.ResponseEmbed;

@Command("powers")
public class CommandMyPowers extends CommandBase
{
    public static void print(CommandContext co)
    {
        print(co, co.getCaller());
    }

    public static void print(CommandContext co, @CommandParam(value = "user", ordinal = 0) User user)
    {
        if (user.isBot())
        {
            co.respond("This command can't be used on bots.");
            return;
        }

        var eb = new ResponseEmbed();
        eb.setColor(0x008080);
        eb.setTitle("Power Listing");
        eb.setDescription(user.getAsMention() + "'s powers:");
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setFooter("User ID: " + user.getIdLong(), null);

        var pwr = BotdirilStatic.getBotdiril()
                                .getComponents()
                                .getComponent(AbstractPowerLevelManager.class);

        pwr.getCumulativePowers(co.getDatabase(), user)
           .stream()
           .sorted(Comparator.comparing(PowerLevel::toString))
           .forEach(c -> eb.addField(c.toString(), c.getDescription(), false));

        co.respond(eb);
    }
}
