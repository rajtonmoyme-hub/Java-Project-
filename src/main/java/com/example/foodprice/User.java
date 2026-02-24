package com.example.foodprice;

import java.io.Serializable;

// ei class ta file e save korar jonno serializable (object -> byte -> file)
public class User implements Serializable {

    private String name;     // user name
    private String email;    // login email
    private String phone;    // phone number
    private String password; // password

    //default constructor
    public User(){}

    // new user create korar constructor
    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // getter gula -> data baire theke read kora jabe
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
}