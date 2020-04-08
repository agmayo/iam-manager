package org.acme.iam.manager.dto;

import java.util.List;

public class UserRegisterRequest {
    private String username;
    private String email;
    private List<Credential> credentials;
    private boolean enabled;

    public UserRegisterRequest(String username, String email, List<Credential> credentials, boolean enabled) {
        this.username = username;
        this.email = email;
        this.credentials = credentials;
        this.enabled = enabled;
    }

    public UserRegisterRequest() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Credential> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<Credential> credentials) {
        this.credentials = credentials;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    

}