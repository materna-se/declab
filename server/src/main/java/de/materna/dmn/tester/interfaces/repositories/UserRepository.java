package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.user.User;

public interface UserRepository {

	List<User> findAll();

	User findByUuid(String userUuid);

	User findByEmail(String email);

	User findByUsername(String username);

	User findBySessionToken(String tokenUuid);

	User put(User user);

	boolean delete(User user);

	User register(String email, String username, String password);

	User register(String email, String username, String password, String lastname, String firstname);
}