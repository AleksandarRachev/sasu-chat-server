package com.sasu.chatserver.dtos.chat;

import com.sasu.chatserver.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserActivity {

    private MessageType type;

    private ChatUser user;

    private Set<ChatUser> users;

}
