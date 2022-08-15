package de.materna.dmn.tester.beans.workspace;

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

import de.materna.dmn.tester.beans.permission.PermissionHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.workspace.filter.NameFilter;
import de.materna.dmn.tester.beans.workspace.filter.VisabilityFilter;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.filters.WorkspaceFilter;
import de.materna.dmn.tester.interfaces.repositories.PermissionRepository;
import de.materna.dmn.tester.interfaces.repositories.WorkspaceRepository;

public class WorkspaceHibernateH2RepositoryImpl implements WorkspaceRepository {
	private final PermissionRepository permissionRepository = new PermissionHibernateH2RepositoryImpl();

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private final EntityManager em = entityManagerFactory.createEntityManager();
	private final EntityTransaction transaction = em.getTransaction();

	@Override
	public List<Workspace> findAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Workspace> cq = cb.createQuery(Workspace.class);
		final Root<Workspace> rootEntry = cq.from(Workspace.class);
		final CriteriaQuery<Workspace> all = cq.select(rootEntry);
		final TypedQuery<Workspace> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public Workspace findByUuid(String uuid) {
		try {
			transaction.begin();
			final Workspace Workspace = em.find(Workspace.class, uuid);
			transaction.commit();
			return Optional.ofNullable(Workspace).get();
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return null;
		}
	}

	@Override
	public List<Workspace> findByName(String name) {
		return findByFilter(new NameFilter(name));
	}

	@Override
	public List<Workspace> findByVisability(VisabilityType visability) {
		return findByFilter(new VisabilityFilter(visability));
	}

	@Override
	public List<Workspace> findByUser(String userUuid) {
		return permissionRepository.findByUserUuid(userUuid).stream()
				.filter(permission -> permission.getWorkspaceUuid() != null)
				.map(permission -> findByUuid(permission.getWorkspaceUuid())).collect(Collectors.toList());
	}

	@Override
	public List<Workspace> findByLaboratory(String laboratoryUuid) {
		return findAll().stream().filter(workspace -> workspace.getLaboratoryUuid() == laboratoryUuid)
				.collect(Collectors.toList());
	}

	@Override
	public Workspace put(Workspace workspace) {
		try {
			transaction.begin();
			em.persist(workspace);
			transaction.commit();
			return findByUuid(workspace.getUuid()) != null ? workspace : null;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return null;
		}
	}

	@Override
	public Workspace create(String name, String description, VisabilityType visability, String laboratoryUuid) {
		final Workspace workspace = new Workspace(name, description, visability, laboratoryUuid);
		return put(workspace);
	}

	@Override
	public boolean delete(Workspace workspace) {
		try {
			transaction.begin();
			em.remove(em.contains(workspace) ? workspace : em.merge(workspace));
			transaction.commit();
			return findByUuid(workspace.getUuid()) == null;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return false;
		}
	}

	public List<Workspace> findByFilter(WorkspaceFilter... filterArray) {
		final CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Workspace> cquery = cbuilder.createQuery(Workspace.class);
		final Root<Workspace> WorkspaceRoot = cquery.from(Workspace.class);
		cquery.select(WorkspaceRoot);
		final List<Predicate> predicates = new ArrayList<>();
		for (final WorkspaceFilter filter : filterArray) {
			predicates.add(filter.toPredicate(WorkspaceRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		final TypedQuery<Workspace> query = em.createQuery(cquery);
		return query.getResultList();
	}
}
