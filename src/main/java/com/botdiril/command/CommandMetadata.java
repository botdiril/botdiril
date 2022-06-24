package com.botdiril.command;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Supplier;

import com.botdiril.command.behavior.CommandBase;

public class CommandMetadata implements Comparable<CommandMetadata>
{
    private final Command command;

    private final CommandInfo commandInfo;

    private final Class<? extends CommandBase<?>> definingClass;

    private final Supplier<? extends CommandBase<?>> generator;

    private final CommandCategory commandCategory;

    public CommandMetadata(CommandCategory commandCategory,
                           Command command,
                           CommandInfo commandInfo,
                           Class<? extends CommandBase<?>> definingClass,
                           Supplier<? extends CommandBase<?>> generator)
    {
        this.commandCategory = commandCategory;
        this.command = command;
        this.commandInfo = commandInfo;
        this.definingClass = definingClass;
        this.generator = generator;
    }

    public String getName()
    {
        return this.command.value();
    }

    public Command getCommand()
    {
        return this.command;
    }

    public Class<? extends CommandBase<?>> getDefiningClass()
    {
        return this.definingClass;
    }

    public CommandBase<?> createInstance()
    {
        return this.generator.get();
    }

    public CommandCategory getCommandCategory()
    {
        return this.commandCategory;
    }

    public CommandInfo getInfo()
    {
        return this.commandInfo;
    }

    @Override
    public int compareTo(@NotNull CommandMetadata o)
    {
        return Comparator.comparing(CommandMetadata::getName).compare(this, o);
    }
}
