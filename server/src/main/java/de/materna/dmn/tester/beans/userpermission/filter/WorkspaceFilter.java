package de.materna.dmn.tester.beans.userpermission.filter;

import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.interfaces.filters.UserPermissionFilter;

public class WorkspaceFilter implements UserPermissionFilter {

	UUID workspaceUuid;

	public WorkspaceFilter(UUID workspaceUuid) {
		this.workspaceUuid = workspaceUuid;
	}

	@Override
	public Predicate toPredicate(Root<UserPermission> root, CriteriaQuery<UserPermission> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("workspace"), workspaceUuid);
	}
}