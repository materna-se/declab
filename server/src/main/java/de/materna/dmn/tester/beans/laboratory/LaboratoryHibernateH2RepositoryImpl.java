package de.materna.dmn.tester.beans.laboratory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import de.materna.dmn.tester.beans.permission.PermissionHibernateH2RepositoryImpl;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.filters.LaboratoryFilter;
import de.materna.dmn.tester.interfaces.repositories.LaboratoryRepository;
import de.materna.dmn.tester.interfaces.repositories.PermissionRepository;

public class LaboratoryHibernateH2RepositoryImpl implements LaboratoryRepository {
	private final PermissionRepository userPermissionRepository = new PermissionHibernateH2RepositoryImpl();

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
	public Laboratory findByUuid(String laboratoryUuid) {
		try {
			transaction.begin();
			final Laboratory Laboratory = em.find(Laboratory.class, laboratoryUuid);
			transaction.commit();
			return Optional.ofNullable(Laboratory).get();
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return null;
		}
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
	public List<Laboratory> findByUser(String userUuid) {
		return userPermissionRepository.findByUserUuid(userUuid).stream()
				.filter(userPermission -> userPermission.getLaboratoryUuid() != null)
				.map(userPermission -> findByUuid(userPermission.getLaboratoryUuid())).collect(Collectors.toList());
	}

	@Override
	public Laboratory put(Laboratory laboratory) {
		try {
			transaction.begin();
			em.persist(laboratory);
			transaction.commit();
			return findByUuid(laboratory.getUuid()) != null ? laboratory : null;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return null;
		}
	}

	@Override
	public Laboratory create(String name, String description, VisabilityType visability) {
		final Laboratory laboratory = new Laboratory(name, description, visability);
		return put(laboratory);
	}

	@Override
	public boolean delete(Laboratory laboratory) {
		try {
			transaction.begin();
			em.remove(em.contains(laboratory) ? laboratory : em.merge(laboratory));
			transaction.commit();
			return findByUuid(laboratory.getUuid()) == null;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return false;
		}
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
