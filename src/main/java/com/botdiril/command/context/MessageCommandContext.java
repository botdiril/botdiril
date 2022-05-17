package com.botdiril.command.context;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import com.botdiril.response.DiscordMessageResponse;
import com.botdiril.response.MessageOutputTransformer;

public class MessageCommandContext extends CommandContext
{
    protected String contents;
    protected String usedPrefix;
    protected String usedAlias;

    protected MessageChannel channel;
    protected Message message;

    public MessageCommandContext(MessageChannel textChannel, Message message)
    {
        this.channel = textChannel;
        this.message = message;
        this.response = this.createResponse();
    }

    public Message getMessage()
    {
        return this.message;
    }

    public MessageChannel getChannel()
    {
        return this.channel;
    }

    public String getContents()
    {
        return this.contents;
    }

    public String getUsedAlias()
    {
        return this.usedAlias;
    }

    public String getUsedPrefix()
    {
        return this.usedPrefix;
    }

    @Override
    public DiscordMessageResponse createResponse()
    {
        return new DiscordMessageResponse(this.channel, this.message);
    }

    public DiscordMessageResponse createResponse(MessageChannel channel)
    {
        return new DiscordMessageResponse(channel);
    }

    public DiscordMessageResponse createResponse(MessageChannel channel, Message message)
    {
        return new DiscordMessageResponse(channel, message);
    }

    @Override
    public DiscordMessageResponse getDefaultResponse()
    {
        return (DiscordMessageResponse) this.response;
    }

    public void respond(EmbedBuilder msg)
    {
        transformEmbed(msg);
        this.channel.sendMessageEmbeds(msg.build()).queue();
    }

    private void transformEmbed(EmbedBuilder msg)
    {
        var fakeEmbed = msg.build();

        msg.setDescription(MessageOutputTransformer.transformMessage(fakeEmbed.getDescription()));
        msg.setTitle(MessageOutputTransformer.transformMessage(fakeEmbed.getTitle()));

        msg.clearFields();
        fakeEmbed.getFields().forEach(field -> msg.addField(MessageOutputTransformer.transformMessage(field.getName()),
            MessageOutputTransformer.transformMessage(field.getValue()),
            field.isInline()));

        var authorInfo = fakeEmbed.getAuthor();
        if (authorInfo != null)
            msg.setAuthor(MessageOutputTransformer.transformMessage(authorInfo.getName()), authorInfo.getUrl(), authorInfo.getIconUrl());

        var footerInfo = fakeEmbed.getFooter();
        if (footerInfo != null)
            msg.setFooter(MessageOutputTransformer.transformMessage(footerInfo.getText()));
    }
}
