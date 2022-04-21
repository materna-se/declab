package de.materna.dmn.tester.beans.workspace.repository;

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

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.interfaces.filters.WorkspaceFilter;
import de.materna.dmn.tester.interfaces.repositories.WorkspaceRepository;

public class WorkspaceHibernateH2RepositoryImpl implements WorkspaceRepository {

	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("main");
	private EntityManager em = entityManagerFactory.createEntityManager();
	private EntityTransaction transaction = em.getTransaction();

	@Override
	public Workspace findByUuid(String uuid) {
		transaction.begin();
		Workspace Workspace = em.find(Workspace.class, uuid);
		transaction.commit();
		return Optional.ofNullable(Workspace).get();
	}

	@Override
	public void put(Workspace workspace) {
		transaction.begin();
		em.persist(workspace);
		transaction.commit();
	}

	@Override
	public void delete(Workspace workspace) {
		transaction.begin();
		em.remove(em.contains(workspace) ? workspace : em.merge(workspace));
		transaction.commit();
	}

	public List<Workspace> loadByFilter(WorkspaceFilter... filterArray) {
		CriteriaBuilder cbuilder = em.getCriteriaBuilder();
		CriteriaQuery<Workspace> cquery = cbuilder.createQuery(Workspace.class);
		Root<Workspace> WorkspaceRoot = cquery.from(Workspace.class);
		cquery.select(WorkspaceRoot);
		List<Predicate> predicates = new ArrayList<>();
		for (WorkspaceFilter filter : filterArray) {
			predicates.add(filter.toPredicate(WorkspaceRoot, cquery, cbuilder));
		}
		cquery.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Workspace> query = em.createQuery(cquery);
		return query.getResultList();
	}
}
