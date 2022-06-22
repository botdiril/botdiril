package com.botdiril.permission;

import net.dv8tion.jda.api.entities.User;

public interface IPowerLevelManager
{
    boolean check(IPermissionDataSource ds, User user, PowerLevel powerLevel);

    PowerLevel getDefault();

    PowerLevel getByName(String name);
}
