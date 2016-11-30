package com.example.boris.manylists;

public class TodoListClass{

    private int id;
    private String todoCategory;

    // Constructor
    public TodoListClass(){}

    public TodoListClass(String todoCategory) {
        this.todoCategory = todoCategory;
    }

    // Setter
    public void setCategory(String newcategory) {
        this.todoCategory = newcategory;
    }

    public void setId(int id){
        this.id = id;
    }

    // Getter
    public String gettodoCategory(){
        return this.todoCategory;
    }

    public int getId(){
        return this.id;
    }
}
