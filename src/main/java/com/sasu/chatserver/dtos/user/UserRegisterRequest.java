package com.sasu.chatserver.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "Username must not be empty!")
    private String username;

    @NotBlank(message = "Password must not be empty!")
    private String password;

    @NotBlank(message = "Repeat password must not be empty!")
    private String repeatPassword;

}
