package com.botdiril.permission;

import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class DefaultPowerLevelManager extends AbstractPowerLevelManager
{
    private PowerLevel defaultPowerLevel;

    private DefaultPowerLevelManager(Set<PowerLevel> powerLevels)
    {
        super(powerLevels);
    }

    public boolean check(IPermissionDataSource db, User user, PowerLevel powerLevel)
    {
        return this.getCumulativePowers(db, user)
                   .stream()
                   .anyMatch(powerLevel::isChildOf);
    }

    public static DefaultPowerLevelManager create(PowerLevelTreeBuilderFunction function)
    {
        var builder = new PowerLevelTreeBuilder();
        function.declareTree(builder);
        var inst = new DefaultPowerLevelManager(builder.get());
        inst.defaultPowerLevel = builder.defaultPowerLevel;
        return inst;
    }

    @FunctionalInterface
    public interface PowerLevelTreeBuilderFunction
    {
        void declareTree(PowerLevelTreeBuilder builder);
    }

    public static class PowerLevelTreeBuilder
    {
        private final Set<PowerLevel> powerLevels;
        protected PowerLevel defaultPowerLevel;

        private PowerLevelTreeBuilder()
        {
            this.powerLevels = new HashSet<>();
        }

        public PowerLevel declarePowerLevel(String id, String name, String description, Consumer<PowerLevelExtraDeclaration> extraPropertiesClosure)
        {
            var extraPropertyDecl = new PowerLevelExtraDeclaration();

            extraPropertiesClosure.accept(extraPropertyDecl);

            var powerLevel = new PowerLevel(id, name, description, extraPropertyDecl.superPowers, extraPropertyDecl.implicitGrantPredicate, extraPropertyDecl.assignable);

            this.powerLevels.add(powerLevel);

            return powerLevel;
        }

        public void setDefaultPowerLevel(PowerLevel powerLevel)
        {
            this.defaultPowerLevel = powerLevel;
        }

        private Set<PowerLevel> get()
        {
            return this.powerLevels;
        }
    }

    public static class PowerLevelExtraDeclaration
    {
        private Set<PowerLevel> superPowers;
        private Predicate<User> implicitGrantPredicate;
        private boolean assignable;

        private PowerLevelExtraDeclaration()
        {
            this.superPowers = Set.of();
            this.implicitGrantPredicate = user -> false;
            this.assignable = false;
        }

        public void inherits(PowerLevel... powerLevels)
        {
            this.superPowers = Set.of(powerLevels);
        }

        public void implicitlyGrantedOn(Predicate<User> predicate)
        {
            this.implicitGrantPredicate = predicate;
        }

        public void setAssignable(boolean assignable)
        {
            this.assignable = assignable;
        }
    }

    @Override
    public PowerLevel getDefault()
    {
        return this.defaultPowerLevel;
    }
}
