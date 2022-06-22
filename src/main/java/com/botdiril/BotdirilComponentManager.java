package com.botdiril;

import org.plutoengine.component.ComponentManager;

public class BotdirilComponentManager extends ComponentManager<BotdirilComponent>
{
    private final Botdiril botdiril;

    public BotdirilComponentManager(Botdiril botdiril)
    {
        super(BotdirilComponent.class);
        this.botdiril = botdiril;
    }

    @Override
    protected void onComponentAdded(BotdirilComponent component)
    {
        component.botdiril = this.botdiril;
        super.onComponentAdded(component);
    }
}
