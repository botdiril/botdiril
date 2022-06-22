package com.botdiril.command.context;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import org.intellij.lang.annotations.PrintFormat;

import com.botdiril.response.IResponse;
import com.botdiril.response.ResponseEmbed;

public abstract class CommandContext
{
    protected JDA jda;
    protected SelfUser bot;

    protected User caller;

    protected String botIconURL;

    protected IResponse response;
    protected boolean shouldSend = false;

    public JDA getJDA()
    {
        return this.jda;
    }

    public SelfUser getBot()
    {
        return this.bot;
    }

    public String getBotIconURL()
    {
        return this.botIconURL;
    }

    public User getCaller()
    {
        return this.caller;
    }

    public void clearResponse()
    {
        this.shouldSend = false;
        this.response = this.createResponse();
    }

    public void respondf(@PrintFormat String msg, Object... objects)
    {
        this.response.addText(msg.formatted(objects));
        this.shouldSend = true;
    }

    public void respond(String msg)
    {
        this.response.addText(msg);
        this.shouldSend = true;
    }

    public void respond(ResponseEmbed embed)
    {
        this.response.setEmbed(embed);
        this.shouldSend = true;
    }

    public void send()
    {
        if (!this.shouldSend)
            return;

        var defaultResponse = this.getDefaultResponse();
        defaultResponse.send();

        this.clearResponse();
    }

    public IResponse getDefaultResponse()
    {
        return this.response;
    }

    public abstract IResponse createResponse();
}
