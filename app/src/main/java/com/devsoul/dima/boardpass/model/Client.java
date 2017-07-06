package com.devsoul.dima.boardpass.model;

/**
 * This is the client class
 * @params Full Name, Birth Date, ID, VIP, Email, Password, Created At
 */
public class Client
{
    // Variables
    private String Full_Name;
    private String BirthDate;
    private String ID;
    private String VIP;
    private String Email;
    private String Password;
    private String Created_At;

    // Default Constructor
    public Client() {}

    // Getters and Setters
    // Full Name
    public void SetFullName(String Name)
    {
        this.Full_Name = Name;
    }
    public String GetFullName()
    {
        return this.Full_Name;
    }

    // Birth Date
    public void SetBirthDate(String BirthDate)
    {
        this.BirthDate = BirthDate;
    }
    public String GetBirthDate()
    {
        return this.BirthDate;
    }

    // ID
    public void SetID(String ID)
    {
        this.ID = ID;
    }
    public String GetID()
    {
        return this.ID;
    }

    // VIP
    public void SetVIP(String VIP)
    {
        this.VIP = VIP;
    }
    public String GetVIP()
    {
        return this.VIP;
    }

    // Email
    public void SetEmail(String Email)
    {
        this.Email = Email;
    }
    public String GetEmail()
    {
        return this.Email;
    }

    // Password
    public void SetPassword(String Pass)
    {
        this.Password = Pass;
    }
    public String GetPassword()
    {
        return this.Password;
    }

    // Created At
    public void SetCreated_At(String Created_At)
    {
        this.Created_At = Created_At;
    }
    public String GetCreated_At()
    {
        return this.Created_At;
    }
}
