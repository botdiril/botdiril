package com.botdiril.command;

import java.util.stream.Stream;

public interface ICommandManager
{
    Stream<CommandMetadata> commands();

    int commandCount();

    CommandCategory findCategory(String name);

    CommandMetadata findCommand(String alias);
}
