package com.botdiril.command.usage;

import com.botdiril.command.Command;

public interface IUsageGenerator
{
    String generateUsage(String prefix, String alias, Command cmd);
}
