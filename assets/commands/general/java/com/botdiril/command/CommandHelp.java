package com.botdiril.command;

import com.botdiril.command.usage.DefaultUsageGenerator;
import com.botdiril.command.context.MessageCommandContext;
import com.botdiril.command.invoke.CommandParam;
import com.botdiril.command.invoke.CommandException;
import com.botdiril.response.ResponseEmbed;
import com.botdiril.util.CommandAssert;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@Command("help")
public class CommandHelp
{
    public static void show(MessageCommandContext co)
    {
        var eb = new ResponseEmbed();
        eb.setColor(Color.CYAN.getRGB());
        eb.setTitle("Stuck? Here is your help:");

        Arrays.stream(CommandCategory.values()).forEach(cat -> {
            var info = cat.getInfo();
            eb.addField(
                "%s [%d]".formatted(info.name(), CommandManager.commandCountInCategory(cat)),
                "Type `%s%s %s`".formatted( co.usedPrefix, co.usedAlias, cat.toString().toLowerCase()),
                false);
        });

        long cmdCnt = CommandManager.commandCount();
        int catCnt = CommandCategory.values().length;

        eb.setDescription("There are %d commands in %d categories total.".formatted(cmdCnt, catCnt));

        co.respond(eb);
    }

    public static void show(MessageCommandContext co, @CommandParam("category or command") String tbp)
    {
        try
        {
            var command = CommandAssert.parseCommand(tbp);
            var sb = new StringBuilder("**Command `%s`**:".formatted(command.value()));
            var info = CommandManager.getCommandInfo(command);
            var aliases = info.aliases();

            if (!aliases.isEmpty())
            {
                sb.append("\n**Aliases:** ");
                sb.append(aliases.stream().map("`%s`"::formatted).collect(Collectors.joining(", ")));
            }

            sb.append("\n**Description:** ");
            sb.append(info.description());
            sb.append("\n**Power level required:** ");
            sb.append(info.powerLevel());
            sb.append("\n**Available from level:** ");
            sb.append(info.levelLock() == 0 ? "Always available" : info.levelLock());
            sb.append("\n**Usage:**\n");
            sb.append(DefaultUsageGenerator.usage(co.usedPrefix, tbp, command));

            co.respond(sb.toString());
        }
        catch (CommandException e)
        {
            var found = CommandAssert.parseCommandGroup(tbp);

            var eb = new ResponseEmbed();
            eb.setColor(Color.CYAN.getRGB());
            eb.setTitle("Help for the %s category".formatted(found.getInfo().name()));

            CommandManager.getCommandsByCategory(found).forEach(comm -> eb.addField(comm.value(), CommandManager.getCommandInfo(comm).description(), false));

            eb.setDescription("Type `%s%s <command>` to show more information for each command.".formatted(co.usedPrefix, co.usedAlias));

            co.respond(eb);
        }
    }
}
