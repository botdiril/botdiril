package com.botdiril.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.Objects;
import java.util.Set;

import com.botdiril.BotdirilStatic;
import com.botdiril.permission.AbstractPowerLevelManager;
import com.botdiril.permission.PowerLevel;

@JsonDeserialize(converter = CommandInfo.CommandInfoConverter.class)
public record CommandInfo(
    Set<String> aliases,
    PowerLevel powerLevel,
    int levelLock,
    String description
)
{
    /**
     * This is so tragic.
     *
     * Recreate the {@link CommandInfo} object with the correct default parameters.
     * */
    static class CommandInfoConverter extends StdConverter<CommandInfo, CommandInfo>
    {
        @Override
        public CommandInfo convert(CommandInfo info)
        {
            var defaultValues = defaultValue();

            return new CommandInfo(
                Objects.requireNonNullElseGet(info.aliases(), defaultValues::aliases),
                Objects.requireNonNullElseGet(info.powerLevel(), defaultValues::powerLevel),
                info.levelLock(),
                Objects.requireNonNullElseGet(info.description(), defaultValues::description)
            );
        }
    }

    static CommandInfo defaultValue()
    {
        var powerLevelMgr = BotdirilStatic.getBotdiril()
                                          .getComponents()
                                          .getComponent(AbstractPowerLevelManager.class);

        return new CommandInfo(
            Set.of(),
            powerLevelMgr.getDefault(),
            0,
            "<description missing>"
        );
    }
}
