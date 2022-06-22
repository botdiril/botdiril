package com.botdiril.request;

import net.dv8tion.jda.api.entities.GuildMessageChannel;
import org.apache.commons.lang3.StringUtils;

import com.botdiril.data.IGuildConfiguration;

public final class SimpleGuildPrefixMatcher extends AbstractGuildPrefixMatcher
{
    private final String prefix;

    public SimpleGuildPrefixMatcher(String prefix)
    {
        this.prefix = prefix;
    }

    @Override
    public ChannelMatch match(IGuildConfiguration guildConfiguration, GuildMessageChannel guildChannel, String content, boolean bypassDisable)
    {
        if (StringUtils.startsWithIgnoreCase(content, this.prefix))
            return new ChannelMatch(true, EnumPrefixMatch.DEFAULT_SERVER_PREFIX, this.prefix);

        var botdirilGlobalPrefix = this.getBotdiril()
                                       .getDefaultPrefix();
        if (StringUtils.startsWithIgnoreCase(content, botdirilGlobalPrefix))
            return new ChannelMatch(true, EnumPrefixMatch.GLOBAL_PREFIX, botdirilGlobalPrefix);


        return new ChannelMatch(false, null, null);
    }
}
