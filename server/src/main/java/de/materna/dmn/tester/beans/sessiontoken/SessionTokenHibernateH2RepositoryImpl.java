package de.materna.dmn.tester.beans.sessiontoken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.sessiontoken.filter.JwtFilter;
import de.materna.dmn.tester.beans.sessiontoken.filter.UserUuidFilter;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.interfaces.filters.SessionTokenFilter;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;
import io.jsonwebtoken.JwtException;

public class SessionTokenHibernateH2RepositoryImpl implements SessionTokenRepository {

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private final EntityManager em = entityManagerFactory.createEntityManager();
	private final EntityTransaction transaction = em.getTransaction();

	@Override
	public List<SessionToken> getAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<SessionToken> cq = cb.createQuery(SessionToken.class);
		final Root<SessionToken> rootEntry = cq.from(SessionToken.class);
		final CriteriaQuery<SessionToken> all = cq.select(rootEntry);
		final TypedQuery<SessionToken> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public List<SessionToken> getAllByUserUuid(String userUuid) {
		return findByFilter(new UserUuidFilter(userUuid));
	}

	@Override
	public SessionToken getByUuid(String uuid) throws SessionTokenNotFoundException {
		try {
			transaction.begin();
			final SessionToken sessionToken = em.find(SessionToken.class, uuid);
			transaction.commit();
			return Optional.ofNullable(sessionToken).isPresent() ? Optional.ofNullable(sessionToken).get() : null;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw new SessionTokenNotFoundException("SessionToken not found by Uuid : " + uuid);
		}
	}

	@Override
	public SessionToken getByJwt(String jwt) throws JwtException {
		Jwt.verify(jwt);
		final List<SessionToken> sessionTokens = findByFilter(new JwtFilter(jwt));
		return sessionTokens.size() == 1 ? sessionTokens.get(0) : null;
	}

	@Override
	public SessionToken getCurrentByUser(User user) {
		return getCurrentByUserUuid(user.getUuid());
	}

	@Override
	public SessionToken getCurrentByUserUuid(String userUuid) {
		final List<SessionToken> tokens = getAllByUserUuid(userUuid);
		Collections.sort(tokens, new SessionTokenComparator());
		return tokens.get(0);
	}

	@Override
	public SessionToken put(SessionToken sessionToken) {
		try {
			transaction.begin();
			em.persist(sessionToken);
			transaction.commit();
			return getByUuid(sessionToken.getUuid()) != null ? sessionToken : null;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return null;
		}
	}

	@Override
	public SessionToken update(SessionToken sessionToken) {
		if (sessionToken != null) {
			sessionToken.setLastUpdate(LocalDateTime.now());
			sessionToken.setExpiration(SessionToken.addWorkdays(LocalDateTime.now(), 3));
			return put(sessionToken);
		}
		return null;
	}

	@Override
	public boolean delete(SessionToken sessionToken) {
		try {
			transaction.begin();
			em.remove(em.contains(sessionToken) ? sessionToken : em.merge(sessionToken));
			transaction.commit();
			return getByUuid(sessionToken.getUuid()) == null;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return false;
		}
	}

	public List<SessionToken> findByFilter(SessionTokenFilter... filterArray) {
		final CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		final CriteriaQuery<SessionToken> cquery = cbuilder.createQuery(SessionToken.class);
		final Root<SessionToken> tokenRoot = cquery.from(SessionToken.class);
		cquery.select(tokenRoot);
		final List<Predicate> predicates = new ArrayList<>();
		for (final SessionTokenFilter filter : filterArray) {
			predicates.add(filter.toPredicate(tokenRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		final TypedQuery<SessionToken> query = em.createQuery(cquery);
		return query.getResultList();
	}
}
