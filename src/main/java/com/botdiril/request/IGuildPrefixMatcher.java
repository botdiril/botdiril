package com.botdiril.request;

import net.dv8tion.jda.api.entities.GuildMessageChannel;

import com.botdiril.data.IGuildConfiguration;

public interface IGuildPrefixMatcher
{
    ChannelMatch match(IGuildConfiguration guildConfiguration, GuildMessageChannel guildChannel, String content, boolean bypassDisable);
}
