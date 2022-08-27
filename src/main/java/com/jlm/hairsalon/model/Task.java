package com.jlm.hairsalon.model;

import java.sql.Time;
import java.sql.Date;



public class Task extends Table{

    @Entity(type="INTEGER",size = 32, primary = true)
    int id;

    @Entity(type="DOUBLE",size = 25)
    Double pauseLength;

    @Entity(type = "DATE",isnull = false)
    Date date;

    @Entity(type = "TIME")
    Time startTime;

    @Entity(type = "TIME")
    Time endTime;

    @Entity(type="INTEGER",size=32)
    @ForeignKey(table ="User", attribute = "id")
    int User_FK;

    @Entity(type = "INTEGER",size = 32)
    @ForeignKey(table = "Client",attribute = "id")
    int Client_FK;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getPauseLength() {
        return pauseLength;
    }

    public void setPauseLength(Double pauseLength) {
        this.pauseLength = pauseLength;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date=date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public User getUser()throws Exception { return (User) Table.get(User.class,User_FK); }

    public int getUser_FK() {
        return User_FK;
    }

    public void setUser_FK(int user_FK) {
        User_FK = user_FK;
    }

    public Client getClient()throws Exception { return (Client) Table.get(Client.class,Client_FK); }

    public void setClient_FK(int client_FK) {
        Client_FK = client_FK;
    }
}
