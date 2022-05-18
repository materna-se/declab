package de.materna.dmn.tester.beans.sessiontoken;

import java.time.LocalDate;
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
import javax.xml.registry.JAXRException;

import de.materna.dmn.tester.beans.sessiontoken.filter.UserUuidFilter;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.interfaces.filters.SessionTokenFilter;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;

public class SessionTokenHibernateH2RepositoryImpl implements SessionTokenRepository {

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private final EntityManager em = entityManagerFactory.createEntityManager();
	private final EntityTransaction transaction = em.getTransaction();

	@Override
	public List<SessionToken> findAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<SessionToken> cq = cb.createQuery(SessionToken.class);
		final Root<SessionToken> rootEntry = cq.from(SessionToken.class);
		final CriteriaQuery<SessionToken> all = cq.select(rootEntry);
		final TypedQuery<SessionToken> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public List<SessionToken> findAllByUserUuid(String userUuid) {
		return findByFilter(new UserUuidFilter(userUuid));
	}

	@Override
	public SessionToken findByUuid(String tokenUuid) {
		transaction.begin();
		final SessionToken sessionToken = em.find(SessionToken.class, tokenUuid);
		transaction.commit();
		return Optional.ofNullable(sessionToken).get();
	}

	@Override
	public SessionToken findCurrentByUser(User user) {
		return findCurrentByUserUuid(user.getUuid());
	}

	@Override
	public SessionToken findCurrentByUserUuid(String userUuid) {
		final List<SessionToken> tokens = findAllByUserUuid(userUuid);
		Collections.sort(tokens, new SessionTokenComparator());
		return tokens.get(0);
	}

	@Override
	public SessionToken put(SessionToken token) {
		transaction.begin();
		em.persist(token);
		transaction.commit();
		return findByUuid(token.getUuid()) != null ? token : null;
	}

	@Override
	public SessionToken update(SessionToken token) throws JAXRException {
		if (token != null) {
			token.setLastUpdate(LocalDate.now());
			return put(token);
		}
		return null;
	}

	@Override
	public boolean delete(SessionToken sessionToken) {
		transaction.begin();
		em.remove(em.contains(sessionToken) ? sessionToken : em.merge(sessionToken));
		transaction.commit();
		return findByUuid(sessionToken.getUuid()) == null;
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
