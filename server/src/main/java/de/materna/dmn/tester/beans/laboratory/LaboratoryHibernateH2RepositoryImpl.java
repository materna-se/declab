package de.materna.dmn.tester.beans.laboratory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.laboratory.filter.NameFilter;
import de.materna.dmn.tester.beans.laboratory.filter.VisabilityFilter;
import de.materna.dmn.tester.beans.relationship.RelationshipHibernateH2RepositoryImpl;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.filters.LaboratoryFilter;
import de.materna.dmn.tester.interfaces.repositories.LaboratoryRepository;
import de.materna.dmn.tester.interfaces.repositories.RelationshipRepository;

public class LaboratoryHibernateH2RepositoryImpl implements LaboratoryRepository {
	private final RelationshipRepository relationshipRepository = new RelationshipHibernateH2RepositoryImpl();

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private final EntityManager em = entityManagerFactory.createEntityManager();
	private final EntityTransaction transaction = em.getTransaction();

	@Override
	public List<Laboratory> findAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Laboratory> cq = cb.createQuery(Laboratory.class);
		final Root<Laboratory> rootEntry = cq.from(Laboratory.class);
		final CriteriaQuery<Laboratory> all = cq.select(rootEntry);
		final TypedQuery<Laboratory> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public Laboratory findByUuid(UUID laboratoryUuid) {
		transaction.begin();
		final Laboratory Laboratory = em.find(Laboratory.class, laboratoryUuid);
		transaction.commit();
		return Optional.ofNullable(Laboratory).get();
	}

	@Override
	public List<Laboratory> findByName(String name) {
		return findByFilter(new NameFilter(name));
	}

	@Override
	public List<Laboratory> findByVisability(VisabilityType visability) {
		return findByFilter(new VisabilityFilter(visability));
	}

	@Override
	public List<Laboratory> findByUser(UUID ownerUuid) {
		return relationshipRepository.findByUser(ownerUuid).stream()
				.filter(relationship -> relationship.getLaboratory() != null)
				.map(relationship -> findByUuid(relationship.getLaboratory())).collect(Collectors.toList());
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
		final Laboratory laboratory = new Laboratory(name, description, visability);
		return put(laboratory);
	}

	@Override
	public void delete(Laboratory laboratory) {
		transaction.begin();
		em.remove(em.contains(laboratory) ? laboratory : em.merge(laboratory));
		transaction.commit();
	}

	public List<Laboratory> findByFilter(LaboratoryFilter... filterArray) {
		final CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Laboratory> cquery = cbuilder.createQuery(Laboratory.class);
		final Root<Laboratory> LaboratoryRoot = cquery.from(Laboratory.class);
		cquery.select(LaboratoryRoot);
		final List<Predicate> predicates = new ArrayList<>();
		for (final LaboratoryFilter filter : filterArray) {
			predicates.add(filter.toPredicate(LaboratoryRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		final TypedQuery<Laboratory> query = em.createQuery(cquery);
		return query.getResultList();
	}
}
