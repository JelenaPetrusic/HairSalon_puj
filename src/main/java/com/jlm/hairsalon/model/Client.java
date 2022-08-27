package com.jlm.hairsalon.model;

public class Client extends Table{
    @Entity(type = "INTEGER",size = 32,primary = true)
    int id;

    @Entity(type = "VARCHAR",size = 50)
    String firstName;

    @Entity(type = "VARCHAR",size = 50)
    String lastName;

    @Entity(type = "VARCHAR",size = 50)
    String address;

    @Entity(type = "VARCHAR",size = 50)
    String phoneNumber;

    @Entity(type = "VARCHAR",size = 50)
    String email;

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAdress() {
        return address;
    }

    public void setAdress(String adress) {
        this.address = adress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString()
    {
        return this.firstName;
    }

}
