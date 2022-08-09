package de.materna.dmn.tester.beans.permission.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.permission.Permission;
import de.materna.dmn.tester.interfaces.filters.PermissionFilter;

public class LaboratoryAndWorkspaceUuidsFilter implements PermissionFilter {

	String laboratoryUuid, workspaceUuid;

	public LaboratoryAndWorkspaceUuidsFilter(String laboratoryUuid, String workspaceUuid) {
		this.laboratoryUuid = laboratoryUuid;
		this.workspaceUuid = workspaceUuid;
	}

	@Override
	public Predicate toPredicate(Root<Permission> root, CriteriaQuery<Permission> cq, CriteriaBuilder cb) {
		return cb.and(cb.equal(root.get("laboratoryUuid"), laboratoryUuid),
				cb.equal(root.get("workspaceUuid"), workspaceUuid));
	}
}