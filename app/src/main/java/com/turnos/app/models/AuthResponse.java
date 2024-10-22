package com.turnos.app.models;

public class AuthResponse {
    private String token;
   

    private String id;
    

    private String role;

    

    public AuthResponse(String token, String id, String role) {
        this.token = token;
        this.id = id;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getRole() {
            return role;
        }
    
     public void setRole(String role) {
            this.role = role;
        }
    
}
