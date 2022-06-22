package com.botdiril.permission;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.dv8tion.jda.api.entities.User;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.botdiril.Botdiril;

@JsonDeserialize(using = PowerLevel.PowerLevelDeserializer.class)
public final class PowerLevel
{
    private final String formalName;
    private final Predicate<User> predicate;
    private final Set<PowerLevel> cumulativePowers;
    private final Set<PowerLevel> managedPowers;
    private final boolean assignable;

    private final String id;
    private final String description;

    PowerLevel(String id,
               String formalName,
               String description,
               Set<PowerLevel> inheritsFrom,
               Predicate<User> implicitGrantPredicate,
               boolean assignable)
    {
        this.id = id;
        this.formalName = formalName;
        this.description = description;
        this.assignable = assignable;
        this.predicate = implicitGrantPredicate;

        this.cumulativePowers = inheritsFrom.stream()
                                            .map(PowerLevel::getImplicitCumulativePowers)
                                            .flatMap(Set::stream)
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toSet());

        this.managedPowers = Set.copyOf(this.cumulativePowers);

        this.cumulativePowers.add(this);
    }

    public boolean isAssignable()
    {
        return this.assignable;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getID()
    {
        return this.id;
    }

    public Set<PowerLevel> getManagedPowers()
    {
        return this.managedPowers;
    }

    public Set<PowerLevel> getImplicitCumulativePowers()
    {
        return Collections.unmodifiableSet(this.cumulativePowers);
    }

    public boolean isImplicitlyGranted(User user)
    {
        return this.predicate.test(user);
    }

    public boolean isParentOf(PowerLevel permLevel)
    {
        if (permLevel == this)
            return true;

        return this.getImplicitCumulativePowers()
                   .contains(permLevel);
    }

    public boolean isChildOf(PowerLevel permLevel)
    {
        if (permLevel == this)
            return true;

        return permLevel.getImplicitCumulativePowers()
                        .contains(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof PowerLevel pwr))
            return false;

        return this.id.equals(pwr.id);
    }

    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }

    @Override
    public String toString()
    {
        return this.formalName;
    }

    public static class PowerLevelDeserializer extends StdDeserializer<PowerLevel>
    {
        public PowerLevelDeserializer()
        {
            super(PowerLevel.class);
        }

        @Override
        public PowerLevel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
        {
            var botdiril = (Botdiril) ctxt.getAttribute(Botdiril.class);

            var name = ctxt.readValue(p, String.class);
            var pwrMgr = botdiril.getComponents()
                                 .getComponent(AbstractPowerLevelManager.class);

            return pwrMgr.getByName(name.toLowerCase());
        }
    }

    public static class DefaultingPowerLevelDeserializer extends PowerLevel.PowerLevelDeserializer
    {
        @Override
        public PowerLevel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
        {
            var botdiril = (Botdiril) ctxt.getAttribute(Botdiril.class);
            var powerLevelMgr = botdiril.getComponents()
                                        .getComponent(AbstractPowerLevelManager.class);

            return Objects.requireNonNullElseGet(super.deserialize(p, ctxt), powerLevelMgr::getDefault);
        }
    }
}
