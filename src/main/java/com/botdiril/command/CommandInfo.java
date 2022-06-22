package com.botdiril.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.Set;

import com.botdiril.permission.PowerLevel;

public class CommandInfo
{
    private final Set<String> aliases;

    @JsonDeserialize(using = PowerLevel.DefaultingPowerLevelDeserializer.class)
    private final PowerLevel powerLevel;

    private final String description;

    @JsonCreator
    public CommandInfo(
        @JsonProperty("aliases") Set<String> aliases,
        @JsonProperty("powerLevel") PowerLevel powerLevel,
        @JsonProperty("description") String description)
    {
        this.aliases = Objects.requireNonNullElseGet(aliases, Set::of);
        this.powerLevel = powerLevel;
        this.description = Objects.requireNonNullElse(description, "<description missing>");
    }

    /**
     * @return The power level required to execute this command.
     */
    public PowerLevel getPowerLevel()
    {
        return this.powerLevel;
    }

    /**
     * @return The aliases for this command.
     */
    public Set<String> getAliases()
    {
        return this.aliases;
    }

    /**
     * @return The description of this command.
     */
    public String getDescription()
    {
        return this.description;
    }
}
