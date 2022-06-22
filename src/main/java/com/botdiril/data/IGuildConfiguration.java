package com.botdiril.data;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface IGuildConfiguration extends IDataSource
{
    @NotNull Optional<PrefixInformation> getPrefix(GuildMessageChannel channel);

    default void setPrefix(GuildMessageChannel channel, String prefix)
    {
        this.setPrefix(channel.getGuild(), prefix);
    }

    void setPrefix(Guild guild, String prefix);

    default boolean isEnabled(GuildMessageChannel tc)
    {
        return true;
    }
}
