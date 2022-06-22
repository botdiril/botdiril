package com.botdiril;

public class BotdirilConfig
{
    protected String apiKey = "<insert Discord bot API key here>";

    public BotdirilConfig()
    {

    }

    public BotdirilConfig(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getApiKey()
    {
        return this.apiKey;
    }
}
