package com.cinque.ojtg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cinque.ojtg.dao.RoleDao;
import com.cinque.ojtg.dao.UserDao;
import com.cinque.ojtg.dto.UserDto;
import com.cinque.ojtg.model.RoleType;
import com.cinque.ojtg.model.User;
import com.cinque.ojtg.service.UserService;

@Transactional
@Service(value = "userService")
class UserServiceImpl implements UserDetailsService, UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		User user = userDao.findByUsername(userId);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				getAuthority());
	}

	private List<GrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}

	public List<UserDto> findAll() {
		List list = new ArrayList();
		userDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public User findOne(long id) {
		return userDao.findById(id).get();
	}

	@Override
	public void delete(long id) {
		userDao.deleteById(id);
	}

	@Override
	public UserDto save(UserDto userDto) {
		User userWithDuplicateUsername = userDao.findByUsername(userDto.getUsername());
		if (userWithDuplicateUsername != null) {
			System.out.println(String.format("Duplicate username %", userDto.getUsername()));
			throw new RuntimeException("Duplicate username.");
		}
		User userWithDuplicateEmail = userDao.findByEmail(userDto.getEmail());
		if (userWithDuplicateEmail != null) {
			System.out.println(String.format("Duplicate email %", userDto.getEmail()));
			throw new RuntimeException("Duplicate email.");
		}
		User user = new User();
		user.setEmail(userDto.getEmail());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setUsername(userDto.getUsername());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		List<RoleType> roleTypes = new ArrayList<>();
		if (userDto.getRole() != null) {
			userDto.getRole().stream().map(role -> roleTypes.add(RoleType.valueOf(role)));
			user.setRoles(roleDao.find(userDto.getRole()));
		}
		else {
			user.setRoles(null);
		}
		user.setSecret(userDto.getSecret());
		userDao.save(user);
		return userDto;
	}

	@Override
	public User getUserDetails(String username) {
		User user = userDao.findByUsername(username);
		return user;
	}
}
