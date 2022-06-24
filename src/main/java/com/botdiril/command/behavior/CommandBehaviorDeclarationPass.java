package com.botdiril.command.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public non-sealed class CommandBehaviorDeclarationPass extends CommandBehavior
{
    private int argCounter;
    private final Map<String, CommandArgument<?>> arguments;

    public CommandBehaviorDeclarationPass()
    {
        this.argCounter = 0;
        this.arguments = new HashMap<>();
    }

    private <T> void addArgument(String name, boolean required, CommandArgument.ConfigurationClosure<T> configuration)
    {
        var arg = new CommandArgument<>(name, required, argCounter++, configuration);
        arguments.put(name, arg);
        argCounter++;
    }

    @Override
    public <T> T defineArgument(String name, Class<T> klazz, CommandArgument.ConfigurationClosure<T> configuration)
    {
        this.addArgument(name, true, configuration);
        return null;
    }

    @Override
    public <T> Optional<T> defineArgumentOptional(String name, Class<T> klazz, CommandArgument.ConfigurationClosure<T> configuration)
    {
        this.addArgument(name, false, configuration);
        return Optional.empty();
    }

    public Map<String, CommandArgument<?>> getArguments()
    {
        return this.arguments;
    }
}
