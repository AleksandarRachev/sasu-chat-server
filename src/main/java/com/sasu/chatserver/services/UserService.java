package com.sasu.chatserver.services;

import com.sasu.chatserver.config.security.CustomUserDetailsService;
import com.sasu.chatserver.dtos.user.UserLoginRequest;
import com.sasu.chatserver.dtos.user.UserLoginResponse;
import com.sasu.chatserver.dtos.user.UserRegisterRequest;
import com.sasu.chatserver.entity.User;
import com.sasu.chatserver.excepions.ElementAlreadyExistsException;
import com.sasu.chatserver.excepions.ElementNotPresentException;
import com.sasu.chatserver.excepions.PasswordsNotMatchingException;
import com.sasu.chatserver.repositories.UserRepository;
import com.sasu.chatserver.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final String USER_EXISTS = "User already registered";
    private static final String PASSWORDS_NOT_MATCHING = "Passwords not matching";
    private static final String WRONG_CREDENTIALS = "Wrong credentials";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String INVALID_FIELDS = "Some of the fields are empty!";

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager,
                       CustomUserDetailsService userDetailsService, JwtUtil jwtUtil, ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword()));

        User user = userRepository.findByUsername(userLoginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException(WRONG_CREDENTIALS));
        if (passwordEncoder.matches(user.getPassword(), userLoginRequest.getPassword())) {
            throw new BadCredentialsException(WRONG_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user);
        UserLoginResponse userLoginResponse = modelMapper.map(user, UserLoginResponse.class);
        userLoginResponse.setToken(token);
        return userLoginResponse;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ElementNotPresentException(USER_NOT_FOUND));
    }

    public UserLoginResponse register(UserRegisterRequest userRegisterRequest) {
        Optional<User> user = userRepository.findByUsername(userRegisterRequest.getUsername());
        validateRegisterInfo(userRegisterRequest, user);

        User userToBeRegistered = new User(userRegisterRequest.getUsername()
                , passwordEncoder.encode(userRegisterRequest.getPassword()), User.Role.USER);

        UserLoginResponse userLoginResponse = modelMapper
                .map(userRepository.save(userToBeRegistered), UserLoginResponse.class);
        String token = jwtUtil.generateToken(userToBeRegistered);
        userLoginResponse.setToken(token);

        return userLoginResponse;
    }

    private void validateRegisterInfo(UserRegisterRequest userRegisterRequest, Optional<User> user) {
        if (user.isPresent()) {
            throw new ElementAlreadyExistsException(USER_EXISTS);
        }

        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getRepeatPassword())) {
            throw new PasswordsNotMatchingException(PASSWORDS_NOT_MATCHING);
        }
    }
}
