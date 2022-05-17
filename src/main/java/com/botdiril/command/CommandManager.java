package com.botdiril.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import com.botdiril.Botdiril;
import com.botdiril.BotdirilComponent;
import com.botdiril.framework.util.BotdirilInitializationException;
import com.botdiril.util.BotdirilLog;

public class CommandManager extends BotdirilComponent
{
    private final Logger logger = LogManager.getLogger(CommandManager.class);

    private final Botdiril botdiril;

    private final Map<String, CommandCategory> categoryMap;

    private final Map<String, CommandBase> aliasMap;

    private final Set<CommandBase> commands;


    public CommandManager(Botdiril botdiril)
    {
        this.botdiril = botdiril;

        this.categoryMap = new HashMap<>();
        this.aliasMap = new HashMap<>();
        this.commands = new HashSet<>();
    }

    public Stream<CommandBase> commands()
    {
        return this.commands.stream();
    }

    public int commandCount()
    {
        return this.commands.size();
    }

    public CommandBase findCommand(String alias)
    {
        return this.aliasMap.get(alias.toLowerCase());
    }

    public void load()
    {
        var eventBus = this.botdiril.getEventBus();
        var writeLock = eventBus.ACCEPTING_COMMANDS.writeLock();

        try
        {
            writeLock.lock();

            try (var reader = Files.newBufferedReader(Path.of("assets", "commands", "groups.yaml")))
            {
                var mapper = new ObjectMapper(new YAMLFactory());
                var categoryNames = mapper.readValue(reader, String[].class);
                var categories = Arrays.stream(categoryNames)
                                       .map(CommandCategory::load)
                                       .toList();

            }

        }
        catch (Exception e)
        {
            throw new BotdirilInitializationException("Failed to initialize command groups:", e);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    public void unload()
    {
        this.unload(() -> BotdirilLog.logger.info("Unloading complete..."));
    }

    public void unload(Runnable andThen)
    {
        var exec = Executors.newSingleThreadExecutor();
        exec.submit(() -> {
            var eventBus = this.botdiril.getEventBus();
            var writeLock = eventBus.ACCEPTING_COMMANDS.writeLock();

            try
            {
                writeLock.lock();

                this.categoryMap.clear();
                this.aliasMap.clear();

                CommandCompiler.unload();
            }
            finally
            {
                writeLock.unlock();
                andThen.run();
            }
        });
    }
}
