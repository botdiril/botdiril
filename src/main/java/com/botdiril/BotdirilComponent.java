package com.botdiril;

import org.plutoengine.component.AbstractComponent;

public abstract class BotdirilComponent extends AbstractComponent<BotdirilComponent>
{
    /**
     * The Botdiril instance this component is attached to.
     */
    Botdiril botdiril;

    protected Botdiril getBotdiril()
    {
        return this.botdiril;
    }

    @Override
    public boolean isUnique()
    {
        return true;
    }
}
