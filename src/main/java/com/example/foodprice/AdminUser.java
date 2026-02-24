package com.example.foodprice;

// admin er sob info catch korar jonno ekta simple data class
public class AdminUser {

    private String name;     // admin name
    private String email;    // admin email (login e use hobe)
    private String phone;    // admin phone (optional login)
    private String password; // admin password

    // constructor (json e load korar somoy lage)
    public AdminUser() {
    }

    // parameterized constructor
    public AdminUser(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // name return kore
    public String getName() {
        return name;
    }

    // email return kore
    public String getEmail() {
        return email;
    }

    // phone return kore
    public String getPhone() {
        return phone;
    }

    // password return kore (login check e use)
    public String getPassword() {
        return password;
    }
}