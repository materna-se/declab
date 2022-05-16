package de.materna.dmn.tester.beans.userpermission.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.interfaces.filters.UserPermissionFilter;

public class LaboratoryAndWorkspaceFilter implements UserPermissionFilter {

	String laboratoryUuid, workspaceUuid;

	public LaboratoryAndWorkspaceFilter(String laboratoryUuid, String workspaceUuid) {
		this.laboratoryUuid = laboratoryUuid;
		this.workspaceUuid = workspaceUuid;
	}

	@Override
	public Predicate toPredicate(Root<UserPermission> root, CriteriaQuery<UserPermission> cq, CriteriaBuilder cb) {
		return cb.and(cb.equal(root.get("laboratory"), laboratoryUuid), cb.equal(root.get("workspace"), workspaceUuid));
	}
}