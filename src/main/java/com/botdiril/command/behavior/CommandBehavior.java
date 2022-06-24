package com.botdiril.command.behavior;

import java.util.Optional;

public abstract sealed class CommandBehavior permits CommandBehaviorDeclarationPass, CommandBehaviorBindingPass
{
    public abstract <T> T defineArgument(String name, Class<T> klazz, CommandArgument.ConfigurationClosure<T> configuration);

    public abstract <T> Optional<T> defineArgumentOptional(String name, Class<T> klazz, CommandArgument.ConfigurationClosure<T> configuration);

    public abstract defineData();
}
