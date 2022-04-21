package de.materna.dmn.tester.beans.laboratory.repository;

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

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.laboratory.LaboratoryFilter;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.repositories.hibernate.h2.LaboratoryHibernateH2Repository;

public class LaboratoryHibernateH2RepositoryImpl implements LaboratoryHibernateH2Repository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	@Override
	public List<Laboratory> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Laboratory> cq = cb.createQuery(Laboratory.class);
		Root<Laboratory> rootEntry = cq.from(Laboratory.class);
		CriteriaQuery<Laboratory> all = cq.select(rootEntry);
		TypedQuery<Laboratory> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public Laboratory findByUuid(String uuid) {
		transaction.begin();
		Laboratory Laboratory = em.find(Laboratory.class, uuid);
		transaction.commit();
		return Optional.ofNullable(Laboratory).get();
	}

	@Override
	public Laboratory put(Laboratory laboratory) {
		transaction.begin();
		em.persist(laboratory);
		transaction.commit();
		return findByUuid(laboratory.getUuid()) != null ? laboratory : null;
	}

	@Override
	public Laboratory create(String name, String description, VisabilityType visability) {
		Laboratory laboratory = new Laboratory(name, description, visability);
		return put(laboratory);
	}

	@Override
	public void delete(Laboratory laboratory) {
		transaction.begin();
		em.remove(em.contains(laboratory) ? laboratory : em.merge(laboratory));
		transaction.commit();
	}

	public List<Laboratory> findByFilter(LaboratoryFilter... filterArray) {
		CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		CriteriaQuery<Laboratory> cquery = cbuilder.createQuery(Laboratory.class);
		Root<Laboratory> LaboratoryRoot = cquery.from(Laboratory.class);
		cquery.select(LaboratoryRoot);
		List<Predicate> predicates = new ArrayList<>();
		for (LaboratoryFilter filter : filterArray) {
			predicates.add(filter.toPredicate(LaboratoryRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Laboratory> query = em.createQuery(cquery);
		return query.getResultList();
	}
}
