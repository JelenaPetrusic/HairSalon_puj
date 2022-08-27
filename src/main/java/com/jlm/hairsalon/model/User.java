package com.jlm.hairsalon.model;


public class User extends Table{
    @Entity(type="INTEGER", size=32, primary = true)
    int id;

    @Entity(type="VARCHAR", size=50)
    String firstName;

    @Entity(type="VARCHAR", size=50)
    String lastName;

    @Entity(type="VARCHAR", size=50)
    String address;

    @Entity(type="VARCHAR", size=50)
    String phoneNumber;

    @Entity(type="VARCHAR", size=50)
    String email;

    @Entity(type="VARCHAR", size=50)
    String userName;

    @Entity(type = "VARCHAR",size=250)
    String password;

    @Entity(type = "INTEGER",size = 32)
    @ForeignKey(table = "Role",attribute = "id")
    int Role_FK;


    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {this.phoneNumber=phoneNumber; }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole_FK() { return Role_FK; }

    public void setRole_FK(int Role_FK) {
        this.Role_FK=Role_FK;
    }

    public Role getRole()throws Exception { return (Role) Table.get(Role.class,Role_FK); }

    public String toString() { return this.userName; }

}
