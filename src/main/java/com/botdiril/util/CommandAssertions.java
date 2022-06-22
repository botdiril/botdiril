package com.botdiril.util;

import com.botdiril.Botdiril;
import com.botdiril.command.CommandCategory;
import com.botdiril.command.CommandMetadata;
import com.botdiril.command.DefaultCommandManager;
import com.botdiril.command.invoke.CommandException;

public class CommandAssertions
{
    protected Botdiril botdiril;

    public CommandAssertions(Botdiril botdiril)
    {
        this.botdiril = botdiril;
    }

    public void assertNotNull(Object o1, String errorMessage) throws CommandException
    {
        if (o1 == null)
            throw new CommandException(errorMessage);
    }

    public void assertEquals(Object o1, Object o2, String errorMessage) throws CommandException
    {
        if (!o1.equals(o2))
            throw new CommandException(errorMessage);
    }

    public void assertIdentity(Object o1, Object o2, String errorMessage) throws CommandException
    {
        if (o1 != o2)
            throw new CommandException(errorMessage);
    }

    public void assertNotEquals(Object o1, Object o2, String errorMessage) throws CommandException
    {
        if (o1.equals(o2))
            throw new CommandException(errorMessage);
    }

    public void assertNotIdentity(Object o1, Object o2, String errorMessage) throws CommandException
    {
        if (o1 == o2)
            throw new CommandException(errorMessage);
    }

    public void assertTrue(boolean b, String errorMessage) throws CommandException
    {
        if (!b)
            throw new CommandException(errorMessage);
    }

    // LONGS

    public void matchesRegex(String s, String regex, String errorMessage) throws CommandException
    {
        if (s.matches(regex))
            throw new CommandException(errorMessage);
    }

    public void numberInBoundsExclusiveD(double number, double levelMin, double levelMax, String errorMessage) throws CommandException
    {
        if (number <= levelMin || number >= levelMax)
            throw new CommandException(errorMessage);
    }

    public void numberInBoundsExclusiveL(long number, long levelMin, long levelMax, String errorMessage) throws CommandException
    {
        if (number <= levelMin || number >= levelMax)
            throw new CommandException(errorMessage);
    }

    public void numberInBoundsInclusiveD(double number, double levelMin, double levelMax, String errorMessage) throws CommandException
    {
        if (number < levelMin || number > levelMax)
            throw new CommandException(errorMessage);
    }

    public void numberInBoundsInclusiveL(long number, long levelMin, long levelMax, String errorMessage) throws CommandException
    {
        if (number < levelMin || number > levelMax)
            throw new CommandException(errorMessage);
    }

    // DOUBLES

    public void numberMoreThanZeroD(double number, String errorMessage) throws CommandException
    {
        if (!(number > 0))
            throw new CommandException(errorMessage);
    }

    public void numberMoreThanZeroL(long number, String errorMessage) throws CommandException
    {
        if (!(number > 0))
            throw new CommandException(errorMessage);
    }

    public void numberNotAboveD(double number, double level, String errorMessage) throws CommandException
    {
        if (number > level)
            throw new CommandException(errorMessage);
    }

    public void numberNotAboveL(long number, long level, String errorMessage) throws CommandException
    {
        if (number > level)
            throw new CommandException(errorMessage);
    }

    public void numberNotBelowD(double number, double level, String errorMessage) throws CommandException
    {
        if (number < level)
            throw new CommandException(errorMessage);
    }

    public void numberNotBelowL(long number, long level, String errorMessage) throws CommandException
    {
        if (number < level)
            throw new CommandException(errorMessage);
    }

    // PARSERS

    public CommandMetadata parseCommand(String arg)
    {
        var cmdMgr = this.botdiril.getComponents()
                                  .getComponent(DefaultCommandManager.class);

        var cmd = cmdMgr.findCommand(arg.trim().toLowerCase());

        if (cmd == null)
            throw new CommandException("No such command.");

        return cmd;
    }

    public CommandCategory parseCommandGroup(String name)
    {
        var cmdMgr = this.botdiril.getComponents()
                                  .getComponent(DefaultCommandManager.class);

        var cg = cmdMgr.findCategory(name.trim().toLowerCase());

        if (cg == null)
            throw new CommandException("No such command group.");

        return cg;
    }

    /**
     * Hard failing
     */
    public double parseDouble(String number, String errorMessage) throws CommandException
    {
        try
        {
            return Double.parseDouble(number);
        }
        catch (NumberFormatException e)
        {
            throw new CommandException(errorMessage);
        }
    }

    /**
     * Hard failing
     */
    public int parseInt(String number, String errorMessage) throws CommandException
    {
        try
        {
            return Integer.parseInt(number);
        }
        catch (NumberFormatException e)
        {
            throw new CommandException(errorMessage);
        }
    }

    public boolean parseBoolean(String bool, String errorMessage)
    {
        return switch (bool.toLowerCase()) {
            case "true", "t", "yes", "1", "on", "enable", "enabled" -> true;
            case "false", "f", "no", "0", "off", "disable", "disabled" -> false;
            default -> throw new CommandException(errorMessage);
        };
    }

    /**
     * Hard failing
     */
    public long parseLong(String number, String errorMessage) throws CommandException
    {
        try
        {
            return Long.parseLong(number);
        }
        catch (NumberFormatException e)
        {
            throw new CommandException(errorMessage);
        }
    }

    public void stringNotEmptyOrNull(String s, String errorMessage) throws CommandException
    {
        if (s == null || s.isEmpty())
            throw new CommandException(errorMessage);
    }

    public void stringNotTooLong(String s, int length, String errorMessage) throws CommandException
    {
        if (s.length() > length)
            throw new CommandException(errorMessage);
    }
}
