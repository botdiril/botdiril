package com.botdiril.command.usage;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.botdiril.command.Command;
import com.botdiril.command.invoke.CommandParam;

public class DefaultUsageGenerator implements IUsageGenerator
{
    public String generateUsage(String prefix, String alias, Command cmd)
    {
        var sb = new StringBuilder();

        var commandFunc = CommandIntrospector.listMethods(cmd);

        for (var meth : commandFunc)
        {
            var parameters = Arrays.stream(meth.getParameters())
                .map(param -> param.getDeclaredAnnotation(CommandParam.class))
                .filter(Objects::nonNull)
                .map(CommandParam::value)
                .map("<%s>"::formatted)
                .collect(Collectors.joining(" "));

            sb.append('`');
            sb.append(prefix);
            sb.append(alias);
            if (!parameters.isEmpty())
                sb.append(' ');
            sb.append(parameters);
            sb.append('`');
            sb.append("\n");
        }

        return sb.toString();
    }
}
