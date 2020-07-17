package com.cinque.ojtg.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cinque.ojtg.dto.UserDto;
import com.cinque.ojtg.model.User;
import com.cinque.ojtg.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public List<UserDto> listUser() {
		return userService.findAll();
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public UserDto create(@RequestBody UserDto user) {
		return userService.save(user);
	}

	/**
	 * 
	 * @param Username
	 * @return
	 */
	@RequestMapping(value="/userdetails/{username}", method = RequestMethod.GET)
	public User getUserDetails(@PathVariable(value = "username") String Username) {
		return userService.getUserDetails(Username);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable(value = "id") Long id) {
		userService.delete(id);
		return "success";
	}

}
