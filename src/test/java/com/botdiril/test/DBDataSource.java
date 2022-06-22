package com.botdiril.test;

import com.botdiril.data.IDataSource;
import com.botdiril.framework.sql.connection.ReadDBConnection;
import com.botdiril.framework.sql.connection.WriteDBConnection;

public abstract class DBDataSource implements IDataSource
{
    protected ReadDBConnection db;
    protected WriteDBConnection wdb;
}
