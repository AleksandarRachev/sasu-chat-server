package com.sasu.chatserver.dtos.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private ChatUser user;

    private ChatUser userTo;

    private String chat;

    private MessageType type;

}
