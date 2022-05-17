package com.botdiril.command;

import com.botdiril.command.context.DiscordCommandContext;
import com.botdiril.command.invoke.CommandParam;
import com.botdiril.command.invoke.CommandException;
import com.botdiril.permission.PowerLevel;
import com.botdiril.util.CommandAssert;
import com.botdiril.serverdata.RolePreferences;
import net.dv8tion.jda.api.entities.Role;

import java.text.MessageFormat;

@Command("unbind")
public class CommandUnbindPower
{
    public static void unbind(DiscordCommandContext co, @CommandParam("role") Role role, @CommandParam("power") PowerLevel powerLevel)
    {
        var mp = PowerLevel.getManageablePowers(co.db, co.callerMember, co.textChannel);

        CommandAssert.assertTrue(mp.contains(powerLevel), "You can't manage that power.");
        CommandAssert.assertTrue(co.callerMember.canInteract(role), "You can't manage that role!");

        var res = RolePreferences.add(co.db, role, powerLevel);

        var response = switch (res) {
            case RolePreferences.REMOVED -> MessageFormat.format("Removed **{0}** from **{1}**.", powerLevel.toString(), role.getName());
            case RolePreferences.NOT_PRESENT -> MessageFormat.format("**{0}** is not bound to **{1}**...", powerLevel.toString(), role.getName());
            default -> throw new CommandException(MessageFormat.format("Unexpected behaviour detected in the command, please report this to a developer.. Response code: {0}", res));
        };

        co.respond(response);
    }
}
