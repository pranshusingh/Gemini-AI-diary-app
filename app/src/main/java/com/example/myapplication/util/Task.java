package com.example.myapplication.util;

public class Task {
    private int id;
    private String name;
    private String date;
    private int priority;
    private boolean completed;

    // Constructor, getters, and setters

    public Task(){
        //empty constructor
    }
    // Constructor for creating a new task
    public Task(String name, String date, int priority, boolean completed) {
        this.name = name;
        this.date = date;
        this.priority = priority;
        this.completed = completed;
    }

    // Constructor for retrieving a task from the database
    public Task(int id, String name, String date, int priority, boolean completed) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.priority = priority;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
