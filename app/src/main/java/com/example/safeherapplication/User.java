package com.example.safeherapplication;

public class User {
    private String email;
    private String firstname;
    private String lastname;
    private String password;

    public User(String email, String firstname, String lastname, String password) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getPassword() { return password; }

    public void setEmail(String email) { this.email = email; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setPassword(String password) { this.password = password; }
}
