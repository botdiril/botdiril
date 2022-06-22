package com.botdiril.request;

public record ChannelMatch(
    boolean matched,
    EnumPrefixMatch prefixType,
    String prefixValue
)
{
}
