package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.registry.JAXRException;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.user.User;

@ApplicationScoped
public interface SessionTokenRepository {

	List<SessionToken> findAll();

	List<SessionToken> findAllByUserUuid(UUID userUuid);

	SessionToken findByUuid(UUID tokenUuid);

	SessionToken findCurrentByUser(User user);

	SessionToken findCurrentByUserUuid(UUID userUuid);

	SessionToken put(SessionToken token);

	SessionToken update(SessionToken token) throws JAXRException;

	boolean delete(SessionToken token);
}
