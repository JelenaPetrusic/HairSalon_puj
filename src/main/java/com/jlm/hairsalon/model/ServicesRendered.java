package com.jlm.hairsalon.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServicesRendered extends Table{
    @Entity(type = "INTEGER",size=32,primary = true)
    int id;

    @Entity(type = "DOUBLE",size = 25)
    Double quantity;

    @Entity(type = "INTEGER",size=32)
    @ForeignKey(table = "ServiceStock",attribute = "id")
    int serviceStock_FK;

    @Entity(type = "INTEGER",size = 32)
    @ForeignKey(table = "Task",attribute = "id")
    int task_FK;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getTask()throws Exception { return (Task) Table.get(Task.class,task_FK); }

    public void setTask_FK(int Task_FK)
    {
        this.task_FK=Task_FK;
    }

    public ServiceStock getServiceStock()throws Exception { return (ServiceStock) Table.get(ServiceStock.class,serviceStock_FK);  }

    public void setServiceStock_FK(int serviceStock_FK) {
        this.serviceStock_FK = serviceStock_FK;
    }

    public Double getQuantity() { return quantity; }

    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public int getServiceStock_FK() {
        return serviceStock_FK;
    }

    public int getTask_FK() {
        return task_FK;
    }

}

