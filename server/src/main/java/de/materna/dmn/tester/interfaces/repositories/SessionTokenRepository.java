package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import javax.xml.registry.JAXRException;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.user.User;

public interface SessionTokenRepository {

	List<SessionToken> findAll();

	List<SessionToken> findAllByUserUuid(String userUuid);

	SessionToken findByUuid(String tokenUuid);

	SessionToken findCurrentByUser(User user);

	SessionToken findCurrentByUserUuid(String userUuid);

	SessionToken put(SessionToken token);

	SessionToken update(SessionToken token) throws JAXRException;

	boolean delete(SessionToken token);
}