package com.cinque.ojtg.service;

import java.util.List;


import com.cinque.ojtg.dto.UserDto;
import com.cinque.ojtg.model.User;


public interface UserService {

	UserDto save(UserDto user);
    List<UserDto> findAll();
    User findOne(long id);
    void delete(long id);
    User getUserDetails(String username);
}
