package com.IAppDevelopment.virtual_marathon.Object_classes;

/**
 * This class creates a user-type object,
 * the class will include 4 String variables,
 * Name, Password, Email, and Image Address
 */
public class User {

    public String pic_url;
    public String name;
    private String password;
    public String email;

    public User(){}

    public User(String Name,String passwrd){
        this.name=Name;
        this.password=passwrd;
    }

    public User(String Name, String passwrd, String email){
        this.name=Name;
        this.password=passwrd;
        this.email=email;
    }

    /**
     * This function receives a password
     * @param password
     */
    public void setPassword (String password){
        this.password=password;
    }

    /**
     * This function returns the password
     * @return returns the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * This function returns the name
     * @return returns the name
     */
    public String toString(){
        return name;
    }

}
