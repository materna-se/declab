package de.materna.dmn.tester.beans.workspace;

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
import javax.xml.registry.JAXRException;

import de.materna.dmn.tester.beans.userpermission.UserPermissionHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.workspace.filter.NameFilter;
import de.materna.dmn.tester.beans.workspace.filter.VisabilityFilter;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.filters.WorkspaceFilter;
import de.materna.dmn.tester.interfaces.repositories.UserPermissionRepository;
import de.materna.dmn.tester.interfaces.repositories.WorkspaceRepository;

public class WorkspaceHibernateH2RepositoryImpl implements WorkspaceRepository {
	private final UserPermissionRepository userPermissionRepository = new UserPermissionHibernateH2RepositoryImpl();

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
	public Workspace findByUuid(UUID workspaceUuid) {
		transaction.begin();
		final Workspace Workspace = em.find(Workspace.class, workspaceUuid);
		transaction.commit();
		return Optional.ofNullable(Workspace).get();
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
	public List<Workspace> findByUser(UUID ownerUuid) {
		return userPermissionRepository.findByUser(ownerUuid).stream()
				.filter(userPermission -> userPermission.getWorkspace() != null)
				.map(userPermission -> findByUuid(userPermission.getWorkspace())).collect(Collectors.toList());
	}

	@Override
	public List<Workspace> findByLaboratory(UUID laboratoryUuid) {
		return userPermissionRepository.findByLaboratory(laboratoryUuid).stream()
				.filter(userPermission -> userPermission.getWorkspace() != null)
				.map(userPermission -> findByUuid(userPermission.getWorkspace())).collect(Collectors.toList());
	}

	@Override
	public Workspace put(Workspace workspace) {
		transaction.begin();
		em.persist(workspace);
		transaction.commit();
		return findByUuid(workspace.getUuid()) != null ? workspace : null;
	}

	@Override
	public Workspace create(String name, String description, VisabilityType visability) {
		final Workspace workspace = new Workspace(name, description, visability);
		return put(workspace);
	}

	@Override
	public Workspace update(UUID workspaceUuid, String name, String description, VisabilityType visability)
			throws JAXRException {
		final Workspace workspace = findByUuid(workspaceUuid);
		if (workspace != null) {
			workspace.setName(name);
			workspace.setDescription(description);
			workspace.setVisability(visability);
			return put(workspace);
		}
		return null;
	}

	@Override
	public boolean delete(Workspace workspace) {
		transaction.begin();
		em.remove(em.contains(workspace) ? workspace : em.merge(workspace));
		transaction.commit();
		return findByUuid(workspace.getUuid()) == null;
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
