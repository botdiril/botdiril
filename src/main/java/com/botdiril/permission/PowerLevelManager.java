package com.botdiril.permission;

import com.botdiril.framework.sql.connection.ReadDBConnection;
import com.botdiril.serverdata.RolePreferences;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.botdiril.framework.sql.DBConnection;

public class PowerLevelManager
{
    private final Map<String, PowerLevel> nameLookup = new HashMap<>();

    public boolean check(ReadDBConnection db, Member member, TextChannel tc)
    {
        final var roles = member.getRoles();

        var pl = RolePreferences.getAllPowerLevels(db, roles);
        pl.addAll(PowerLevelManager.getImplicitlyGrantedPowers(member, tc));

        return pl
            .stream()
            .anyMatch(this::isChildOf);
    }

    /**
     * Generates a set of all power levels the member receives automatically
     * just from being in that channel.
     *
     * <p>
     *     This function works cumulatively.
     * </p>
     *
     * @param member The target member
     * @param tc The channel the command was invoked in
     * @return A set of all power levels that match the member's state
     */
    static Set<PowerLevel> getImplicitlyGrantedPowers(Member member, TextChannel tc)
    {
        return Arrays
            .stream(PowerLevel.values())
            .filter(powerLevel -> powerLevel.isImplicitlyGranted(member, tc))
            .collect(Collectors.toSet());
    }

    /**
     * Generates a set of all power levels the member possesses in the denoted channel.
     *
     * <p>
     *     This function works cumulatively.
     * </p>
     *
     * @param db A connection to a database
     * @param member The target member
     * @param tc The channel the command was invoked in
     * @return A set of all power levels this user this user possesses
     */
    public static Set<PowerLevel> getCumulativePowers(DBConnection db, Member member, TextChannel tc)
    {
        final var roles = member.getRoles();

        var pl = getImplicitlyGrantedPowers(member, tc);

        pl.addAll(RolePreferences.getAllPowerLevels(db, roles));

        return  pl.stream()
            .map(PowerLevel::getImplicitCumulativePowers)
            .flatMap(Set::stream)
            .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Generates a set of all power levels the member can manage in the denoted channel.
     *
     * <p>
     *     This function works cumulatively.
     * </p>
     *
     * @param db A database connection
     * @param member The target member
     * @param tc The channel the command was invoked in
     * @return A set of all power levels this user can manage
     */
    public static Set<PowerLevel> getManageablePowers(DBConnection db, Member member, TextChannel tc)
    {
        var manageable = getCumulativePowers(db, member, tc);

        if (!PowerLevel.SUPERUSER.check(db, member, tc))
        {
            return Set.of();
        }

        return manageable.stream()
            .filter(PowerLevel::isAssignable)
            .map(PowerLevel::getManagedPowers)
            .flatMap(Set::stream)
            .collect(Collectors.toUnmodifiableSet());
    }
}
