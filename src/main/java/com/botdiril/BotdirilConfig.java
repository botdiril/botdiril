package com.botdiril;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.botdiril.framework.util.BotdirilInitializationException;
import com.botdiril.schema.BotdirilDiscordSchema;
import com.botdiril.util.BotdirilLog;

public class BotdirilConfig
{
    @JsonProperty("key")
    private String apiKey;

    @JsonProperty("mysql_host")
    private String sqlHost;

    @JsonProperty("mysql_user")
    private String sqlUser;

    @JsonProperty("mysql_pass")
    private String sqlPass;

    private static final Path CONFIG_FILE = Path.of("settings.json");

    public BotdirilConfig()
    {

    }

    public BotdirilConfig(String apiKey, String sqlHost, String sqlUser, String sqlPass)
    {
        this.apiKey = apiKey;
        this.sqlHost = sqlHost;
        this.sqlUser = sqlUser;
        this.sqlPass = sqlPass;
    }

    public static BotdirilConfig load() throws IOException
    {
        if (!Files.isRegularFile(CONFIG_FILE))
        {
            if (Files.exists(CONFIG_FILE))
            {
                throw new BotdirilInitializationException("%s exists, but is not a regular file!".formatted(CONFIG_FILE));
            }

            var cfg = new BotdirilConfig();
            cfg.apiKey = "<insert Discord bot API key here>";
            cfg.sqlHost = "<insert MySQL hostname here>";
            cfg.sqlUser = "<insert MySQL username here>";
            cfg.sqlPass = "<insert MySQL password here>";

            var mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();

            Files.writeString(CONFIG_FILE, mapper.writeValueAsString(cfg));

            BotdirilLog.logger.error("Could not find %s, aborting.".formatted(CONFIG_FILE.toAbsolutePath()));
            BotdirilLog.logger.error("You need to set up the settings.json file I've just created.");
            BotdirilLog.logger.error("It's just some basic stuff like the API key.");

            throw new BotdirilInitializationException("Unitialized config file!");
        }

        try (var reader = Files.newBufferedReader(CONFIG_FILE))
        {
            var mapper = new ObjectMapper();
            return mapper.readValue(reader, BotdirilConfig.class);
        }
    }

    public String getApiKey()
    {
        return this.apiKey;
    }

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

    public Class<?>[] getSchemaClasses()
    {
        return new Class<?>[] { BotdirilDiscordSchema.class };
    }
}
