package de.materna.dmn.tester.beans.workspace.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.interfaces.filters.WorkspaceFilter;

public class NameFilter implements WorkspaceFilter {

	String workspaceName;

	public NameFilter(String workspaceName) {
		this.workspaceName = workspaceName;
	}

	@Override
	public Predicate toPredicate(Root<Workspace> root, CriteriaQuery<Workspace> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("name"), workspaceName);
	}
}