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

import de.materna.dmn.tester.beans.User;
import de.materna.dmn.tester.beans.repositories.filter.UserFilter;

@ApplicationScoped
public class UserRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	public Optional<User> findByUuid(String uuid) {
		transaction.begin();
		User user = em.find(User.class, uuid);
		transaction.commit();
		return Optional.ofNullable(user);
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

	public void save(User user) {
		transaction.begin();

		em.persist(user);
		transaction.commit();
	}

	public void delete(User user) {
		transaction.begin();
		em.remove(em.contains(user) ? user : em.merge(user));
		transaction.commit();
	}
}
