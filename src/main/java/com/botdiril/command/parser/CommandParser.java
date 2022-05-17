package com.botdiril.command.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.botdiril.command.CommandInfo;
import com.botdiril.command.CommandIntrospector;
import com.botdiril.command.CommandManager;
import com.botdiril.command.EnumSpecialCommandProperty;
import com.botdiril.command.context.DiscordCommandContext;
import com.botdiril.command.invoke.CommandException;
import com.botdiril.command.invoke.CommandParam;
import com.botdiril.command.usage.DefaultUsageGenerator;
import com.botdiril.framework.command.*;
import com.botdiril.permission.PowerLevel;
import com.botdiril.serverdata.ChannelPreferences;
import com.botdiril.userdata.stat.EnumStat;
import com.botdiril.util.BotdirilLog;

public class CommandParser
{
    private CommandManager commandManager;

    public boolean parse(DiscordCommandContext co)
    {
        var cmdParts =  co.contents.split("\\s+", 2);
        var cmdStr = cmdParts[0];
        var cmdParams =  cmdParts.length == 2 ? cmdParts[1] : "";

        var command = this.commandManager.findCommand(cmdStr);

        if (command == null)
        {
            return true;
        }

        var info = command.getInfo();

        if (info == null)
        {
            // Default config
            // Power level is set to executive superuser just in case; to avoid deadly mistakes
            info = new CommandInfo(
                Set.of(),
                PowerLevel.SUPERUSER_OVERRIDE,
                0,
                "<description missing>",
                EnumSet.noneOf(EnumSpecialCommandProperty.class)
            );
        }

        co.usedAlias = cmdStr;


        var powerLevel = info.powerLevel();

        if (!powerLevel.check(co.db, co.callerMember, co.textChannel))
        {
            co.respond(String.format("You need to have the **%s** power level to use this command!", powerLevel));

            return true;
        }

        var commandFunc = CommandIntrospector.listMethods(command);

        for (var meth : commandFunc)
        {
            var parameters = Arrays.stream(meth.getParameters())
                .filter(param -> param.getDeclaredAnnotation(CommandParam.class) != null)
                .collect(Collectors.toList());

            var args = ArgParser.splitArgs(parameters, cmdParams);

            if (args == null)
                continue;

            try
            {
                var argArr = new Object[parameters.size() + 1];
                argArr[0] = co;

                for (int i = 1; i < argArr.length; i++)
                {
                    var paramIdx = i - 1;

                    var parameter = parameters.get(paramIdx);
                    var clazz = parameter.getType();
                    var arg = args.get(paramIdx);
                    var ant = parameter.getAnnotation(CommandParam.class);
                    var type = ant.type();

                    argArr[i] = CommandParserTypeHandler.handleType(co, clazz, argArr, type, i, arg);
                }

                try
                {
                    co.userProperties.incrementStat(EnumStat.COMMANDS_USED);
                    meth.invoke(null, argArr);
                    return true;
                }
                catch (IllegalAccessException | IllegalArgumentException e)
                {
                    if (e instanceof IllegalArgumentException)
                    {
                        BotdirilLog.logger.fatal("Argument type mismatch: " + Arrays.stream(argArr)
                                                                                    .map(Object::getClass)
                                                                                    .map(Class::toString)
                                                                                    .toList());

                        BotdirilLog.logger.fatal("Expected: " + parameters.stream()
                                                                          .map(Parameter::getType)
                                                                          .map(Class::toString)
                                                                          .toList());
                    }

                    co.db.rollback();
                    co.clearResponse();
                    co.respond("**An error has occured while processing the command.**\nPlease report this to the bot owner.");
                    BotdirilLog.logger.fatal("An exception has occured while invoking a command.", e);
                    return false;
                }
                catch (InvocationTargetException e)
                {
                    co.db.rollback();
                    co.clearResponse();

                    var cause = e.getCause();
                    if (cause instanceof CommandException)
                    {
                        co.respond(cause.getMessage());
                    }
                    else
                    {
                        co.respond("**An error has occured while processing the command.**\nPlease report this to the bot owner.");
                        BotdirilLog.logger.fatal("An exception has occured while invoking a command.", cause);
                    }

                    return false;
                }
            }
            catch (CommandException e)
            {
                co.db.rollback();
                co.clearResponse();

                if (e.isEmbedded())
                    co.respond(e.getEmbed());
                else
                    co.respond(e.getMessage());

                return false;
            }
            catch (Exception e)
            {
                co.db.rollback();
                co.clearResponse();
                co.respond("**An error has occured while processing the command.**\nPlease report this to the bot owner.");
                BotdirilLog.logger.fatal("An exception has occured while invoking a command.", e);
                return false;
            }
        }

        String error = "Error! Wrong arguments.\n**Usage:**\n" + DefaultUsageGenerator.usage(co.usedPrefix, co.usedAlias, command);
        co.clearResponse();
        co.respond(error);
        return true;
    }

}
