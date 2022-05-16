package de.materna.dmn.tester.beans.user;

import java.util.ArrayList;
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

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.filter.EmailFilter;
import de.materna.dmn.tester.beans.user.filter.UsernameFilter;
import de.materna.dmn.tester.interfaces.filters.UserFilter;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;
import de.materna.dmn.tester.interfaces.repositories.UserRepository;

public class UserHibernateH2RepositoryImpl implements UserRepository {
	private final SessionTokenRepository sessionTokenRepository = new SessionTokenHibernateH2RepositoryImpl();

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private final EntityManager em = entityManagerFactory.createEntityManager();
	private final EntityTransaction transaction = em.getTransaction();

	@Override
	public List<User> findAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<User> cq = cb.createQuery(User.class);
		final Root<User> rootEntry = cq.from(User.class);
		final CriteriaQuery<User> all = cq.select(rootEntry);
		final TypedQuery<User> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public User findByUuid(String uuid) {
		transaction.begin();
		final User user = em.find(User.class, uuid);
		transaction.commit();
		return Optional.ofNullable(user).get();
	}

	@Override
	public User findByEmail(String email) {
		final List<User> usersFound = findByFilter(new EmailFilter(email));
		return usersFound.size() == 1 ? usersFound.get(0) : null;
	}

	@Override
	public User findByUsername(String username) {
		final List<User> usersFound = findByFilter(new UsernameFilter(username));
		return usersFound.size() == 1 ? usersFound.get(0) : null;
	}

	@Override
	public User findBySessionToken(String tokenUuid) {
		final SessionToken token = sessionTokenRepository.findByUuid(tokenUuid);
		return findByUuid(token.getUserUuid());
	}

	@Override
	public User put(User user) {
		transaction.begin();
		em.persist(user);
		transaction.commit();
		return findByUuid(user.getUuid()) != null ? user : null;
	}

	@Override
	public boolean delete(User user) {
		transaction.begin();
		em.remove(em.contains(user) ? user : em.merge(user));
		transaction.commit();
		return findByUuid(user.getUuid()) == null;
	}

	@Override
	public User register(String email, String username, String password) {
		return register(email, username, password, "", "");
	}

	@Override
	public User register(String email, String username, String password, String firstname, String lastname) {
		if (findByEmail(email) == null) {
			final User user = new User(email, username, password, firstname, lastname);
			return put(user);
		}
		return null;
	}

	public List<User> findByFilter(UserFilter... filterArray) {
		final CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		final CriteriaQuery<User> cquery = cbuilder.createQuery(User.class);
		final Root<User> userRoot = cquery.from(User.class);
		cquery.select(userRoot);
		final List<Predicate> predicates = new ArrayList<>();
		for (final UserFilter filter : filterArray) {
			predicates.add(filter.toPredicate(userRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		final TypedQuery<User> query = em.createQuery(cquery);
		return query.getResultList();
	}

}
