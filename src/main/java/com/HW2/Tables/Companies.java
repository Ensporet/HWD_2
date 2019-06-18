package com.HW2.Tables;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;

@Table(name = "companies")
public class Companies {

    @DBField
    @DBKey
    private int id_companies;
    @DBField
    private String name;
    @DBField
    private String slogan;

    public Companies(int id_companies, String name, String slogan) {
        this.id_companies = id_companies;
        this.name = name;
        this.slogan = slogan;
    }

    public Companies(int id) {
        this.id_companies = id;

    }

    public Companies() {


    }

    public int getId_companies() {
        return id_companies;
    }

    public void setId_companies(int id_companies) {
        this.id_companies = id_companies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    @Override
    public String toString() {
        return "Companies{" +
                "id_companies=" + id_companies +
                ", name='" + name + '\'' +
                ", slogan='" + slogan + '\'' +
                '}';
    }
}
