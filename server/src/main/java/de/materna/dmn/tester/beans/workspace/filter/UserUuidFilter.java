package de.materna.dmn.tester.beans.workspace.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.beans.workspace.WorkspaceFilter;

public class UserUuidFilter implements WorkspaceFilter {

	String userUuid;

	public UserUuidFilter(String userUuid) {
		this.userUuid = userUuid;
	}

	@Override
	public Predicate toPredicate(Root<Workspace> root, CriteriaQuery<Workspace> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("workspace"), userUuid);
	}
}