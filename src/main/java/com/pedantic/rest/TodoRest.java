package com.pedantic.rest;


import com.pedantic.config.SecureAuth;
import com.pedantic.entity.Todo;
import com.pedantic.service.PersistenceService;
import com.pedantic.service.QueryService;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.time.LocalDate;

@Path("todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecureAuth
public class TodoRest {

    @Inject
    private PersistenceService persistenceService;
    @Inject
    private QueryService queryService;

    @Context
    private UriInfo uriInfo;


    @POST
    @Path("create")
    public Response createTodo(Todo todo) {

        persistenceService.saveTodo(todo);

        //Return a direct resource path to the newly created Todo object
        //Return a direct resource path to a list of Todos

        URI todoByIdPath = uriInfo.getBaseUriBuilder().path(TodoRest.class).path(todo.getId().toString()).build();//api/v1/todo/2

        URI listPath = uriInfo.getBaseUriBuilder().path(TodoRest.class).path(TodoRest.class, "listTodo").build();

        JsonObject jsonObject = Json.createObjectBuilder().add("_links", Json.createArrayBuilder().add(Json.createObjectBuilder()
                .add("_self", todoByIdPath.toString()).add("_others", listPath.toString()).build()).build()).build();


        //{"_links": [{"_self":"", "_others": ""}]}

        return Response.ok(jsonObject.toString()).header("links", jsonObject.toString()).build();
    }


    @GET
    @Path("{id}") //api/v1/todo/1
    public Response findById(@NotNull @PathParam("id") Long id) {

        return Response.ok(queryService.findTodoById(id)).build();
    }

    @GET
    @Path("list")
    public Response listTodo() {

        return Response.ok(queryService.findAllTodos()).build();

    }

    @GET
    @Path("find")
    public Response findTodoById(@NotNull @QueryParam("id") Long id) {
       CacheControl cacheControl = new CacheControl();

        cacheControl.setPrivate(true);
        cacheControl.setMaxAge(5);
        cacheControl.setNoStore(true);

        return Response.ok(queryService.findTodoById(id)).cacheControl(cacheControl).build();
    }


    @PUT
    @Path("mark")
    public Response markTodoAsCompleted(@NotNull @QueryParam("id") Long id) {
        queryService.markTodoAsCompleted(id);
        return Response.ok().build();
    }


    @GET
    @Path("completed")
    public Response getAllCompleted() {
        return Response.ok(queryService.getAllCompletedTodos()).build();
    }


    @GET
    @Path("uncompleted")
    public Response getAllUncompleted() {
        return Response.ok(queryService.getUncompletedTodos()).build();
    }

    @GET
    @Path("due-date")//2018-10-76
    public Response getTodoByDueDate(@NotNull @QueryParam("date") @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String date) {
        LocalDate dueDate = LocalDate.parse(date);

        return Response.ok(queryService.getTodosByDueDate(dueDate)).build();

    }

    @PUT
    @Path("archive")
    public Response archiveTodo(@NotNull @QueryParam("id") Long id) {
        queryService.markTodoAsArchived(id);

        return Response.ok().build();
    }


}
