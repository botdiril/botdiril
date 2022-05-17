package com.botdiril;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.plutoengine.component.ComponentManager;
import org.plutoengine.component.ComponentToken;

import javax.security.auth.login.LoginException;
import java.util.Set;

import com.botdiril.framework.sql.SqlEngine;
import com.botdiril.framework.sql.connection.SqlConnectionConfig;
import com.botdiril.framework.sql.connection.SqlConnectionManager;
import com.botdiril.framework.sql.orm.ModelManager;
import com.botdiril.framework.util.BotdirilInitializationException;

public class Botdiril
{
    private final BotdirilConfig config;
    private final EventBus eventBus;
    private final ComponentManager<BotdirilComponent> components;
    private ShardManager shardManager;
    private final ModelManager modelManager;


    public Botdiril(BotdirilConfig config)
    {
        this.config = config;
        this.components = new ComponentManager<>(BotdirilComponent.class);
        this.eventBus = this.components.addComponent(ComponentToken.create(() -> new EventBus(this)));
        var botdirilConnectionConfig = new SqlConnectionConfig(this.config.getSqlHost(), this.config.getSqlUser(), this.config.getSqlPass(), "b50_discord");
        this.modelManager = SqlEngine.create(botdirilConnectionConfig, this.config.getSchemaClasses());
    }

    protected void start()
    {
        try
        {
            MessageAction.setDefaultMentions(Set.of());

            var jdaBuilder = DefaultShardManagerBuilder.createDefault(this.config.getApiKey(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS);
            jdaBuilder.addEventListeners(eventBus);
            jdaBuilder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
            jdaBuilder.setActivity(this.getDefaultActivity());
            this.shardManager = jdaBuilder.build();
        }
        catch (LoginException e)
        {
            throw new BotdirilInitializationException("An exception has occurred while setting up JDA.", e);
        }
    }

    public ShardManager getShardManager()
    {
        return this.shardManager;
    }

    public EventBus getEventBus()
    {
        return this.eventBus;
    }

    public ComponentManager<BotdirilComponent> getComponents()
    {
        return this.components;
    }

    public ModelManager getModelManager()
    {
        return this.modelManager;
    }

    public SqlConnectionManager getConnectionManager()
    {
        return this.modelManager.getConnectionManager();
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
        return "https://github.com/493msi/botdiril400";
    }
}
