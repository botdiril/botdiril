package com.botdiril.command.invoke;

import org.jetbrains.annotations.Range;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CommandParam
{
    String value();

    @Range(from = 0, to = 999)
    int ordinal();

    boolean required() default true;
}
