package com.pedantic.service;

import com.pedantic.entity.Todo;
import com.pedantic.entity.TodoUser;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class QueryService {

    @PersistenceContext
    EntityManager entityManager;

//    @Inject
//    private MySession mySession;


    @Inject
    private SecurityUtil securityUtil;


    @Context
    private SecurityContext securityContext;




    @Inject
    private Logger logger;



    @PostConstruct
    private void init() {

    }

    public TodoUser findTodoUserByEmail(String email) {
        //TODO impl method body


        try {
            return entityManager.createNamedQuery(TodoUser.FIND_TODO_USER_BY_EMAIL, TodoUser.class)
                    .setParameter("email", email).getSingleResult();

        } catch (NonUniqueResultException | NoResultException e) {
            return null;
        }


    }

    public List<TodoUser> findAllTodoUsers() {

        return entityManager.createNamedQuery(TodoUser.FIND_ALL_TODO_USERS, TodoUser.class).getResultList();
    }


    public TodoUser findTodoUserById(Long id) {

        try {
            return entityManager.createNamedQuery(TodoUser.FIND_TODO_USER_BY_ID, TodoUser.class)
                    .setParameter("id", id).setParameter("email", securityContext.getUserPrincipal().getName()).getSingleResult();
        } catch (NonUniqueResultException | NoResultException e) {
            return null;
        }

    }

    public TodoUser findTodoUser(Long id) { //Todo- discard for above method
        return entityManager.find(TodoUser.class, id);
    }

    public Collection<TodoUser> findTodoUsersByName(String name) {

        return entityManager.createNamedQuery(TodoUser.FIND_TODO_BY_NAME, TodoUser.class)
                .setParameter("name", "%" + name + "%").getResultList(); //jo
    }


    public List<Todo> findAllTodos() {
        return entityManager.createNamedQuery(Todo.FIND_ALL_TODOS_BY_ONWER_EMAIL, Todo.class)
                .setParameter("email", securityContext.getUserPrincipal().getName()).getResultList();
    }

    public List countTodoUserByEmail(String email) {
        List resultList = entityManager.createNativeQuery("select count (id) from TodoUserTable where  exists (select id from TodoUserTable where email = ?)")
                .setParameter(1, email).getResultList();

        return resultList;
    }


    public List countTodoUser(Long id, String email) {
        return entityManager.createNativeQuery("select count (id) from TodoUserTable where  exists (select id from TodoUserTable where email = ?1 and id = ?2)")
                .setParameter(1, email).setParameter(2, id).getResultList();
    }


    public Todo findTodoById(Long id) {
        List<Todo> resultList = entityManager.createQuery("select t from Todo t where t.id = :id and t.todoOwner.email = :email", Todo.class)
                .setParameter("id", id).setParameter("email", securityContext.getUserPrincipal().getName())
                .getResultList();

        if (!resultList.isEmpty()) {
            return resultList.get(0);
        }

        return null;
    }


    public void markTodoAsCompleted(Long id) {
//        entityManager.createQuery("update Todo t set t.completed = true ").executeUpdate();

        Todo todoById = findTodoById(id);

        if (todoById != null) {
            todoById.setCompleted(true);
            entityManager.merge(todoById);
        }
    }



    private List<Todo> getTodoByState(boolean state) {
        return entityManager.createQuery("select t from Todo t where t.todoOwner.email = :email and t.completed = :state ", Todo.class)
                .setParameter("email", securityContext.getUserPrincipal().getName()).setParameter("state", state)
                .getResultList();
    }


    public List<Todo> getAllCompletedTodos() {
        return getTodoByState(true);
    }

    public List<Todo> getUncompletedTodos() {
        return getTodoByState(false);
    }

    public List<Todo> getTodosByDueDate(LocalDate dueDate) {
      return   entityManager.createQuery("select t from Todo t where t.todoOwner.email = :email and t.dueDate = :dueDate", Todo.class)
                .setParameter("email", securityContext.getUserPrincipal().getName())
                .setParameter("dueDate", dueDate).getResultList();
    }



    public void markTodoAsArchived(Long id) {
//        entityManager.createQuery("update Todo t set t.completed = true ").executeUpdate();

        Todo todoById = findTodoById(id);

        if (todoById != null) {
            todoById.setArchived(true);
            entityManager.merge(todoById);
        }
    }



    //Change 2
    public boolean authenticateUser(String email, String plainTextPassword) {

        TodoUser todoUser = findTodoUserByEmail(email);

        if (todoUser != null) {
            return securityUtil.passwordsMatch(todoUser.getPassword(), todoUser.getSalt(), plainTextPassword);
        }
        return false;

    }



}
