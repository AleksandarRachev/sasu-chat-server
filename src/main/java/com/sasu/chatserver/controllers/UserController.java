package com.sasu.chatserver.controllers;

import com.sasu.chatserver.dtos.user.UserLoginRequest;
import com.sasu.chatserver.dtos.user.UserLoginResponse;
import com.sasu.chatserver.dtos.user.UserRegisterRequest;
import com.sasu.chatserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        return ResponseEntity.ok(userService.login(userLoginRequest));
    }

    @PostMapping
    public ResponseEntity<UserLoginResponse> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        return ResponseEntity.ok(userService.register(userRegisterRequest));
    }

    @GetMapping
    public String a(){
        return "asdasd";
    }

}
