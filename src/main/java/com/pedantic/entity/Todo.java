package com.pedantic.entity;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@NamedQuery(name = Todo.FIND_ALL_TODOS_BY_ONWER_EMAIL, query = "select todo from Todo todo where todo.todoOwner.email = :email")
public class Todo extends AbstractEntity {

    public static final String FIND_ALL_TODOS_BY_ONWER_EMAIL = "Todo.findAllByEmail";


    @NotEmpty(message = "A Todo task must be set")
    @Size(min = 3, max = 140, message = "The minimum character length should be 3 and max 140.")
    private String task; //varchar 255

    //Create a Todo - /api/v1/todo/create - Todo(Json)
    //Query for a given TODO by its ID - /api/v1/todo/id?id
    //Mark a Todo as completed - /api/v1/todo/mark?id
    //Query for all completed Todos - /api/v1/todo/completed
    //Query for uncompleted Todos - /api/v1/todo/uncompleted
    //Query for Todos based on due date - /api/v1/todo/{dueDate} - - /api/v1/todo?dueDate
    //Mark a Todo as archived - /api/v1/todo/archive?id


    private LocalDate dateCreated;


    @NotNull(message = "Due date must be set")
    @FutureOrPresent(message = "Due date must be in the present or future.")
    @JsonbDateFormat(value = "yyyy-MM-dd")
    private LocalDate dueDate; //yyyy-mm-dd eg 2018-10-31


    private boolean completed;


    private boolean archived;
    private boolean remind;


    @ManyToOne
    @JoinColumn(name = "TodoUser_Id")
    private TodoUser todoOwner; //Many Todos can belong to one TodoUser. todoOwner_id


    @PrePersist
    private void init() {
        setDateCreated(LocalDate.now());

    }


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isRemind() {
        return remind;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
    }

    public TodoUser getTodoOwner() {
        return todoOwner;
    }

    public void setTodoOwner(TodoUser todoOwner) {
        this.todoOwner = todoOwner;
    }
}
