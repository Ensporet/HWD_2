package com.HW2.Tables;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;
import com.HW2.CRUD.DBReference;

@Table(name = "cus_pro")
public class CusPro {

    @DBReference(table = "customers", nameColumn = "id_customers")
    @DBField
    @DBKey
    private int id_customers;

    @DBReference(table = "projects", nameColumn = "id_projects")
    @DBField
    @DBKey
    private int id_projects;

    public CusPro() {
    }

    public CusPro(int id_customers, int id_projects) {
        this.id_customers = id_customers;
        this.id_projects = id_projects;
    }

    public int getId_customers() {
        return id_customers;
    }

    public void setId_customers(int id_customers) {
        this.id_customers = id_customers;
    }

    public int getId_projects() {
        return id_projects;
    }

    public void setId_projects(int id_projects) {
        this.id_projects = id_projects;
    }

    @Override
    public String toString() {
        return "CusPro{" +
                "id_customers=" + id_customers +
                ", id_projects=" + id_projects +
                '}';
    }
}
