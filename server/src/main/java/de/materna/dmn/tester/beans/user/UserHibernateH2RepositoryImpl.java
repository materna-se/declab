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

import de.materna.dmn.tester.beans.sessiontoken.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.filter.EmailFilter;
import de.materna.dmn.tester.beans.user.filter.UsernameFilter;
import de.materna.dmn.tester.interfaces.filters.UserFilter;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;
import de.materna.dmn.tester.interfaces.repositories.UserRepository;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.UserNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.registration.EmailInUseException;
import de.materna.dmn.tester.servlets.exceptions.registration.RegistrationFailureException;
import de.materna.dmn.tester.servlets.exceptions.registration.UsernameInUseException;
import io.jsonwebtoken.JwtException;

public class UserHibernateH2RepositoryImpl implements UserRepository {
	private final SessionTokenRepository sessionTokenRepository = new SessionTokenHibernateH2RepositoryImpl();

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private final EntityManager em = entityManagerFactory.createEntityManager();
	private final EntityTransaction transaction = em.getTransaction();

	@Override
	public List<User> getAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<User> cq = cb.createQuery(User.class);
		final Root<User> rootEntry = cq.from(User.class);
		final CriteriaQuery<User> all = cq.select(rootEntry);
		final TypedQuery<User> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public User getByUuid(String uuid) throws UserNotFoundException {
		try {
			transaction.begin();
			final User user = em.find(User.class, uuid);
			transaction.commit();
			return Optional.ofNullable(user).get();
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw new UserNotFoundException("User not found by UUID : " + uuid);
		}
	}

	@Override
	public User getByEmail(String email) throws UserNotFoundException {
		final List<User> usersFound = findByFilter(new EmailFilter(email));
		if (usersFound.size() == 1) {
			return usersFound.get(0);
		}
		throw new UserNotFoundException("User not found by email address : " + email);
	}

	@Override
	public User getByUsername(String username) throws UserNotFoundException {
		final List<User> usersFound = findByFilter(new UsernameFilter(username));
		if (usersFound.size() == 1) {
			return usersFound.get(0);
		}
		throw new UserNotFoundException("User not found by username : " + username);
	}

	@Override
	public User getByJwt(String jwt) throws JwtException, UserNotFoundException, SessionTokenNotFoundException {
		return getByUuid(sessionTokenRepository.getByJwt(jwt).getUserUuid());
	}

	@Override
	public User put(User user) throws UserNotFoundException {
		try {
			transaction.begin();
			em.persist(user);
			transaction.commit();
			return getByUuid(user.getUuid());
		} catch (final IllegalStateException e) {
			e.printStackTrace();
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
		return null;
	}

	@Override
	public boolean delete(User user) {
		try {
			transaction.begin();
			em.remove(em.contains(user) ? user : em.merge(user));
			transaction.commit();
			getByUuid(user.getUuid());
			return false;
		} catch (final UserNotFoundException e) {
			return true; // all fine!
		}
	}

	@Override
	public User register(String email, String username, String password) throws RegistrationFailureException {
		return register(email, username, password, "", "");
	}

	@Override
	public User register(String email, String username, String password, String firstname, String lastname)
			throws RegistrationFailureException {
		try {
			getByUsername(username);
			throw new UsernameInUseException("Username is already in use : " + username);
		} catch (final UserNotFoundException e1) {
			try {
				getByEmail(email);
				throw new EmailInUseException("Email address is already in use : " + email);
			} catch (final UserNotFoundException e2) {
				try {
					return put(new User(email, username, password, firstname, lastname));
				} catch (final UserNotFoundException e) {
					e.printStackTrace();
					throw new RegistrationFailureException("User could not be added to database : " + username);
				}
			}
		}
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
