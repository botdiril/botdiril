package com.botdiril.permission;

import net.dv8tion.jda.api.entities.User;

import com.botdiril.framework.sql.connection.ReadDBConnection;

public interface IPowerLevelManager
{
    boolean check(ReadDBConnection db, User user, PowerLevel powerLevel);

    PowerLevel getDefault();

    PowerLevel getByName(String name);
}
