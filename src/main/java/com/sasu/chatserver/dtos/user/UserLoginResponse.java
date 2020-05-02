package com.sasu.chatserver.dtos.user;

import com.sasu.chatserver.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {

    private String token;

    private String uid;

    private String username;

    private User.Role role;

}
