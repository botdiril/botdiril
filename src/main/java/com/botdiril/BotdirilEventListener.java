package com.botdiril;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import com.botdiril.util.BotdirilLog;

public class BotdirilEventListener extends ListenerAdapter
{
    private final EventBus eventBus;

    BotdirilEventListener(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        eventBus.onMessage(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {

    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event)
    {

    }

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        var jda = event.getJDA();
        var shardInfo = jda.getShardInfo();

        BotdirilLog.logger.info("Shard {} ready.", shardInfo.getShardString());
    }
}
