package com.botdiril.test;

import org.plutoengine.component.ComponentManager;
import org.plutoengine.component.ComponentToken;

import com.botdiril.data.AbstractDataProvider;
import com.botdiril.data.IDataScope;
import com.botdiril.data.IDataSource;

public class MesonDataProvider extends AbstractDataProvider
{
    private ComponentManager<DBDataSource> dataSources;

    public MesonDataProvider()
    {
        this.init();
    }

    public void init()
    {
        this.dataSources = new ComponentManager<>(DBDataSource.class);
        this.dataSources.addComponent(ComponentToken.create(GuildConfiguration::new));
    }

    @Override
    public IDataScope createScope()
    {
        return new IDataScope()
        {
            @Override
            public <C extends IDataSource> C get(Class<C> klass)
            {
                return (C) MesonDataProvider.this.dataSources.getComponent((Class<DBDataSource>) klass);
            }
        };
    }
}
