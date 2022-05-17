package com.botdiril;

import org.plutoengine.component.AbstractComponent;

public abstract class BotdirilComponent extends AbstractComponent<BotdirilComponent>
{
    @Override
    public boolean isUnique()
    {
        return true;
    }
}
