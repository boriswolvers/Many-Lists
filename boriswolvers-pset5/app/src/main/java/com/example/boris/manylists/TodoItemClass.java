package com.example.boris.manylists;

public class TodoItemClass{

    private String description;
    private int id;
    private int image_checked_or_unchecked;
    private long fromwich;

    // Constructor
    public TodoItemClass(){}

    public TodoItemClass(String description, int image_checked_or_unchecked, long fromwich) {
        this.description = description;
        this.image_checked_or_unchecked = image_checked_or_unchecked;
        this.fromwich = fromwich;
    }

    // Setters
    public void setDescription(String desc){
        this.description = desc;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setImage_checked_or_unchecked(int new_image_check){
        this.image_checked_or_unchecked = new_image_check;
    }

    public void setFromwich(long new_from){
        this.fromwich = new_from;
    }

    // Getters
    public String getDescription(){
        return this.description;
    }

    public int getId(){
        return this.id;
    }

    public long getFromwich(){
        return this.fromwich;
    }

    public int getImage_checked_or_unchecked()
    {
        return this.image_checked_or_unchecked;
    }
}
