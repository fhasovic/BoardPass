package com.devsoul.dima.boardpass.beans;

/**
 * This class is used to store the data for each row in ListView.
 */
public class RowItem
{
    // Variables
    private int imageId;
    private String title;
    private String place;

    // Default Constructor
    public RowItem() {}

    // Constructor
    public RowItem(int imageId, String title, String place)
    {
        this.imageId = imageId;
        this.title = title;
        this.place = place;
    }

    // Getters + Setters
    public int getImageId()
    {
        return imageId;
    }

    public void setImageId(int imageId)
    {
        this.imageId = imageId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getPlace()
    {
        return place;
    }

    public void setPlace(String place)
    {
        this.place = place;
    }

    @Override
    public String toString()
    {
        return title + "\n" + place;
    }
}