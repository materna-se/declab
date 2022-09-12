package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;
import io.jsonwebtoken.JwtException;

public interface SessionTokenRepository {

	List<SessionToken> getAll();

	List<SessionToken> getAllByUserUuid(String userUuid);

	SessionToken getByUuid(String tokenUuid) throws SessionTokenNotFoundException;

	SessionToken getByJwt(String jwt) throws JwtException, SessionTokenNotFoundException;

	SessionToken getCurrentByUser(User user) throws SessionTokenNotFoundException;

	SessionToken getCurrentByUserUuid(String userUuid) throws SessionTokenNotFoundException;

	SessionToken put(SessionToken sessionToken) throws SessionTokenNotFoundException;

	SessionToken update(SessionToken sessionToken) throws SessionTokenNotFoundException;

	boolean delete(SessionToken sessionToken);
}