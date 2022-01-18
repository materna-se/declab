package de.materna.dmn.tester.beans.workspace;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface WorkspaceFilter {
	public Predicate toPredicate(Root<Workspace> root, CriteriaQuery<Workspace> cq, CriteriaBuilder cb);
}