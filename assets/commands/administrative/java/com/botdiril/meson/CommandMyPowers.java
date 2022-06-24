package com.botdiril.meson;

import net.dv8tion.jda.api.entities.User;

import java.util.Comparator;

import com.botdiril.command.Command;
import com.botdiril.command.behavior.CommandBase;
import com.botdiril.command.behavior.CommandBehavior;
import com.botdiril.command.behavior.EnumVolatility;
import com.botdiril.command.context.CommandContext;
import com.botdiril.permission.AbstractPowerLevelManager;
import com.botdiril.permission.IPermissionDataSource;
import com.botdiril.permission.PowerLevel;
import com.botdiril.response.ResponseEmbed;

@Command("powers")
public class CommandMyPowers extends CommandBase<CommandContext>
{
    @Override
    public Runnable declareBehavior(CommandBehavior behavior)
    {
        behavior.setVolatility(EnumVolatility.READ_ONLY);

        var user = behavior.defineArgument("user", User.class, arg -> {
            arg.setDefaultValue(this.co::getCaller);
        });

        return () -> {
            this.assertion.assertTrue(!user.isBot(), "This command can't be used on bots.");

            var eb = new ResponseEmbed();
            eb.setColor(0x008080);
            eb.setTitle("Power Listing");
            eb.setDescription(user.getAsMention() + "'s powers:");
            eb.setThumbnail(user.getEffectiveAvatarUrl());
            eb.setFooter("User ID: " + user.getIdLong(), null);

            var pwr = this.botdiril.getComponents()
                                   .getComponent(AbstractPowerLevelManager.class);

            pwr.getCumulativePowers(this.ds.get(IPermissionDataSource.class), user)
               .stream()
               .sorted(Comparator.comparing(PowerLevel::toString))
               .forEach(c -> eb.addField(c.toString(), c.getDescription(), false));

            this.co.respond(eb);
        };
    }
}
