package com.botdiril.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import com.botdiril.framework.util.BotdirilInitializationException;
import com.botdiril.util.BotdirilLog;

public class CommandCategory
{
    private final String name;

    private CategoryInfo info;

    private final SortedSet<CommandMetadata> commands;

    private CommandCategory(String name)
    {
        this.name = name;
        this.commands = new TreeSet<>();
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
    }

    void addCommand(CommandMetadata command)
    {
        this.commands.add(command);
    }

    public String getName()
    {
        return this.name;
    }

    public CategoryInfo getInfo()
    {
        return this.info;
    }

    public Set<CommandMetadata> getCommands()
    {
        return Collections.unmodifiableSet(this.commands);
    }

    public Stream<CommandMetadata> commands()
    {
        return this.commands.stream();
    }
}
