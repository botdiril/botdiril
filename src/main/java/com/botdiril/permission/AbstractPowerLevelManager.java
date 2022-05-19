package com.botdiril.permission;

import net.dv8tion.jda.api.entities.User;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.botdiril.BotdirilComponent;
import com.botdiril.framework.sql.connection.ReadDBConnection;

public abstract class AbstractPowerLevelManager extends BotdirilComponent implements IPowerLevelManager
{
    protected final Map<String, PowerLevel> nameLookup;

    protected AbstractPowerLevelManager(Set<PowerLevel> powerLevels)
    {
        this.nameLookup = powerLevels.stream()
                                     .collect(Collectors.toMap(PowerLevel::getID, Function.identity()));
    }

    public PowerLevel getByName(String name)
    {
        return this.nameLookup.get(name);
    }

    /**
     * Generates a set of all power levels the user receives automatically.
     *
     * @param user The invoking user
     * @return A set of all power levels that match the user's state
     */
    protected Set<PowerLevel> getImplicitlyGrantedPowers(User user)
    {
        return this.nameLookup.values()
                              .stream()
                              .filter(powerLevel -> powerLevel.isImplicitlyGranted(user))
                              .collect(Collectors.toSet());
    }

    /**
     * Generates a set of all power levels the user possesses.
     *
     * <p>
     *     This function works cumulatively.
     * </p>
     *
     * @param db A connection to a database
     * @param user The invoking user
     * @return A set of all power levels this user possesses
     */
    public Set<PowerLevel> getCumulativePowers(ReadDBConnection db, User user)
    {
        return this.getImplicitlyGrantedPowers(user)
                   .stream()
                   .map(PowerLevel::getImplicitCumulativePowers)
                   .flatMap(Set::stream)
                   .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Generates a set of all power levels the user can manage.
     *
     * <p>
     *     This function works cumulatively.
     * </p>
     *
     * @param db A database connection
     * @param user The invoking user
     * @return A set of all power levels this user can manage
     */
    public Set<PowerLevel> getManageablePowers(ReadDBConnection db, User user)
    {
        return this.getCumulativePowers(db, user)
                   .stream()
                   .filter(PowerLevel::isAssignable)
                   .map(PowerLevel::getManagedPowers)
                   .flatMap(Set::stream)
                   .collect(Collectors.toUnmodifiableSet());
    }
}
