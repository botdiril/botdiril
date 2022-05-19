package com.botdiril.response;

import java.util.regex.Pattern;

public class MessageOutputTransformer
{
    //  Quick reference:
    //
    //    $!...$      emote/icon
    //    $&...$      reserved for future use
    //    $#...$      reserved for future use
    //    $*...$      reserved for future use
    //    $%...$      reserved for future use
    //    $?...$      reserved for future use
    //

    private static final Pattern TAG_PATTERN = Pattern.compile("\\$[!&#*%?](?:[a-z0-9_-]+\\.)*[a-z0-9_-]+\\$", Pattern.CASE_INSENSITIVE);

    public static String transformMessage(String message)
    {
        if (message == null)
            return null;

        var matcher = TAG_PATTERN.matcher(message);
        return matcher.replaceAll(matchResult -> {
            var tag = matchResult.group();

            var specifier = tag.charAt(1);

            return switch (specifier)
            {
                default -> tag;
            };
        });
    }
}
