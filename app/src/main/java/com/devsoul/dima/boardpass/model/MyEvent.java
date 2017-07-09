package com.devsoul.dima.boardpass.model;

/**
 * This is the My Event class
 * @params Event Name, Place Name, Event Date, Event Time, Address, Price, Picture
 */
public class MyEvent
{
    // Variables
    private String Event_Name;
    private String Place_Name;
    private String Event_Date;
    private String Event_Time;
    private String Address;
    private String Price;
    private String Picture;

    // Constructor
    public MyEvent() {}

    public MyEvent(String event_Name, String place_Name, String event_Date, String event_Time, String address, String price, String picture)
    {
        Event_Name = event_Name;
        Place_Name = place_Name;
        Event_Date = event_Date;
        Event_Time = event_Time;
        Address = address;
        Price = price;
        Picture = picture;
    }

    // Getters and Setters
    public String getEvent_Name()
    {
        return Event_Name;
    }

    public void setEvent_Name(String event_Name)
    {
        Event_Name = event_Name;
    }

    public String getPlace_Name()
    {
        return Place_Name;
    }

    public void setPlace_Name(String place_Name)
    {
        Place_Name = place_Name;
    }

    public String getEvent_Date()
    {
        return Event_Date;
    }

    public void setEvent_Date(String event_Date)
    {
        Event_Date = event_Date;
    }

    public String getEvent_Time()
    {
        return Event_Time;
    }

    public void setEvent_Time(String event_Time)
    {
        Event_Time = event_Time;
    }

    public String getAddress()
    {
        return Address;
    }

    public void setAddress(String address)
    {
        Address = address;
    }

    public String getPrice()
    {
        return Price;
    }

    public void setPrice(String price)
    {
        Price = price;
    }

    public String getPicture()
    {
        return Picture;
    }

    public void setPicture(String picture)
    {
        Picture = picture;
    }
}
