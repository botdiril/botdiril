package com.botdiril.command.behavior;

import com.botdiril.Botdiril;
import com.botdiril.command.context.CommandContext;
import com.botdiril.data.IDataScope;
import com.botdiril.data.IDataSource;
import com.botdiril.util.CommandAssertions;

public abstract class CommandBase<T extends CommandContext>
{
    protected Botdiril botdiril;
    protected T co;
    protected CommandAssertions assertion;

    protected IDataScope ds;
    protected Runnable invokeHandler;

    public abstract Runnable declareBehavior(CommandBehavior behavior);

    public final void invoke()
    {
        this.invokeHandler.run();
    }

    protected IDataScope getDataScope()
    {
        return this.ds;
    }

    protected <R extends IDataSource> R getDataSource(Class<R> klass)
    {
        return this.ds.get(klass);
    }
}
