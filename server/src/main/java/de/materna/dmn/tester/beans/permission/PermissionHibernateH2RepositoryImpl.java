package de.materna.dmn.tester.beans.permission;

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

import de.materna.dmn.tester.beans.permission.filter.LaboratoryAndWorkspaceUuidsFilter;
import de.materna.dmn.tester.beans.permission.filter.LaboratoryUuidFilter;
import de.materna.dmn.tester.beans.permission.filter.PermissionTypeFilter;
import de.materna.dmn.tester.beans.permission.filter.UserAndLaboratoryUuidsFilter;
import de.materna.dmn.tester.beans.permission.filter.UserAndWorkspaceUuidsFilter;
import de.materna.dmn.tester.beans.permission.filter.UserUuidFilter;
import de.materna.dmn.tester.beans.permission.filter.WorkspaceUuidFilter;
import de.materna.dmn.tester.enums.PermissionType;
import de.materna.dmn.tester.interfaces.filters.PermissionFilter;
import de.materna.dmn.tester.interfaces.repositories.PermissionRepository;

public class PermissionHibernateH2RepositoryImpl implements PermissionRepository {

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private final EntityManager em = entityManagerFactory.createEntityManager();
	private final EntityTransaction transaction = em.getTransaction();

	@Override
	public List<Permission> findAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Permission> cq = cb.createQuery(Permission.class);
		final Root<Permission> rootEntry = cq.from(Permission.class);
		final CriteriaQuery<Permission> all = cq.select(rootEntry);
		final TypedQuery<Permission> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public Permission findById(Long id) {
		try {
			transaction.begin();
			final Permission relationship = em.find(Permission.class, id);
			transaction.commit();
			return Optional.ofNullable(relationship).get();
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return null;
		}
	}

	@Override
	public List<Permission> findByUserUuid(String userUuid) {
		return findByFilter(new UserUuidFilter(userUuid));
	}

	@Override
	public List<Permission> findByLaboratoryUuid(String laboratoryUuid) {
		return findByFilter(new LaboratoryUuidFilter(laboratoryUuid));
	}

	@Override
	public List<Permission> findByWorkspaceUuid(String workspaceUuid) {
		return findByFilter(new WorkspaceUuidFilter(workspaceUuid));
	}

	@Override
	public List<Permission> findByType(PermissionType type) {
		return findByFilter(new PermissionTypeFilter(type));
	}

	@Override
	public Permission findByUserAndLaboratoryUuids(String userUuid, String laboratoryUuid) {
		final List<Permission> relationshipsFound = findByFilter(
				new UserAndLaboratoryUuidsFilter(userUuid, laboratoryUuid));
		return relationshipsFound.size() == 1 ? relationshipsFound.get(0) : null;
	}

	@Override
	public Permission findByUserAndWorkspaceUuids(String userUuid, String workspaceUuid) {
		final List<Permission> relationshipsFound = findByFilter(
				new UserAndWorkspaceUuidsFilter(userUuid, workspaceUuid));
		return relationshipsFound.size() == 1 ? relationshipsFound.get(0) : null;
	}

	@Override
	public Permission findByLaboratoryAndWorkspaceUuids(String laboratoryUuid, String workspaceUuid) {
		final List<Permission> relationshipsFound = findByFilter(
				new LaboratoryAndWorkspaceUuidsFilter(laboratoryUuid, workspaceUuid));
		return relationshipsFound.size() == 1 ? relationshipsFound.get(0) : null;
	}

	@Override
	public Permission put(Permission userPermission) {
		try {
			transaction.begin();
			em.persist(userPermission);
			transaction.commit();
			return userPermission;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return null;
		}
	}

	@Override
	public Permission create(String userUuid, String laboratoryUuid, String workspaceUuid, PermissionType type) {
		final Permission userPermission = new Permission(userUuid, laboratoryUuid, workspaceUuid, type);
		return put(userPermission);
	}

	@Override
	public boolean delete(Permission userPermission) {
		try {
			transaction.begin();
			em.remove(em.contains(userPermission) ? userPermission : em.merge(userPermission));
			transaction.commit();
			return findById(userPermission.getId()) == null;
		} catch (final Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			return false;
		}
	}

	public List<Permission> findByFilter(PermissionFilter... filterArray) {
		final CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Permission> cquery = cbuilder.createQuery(Permission.class);
		final Root<Permission> relationshipRoot = cquery.from(Permission.class);
		cquery.select(relationshipRoot);
		final List<Predicate> predicates = new ArrayList<>();
		for (final PermissionFilter filter : filterArray) {
			predicates.add(filter.toPredicate(relationshipRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		final TypedQuery<Permission> query = em.createQuery(cquery);
		return query.getResultList();
	}
}
