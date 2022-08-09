package de.materna.dmn.tester.beans.permission.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.permission.Permission;
import de.materna.dmn.tester.enums.PermissionType;
import de.materna.dmn.tester.interfaces.filters.PermissionFilter;

public class PermissionTypeFilter implements PermissionFilter {

	PermissionType type;

	public PermissionTypeFilter(PermissionType type) {
		this.type = type;
	}

	@Override
	public Predicate toPredicate(Root<Permission> root, CriteriaQuery<Permission> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("type"), type);
	}
}