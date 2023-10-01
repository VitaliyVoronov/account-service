package com.game.slot.account.controller.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestModel {

    @NotNull(message = "First name cannot be empty")
    @Size(min = 2)
    private String firstName;
    @NotNull(message = "Last name cannot be empty")
    @Size(min = 2)
    private String lastName;
    @NotNull(message = "Email cannot be empty")
    @Email
    private String email;
    @NotNull(message = "Password cannot be empty")
    @Size(min = 8, max = 20, message = "Password should be between ${min} and ${max}")
    private String password;
}
