package com.botdiril;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.plutoengine.component.ComponentManager;
import org.plutoengine.component.ComponentToken;

import javax.security.auth.login.LoginException;
import java.util.Set;
import java.util.function.Predicate;

import com.botdiril.command.AbstractCommandManager;
import com.botdiril.command.DefaultCommandManager;
import com.botdiril.data.AbstractDataProvider;
import com.botdiril.data.IDataProvider;
import com.botdiril.data.IDataScope;
import com.botdiril.data.IDataSource;
import com.botdiril.permission.AbstractPowerLevelManager;
import com.botdiril.permission.DefaultPowerLevelManager;
import com.botdiril.request.AbstractGuildPrefixMatcher;
import com.botdiril.request.SimpleGuildPrefixMatcher;
import com.botdiril.util.BotdirilSetupException;

public class Botdiril
{
    private final BotdirilConfig config;
    private final BotdirilComponentManager components;
    private ShardManager shardManager;
    private IDataProvider dataProvider;


    public Botdiril(BotdirilConfig config)
    {
        BotdirilStatic.initialize();

        this.config = config;
        this.components = new BotdirilComponentManager(this);
    }

    protected void start()
    {
        this.dataProvider = this.components.addComponent(ComponentToken.create(this::createDataProvider));
        this.components.addComponent(ComponentToken.create(() -> new EventBus(this)));

        try
        {
            MessageAction.setDefaultMentions(Set.of());

            var eventBus = this.getEventBus();

            var jdaBuilder = DefaultShardManagerBuilder.createDefault(this.config.getApiKey(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS);
            jdaBuilder.addEventListeners((Object[]) eventBus.getListeners());
            jdaBuilder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
            jdaBuilder.setActivity(this.getDefaultActivity());
            this.shardManager = jdaBuilder.build();
        }
        catch (LoginException e)
        {
            throw new BotdirilSetupException("An exception has occurred while setting up JDA.", e);
        }
    }

    public ShardManager getShardManager()
    {
        return this.shardManager;
    }

    public EventBus getEventBus()
    {
        return this.components.getComponent(EventBus.class);
    }

    public IDataProvider getDataProvider()
    {
        return this.dataProvider;
    }

    public ComponentManager<BotdirilComponent> getComponents()
    {
        return this.components;
    }

    public String getDefaultPrefix()
    {
        return "botdiril.";
    }

    public String getBranding()
    {
        return "Meson";
    }

    public Activity getDefaultActivity()
    {
        return Activity.listening("botdiril.com");
    }

    public String getRepositoryURL()
    {
        return "https://github.com/botdiril/botdiril";
    }

    public AbstractDataProvider createDataProvider()
    {
        return new AbstractDataProvider()
        {
            @Override
            public IDataScope createScope()
            {
                return new IDataScope() {

                    @Override
                    public <C extends IDataSource> C get(Class<C> klass)
                    {
                        return null;
                    }
                };
            }
        };
    }

    public AbstractGuildPrefixMatcher createGuildPrefixMatcher()
    {
        return new SimpleGuildPrefixMatcher(">");
    }

    public AbstractCommandManager createCommandManager()
    {
        return new DefaultCommandManager();
    }

    public AbstractPowerLevelManager createPowerLevelManager()
    {
        return DefaultPowerLevelManager.create(tree -> {
           var defaultLevel = tree.declarePowerLevel("default", "Default", "The default power level given to everyone.", pl -> {
               pl.implicitlyGrantedOn(Predicate.not(User::isBot));
           });

           tree.setDefaultPowerLevel(defaultLevel);
        });
    }
}
