package com.botdiril.test;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.botdiril.BotdirilConfig;
import com.botdiril.schema.BotdirilDiscordSchema;

public class MiniMesonConfig extends BotdirilConfig
{
    @JsonProperty("mysql_host")
    protected String sqlHost;

    @JsonProperty("mysql_user")
    protected String sqlUser;

    @JsonProperty("mysql_pass")
    protected String sqlPass;

    public String getSqlHost()
    {
        return this.sqlHost;
    }

    public String getSqlUser()
    {
        return this.sqlUser;
    }

    public String getSqlPass()
    {
        return this.sqlPass;
    }

    @JsonProperty("key")
    @Override
    public String getApiKey()
    {
        return super.getApiKey();
    }

    public Class<?>[] getSchemaClasses()
    {
        return new Class<?>[] { BotdirilDiscordSchema.class };
    }
}
