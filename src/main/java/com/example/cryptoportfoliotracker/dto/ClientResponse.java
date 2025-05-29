package com.example.cryptoportfoliotracker.dto;

public class ClientResponse {
    private Long id;
    private String username;
    private String name;
    private String email;

    public ClientResponse(Long id, String username, String name, String email) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
