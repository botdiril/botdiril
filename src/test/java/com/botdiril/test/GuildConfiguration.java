package com.botdiril.test;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import com.botdiril.data.IGuildConfiguration;
import com.botdiril.data.PrefixInformation;
import com.botdiril.schema.BotdirilDiscordSchema;

public class GuildConfiguration extends DBDataSource implements IGuildConfiguration
{
    @Override
    public @NotNull Optional<PrefixInformation> getPrefix(GuildMessageChannel channel)
    {
        var guild = channel.getGuild();

        return this.db.getRecord("""
            SELECT
                `sc_prefix`,
                `cc_prefix`
            FROM `b50_discord`.`server_config`
            LEFT JOIN `b50_discord`.`channel_config` cc ON `server_config`.`sc_id` = cc.`cc_sc_id`
            WHERE `cc_id` = ? OR (`cc_id` IS NULL AND `sc_id` = ?)
            """, PrefixInformation.class, channel.getIdLong(), guild.getIdLong());
    }

    public boolean isEnabled(GuildMessageChannel channel)
    {
        return this.db.getValueOr("""
            SELECT
                `cc_enabled`
            FROM `b50_discord`.`channel_config`
            WHERE `cc_id` = ?
            """, BotdirilDiscordSchema.ChannelConfig.enabled, true, channel.getIdLong());
    }


    @Override
    public void setPrefix(GuildMessageChannel channel, String prefix)
    {
        var guild = channel.getGuild();

        this.wdb.simpleUpdate("""
            INSERT INTO `b50_discord`.`channel_config` (`cc_sc_id`, `cc_id`, `cc_prefix`)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE `cc_prefix` = ?
            """, guild.getIdLong(), channel.getIdLong(), prefix, prefix);
    }

    @Override
    public void setPrefix(Guild guild, String prefix)
    {
        this.wdb.simpleUpdate("""
            INSERT INTO `b50_discord`.`server_config` (`sc_id`, `sc_prefix`)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE `sc_prefix` = ?
            """, guild.getIdLong(), prefix, prefix);
    }
}
