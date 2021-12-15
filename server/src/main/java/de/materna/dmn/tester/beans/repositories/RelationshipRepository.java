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

import de.materna.dmn.tester.beans.Relationship;
import de.materna.dmn.tester.beans.repositories.filter.FilterRelationship;

@ApplicationScoped
public class RelationshipRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	public Optional<Relationship> findByUuid(Long id) {
		transaction.begin();
		Relationship relationship = em.find(Relationship.class, id);
		transaction.commit();
		return Optional.ofNullable(relationship);
	}

	public List<Relationship> findByFilter(FilterRelationship... filterArray) {
		CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		CriteriaQuery<Relationship> cquery = cbuilder.createQuery(Relationship.class);
		Root<Relationship> relationshipRoot = cquery.from(Relationship.class);
		cquery.select(relationshipRoot);
		List<Predicate> predicates = new ArrayList<>();
		for (FilterRelationship filter : filterArray) {
			predicates.add(filter.toPredicate(relationshipRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Relationship> query = em.createQuery(cquery);
		return query.getResultList();
	}

	public void save(Relationship relationship) {
		transaction.begin();

		em.persist(relationship);
		transaction.commit();
	}

	public void delete(Relationship relationship) {
		transaction.begin();
		em.remove(em.contains(relationship) ? relationship : em.merge(relationship));
		transaction.commit();
	}
}
