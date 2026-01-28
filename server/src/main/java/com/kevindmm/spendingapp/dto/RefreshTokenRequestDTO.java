package com.kevindmm.spendingapp.dto;

import javax.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
    @NotBlank(message = "Refresh token cannot be blank") String refreshToken
    ) {}