package com.botdiril.command.loader;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.StandardJavaFileManager;
import java.util.ArrayList;
import java.util.List;

class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager>
{
    private final List<JavaFileObject> objects;

    ClassFileManager(StandardJavaFileManager fileManager)
    {
        super(fileManager);
        this.objects = new ArrayList<>();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
    {
        var object = new JavaFileObject(className, kind);
        this.objects.add(object);
        return object;
    }

    public List<JavaFileObject> getObjects()
    {
        return this.objects;
    }
}
