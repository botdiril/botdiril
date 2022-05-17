package com.botdiril.permission;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class PowerLevel
{
    private final String formalName;
    private final BiPredicate<Member, TextChannel> predicate;
    private final Set<PowerLevel> cumulativePowers;
    private final Set<PowerLevel> managedPowers;
    private final boolean assignable;

    private final String id;
    private final String description;

    PowerLevel(String formalName, String description, Set<PowerLevel> inheritsFrom, BiPredicate<Member, TextChannel> preconditions, boolean assignable, String id)
    {
        this.description = description;
        this.formalName = formalName;
        this.predicate = preconditions;
        this.assignable = assignable;
        this.id = id;

        this.cumulativePowers = inheritsFrom
            .stream()
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

    public boolean isImplicitlyGranted(Member member, TextChannel tc)
    {
        return this.predicate.test(member, tc);
    }

    public boolean isParentOf(PowerLevel permLevel)
    {
        if (permLevel == this)
            return true;

        return this.getImplicitCumulativePowers().contains(permLevel);
    }

    public boolean isChildOf(PowerLevel permLevel)
    {
        if (permLevel == this)
            return true;

        return permLevel.getImplicitCumulativePowers().contains(this);
    }

    @Override
    public String toString()
    {
        return this.formalName;
    }
}
