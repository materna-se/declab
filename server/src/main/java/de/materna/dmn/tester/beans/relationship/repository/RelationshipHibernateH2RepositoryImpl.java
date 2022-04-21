package de.materna.dmn.tester.beans.relationship.repository;

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

import de.materna.dmn.tester.beans.relationship.Relationship;
import de.materna.dmn.tester.beans.relationship.filter.LaboratoryFilter;
import de.materna.dmn.tester.beans.relationship.filter.RelationshipTypeFilter;
import de.materna.dmn.tester.beans.relationship.filter.UserFilter;
import de.materna.dmn.tester.beans.relationship.filter.WorkspaceFilter;
import de.materna.dmn.tester.enums.RelationshipType;
import de.materna.dmn.tester.interfaces.filters.RelationshipFilter;
import de.materna.dmn.tester.interfaces.repositories.RelationshipRepository;

public class RelationshipHibernateH2RepositoryImpl implements RelationshipRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	@Override
	public List<Relationship> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Relationship> cq = cb.createQuery(Relationship.class);
		Root<Relationship> rootEntry = cq.from(Relationship.class);
		CriteriaQuery<Relationship> all = cq.select(rootEntry);
		TypedQuery<Relationship> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public Relationship findByUuid(Long id) {
		transaction.begin();
		Relationship relationship = em.find(Relationship.class, id);
		transaction.commit();
		return Optional.ofNullable(relationship).get();
	}

	@Override
	public List<Relationship> findByUser(UUID userUuid) {
		List<Relationship> relationshipsFound = findByFilter(new UserFilter(userUuid));
		return relationshipsFound;
	}

	@Override
	public List<Relationship> findByLaboratory(UUID laboratoryUuid) {
		List<Relationship> relationshipsFound = findByFilter(new LaboratoryFilter(laboratoryUuid));
		return relationshipsFound;
	}

	@Override
	public List<Relationship> findByWorkspace(UUID workspaceUuid) {
		List<Relationship> relationshipsFound = findByFilter(new WorkspaceFilter(workspaceUuid));
		return relationshipsFound;
	}

	@Override
	public List<Relationship> findByType(RelationshipType type) {
		List<Relationship> relationshipsFound = findByFilter(new RelationshipTypeFilter(type));
		return relationshipsFound;
	}

	@Override
	public void put(Relationship relationship) {
		transaction.begin();
		em.persist(relationship);
		transaction.commit();
	}

	@Override
	public void delete(Relationship relationship) {
		transaction.begin();
		em.remove(em.contains(relationship) ? relationship : em.merge(relationship));
		transaction.commit();
	}

	public List<Relationship> findByFilter(RelationshipFilter... filterArray) {
		CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		CriteriaQuery<Relationship> cquery = cbuilder.createQuery(Relationship.class);
		Root<Relationship> relationshipRoot = cquery.from(Relationship.class);
		cquery.select(relationshipRoot);
		List<Predicate> predicates = new ArrayList<>();
		for (RelationshipFilter filter : filterArray) {
			predicates.add(filter.toPredicate(relationshipRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Relationship> query = em.createQuery(cquery);
		return query.getResultList();
	}
}
