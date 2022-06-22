package com.botdiril;

import java.util.Locale;

import com.botdiril.util.BotdirilLog;

class BotdirilStatic
{
    private static boolean initialized = false;

    static void initialize()
    {
        if (initialized)
            return;

        initialized = true;

        BotdirilLog.init();

        BotdirilLog.logger.info("=====================================");
        BotdirilLog.logger.info("####        BOTDIRIL 500         ####");
        BotdirilLog.logger.info("=====================================");

        Locale.setDefault(Locale.US);
    }
}
