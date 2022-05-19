package com.botdiril.test;

import java.io.IOException;

import com.botdiril.Botdiril;
import com.botdiril.BotdirilConfig;
import com.botdiril.BotdirilStatic;

public class MiniMeson extends Botdiril
{
    public MiniMeson(BotdirilConfig config)
    {
        super(config);
    }

    public static void main(String[] args) throws IOException
    {
        var config = BotdirilConfig.load();

        var meson = new MiniMeson(config);

        BotdirilStatic.run(meson);
    }
}
