package de.materna.dmn.tester.beans.sessiontoken.repository;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.registry.JAXRException;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.user.User;

@ApplicationScoped
public interface SessionTokenRepository {

	public List<SessionToken> findAllByUserUuid(UUID userUuid);

	public SessionToken findById(long id);

	public SessionToken findCurrentByUser(User user);

	public SessionToken findCurrentByUserUuid(UUID userUuid);

	public SessionToken findBySessionToken(String tokenString);

	public SessionToken put(SessionToken token);

	public SessionToken update(long id) throws JAXRException;

	public SessionToken update(SessionToken token) throws JAXRException;

	public boolean delete(SessionToken token);
}
