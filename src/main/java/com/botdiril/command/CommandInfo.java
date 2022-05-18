package com.botdiril.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

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
            return new CommandInfo(
                Objects.requireNonNullElseGet(info.aliases(), Set::of),
                Objects.requireNonNullElse(info.powerLevel(), PowerLevel.EVERYONE),
                info.levelLock(),
                Objects.requireNonNull(info.description(), "<description missing>")
            );
        }
    }

    static CommandInfo defaultValue()
    {
        return new CommandInfo(
            Set.of(),
            PowerLevel.SUPERUSER_OVERRIDE,
            0,
            "<description missing>",
            EnumSet.noneOf(EnumSpecialCommandProperty.class)
        );
    }
}
