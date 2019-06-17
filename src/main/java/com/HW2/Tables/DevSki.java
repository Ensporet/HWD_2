package com.HW2.Tables;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;

public class DevSki {
    @DBField
    @DBKey
    private int id_developers;
    @DBField
    @DBKey
    private int id_skills;

    public DevSki() {
    }

    public DevSki(int id_developers, int id_skills) {
        this.id_developers = id_developers;
        this.id_skills = id_skills;
    }

    public int getId_developers() {
        return id_developers;
    }

    public void setId_developers(int id_developers) {
        this.id_developers = id_developers;
    }

    public int getId_skills() {
        return id_skills;
    }

    public void setId_skills(int id_skills) {
        this.id_skills = id_skills;
    }

    @Override
    public String toString() {
        return "DevSki{" +
                "id_developers=" + id_developers +
                ", id_skills=" + id_skills +
                '}';
    }
}
