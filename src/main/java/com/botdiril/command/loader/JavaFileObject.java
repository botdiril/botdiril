package com.botdiril.command.loader;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

final class JavaFileObject extends SimpleJavaFileObject
{
    private final String className;
    private final ByteArrayOutputStream baos;

    JavaFileObject(String name, Kind kind)
    {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
        this.baos = new ByteArrayOutputStream();
        this.className = name;
    }

    public String getClassName()
    {
        return this.className;
    }

    byte[] getBytes()
    {
        return this.baos.toByteArray();
    }

    @Override
    public OutputStream openOutputStream()
    {
        return this.baos;
    }
}
