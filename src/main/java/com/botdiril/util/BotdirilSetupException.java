package com.botdiril.util;

public class BotdirilSetupException extends RuntimeException
{
    public BotdirilSetupException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public BotdirilSetupException(Throwable cause)
    {
        super(cause);
    }

    public BotdirilSetupException(String message)
    {
        super(message);
    }
}
