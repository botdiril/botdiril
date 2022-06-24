package com.botdiril.command.loader;

import javax.tools.ToolProvider;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.botdiril.BotdirilComponent;
import com.botdiril.command.Command;
import com.botdiril.command.behavior.CommandBase;
import com.botdiril.command.CommandCategory;
import com.botdiril.util.BotdirilLog;
import com.botdiril.util.BotdirilSetupException;

public class CommandCompiler extends BotdirilComponent
{
    private static final Path COMMANDS_DIR = Path.of("assets", "commands");

    private CommandClassLoader classLoader;

    private final Set<CommandCategory> categories;

    private final Map<CommandCategory, Map<Command, Class<? extends CommandBase<?>>>> foundCommands = new HashMap<>();

    public CommandCompiler(Set<CommandCategory> categories)
    {
        this.categories = categories;
    }

    @Override
    protected void onMount(ComponentDependencyManager manager)
    {
        try
        {
            this.classLoader = new CommandClassLoader();

            var cats = this.categories.stream()
                                      .collect(Collectors.toMap(
                                          Function.identity(),
                                          this::tryLoadCommandGroup
                                      ));

            var classFiles = cats.values()
                                 .stream()
                                 .flatMap(Collection::stream)
                                 .toList();

            this.compileFiles(classFiles);

            cats.forEach((category, classFileList) -> this.foundCommands.put(category, this.loadCommands(classFileList)));
        }
        catch (Exception e)
        {
            throw new BotdirilSetupException("Failed to load command classes:", e);
        }
    }

    private Map<Command, Class<? extends CommandBase<?>>> loadCommands(List<StringJavaFileObject> javaFiles)
    {
        var commands = new HashMap<Command, Class<? extends CommandBase<?>>>();

        javaFiles.stream()
                 .map(StringJavaFileObject::getClassName)
                 .map(this::loadClass)
                 .forEach(clazz -> {
                     var cmd = this.getClassCommand(clazz);

                     if (cmd == null)
                         return;

                     @SuppressWarnings("unchecked")
                     var clz = (Class<? extends CommandBase<?>>) clazz;

                     commands.put(cmd, clz);
                 });

        return commands;
    }

    public Map<CommandCategory, Map<Command, Class<? extends CommandBase<?>>>> getFoundCommands()
    {
        return Collections.unmodifiableMap(this.foundCommands);
    }

    @Override
    protected void onUnmount()
    {
        BotdirilLog.logger.info("Unloading command classes and the classloader.");

        this.classLoader = null;
        this.foundCommands.clear();

        System.gc();
    }

    private Command getClassCommand(Class<?> clazz)
    {
        var annt = clazz.getDeclaredAnnotation(Command.class);

        if (annt != null && !CommandBase.class.isAssignableFrom(clazz))
        {
            BotdirilLog.logger.warn("Command '%s' is annotated as a command but does not subclass '%s'. It will not be registered!".formatted(clazz, CommandBase.class));
            return null;
        }

        return annt;
    }

    private List<StringJavaFileObject> tryLoadCommandGroup(CommandCategory group)
    {
        var path = COMMANDS_DIR.resolve(group.getName());

        try
        {
            if (!Files.isDirectory(path))
                return List.of();

            var javaCommandsDir = path.resolve("java");
            if (!Files.isDirectory(javaCommandsDir))
                return List.of();

            try (var tree = Files.walk(javaCommandsDir))
            {
                return tree.filter(Files::isRegularFile)
                           .filter(fileName -> fileName.toString().endsWith(".java"))
                           .map(file -> this.createJavaObject(javaCommandsDir, file))
                           .toList();
            }
        }
        catch (Exception e)
        {
            throw new BotdirilSetupException("Failed to load command group:", e);
        }
    }

    private StringJavaFileObject createJavaObject(Path sourceDir, Path file)
    {
        var fileNameStr = file.toString();

        var sourcePath = sourceDir.relativize(file);
        var fs = FileSystems.getDefault();

        var sourcePathStr = sourcePath.toString();

        var className = sourcePathStr
            .replaceAll("\\.java$", "")
            .replace(fs.getSeparator(), ".");

        BotdirilLog.logger.info("Compiling class '{}' from '{}'", className, fileNameStr);

        return StringJavaFileObject.loadFrom(className, file);
    }

    private Class<?> loadClass(String className)
    {
        try
        {
            return this.classLoader.loadClass(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void compileFiles(Iterable<StringJavaFileObject> files)
    {
        var compiler = ToolProvider.getSystemJavaCompiler();

        var standardFileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        var fileManager = new ClassFileManager(standardFileManager);

        var compileTask = compiler.getTask(null, fileManager, null, null, null, files);
        compileTask.call();

        var objects = fileManager.getObjects();

        if (objects.contains(null))
            throw new RuntimeException("Compile failed.");

        objects.forEach(object -> this.classLoader.createClass(object.getClassName(), object.getBytes()));
    }
}
