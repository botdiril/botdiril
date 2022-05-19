package com.botdiril.util;

import com.botdiril.BotdirilStatic;
import com.botdiril.command.CommandCategory;
import com.botdiril.command.CommandManager;
import com.botdiril.command.CommandMetadata;
import com.botdiril.command.invoke.CommandException;

public class CommandAssert
{
    public static void assertNotNull(Object o1, String errorMessage) throws CommandException
    {
        if (o1 == null)
            throw new CommandException(errorMessage);
    }

    public static void assertEquals(Object o1, Object o2, String errorMessage) throws CommandException
    {
        if (!o1.equals(o2))
            throw new CommandException(errorMessage);
    }

    public static void assertIdentity(Object o1, Object o2, String errorMessage) throws CommandException
    {
        if (o1 != o2)
            throw new CommandException(errorMessage);
    }

    public static void assertNotEquals(Object o1, Object o2, String errorMessage) throws CommandException
    {
        if (o1.equals(o2))
            throw new CommandException(errorMessage);
    }

    public static void assertNotIdentity(Object o1, Object o2, String errorMessage) throws CommandException
    {
        if (o1 == o2)
            throw new CommandException(errorMessage);
    }

    public static void assertTrue(boolean b, String errorMessage) throws CommandException
    {
        if (!b)
            throw new CommandException(errorMessage);
    }

    // LONGS

    public static void matchesRegex(String s, String regex, String errorMessage) throws CommandException
    {
        if (s.matches(regex))
            throw new CommandException(errorMessage);
    }

    public static void numberInBoundsExclusiveD(double number, double levelMin, double levelMax, String errorMessage) throws CommandException
    {
        if (number <= levelMin || number >= levelMax)
            throw new CommandException(errorMessage);
    }

    public static void numberInBoundsExclusiveL(long number, long levelMin, long levelMax, String errorMessage) throws CommandException
    {
        if (number <= levelMin || number >= levelMax)
            throw new CommandException(errorMessage);
    }

    public static void numberInBoundsInclusiveD(double number, double levelMin, double levelMax, String errorMessage) throws CommandException
    {
        if (number < levelMin || number > levelMax)
            throw new CommandException(errorMessage);
    }

    public static void numberInBoundsInclusiveL(long number, long levelMin, long levelMax, String errorMessage) throws CommandException
    {
        if (number < levelMin || number > levelMax)
            throw new CommandException(errorMessage);
    }

    // DOUBLES

    public static void numberMoreThanZeroD(double number, String errorMessage) throws CommandException
    {
        if (!(number > 0))
            throw new CommandException(errorMessage);
    }

    public static void numberMoreThanZeroL(long number, String errorMessage) throws CommandException
    {
        if (!(number > 0))
            throw new CommandException(errorMessage);
    }

    public static void numberNotAboveD(double number, double level, String errorMessage) throws CommandException
    {
        if (number > level)
            throw new CommandException(errorMessage);
    }

    public static void numberNotAboveL(long number, long level, String errorMessage) throws CommandException
    {
        if (number > level)
            throw new CommandException(errorMessage);
    }

    public static void numberNotBelowD(double number, double level, String errorMessage) throws CommandException
    {
        if (number < level)
            throw new CommandException(errorMessage);
    }

    public static void numberNotBelowL(long number, long level, String errorMessage) throws CommandException
    {
        if (number < level)
            throw new CommandException(errorMessage);
    }

    // PARSERS

    public static CommandMetadata parseCommand(String arg)
    {
        var cmdMgr = BotdirilStatic.getBotdiril()
                                   .getComponents()
                                   .getComponent(CommandManager.class);

        var cmd = cmdMgr.findCommand(arg.trim().toLowerCase());

        if (cmd == null)
            throw new CommandException("No such command.");

        return cmd;
    }

    public static CommandCategory parseCommandGroup(String name)
    {
        var cmdMgr = BotdirilStatic.getBotdiril()
                                   .getComponents()
                                   .getComponent(CommandManager.class);

        var cg = cmdMgr.findCategory(name.trim().toLowerCase());

        if (cg == null)
        {
            throw new CommandException("No such command group.");
        }

        return cg;
    }

    /**
     * Hard failing
     */
    public static double parseDouble(String number, String errorMessage) throws CommandException
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
    public static int parseInt(String number, String errorMessage) throws CommandException
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

    public static boolean parseBoolean(String bool, String errorMessage)
    {
        if ("true".equalsIgnoreCase(bool) || "yes".equalsIgnoreCase(bool) || "1".equalsIgnoreCase(bool) || "on".equalsIgnoreCase(bool) || "enable".equalsIgnoreCase(bool))
        {
            return true;
        }
        else if ("false".equalsIgnoreCase(bool) || "no".equalsIgnoreCase(bool) || "0".equalsIgnoreCase(bool) || "off".equalsIgnoreCase(bool) || "disable".equalsIgnoreCase(bool))
        {
            return false;
        }
        else
        {
            throw new CommandException(errorMessage);
        }
    }

    /**
     * Hard failing
     */
    public static long parseLong(String number, String errorMessage) throws CommandException
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

    public static void stringNotEmptyOrNull(String s, String errorMessage) throws CommandException
    {
        if (s == null || s.isEmpty())
            throw new CommandException(errorMessage);
    }

    public static void stringNotTooLong(String s, int length, String errorMessage) throws CommandException
    {
        if (s.length() > length)
            throw new CommandException(errorMessage);
    }
}
