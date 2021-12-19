package de.materna.dmn.tester.beans.repositories;

import java.util.ArrayList;
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

import de.materna.dmn.tester.beans.User;
import de.materna.dmn.tester.beans.repositories.filter.UserFilter;
import de.materna.dmn.tester.beans.repositories.filter.userfilter.EmailFilter;

@ApplicationScoped
public class UserRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	public User findByUuid(String uuid) {
		transaction.begin();
		User user = em.find(User.class, uuid);
		transaction.commit();
		return Optional.ofNullable(user).get();
	}

	public User findByEmail(String email) throws JAXRException {
		List<User> usersFound = findByFilter(new EmailFilter(email));
		if (usersFound.size() == 1) {
			return usersFound.get(0);
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

	public boolean save(User user) {
		transaction.begin();
		em.persist(user);
		transaction.commit();
		return findByUuid(user.getUuid()) != null;
	}

	public boolean save(String email, String userName, String firstName, String lastName, String password,
			String imageId) throws JAXRException {
		if (findByEmail(email) == null) {
			User user = new User(email, userName, firstName, lastName, password, imageId);
			return save(user);
		}
		return false;
	}

	public void delete(User user) {
		transaction.begin();
		em.remove(em.contains(user) ? user : em.merge(user));
		transaction.commit();
	}
}
