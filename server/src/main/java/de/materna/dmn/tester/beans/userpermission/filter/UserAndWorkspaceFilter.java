package de.materna.dmn.tester.beans.userpermission.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.interfaces.filters.UserPermissionFilter;

public class UserAndWorkspaceFilter implements UserPermissionFilter {

	String userUuid, workspaceUuid;

	public UserAndWorkspaceFilter(String userUuid, String workspaceUuid) {
		this.userUuid = userUuid;
		this.workspaceUuid = workspaceUuid;
	}

	@Override
	public Predicate toPredicate(Root<UserPermission> root, CriteriaQuery<UserPermission> cq, CriteriaBuilder cb) {
		return cb.and(cb.equal(root.get("user"), userUuid), cb.equal(root.get("workspace"), workspaceUuid));
	}
}