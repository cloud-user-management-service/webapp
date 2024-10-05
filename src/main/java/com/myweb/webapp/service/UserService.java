package com.myweb.webapp.service;

import com.myweb.webapp.dto.UserRequestDto;
import com.myweb.webapp.entity.User;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface UserService {
    User createUser(Map<String, Object> userRequest);


    ResponseEntity updateUser(Map<String, Object> userUpdateRequest, User currentUser);

}
