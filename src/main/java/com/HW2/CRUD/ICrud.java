package com.HW2.CRUD;

public interface ICrud<T> {


    void add(T t);

    void set(T t);

    boolean get(T t);

    void delete(T t);


}
