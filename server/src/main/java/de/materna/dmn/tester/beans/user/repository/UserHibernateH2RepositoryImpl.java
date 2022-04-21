package de.materna.dmn.tester.beans.user.repository;

import java.util.ArrayList;
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
import de.materna.dmn.tester.beans.sessiontoken.repository.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.UserFilter;
import de.materna.dmn.tester.beans.user.filter.EmailFilter;
import de.materna.dmn.tester.beans.user.filter.UsernameFilter;
import de.materna.dmn.tester.interfaces.repositories.hibernate.h2.UserHibernateH2Repository;

public class UserHibernateH2RepositoryImpl implements UserHibernateH2Repository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	@Override
	public List<User> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> rootEntry = cq.from(User.class);
		CriteriaQuery<User> all = cq.select(rootEntry);
		TypedQuery<User> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public User findByUuid(UUID uuid) {
		transaction.begin();
		User user = em.find(User.class, uuid);
		transaction.commit();
		return Optional.ofNullable(user).get();
	}

	@Override
	public User findByEmail(String email) {
		List<User> usersFound = findByFilter(new EmailFilter(email));
		return usersFound.size() == 1 ? usersFound.get(0) : null;
	}

	@Override
	public User findByUsername(String username) {
		List<User> usersFound = findByFilter(new UsernameFilter(username));
		return usersFound.size() == 1 ? usersFound.get(0) : null;
	}

	@Override
	public User findBySessionToken(String tokenString) {
		SessionToken token = new SessionTokenHibernateH2RepositoryImpl().findBySessionToken(tokenString);
		return findByUuid(token.getUserUuid());
	}

	@Override
	public User update(String email, String username, String firstname, String lastname, String password)
			throws JAXRException {
		User user = findByEmail(email);
		if (user != null) {
			user.setEmail(email);
			user.setUsername(username);
			user.setFirstname(firstname);
			user.setLastname(lastname);
			user.setPassword(password);
			return put(user);
		}
		return null;
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
	public User register(String email, String username, String password, String lastname, String firstname) {
		if (findByEmail(email) == null) {
			User user = new User(email, username, password, lastname, firstname);
			return put(user);
		}
		return null;
	}

	public List<User> findByFilter(UserFilter... filterArray) {
		CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> cquery = cbuilder.createQuery(User.class);
		Root<User> userRoot = cquery.from(User.class);
		cquery.select(userRoot);
		List<Predicate> predicates = new ArrayList<>();
		for (UserFilter filter : filterArray) {
			predicates.add(filter.toPredicate(userRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<User> query = em.createQuery(cquery);
		return query.getResultList();
	}

}
