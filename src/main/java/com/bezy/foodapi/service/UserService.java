package com.bezy.foodapi.service;

import com.bezy.foodapi.io.UserRequest;
import com.bezy.foodapi.io.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request);
    String findByUserId();
}
