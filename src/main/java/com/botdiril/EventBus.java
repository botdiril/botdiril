package com.botdiril;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.plutoengine.component.AbstractComponent;
import org.plutoengine.component.ComponentToken;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.botdiril.command.ICommandManager;
import com.botdiril.permission.IPowerLevelManager;
import com.botdiril.serverdata.GuildPrefixMatcher;

public class EventBus extends BotdirilComponent
{
    private final Botdiril botdiril;

    private GuildPrefixMatcher guildPrefixMatcher;

    private ICommandManager commandManager;

    private IPowerLevelManager powerLevelManager;

    public final ReentrantReadWriteLock ACCEPTING_COMMANDS;

    private ExecutorService commandThreadPool;

    private final EventListener[] listeners;

    public EventBus(Botdiril botdiril)
    {
        this.botdiril = botdiril;
        this.ACCEPTING_COMMANDS = new ReentrantReadWriteLock();
        this.listeners = new EventListener[] {
            new BotdirilEventListener(this)
        };
    }

    @Override
    protected void onMount(AbstractComponent<BotdirilComponent>.ComponentDependencyManager manager)
    {
        this.guildPrefixMatcher = manager.declareDependency(ComponentToken.create(() -> new GuildPrefixMatcher(this.botdiril)));
        this.powerLevelManager = manager.declareDependency(ComponentToken.create(this.botdiril::createPowerLevelManager));
        this.commandManager = manager.declareDependency(ComponentToken.create(this.botdiril::createCommandManager));
        this.commandThreadPool = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected void onUnmount()
    {
        try
        {
            if (!this.commandThreadPool.awaitTermination(15, TimeUnit.SECONDS))
                this.commandThreadPool.shutdown();
        }
        catch (InterruptedException e)
        {
            this.commandThreadPool.shutdown();
        }
    }

    void onMessage(MessageReceivedEvent event)
    {
        CompletableFuture.runAsync(() -> this.withExecutionLock(() -> this.handleMessage(event)), this.commandThreadPool);
    }

    private void withExecutionLock(Runnable runnable)
    {
        var readLock = this.ACCEPTING_COMMANDS.readLock();

        if (!readLock.tryLock())
            return;

        try
        {
            runnable.run();
        }
        finally
        {
            readLock.unlock();
        }
    }

    private void handleMessage(MessageReceivedEvent event)
    {
        var cm = this.botdiril.getConnectionManager();

        try (var db = cm.getReadOnly())
        {
            if (event.isFromGuild())
            {
                var message = event.getMessage();
                var content = message.getContentRaw();

                var match = this.guildPrefixMatcher.match(db, event.getGuildChannel(), content, false);

                if (!match.matched())
                    return;

                var prefix = match.prefixValue();
                var prefixLength = prefix.codePoints()
                                         .count();

                var contentNoPrefix = content.codePoints()
                                             .skip(prefixLength)
                                             .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                             .toString();

                var cmdParts = contentNoPrefix.split("\\s+", 2);
                var cmdStr = cmdParts[0];
                var cmdParams =  cmdParts.length == 2 ? cmdParts[1] : "";

                var command = this.commandManager.findCommand(cmdStr);

                if (command == null)
                    return;

                System.out.println(command);
            }
        }
    }

    public EventListener[] getListeners()
    {
        return this.listeners;
    }
}
