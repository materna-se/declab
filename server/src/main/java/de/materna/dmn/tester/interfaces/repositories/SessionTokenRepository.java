package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.user.User;

public interface SessionTokenRepository {

	List<SessionToken> findAll();

	List<SessionToken> findAllByUserUuid(String userUuid);

	SessionToken findByUuid(String tokenUuid);

	SessionToken findByJwt(String jwt);

	SessionToken findCurrentByUser(User user);

	SessionToken findCurrentByUserUuid(String userUuid);

	SessionToken put(SessionToken sessionToken);

	SessionToken update(SessionToken sessionToken);

	boolean delete(SessionToken sessionToken);
}