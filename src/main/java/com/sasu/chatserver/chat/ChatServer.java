package com.sasu.chatserver.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasu.chatserver.dtos.chat.ChatMessage;
import com.sasu.chatserver.dtos.chat.ChatUser;
import com.sasu.chatserver.dtos.chat.MessageType;
import com.sasu.chatserver.dtos.chat.UserActivity;
import com.sasu.chatserver.entity.User;
import com.sasu.chatserver.services.UserService;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatServer extends WebSocketServer {

    private static final String CHAT_SERVER_STARTED = "Chat server on port(s): ";
    private static final String WEB_SOCKET_INFO = " (ws) with context path ''";
    private static final int CHAT_PORT = 9000;

    static Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private Set<ChatUser> users;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    private final ModelMapper modelMapper;

    @Autowired
    public ChatServer(UserService userService) {
        super(new InetSocketAddress(CHAT_PORT));
        this.users = new HashSet<>();
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.modelMapper = new ModelMapper();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info("Connection open " + webSocket.getRemoteSocketAddress().getAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String message, boolean b) {
//        connections.remove(webSocket);
        this.users.removeIf(chatUser -> chatUser.getWebSocket().equals(webSocket));
        logger.info("Connection closed " + webSocket.getRemoteSocketAddress().getAddress());
        try {
            broadcastMessage(new ChatMessage(), MessageType.USER_LEFT);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
            switch (chatMessage.getType()) {
                case USER_JOINED: {
                    User user = userService.getUserByUsername(chatMessage.getUser().getUsername());
                    ChatUser chatUser = modelMapper.map(user, ChatUser.class);
                    chatUser.setWebSocket(webSocket);
                    this.users.add(chatUser);
                    broadcastMessage(chatMessage, MessageType.USER_JOINED);
                }
                break;
                case USER_LEFT: {
                    this.users.removeIf(chatUser1 -> chatUser1.getWebSocket().equals(webSocket));
                    broadcastMessage(chatMessage, MessageType.USER_LEFT);
                }
                break;
                case TEXT: {
                    broadcastMessage(chatMessage, MessageType.TEXT);
                }
                break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(ChatMessage chatMessage, MessageType messageType) throws JsonProcessingException {
        if (messageType == MessageType.USER_LEFT || messageType == MessageType.USER_JOINED) {
            for (ChatUser user : this.users) {
                user.getWebSocket().send(objectMapper.writeValueAsString(
                        new UserActivity(messageType, chatMessage.getUser(), this.users)));
            }
        } else {
            for (ChatUser user : this.users
                    .stream()
                    .filter(user -> (user.getUid().equals(chatMessage.getUserTo().getUid())
                            || (user.getUid().equals(chatMessage.getUser().getUid())))).collect(Collectors.toList())) {

                user.getWebSocket().send(objectMapper.writeValueAsString(chatMessage));
            }
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        this.users.removeIf(chatUser1 -> chatUser1.getWebSocket().equals(webSocket));
        logger.error(e.getMessage());
    }

    public static void startChat(UserService userService) {
        new ChatServer(userService).start();
        logger.info(CHAT_SERVER_STARTED + CHAT_PORT + WEB_SOCKET_INFO);
    }
}
