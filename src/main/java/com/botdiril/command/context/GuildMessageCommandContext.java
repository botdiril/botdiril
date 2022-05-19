package com.botdiril.command.context;

import net.dv8tion.jda.api.entities.*;

public class GuildMessageCommandContext extends MessageCommandContext
{
    protected Member member;
    protected Guild guild;

    public GuildMessageCommandContext(TextChannel textChannel, Message message)
    {
        super(textChannel, message);
        this.member = message.getMember();
        this.guild = message.getGuild();
    }

    public Guild getGuild()
    {
        return this.guild;
    }

    public Member getMember()
    {
        return this.member;
    }

    @Override
    public TextChannel getChannel()
    {
        return (TextChannel) super.getChannel();
    }
}
