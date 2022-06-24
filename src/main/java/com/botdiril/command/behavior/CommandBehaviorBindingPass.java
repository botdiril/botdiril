package com.botdiril.command.behavior;

import java.util.Map;
import java.util.Optional;

public non-sealed class CommandBehaviorBindingPass extends CommandBehavior
{
    private final Map<String, ?> arguments;

    public CommandBehaviorBindingPass(Map<String, ?> arguments)
    {
        this.arguments = arguments;
    }

    @Override
    public <T> T defineArgument(String name, Class<T> klazz, CommandArgument.ConfigurationClosure<T> configuration)
    {
        return klazz.cast(arguments.get(name));
    }

    @Override
    public <T> Optional<T> defineArgumentOptional(String name, Class<T> klazz, CommandArgument.ConfigurationClosure<T> configuration)
    {
        return Optional.ofNullable(this.defineArgument(name, klazz, configuration));
    }
}
