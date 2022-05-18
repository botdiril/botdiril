package com.botdiril.command.loader;

import java.net.URL;
import java.util.*;

class CommandClassLoader extends ClassLoader
{
    private final Map<String, Class<?>> classMap;
    private final Map<String, URL> resourceMap;

    CommandClassLoader()
    {
        this.classMap = new HashMap<>();
        this.resourceMap = new HashMap<>();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        var clazz = this.classMap.get(name);

        if (clazz == null)
        {
            throw new ClassNotFoundException(String.format("Undefined class: '%s'", name));
        }

        return clazz;
    }

    @Override
    protected URL findResource(String name)
    {
        return this.resourceMap.get(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name)
    {
        var resource = this.findResource(name);
        return resource == null ? Collections.emptyEnumeration() : Collections.enumeration(List.of(resource));
    }

    void createClass(String name, byte[] data)
    {
        var clazz = this.defineClass(name, data, 0, data.length);
        this.resolveClass(clazz);
        this.classMap.put(name, clazz);
    }

    private void createResource(String name, URL url)
    {
        this.resourceMap.put(name, url);
    }
}
