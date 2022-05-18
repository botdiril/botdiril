package com.botdiril.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.function.Failable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.plutoengine.component.ComponentToken;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.botdiril.Botdiril;
import com.botdiril.BotdirilComponent;
import com.botdiril.command.loader.CommandCompiler;
import com.botdiril.framework.util.BotdirilInitializationException;

public class CommandManager extends BotdirilComponent
{
    private final Logger logger = LogManager.getLogger(CommandManager.class);

    private final Botdiril botdiril;

    private final Map<String, CommandCategory> categoryMap;

    private final Map<String, CommandMetadata> aliasMap;

    private SortedSet<CommandMetadata> commands;


    public CommandManager(Botdiril botdiril)
    {
        this.botdiril = botdiril;

        this.categoryMap = new HashMap<>();
        this.aliasMap = new HashMap<>();
    }

    public Stream<CommandMetadata> commands()
    {
        return this.commands.stream()
                            .sequential();
    }

    public int commandCount()
    {
        return this.commands.size();
    }

    public CommandCategory findCategory(String name)
    {
        return this.categoryMap.get(name.toLowerCase());
    }

    public CommandMetadata findCommand(String alias)
    {
        return this.aliasMap.get(alias.toLowerCase());
    }

    @Override
    protected void onMount(ComponentDependencyManager manager)
    {
        var eventBus = this.botdiril.getEventBus();
        var writeLock = eventBus.ACCEPTING_COMMANDS.writeLock();

        try (var reader = Files.newBufferedReader(Path.of("assets", "commands", "groups.yaml")))
        {
            writeLock.lock();

            var mapper = new ObjectMapper(new YAMLFactory());
            var categoryNames = mapper.readValue(reader, String[].class);
            var categories = Arrays.stream(categoryNames)
                                   .map(CommandCategory::load)
                                   .collect(Collectors.toSet());

            categories.forEach(cat -> this.categoryMap.put(cat.getName(), cat));

            var compiler = manager.declareDependency(ComponentToken.create(() -> new CommandCompiler(categories)));

            var commands = compiler.getFoundCommands();

            commands.forEach((category, commandMap) -> {
                var categoryInfo = category.getInfo();
                var categoryCommandsMeta = categoryInfo.commands();

                commandMap.forEach((commandAnnotation, commandClazz) -> {
                    var commandName = commandAnnotation.value();
                    this.logger.info("[+CMD] %s:%s of '%s'".formatted(category.getName(), commandName, commandClazz));

                    var handle = this.createCommandHandle(commandClazz);

                    if (handle == null)
                        return;

                    var info = categoryCommandsMeta.getOrDefault(commandName, CommandInfo.defaultValue());

                    var cmdMeta = new CommandMetadata(
                        category,
                        commandAnnotation,
                        info,
                        commandClazz,
                        handle
                    );

                    category.addCommand(cmdMeta);
                });

            });

            this.commands = this.categoryMap.values()
                                            .stream()
                                            .flatMap(CommandCategory::commands)
                                            .collect(Collectors.toCollection(TreeSet::new));
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

    private <T extends CommandBase> Supplier<T> createCommandHandle(Class<T> clazz)
    {
        var lookup = MethodHandles.publicLookup();

        try
        {
            var constructorHandle = lookup.findConstructor(clazz, MethodType.methodType(void.class));

            return Failable.asSupplier(() -> clazz.cast(constructorHandle.invokeExact()));
        }
        catch (NoSuchMethodException e)
        {
            this.logger.warn("Command '%s' does not have a public no-arg constructor! It will not be registered as a command.".formatted(clazz));
            return null;
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onUnmount()
    {
        var eventBus = this.botdiril.getEventBus();
        var writeLock = eventBus.ACCEPTING_COMMANDS.writeLock();

        try
        {
            writeLock.lock();

            this.categoryMap.clear();
            this.aliasMap.clear();
            this.commands.clear();
        }
        finally
        {
            writeLock.unlock();
        }
    }
}
