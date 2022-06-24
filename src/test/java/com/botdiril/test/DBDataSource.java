package com.botdiril.test;

import org.plutoengine.component.AbstractComponent;

import com.botdiril.data.IDataSource;
import com.botdiril.framework.sql.connection.ReadDBConnection;
import com.botdiril.framework.sql.connection.WriteDBConnection;

public abstract class DBDataSource extends AbstractComponent<DBDataSource> implements IDataSource
{
    protected ReadDBConnection db;
    protected WriteDBConnection wdb;

    @Override
    public boolean isUnique()
    {
        return false;
    }
}
