package com.sasu.chatserver.dtos.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sasu.chatserver.entity.User;
import lombok.*;
import org.java_websocket.WebSocket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ChatUser {

    private String token;

    private String uid;

    private String username;

    private User.Role role;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private WebSocket webSocket;

}
