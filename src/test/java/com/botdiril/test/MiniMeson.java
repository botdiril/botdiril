package com.botdiril.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

import com.botdiril.Botdiril;
import com.botdiril.data.AbstractDataProvider;
import com.botdiril.data.IDataScope;
import com.botdiril.framework.sql.SqlEngine;
import com.botdiril.framework.sql.connection.SqlConnectionConfig;
import com.botdiril.framework.sql.orm.ModelManager;
import com.botdiril.request.AbstractGuildPrefixMatcher;

public class MiniMeson extends Botdiril
{
    private final MiniMesonConfig config;
    private ModelManager modelManager;

    public MiniMeson(MiniMesonConfig config)
    {
        super(config);

        this.config = config;
    }

    @Override
    protected void start()
    {
        super.start();

        var botdirilConnectionConfig = new SqlConnectionConfig(this.config.getSqlHost(), this.config.getSqlUser(), this.config.getSqlPass(), "b50_discord");
        this.modelManager = SqlEngine.create(botdirilConnectionConfig, this.config.getSchemaClasses());
    }

    @Override
    public AbstractDataProvider createDataProvider()
    {
        return new AbstractDataProvider()
        {
            @Override
            public IDataScope createScope()
            {
                return null;
            }
        };
    }

    @Override
    public AbstractGuildPrefixMatcher createGuildPrefixMatcher()
    {
        return new GuildPrefixMatcher(this);
    }

    public ModelManager getModelManager()
    {
        return this.modelManager;
    }

    public static void main(String[] args) throws Exception
    {
        try (var reader = Files.newBufferedReader(Path.of("settings.json")))
        {
            var mapper = new ObjectMapper();
            var config = mapper.readValue(reader, MiniMesonConfig.class);
            var meson = new MiniMeson(config);
            meson.start();
        }
    }
}
