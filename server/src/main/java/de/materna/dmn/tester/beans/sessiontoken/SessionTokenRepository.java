package de.materna.dmn.tester.beans.sessiontoken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
public class SessionTokenRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	public SessionToken findById(long id) {
		transaction.begin();
		SessionToken token = em.find(SessionToken.class, id);
		transaction.commit();
		return Optional.ofNullable(token).get();
	}

	public List<SessionToken> findByUserUuid(String userUuid) {
		List<SessionToken> tokensFound = findByFilter(new UserUuidFilter(userUuid));
		return tokensFound;
	}

	public SessionToken findCurrentByUser(User user) {
		return findCurrentByUserUuid(user.getUuid());
	}

	public SessionToken findCurrentByUserUuid(String userUuid) {
		List<SessionToken> tokens = findByUserUuid(userUuid);
		Collections.sort(tokens, new SessionTokenComparator());
		return tokens.get(0);
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

	public SessionToken save(SessionToken token) {
		transaction.begin();
		em.persist(token);
		transaction.commit();
		return findById(token.getId()) != null ? token : null;
	}

	public SessionToken update(long id) throws JAXRException {
		return save(findById(id));
	}

	public SessionToken update(SessionToken token) throws JAXRException {
		if (token != null) {
			token.setLastUpdate(LocalDateTime.now());
			return save(token);
		}
		return null;
	}

	public boolean delete(SessionToken token) {
		transaction.begin();
		em.remove(em.contains(token) ? token : em.merge(token));
		transaction.commit();
		return findById(token.getId()) == null;
	}
}
