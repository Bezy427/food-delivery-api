package com.bezy.foodapi.service;

import com.bezy.foodapi.entity.UserEntity;
import com.bezy.foodapi.io.UserRequest;
import com.bezy.foodapi.io.UserResponse;
import com.bezy.foodapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements  UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private AuthFacade authenticationFacade;

    @Override
    public UserResponse registerUser(UserRequest request) {
        UserEntity newUser = convertToEntity(request);

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }


    @Override
    public String findByUserId() {
        String loggedInUserEmail = authenticationFacade.getAuthentication().getName();
        UserEntity loggedInUser = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return loggedInUser.getId();
    }

    private UserEntity convertToEntity(UserRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword())   // RAW password here
                .name(request.getName())
                .build();
    }


    private UserResponse convertToResponse(UserEntity response) {
        return UserResponse.builder()
                .id(response.getId())
                .email(response.getEmail())
                .name(response.getName())
                .build();
    }
}
