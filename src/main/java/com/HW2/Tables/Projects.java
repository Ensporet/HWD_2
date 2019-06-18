package com.HW2.Tables;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;

@Table(name = "projects")
public class Projects {
    @DBField
    @DBKey
    private int id_projects;
    @DBField
    private String name;
    @DBField
    private String stage;

    public Projects() {
    }

    public Projects(int id_projects, String name, String stage) {
        this.id_projects = id_projects;
        this.name = name;
        this.stage = stage;
    }

    public Projects(int id) {
        this.id_projects = id;
    }

    public int getId_projects() {
        return id_projects;
    }

    public void setId_projects(int id_projects) {
        this.id_projects = id_projects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    @Override
    public String toString() {
        return "Projects{" +
                "id_projects=" + id_projects +
                ", name='" + name + '\'' +
                ", stage='" + stage + '\'' +
                '}';
    }
}
