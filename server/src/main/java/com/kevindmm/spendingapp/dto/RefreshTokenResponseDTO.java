package com.kevindmm.spendingapp.dto;

import java.util.Objects;

public class RefreshTokenResponseDTO {

    private String accessToken;
    private String tokenType = "Bearer";

    //Empty constructor for Jackson
    public RefreshTokenResponseDTO() {}

    public RefreshTokenResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType){
        this.tokenType = tokenType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshTokenResponseDTO that = (RefreshTokenResponseDTO) o;
        return Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(tokenType, that.tokenType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, tokenType);
    }

    @Override
    public String toString() {
        return "RefreshTokenResponseDTO{" +
                "accessToken='***REDACTED***'" +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}