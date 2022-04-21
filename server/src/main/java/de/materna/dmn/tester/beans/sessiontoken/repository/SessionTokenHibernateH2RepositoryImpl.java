package de.materna.dmn.tester.beans.sessiontoken.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenComparator;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenFilter;
import de.materna.dmn.tester.beans.sessiontoken.filter.TokenFilter;
import de.materna.dmn.tester.beans.sessiontoken.filter.UserUuidFilter;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.interfaces.repositories.hibernate.h2.SessionTokenHibernateH2Repository;

public class SessionTokenHibernateH2RepositoryImpl implements SessionTokenHibernateH2Repository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	@Override
	public List<SessionToken> findAllByUserUuid(UUID userUuid) {
		List<SessionToken> tokensFound = findByFilter(new UserUuidFilter(userUuid));
		return tokensFound;
	}

	@Override
	public SessionToken findById(long id) {
		transaction.begin();
		SessionToken token = em.find(SessionToken.class, id);
		transaction.commit();
		return Optional.ofNullable(token).get();
	}

	@Override
	public SessionToken findCurrentByUser(User user) {
		return findCurrentByUserUuid(user.getUuid());
	}

	@Override
	public SessionToken findCurrentByUserUuid(UUID userUuid) {
		List<SessionToken> tokens = findAllByUserUuid(userUuid);
		Collections.sort(tokens, new SessionTokenComparator());
		return tokens.get(0);
	}

	@Override
	public SessionToken findBySessionToken(String tokenString) {
		List<SessionToken> tokens = findByFilter(new TokenFilter(tokenString));
		return tokens.size() == 1 ? tokens.get(0) : null;
	}

	public List<SessionToken> findByFilter(SessionTokenFilter... filterArray) {
		CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		CriteriaQuery<SessionToken> cquery = cbuilder.createQuery(SessionToken.class);
		Root<SessionToken> tokenRoot = cquery.from(SessionToken.class);
		cquery.select(tokenRoot);
		List<Predicate> predicates = new ArrayList<>();
		for (SessionTokenFilter filter : filterArray) {
			predicates.add(filter.toPredicate(tokenRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<SessionToken> query = em.createQuery(cquery);
		return query.getResultList();
	}

	@Override
	public SessionToken put(SessionToken token) {
		transaction.begin();
		em.persist(token);
		transaction.commit();
		return findById(token.getId()) != null ? token : null;
	}

	@Override
	public SessionToken update(long id) throws JAXRException {
		return put(findById(id));
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
	public boolean delete(SessionToken token) {
		transaction.begin();
		em.remove(em.contains(token) ? token : em.merge(token));
		transaction.commit();
		return findById(token.getId()) == null;
	}
}
