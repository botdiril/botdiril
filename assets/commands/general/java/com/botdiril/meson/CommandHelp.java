package com.botdiril.meson;

import com.botdiril.command.Command;
import com.botdiril.command.behavior.CommandBase;
import com.botdiril.command.behavior.CommandBehavior;
import com.botdiril.command.behavior.EnumVolatility;
import com.botdiril.command.context.MessageCommandContext;

@Command("help")
public class CommandHelp extends CommandBase<MessageCommandContext>
{
    @Override
    public Runnable declareBehavior(CommandBehavior behavior)
    {
        behavior.setVolatility(EnumVolatility.STATELESS);

        return () -> {

        };
    }
}
