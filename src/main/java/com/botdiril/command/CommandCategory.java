package com.botdiril.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import com.botdiril.framework.util.BotdirilInitializationException;
import com.botdiril.util.BotdirilLog;

public class CommandCategory
{
    private final String name;

    private CategoryInfo info;

    private Set<CommandBase> commands;

    private CommandCategory(String name)
    {
        this.name = name;
    }

    public static CommandCategory load(String name)
    {
        BotdirilLog.logger.info("Loading category info '{}'", name);

        var lowerName = name.toLowerCase(Locale.ROOT);

        try (var reader = Files.newBufferedReader(Path.of("assets", "commands", lowerName, "group-info.yaml")))
        {
            var mapper = new ObjectMapper(new YAMLFactory());
            var category = new CommandCategory(name);
            category.info = mapper.readValue(reader, CategoryInfo.class);
            return category;
        }
        catch (Exception e)
        {
            throw new BotdirilInitializationException("Failed to initialize command groups:", e);
        }

        var commands = CommandCompiler.load();

        commands.forEach((command, clazz) -> {
            var commandName = command.value();

            this.logger.info("%s of '%s'".formatted(commandName, clazz));

            var info = commandNameToInfoMap.get(commandName);

            if (info == null)
            {
                this.logger.error("Command '%s' info not found!".formatted(commandName));
                return;
            }

            var category = commandNameToCategoryMap.get(commandName);

            this.commandInfoMap.put(command, info);
            this.categoryMap.get(category).add(command);
            this.classMap.put(command, clazz);

            this.aliasMap.put(command.value(), command);
            for (var alias : info.aliases())
                this.aliasMap.put(alias, command);

        });
    }

    public String getName()
    {
        return this.name;
    }

    public CategoryInfo getInfo()
    {
        return this.info;
    }

    public Set<CommandBase> getCommands()
    {
        return Collections.unmodifiableSet(this.commands);
    }
}
