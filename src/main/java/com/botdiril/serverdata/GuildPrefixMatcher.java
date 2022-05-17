package com.botdiril.serverdata;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import com.botdiril.Botdiril;
import com.botdiril.BotdirilComponent;
import com.botdiril.framework.sql.connection.ReadDBConnection;
import com.botdiril.schema.BotdirilDiscordSchema;

public class GuildPrefixMatcher extends BotdirilComponent
{
    private final Botdiril botdiril;

    public enum EnumPrefixMatch
    {
        GLOBAL_PREFIX,
        DEFAULT_SERVER_PREFIX,
        SERVER_PREFIX,
        CHANNEL_PREFIX
    }

    public record ChannelMatch(
        boolean matched,
        EnumPrefixMatch matchedPrefix,
        String prefixValue
    )
    {
    }
    
    public GuildPrefixMatcher(Botdiril botdiril)
    {
        this.botdiril = botdiril;
    }

    public @NotNull ChannelMatch match(ReadDBConnection db, MessageChannel channel, String message, boolean bypassDisable)
    {
        var channelID = channel.getIdLong();

        if (!(channel instanceof TextChannel tc))
            return new ChannelMatch(false, null, null);

        var guild = tc.getGuild();
        var guildID = guild.getIdLong();

        var prefixConfigOpt = db.getRow("""
            SELECT
                `sc_prefix`,
                `cc_prefix`,
                `cc_enabled`
            FROM `b50_discord`.`server_config`
            LEFT JOIN `b50_discord`.`channel_config` cc ON `server_config`.`sc_id` = cc.`cc_sc_id`
            WHERE `cc_id` = ? OR (`cc_id` IS NULL AND `sc_id` = ?)
            """, channelID, guildID);

        var scPrefix = "$";
        var ccPrefix = Optional.<String>empty();
        boolean enabled = true;

        if (prefixConfigOpt.isPresent())
        {
            var prefixConfig = prefixConfigOpt.orElseThrow();

            scPrefix = prefixConfig.getUnwrapValue(BotdirilDiscordSchema.ServerConfig.prefix);
            ccPrefix = prefixConfig.getValue(BotdirilDiscordSchema.ChannelConfig.prefix);
            enabled = prefixConfig.getValue(BotdirilDiscordSchema.ChannelConfig.enabled).orElse(true);
        }
        else if (StringUtils.startsWithIgnoreCase(message, scPrefix))
            return new ChannelMatch(true, EnumPrefixMatch.DEFAULT_SERVER_PREFIX, scPrefix);

        if (!enabled && !bypassDisable)
            return new ChannelMatch(false, null, null);

        var botdirilGlobalPrefix = botdiril.getDefaultPrefix();
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
