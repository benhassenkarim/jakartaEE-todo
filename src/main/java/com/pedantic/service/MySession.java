package com.pedantic.service;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@SessionScoped
public class MySession implements Serializable {


    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
