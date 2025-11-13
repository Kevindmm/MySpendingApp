package com.kevindmm.spendingapp.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record LoginRequestDTO(
    //Using Record to reduce boilerplate code compared to a traditional class

    @NotBlank @Email (message = "Valid Email is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
){}