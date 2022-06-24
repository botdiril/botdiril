package com.botdiril.data;

public interface IDataScope extends AutoCloseable
{
    <C extends IDataSource> C get(Class<C> klass);

    @Override
    default void close()
    {

    }
}
