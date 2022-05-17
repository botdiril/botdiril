package com.botdiril.util;

import java.util.Scanner;

import com.botdiril.framework.sql.SqlEngine;
import com.botdiril.framework.sql.connection.SqlConnectionConfig;
import com.botdiril.schema.BotdirilDiscordSchema;

public class ConstructDBUtil
{
    public static void main(String[] args)
    {
        var scn = new Scanner(System.in);

        System.out.print("Host: ");
        var dbHost = scn.nextLine();
        System.out.print("Username: ");
        var dbUsername = scn.nextLine();
        System.out.print("Password: ");
        var dbPassword = scn.nextLine();

        var cfg = new SqlConnectionConfig(dbHost, dbUsername, dbPassword, "b50_discord");

        try (var modelManager = SqlEngine.create(cfg, BotdirilDiscordSchema.class))
        {
            System.out.println(modelManager);
        }
    }
}
