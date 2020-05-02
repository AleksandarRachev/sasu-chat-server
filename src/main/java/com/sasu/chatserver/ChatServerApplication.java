package com.sasu.chatserver;

import com.sasu.chatserver.chat.ChatServer;
import com.sasu.chatserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ChatServerApplication {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(ChatServerApplication.class, args);
	}

	@PostConstruct
	public void init() {
		ChatServer.startChat(userService);
	}

}
