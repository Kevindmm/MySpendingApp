package com.kevindmm.spendingapp.dto;

import java.util.Objects;

public class LoginResponseDTO {
    private String token;
    private String username;
    private String refreshToken;

    public LoginResponseDTO(){}

    public LoginResponseDTO(String token, String refreshToken, String username ){
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username; // Fixed to email
    }

    public String getToken(){
        return token;
    }

    public String getUsername(){
        return username;
    }

    public String getRefreshToken(){
        return refreshToken;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        LoginResponseDTO that = (LoginResponseDTO) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(refreshToken, that.refreshToken) &&
                Objects.equals(username, that.username);
    }


    @Override
    public int hashCode(){
        return Objects.hash(token, username, refreshToken);
    }

    @Override
    public String toString(){
        return "LoginResponseDTO{" +
                "token='***REDACTED***'" +
                ", refreshToken='***REDACTED***'" +
                ", username='" + username + '\'' +
                '}';
    }

}