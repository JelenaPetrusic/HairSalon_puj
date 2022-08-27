package com.jlm.hairsalon.model;

public class ServiceStock extends Table{

    @Entity(type="INTEGER", size=32, primary = true)
    int id;

    @Entity(type="VARCHAR", size=50)
    String name;

    @Entity(type="DOUBLE", isnull = false)
    Double quantity;

    @Entity(type="DOUBLE", size=25)
    Double Price;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice (Double price) {
        this.Price = Price;
    }

    public String toString()
    {
        return this.name;
    }
}


