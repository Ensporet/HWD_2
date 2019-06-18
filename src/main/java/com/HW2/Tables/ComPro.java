package com.HW2.Tables;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;
import com.HW2.CRUD.DBReference;

@Table(name = "com_pro")
public class ComPro {

    @DBReference(table = "companies", nameColumn = "id_companies")
    @DBField
    @DBKey
    private int id_companies;

    @DBReference(table = "projects", nameColumn = "id_projects")
    @DBField
    @DBKey
    private int id_projects;


    public ComPro(int id_companies, int id_projects) {
        this.id_companies = id_companies;
        this.id_projects = id_projects;
    }

    public ComPro() {
    }

    public int getId_companies() {
        return id_companies;
    }

    public void setId_companies(int id_companies) {
        this.id_companies = id_companies;
    }

    public int getId_projects() {
        return id_projects;
    }

    public void setId_projects(int id_projects) {
        this.id_projects = id_projects;
    }

    @Override
    public String toString() {
        return "ComPro{" +
                "id_companies=" + id_companies +
                ", id_projects=" + id_projects +
                '}';
    }
}
