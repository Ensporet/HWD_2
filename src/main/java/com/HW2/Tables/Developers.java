package com.HW2.Tables;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;

public class Developers {

    @DBField
    @DBKey
    private int id_developers;
    @DBField
    private String name;
    @DBField
    private int age;
    @DBField
    private boolean sex;
    @DBField
    private int salary;

    public Developers() {
    }

    public Developers(int id_developers, String name, int age, boolean sex, int salary) {
        this.id_developers = id_developers;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.salary = salary;
    }

    public Developers(int id) {
        this.id_developers = id;
        this.name = "default";
        this.age = 0;
        this.sex = true;
        this.salary = 0;
    }

    public int getId_developers() {
        return id_developers;
    }

    public void setId_developers(int id_developers) {
        this.id_developers = id_developers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Developers{" +
                "id_developers=" + id_developers +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}
