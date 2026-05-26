package com.kaninnyc.controller;

import com.kaninnyc.model.AppUser;
import org.springframework.http.ResponseEntity;

public class AuthorizationResult {
    private AppUser user;
    private ResponseEntity<Object> responseEntity;

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public ResponseEntity<Object> getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<Object> responseEntity) {
        this.responseEntity = responseEntity;
    }

    public boolean isSuccess() {
        return user != null;
    }
}
