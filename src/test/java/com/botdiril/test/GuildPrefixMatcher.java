package com.botdiril.test;

import net.dv8tion.jda.api.entities.GuildMessageChannel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import com.botdiril.data.IGuildConfiguration;
import com.botdiril.request.AbstractGuildPrefixMatcher;
import com.botdiril.request.ChannelMatch;
import com.botdiril.request.EnumPrefixMatch;

public class GuildPrefixMatcher extends AbstractGuildPrefixMatcher
{
    private final MiniMeson botdiril;

    public GuildPrefixMatcher(MiniMeson botdiril)
    {
        this.botdiril = botdiril;
    }

    public @NotNull ChannelMatch match(IGuildConfiguration guildConfiguration, GuildMessageChannel tc, String message, boolean bypassDisable)
    {
        var prefixConfigOpt = guildConfiguration.getPrefix(tc);

        var scPrefix = "$";
        var ccPrefix = Optional.<String>empty();
        boolean enabled = true;

        if (prefixConfigOpt.isPresent())
        {
            var prefixConfig = prefixConfigOpt.orElseThrow();

            scPrefix = prefixConfig.guildPrefix();
            ccPrefix = Optional.ofNullable(prefixConfig.channelPrefix());
            enabled = guildConfiguration.isEnabled(tc);
        }
        else if (StringUtils.startsWithIgnoreCase(message, scPrefix))
            return new ChannelMatch(true, EnumPrefixMatch.DEFAULT_SERVER_PREFIX, scPrefix);

        if (!enabled && !bypassDisable)
            return new ChannelMatch(false, null, null);

        var botdirilGlobalPrefix = this.botdiril.getDefaultPrefix();
        if (StringUtils.startsWithIgnoreCase(message, botdirilGlobalPrefix))
            return new ChannelMatch(true, EnumPrefixMatch.GLOBAL_PREFIX, botdirilGlobalPrefix);

        if (StringUtils.startsWithIgnoreCase(message, scPrefix))
            return new ChannelMatch(true, EnumPrefixMatch.SERVER_PREFIX, botdirilGlobalPrefix);

        if (ccPrefix.isPresent())
        {
            var ccPref = ccPrefix.get();
            if (StringUtils.startsWithIgnoreCase(message, ccPref))
                return new ChannelMatch(true, EnumPrefixMatch.CHANNEL_PREFIX, ccPref);
        }

        return new ChannelMatch(false, null, null);
    }
}
