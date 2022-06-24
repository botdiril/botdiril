package com.botdiril.command.behavior;

import java.util.function.Supplier;

public class CommandArgument<T>
{
    private final String name;
    private final int ordinal;
    private final boolean required;
    private Supplier<T> defaultValueSupplier;

    public CommandArgument(String name, boolean required, int ordinal, CommandArgument.ConfigurationClosure<T> config)
    {
        this.name = name;
        this.required = required;
        this.ordinal = ordinal;
        config.configure(this);
    }

    public String getName()
    {
        return this.name;
    }

    public int getOrdinal()
    {
        return this.ordinal;
    }

    public boolean isRequired()
    {
        return this.required;
    }

    public T getDefaultValue()
    {
        return this.defaultValueSupplier.get();
    }

    public void setDefaultValue(Supplier<T> defaultValue)
    {
        this.defaultValueSupplier = defaultValue;
    }

    @FunctionalInterface
    public interface ConfigurationClosure<T>
    {
        void configure(CommandArgument<T> arg);
    }
}
