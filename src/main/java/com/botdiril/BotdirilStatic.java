package com.botdiril;

import java.util.Locale;

import com.botdiril.util.BotdirilLog;

public class BotdirilStatic
{
    private static boolean initialized = false;

    private static Botdiril botdiril;

    static void initialize()
    {
        if (initialized)
            return;

        initialized = true;

        BotdirilLog.init();

        BotdirilLog.logger.info("=====================================");
        BotdirilLog.logger.info("####        BOTDIRIL 350         ####");
        BotdirilLog.logger.info("=====================================");

        Locale.setDefault(Locale.US);
    }

    public static void run(Botdiril botdirilInstance)
    {
        initialize();
        botdiril = botdirilInstance;
        botdiril.start();
    }

    public static Botdiril getBotdiril()
    {
        return botdiril;
    }
}
