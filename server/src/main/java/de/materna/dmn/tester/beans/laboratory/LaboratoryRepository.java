package de.materna.dmn.tester.beans.laboratory;

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

@ApplicationScoped
public class LaboratoryRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	public Laboratory loadByUuid(String uuid) {
		transaction.begin();
		Laboratory Laboratory = em.find(Laboratory.class, uuid);
		transaction.commit();
		return Optional.ofNullable(Laboratory).get();
	}

	public List<Laboratory> loadByFilter(LaboratoryFilter... filterArray) {
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

	public void save(Laboratory laboratory) {
		transaction.begin();
		em.persist(laboratory);
		transaction.commit();
	}

	public void delete(Laboratory laboratory) {
		transaction.begin();
		em.remove(em.contains(laboratory) ? laboratory : em.merge(laboratory));
		transaction.commit();
	}
}
