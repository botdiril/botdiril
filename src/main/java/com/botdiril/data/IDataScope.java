package com.botdiril.data;

public interface IDataScope extends AutoCloseable
{
    <T extends IDataSource> T get(Class<? extends T> klass);

    @Override
    default void close()
    {

    }
}
