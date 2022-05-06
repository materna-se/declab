package de.materna.dmn.tester.beans.userpermission;

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

import de.materna.dmn.tester.beans.userpermission.filter.LaboratoryAndWorkspaceFilter;
import de.materna.dmn.tester.beans.userpermission.filter.LaboratoryFilter;
import de.materna.dmn.tester.beans.userpermission.filter.RelationshipTypeFilter;
import de.materna.dmn.tester.beans.userpermission.filter.UserAndLaboratoryFilter;
import de.materna.dmn.tester.beans.userpermission.filter.UserAndWorkspaceFilter;
import de.materna.dmn.tester.beans.userpermission.filter.UserFilter;
import de.materna.dmn.tester.beans.userpermission.filter.WorkspaceFilter;
import de.materna.dmn.tester.enums.UserPermissionType;
import de.materna.dmn.tester.interfaces.filters.UserPermissionFilter;
import de.materna.dmn.tester.interfaces.repositories.UserPermissionRepository;

public class UserPermissionHibernateH2RepositoryImpl implements UserPermissionRepository {

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private final EntityManager em = entityManagerFactory.createEntityManager();
	private final EntityTransaction transaction = em.getTransaction();

	@Override
	public List<UserPermission> findAll() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<UserPermission> cq = cb.createQuery(UserPermission.class);
		final Root<UserPermission> rootEntry = cq.from(UserPermission.class);
		final CriteriaQuery<UserPermission> all = cq.select(rootEntry);
		final TypedQuery<UserPermission> allQuery = em.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public UserPermission findByUuid(Long id) {
		transaction.begin();
		final UserPermission relationship = em.find(UserPermission.class, id);
		transaction.commit();
		return Optional.ofNullable(relationship).get();
	}

	@Override
	public List<UserPermission> findByUser(UUID userUuid) {
		return findByFilter(new UserFilter(userUuid));
	}

	@Override
	public List<UserPermission> findByLaboratory(UUID laboratoryUuid) {
		return findByFilter(new LaboratoryFilter(laboratoryUuid));
	}

	@Override
	public List<UserPermission> findByWorkspace(UUID workspaceUuid) {
		return findByFilter(new WorkspaceFilter(workspaceUuid));
	}

	@Override
	public List<UserPermission> findByType(UserPermissionType type) {
		return findByFilter(new RelationshipTypeFilter(type));
	}

	@Override
	public UserPermission findByUserAndLaboratory(UUID userUuid, UUID laboratoryUuid) {
		final List<UserPermission> relationshipsFound = findByFilter(
				new UserAndLaboratoryFilter(userUuid, laboratoryUuid));
		return relationshipsFound.size() == 1 ? relationshipsFound.get(0) : null;
	}

	@Override
	public UserPermission findByUserAndWorkspace(UUID userUuid, UUID workspaceUuid) {
		final List<UserPermission> relationshipsFound = findByFilter(new UserAndWorkspaceFilter(userUuid, workspaceUuid));
		return relationshipsFound.size() == 1 ? relationshipsFound.get(0) : null;
	}

	@Override
	public UserPermission findByLaboratoryAndWorkspace(UUID laboratoryUuid, UUID workspaceUuid) {
		final List<UserPermission> relationshipsFound = findByFilter(
				new LaboratoryAndWorkspaceFilter(laboratoryUuid, workspaceUuid));
		return relationshipsFound.size() == 1 ? relationshipsFound.get(0) : null;
	}

	@Override
	public void put(UserPermission relationship) {
		transaction.begin();
		em.persist(relationship);
		transaction.commit();
	}

	@Override
	public void delete(UserPermission relationship) {
		transaction.begin();
		em.remove(em.contains(relationship) ? relationship : em.merge(relationship));
		transaction.commit();
	}

	public List<UserPermission> findByFilter(UserPermissionFilter... filterArray) {
		final CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		final CriteriaQuery<UserPermission> cquery = cbuilder.createQuery(UserPermission.class);
		final Root<UserPermission> relationshipRoot = cquery.from(UserPermission.class);
		cquery.select(relationshipRoot);
		final List<Predicate> predicates = new ArrayList<>();
		for (final UserPermissionFilter filter : filterArray) {
			predicates.add(filter.toPredicate(relationshipRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		final TypedQuery<UserPermission> query = em.createQuery(cquery);
		return query.getResultList();
	}
}
