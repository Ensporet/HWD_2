package com.HW2.CRUD;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(value = RUNTIME)
public @interface DBReference {

    String table();

    String nameColumn();
}
