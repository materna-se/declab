package de.materna.dmn.tester.beans.user.repository;

import java.util.List;
import java.util.UUID;

import javax.xml.registry.JAXRException;

import de.materna.dmn.tester.beans.user.User;

public interface UserRepository {

	List<User> findAll();

	User findByUuid(UUID uuid);

	User findByEmail(String email);

	User findByUsername(String username);

	User findBySessionToken(String tokenString);

	User put(User user);

	User update(String email, String username, String firstname, String lastname, String password) throws JAXRException;

	boolean delete(User user);

	User register(String email, String username, String password);

	User register(String email, String username, String password, String lastname, String firstname);
}
