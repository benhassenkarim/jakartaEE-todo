package com.pedantic.config;

import com.pedantic.entity.TodoUser;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;



//@Provider //Register with JAX-RS
//@Produces(MediaType.APPLICATION_JSON) //Produces json rep
public class MyTodoUserBodyWriter implements MessageBodyWriter<TodoUser>, MessageBodyReader<TodoUser> {



    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return TodoUser.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(TodoUser todoUser, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {


        JsonObject todoUserJson = Json.createObjectBuilder().add("email", todoUser.getEmail())
                .add("fullName", todoUser.getFullName()).add("id", todoUser.getId()).build();



        OutputStreamWriter streamWriter = new OutputStreamWriter(entityStream);
        streamWriter.write(todoUserJson.toString());
        streamWriter.close();
        entityStream.close();
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return false;
    }

    @Override
    public TodoUser readFrom(Class<TodoUser> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        return null;
    }
}
