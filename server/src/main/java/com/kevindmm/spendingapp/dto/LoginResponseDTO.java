package com.kevindmm.spendingapp.dto;

import java.util.Objects;

public class LoginResponseDTO {
    private String token;
    private String username;

    public LoginResponseDTO(){}

    public LoginResponseDTO(String token, String username){
        this.token = token;
        this.username = username; // Fixed to email
    }

    public String getToken(){
        return token;
    }

    public String getUsername(){
        return username;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        LoginResponseDTO that = (LoginResponseDTO) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode(){
        return Objects.hash(token, username);
    }

    @Override
    public String toString(){
        return "LoginResponseDTO{" +
                "token='***REDACTED***'" +
                ", username='" + username + '\'' +
                '}';
    }
}
