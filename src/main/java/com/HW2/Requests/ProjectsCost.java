package com.HW2.Requests;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;

public class ProjectsCost {


    @DBField
    @DBKey
    private int id_projects;
    @DBField
    private String name;
    @DBField
    private int cost;


    public ProjectsCost() {
    }

    public ProjectsCost(int id_projects, int cost) {
        this.id_projects = id_projects;
        this.cost = cost;
    }

    public ProjectsCost(int id_projects) {
        this.id_projects = id_projects;

    }

    public int getId_projects() {
        return id_projects;
    }

    public void setId_projects(int id_projects) {
        this.id_projects = id_projects;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProjectsCost{" +
                "id_projects=" + id_projects +
                ", cost=" + cost +
                ", name='" + name + '\'' +
                '}';
    }
}
