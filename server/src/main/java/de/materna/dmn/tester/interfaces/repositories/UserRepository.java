package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.servlets.exceptions.database.UserNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.registration.EmailInUseException;
import de.materna.dmn.tester.servlets.exceptions.registration.UsernameInUseException;

public interface UserRepository {

	List<User> getAll();

	User getByUuid(String userUuid) throws UserNotFoundException;

	User getByEmail(String email) throws UserNotFoundException;

	User getByUsername(String username) throws UserNotFoundException;

	User getByJwt(String jwt) throws UserNotFoundException;

	User put(User user) throws Exception;

	boolean delete(User user);

	User register(String email, String username, String password) throws EmailInUseException, UsernameInUseException;

	User register(String email, String username, String password, String lastname, String firstname)
			throws EmailInUseException, UsernameInUseException;
}