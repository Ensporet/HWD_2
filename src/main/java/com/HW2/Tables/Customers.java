package com.HW2.Tables;

import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBKey;

@Table(name = "customers")
public class Customers {

    @DBField
    @DBKey
    private int id_customers;
    @DBField
    private String name;
    @DBField
    private int liquidity;

    public Customers() {
    }

    public Customers(int id_customers, String name, int liquidity) {
        this.id_customers = id_customers;
        this.name = name;
        this.liquidity = liquidity;
    }

    public Customers(int id) {
        this.id_customers = id;
    }

    public int getId_customers() {
        return id_customers;
    }

    public void setId_customers(int id_customers) {
        this.id_customers = id_customers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(int liquidity) {
        this.liquidity = liquidity;
    }

    @Override
    public String toString() {
        return "Customers{" +
                "id_customers=" + id_customers +
                ", name='" + name + '\'' +
                ", liquidity=" + liquidity +
                '}';
    }
}
