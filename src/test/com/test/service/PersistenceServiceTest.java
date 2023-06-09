package com.test.service;

import com.test.entity.Todo;
import com.test.entity.TodoUser;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.*;

//@RunWith(Arquillian.class)
public class PersistenceServiceTest {

    @Inject
    QueryService queryService;

    @Inject
    PersistenceService persistenceService;

    @Inject
    MySession mySession;

    @Inject
    Todo todo;

    @Inject
    TodoUser todoUser;


//    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(PersistenceService.class.getPackage())
                .addPackage(Todo.class.getPackage())
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

//    @Before
    public void setUp() throws Exception {

        mySession.setEmail("karim@gmail.com");

        todoUser.setEmail("karim@gmail.com");
        todoUser.setPassword(UUID.randomUUID().toString());
        todoUser.setFullName("karim");

        todo.setDueDate(LocalDate.of(2018, 10, 31));
        todo.setTask("Jakarta EE todo app");
//        todo.setTodoOwner(todoUser);


    }

//    @After
    public void tearDown() throws Exception {
    }

//    @Test
    public void saveTodoUser() {

        persistenceService.saveTodoUser(todoUser);

        persistenceService.saveTodo(todo);




        assertNotNull(todo.getId());


    }

//    @Test
    public void saveTodo() {

    }
}
