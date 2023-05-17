package com.pedantic.service;


import com.pedantic.entity.Todo;
import com.pedantic.entity.TodoUser;

import javax.annotation.PostConstruct;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@DataSourceDefinition(name = "java:app/Todo/MyDS"
        ,className = "org.h2.Driver",
        url = "jdbc:h2:mem:todo",
        user = "sa",
        password = "sa"

)
@Stateless
public class PersistenceService {

    @Inject
    private SecurityUtil securityUtil;

//    @Inject
//    private MySession mySession;

    @Inject
    private QueryService queryService;


    @Context
    private SecurityContext securityContext;

    @PersistenceContext
    EntityManager entityManager;        //new SomeConcreteImplementation;





    @PostConstruct
    private void init() {

    }


    //1. Grab email of currently executing user
    //2. Grab same user from DB using grabbed email in 1.
    //3. Assign the user returned in 2 to a new Todo

    public TodoUser saveTodoUser(TodoUser todoUser) {
        //TODO Implement
        //someInterface.saveToDB(todoUser)
        List list = queryService.countTodoUserByEmail(todoUser.getEmail());

        Integer count = (Integer) list.get(0);


        //Change 3
        Map<String, String> credentialMap = securityUtil.hashPassword(todoUser.getPassword());

        if (todoUser.getId() == null && count == 0) { //If it's a new entity, save it

            todoUser.setPassword(credentialMap.get("hashedPassword"));
            todoUser.setSalt(credentialMap.get("salt"));

            entityManager.persist(todoUser); //#save}
        }
        credentialMap.clear();

        return todoUser;
    }


    public TodoUser updateTodoUser(TodoUser todoUser) {


        List list = queryService.countTodoUser(todoUser.getId(), todoUser.getEmail());
        Integer count = (Integer) list.get(0);


        if (todoUser.getId() != null && count == 1) {
            entityManager.merge(todoUser);

        }

        return todoUser;
    }


    public TodoUser updateTodoUserEmail(Long id, String email) {
        //Count and see if the email exists or not
        //Find the TodoUser
        //Assign the email
        //Merge back to the persistence context
        //return

        List list = queryService.countTodoUserByEmail(email);
        Integer count = (Integer) list.get(0);

        if (count == 0) {
            TodoUser todoUser = queryService.findTodoUser(id); //Substitute for findTodoUserById(Long id)
            if (todoUser != null) {
                todoUser.setEmail(email);
                entityManager.merge(todoUser);

                return todoUser;
            }

        }

        return null;


    }


    public Todo saveTodo(Todo todo) {


        TodoUser todoUserByEmail = queryService.findTodoUserByEmail(securityContext.getUserPrincipal().getName());


        if (todo.getId() == null && todoUserByEmail != null) {

            todo.setTodoOwner(todoUserByEmail);
            entityManager.persist(todo);
        } else {
            entityManager.merge(todo);
        }

        return todo;
    }
}
