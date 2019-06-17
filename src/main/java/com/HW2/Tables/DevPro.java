package com.HW2.Tables;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;

public class DevPro {

    @DBField
    @DBKey
    private int id_developers;
    @DBField
    @DBKey
    private int id_projects;

    public DevPro(int id_developers, int id_projects) {
        this.id_developers = id_developers;
        this.id_projects = id_projects;
    }

    public DevPro() {
    }

    public int getId_developers() {
        return id_developers;
    }

    public void setId_developers(int id_developers) {
        this.id_developers = id_developers;
    }

    public int getId_projects() {
        return id_projects;
    }

    public void setId_projects(int id_projects) {
        this.id_projects = id_projects;
    }

    @Override
    public String toString() {
        return "DevPro{" +
                "id_developers=" + id_developers +
                ", id_projects=" + id_projects +
                '}';
    }
}
