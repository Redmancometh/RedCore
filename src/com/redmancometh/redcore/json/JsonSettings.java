package com.redmancometh.redcore.json;

import java.lang.annotation.*;

@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface JsonSettings {
    String defaultValue() default "null";

    boolean serialize() default true;
}

