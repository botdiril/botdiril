package com.botdiril.schema;

import com.botdiril.framework.sql.orm.ModelColumn;
import com.botdiril.framework.sql.orm.column.*;
import com.botdiril.framework.sql.orm.column.defaultvalue.StaticDefaultValueSupplier;
import com.botdiril.framework.sql.orm.schema.Schema;
import com.botdiril.framework.sql.orm.table.Table;

@Schema(name = "b50_discord")
public class BotdirilDiscordSchema
{
    @Table(name = "server_config", prefix = "sc")
    public static class ServerConfig
    {
        @Column(dataType = Long.class)
        @PrimaryKey
        public static ModelColumn<Long> id;

        @Column(dataType = String.class, bounds = 8)
        @NotNull
        public static ModelColumn<String> prefix;
    }

    @Table(name = "channel_config", prefix = "cc")
    public static class ChannelConfig
    {
        @Column(dataType = Long.class)
        @PrimaryKey
        public static ModelColumn<Long> id;

        @Column(dataType = Long.class)
        @ForeignKey(value = ServerConfig.class, parentDeleteAction = ForeignKey.ParentDeleteAction.CASCADE_DELETE)
        public static ModelColumn<Long> sc_id;

        @Column(dataType = Boolean.class)
        @DefaultValue(StaticDefaultValueSupplier.TrueBoolean.class)
        public static ModelColumn<Boolean> enabled;

        @Column(dataType = String.class, bounds = 8)
        public static ModelColumn<String> prefix;
    }
}
