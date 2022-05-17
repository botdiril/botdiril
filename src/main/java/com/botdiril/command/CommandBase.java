package com.botdiril.command;

public class CommandBase
{
    Command command;

    CommandInfo commandInfo;

    Class<?> definingClass;

    CommandCategory commandCategory;

    protected CommandBase()
    {

    }

    public Command getCommand()
    {
        return this.command;
    }

    public Class<?> getDefiningClass()
    {
        return this.definingClass;
    }

    public CommandCategory getCommandCategory()
    {
        return this.commandCategory;
    }

    public CommandInfo getInfo()
    {
        return this.commandInfo;
    }
}
