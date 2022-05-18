package com.botdiril.command.loader;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

final class StringJavaFileObject extends SimpleJavaFileObject
{
    private final String className;
    private final String content;

    private StringJavaFileObject(String className, String content)
    {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.className = className;
        this.content = content;
    }

    public String getClassName()
    {
        return this.className;
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors)
    {
        return this.content;
    }

    static StringJavaFileObject loadFrom(String className, Path path)
    {
        try
        {
            return new StringJavaFileObject(className, Files.readString(path));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
