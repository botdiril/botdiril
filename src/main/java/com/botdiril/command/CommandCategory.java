package com.botdiril.command;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public class CommandCategory
{
    private final String name;

    private final CategoryInfo info;

    private final SortedSet<CommandMetadata> commands;

    CommandCategory(String name, CategoryInfo info)
    {
        this.name = name;
        this.info = info;
        this.commands = new TreeSet<>();
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
