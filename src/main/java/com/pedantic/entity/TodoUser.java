package com.pedantic.entity;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "TodoUserTable")

@NamedQuery(name =TodoUser.FIND_TODO_USER_BY_EMAIL, query = "select tU from TodoUser  tU where tU.email = :email")

@NamedQuery(name = TodoUser.FIND_ALL_TODO_USERS, query = "select todoUser from TodoUser  todoUser order by todoUser.fullName") //select * from TodoUserTable...
@NamedQuery(name = TodoUser.FIND_TODO_USER_BY_ID, query = "select t from TodoUser t where t.id = :id and t.email = :email")
@NamedQuery(name = TodoUser.FIND_TODO_BY_NAME, query = "select t from TodoUser t where t.fullName like :name ")
public class TodoUser extends AbstractEntity { //TodoUser - Database table
 //BY email, by id, fullNamed like jo

    public static final String FIND_TODO_USER_BY_EMAIL = "TodoUser.findByEmail";
    public static final String FIND_ALL_TODO_USERS = "TodoUser.findAll";
    public static final String FIND_TODO_USER_BY_ID = "TodoUser.findByIdAndEmail";
    public static final String FIND_TODO_BY_NAME = "TodoUser.findByName";

    //Create a TodoUser - /api/v1/user/create -TodoUser(Json)
    //Update a TodoUser - /api/v1/user/update - Id, with path to updated TodoUser
    //Find a TodoUser by Email - /api/v1/user/find/{email} --- /api/v1/user/find?email= -TodoUser(Json)
    //Find a TodoUser by Name - /api/v1/user/name/{name} - List of TodoUsers(Json)
    //search for a TodoUser by email - /api/v1/user/search/{email} -- - /api/v1/user/search?email - A number


    @JsonbTransient
    private String salt;


    @Column(length = 100)
    @NotEmpty(message = "An email must be set")
    @Email(message = "Email must be in the format user@somedomain.com")
    private String email; //varchar 255 user@domain.com

//    @JsonbTransient
    @NotNull(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be a min of 8 and max of 100 characters")
//@Pattern(regexp = "", message = "Password must have at least one upper case, " +
//        "one lower case, a digit and must contain at least one of $%&#!")
    private String password; //#%AdhhddS87987fjvjof


    @NotEmpty(message = "Name must be set")
    @Size(min = 2, max = 100, message = "Name must be a min of 2 and max of 100 characters")
    private String fullName;

//    @OneToMany
//    private final Collection<Todo> todos = new ArrayList<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonbTransient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
