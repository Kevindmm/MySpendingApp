package com.kevindmm.spendingapp.dto;

public record UserProfileDTO(
    String id,
    String email,
    String firstName,
    String lastName,
    String createdAt
) {}