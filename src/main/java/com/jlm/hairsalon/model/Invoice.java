package com.jlm.hairsalon.model;

public class Invoice extends Table{

    private int id;
    private String name;
    private Double Price;
    private Double quantity;
    private Double sum;

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

    public void setPrice(Double Price)
    {
        this.Price = Price;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum() {
        this.sum = this.Price*this.quantity;
    }


}



