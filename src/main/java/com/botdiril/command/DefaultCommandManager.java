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
import com.botdiril.command.loader.CommandCompiler;
import com.botdiril.util.BotdirilLog;
import com.botdiril.util.BotdirilSetupException;

public class DefaultCommandManager extends AbstractCommandManager
{
    private static final Path COMMAND_CONFIG_DIR = Path.of("assets", "commands");

    private final Logger logger = LogManager.getLogger(DefaultCommandManager.class);

    private final Map<String, CommandCategory> categoryMap;

    private final Map<String, CommandMetadata> aliasMap;

    private SortedSet<CommandMetadata> commands;


    public DefaultCommandManager()
    {
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

    private CommandCategory loadCategory(String name)
    {
        BotdirilLog.logger.info("Loading category info '{}'", name);

        var lowerName = name.toLowerCase(Locale.ROOT);

        try (var reader = Files.newBufferedReader(COMMAND_CONFIG_DIR.resolve(Path.of(lowerName, "group-info.yaml"))))
        {
            var mapper = new ObjectMapper(new YAMLFactory())
                .reader()
                .withAttribute(Botdiril.class, this.getBotdiril());
            return new CommandCategory(name, mapper.readValue(reader, CategoryInfo.class));
        }
        catch (Exception e)
        {
            throw new BotdirilSetupException("Failed to initialize command groups:", e);
        }
    }

    @Override
    protected void onMount(ComponentDependencyManager manager)
    {
        var botdiril = this.getBotdiril();
        var eventBus = botdiril.getEventBus();
        var writeLock = eventBus.ACCEPTING_COMMANDS.writeLock();

        try (var reader = Files.newBufferedReader(COMMAND_CONFIG_DIR.resolve(Path.of("groups.yaml"))))
        {
            writeLock.lock();

            var mapper = new ObjectMapper(new YAMLFactory())
                .reader()
                .withAttribute(Botdiril.class, this.getBotdiril());
            var categoryNames = mapper.readValue(reader, String[].class);
            var categories = Arrays.stream(categoryNames)
                                   .map(this::loadCategory)
                                   .collect(Collectors.toSet());

            categories.forEach(cat -> this.categoryMap.put(cat.getName(), cat));

            var compiler = manager.declareDependency(ComponentToken.create(() -> new CommandCompiler(categories)));

            var commands = compiler.getFoundCommands();

            commands.forEach((category, commandMap) -> {
                var categoryInfo = category.getInfo();
                var categoryCommandsMeta = categoryInfo.commands();

                commandMap.forEach((commandAnnotation, commandClazz) -> {
                    var commandName = commandAnnotation.value()
                                                       .toLowerCase();
                    this.logger.info("[+CMD] %s:%s of '%s'".formatted(category.getName(), commandName, commandClazz));

                    var handle = this.createCommandHandle(commandClazz);

                    if (handle == null)
                        return;

                    var info = categoryCommandsMeta.get(commandName);

                    var cmdMeta = new CommandMetadata(
                        category,
                        commandAnnotation,
                        info,
                        commandClazz,
                        handle
                    );

                    info.getAliases()
                        .forEach(alias -> this.aliasMap.put(alias.toLowerCase(), cmdMeta));

                    this.aliasMap.putIfAbsent(commandName, cmdMeta);

                    category.addCommand(cmdMeta);
                });

                this.categoryMap.put(categoryInfo.name().toLowerCase(), category);

            });

            this.commands = this.categoryMap.values()
                                            .stream()
                                            .flatMap(CommandCategory::commands)
                                            .collect(Collectors.toCollection(TreeSet::new));
        }
        catch (Exception e)
        {
            throw new BotdirilSetupException("Failed to initialize command groups:", e);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    private <T extends CommandBase<?>> Supplier<T> createCommandHandle(Class<T> clazz)
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
        var botdiril = this.getBotdiril();
        var eventBus = botdiril.getEventBus();
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
