package de.materna.dmn.tester.beans.userpermission.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.enums.UserPermissionType;
import de.materna.dmn.tester.interfaces.filters.UserPermissionFilter;

public class RelationshipTypeFilter implements UserPermissionFilter {

	UserPermissionType type;

	public RelationshipTypeFilter(UserPermissionType type) {
		this.type = type;
	}

	@Override
	public Predicate toPredicate(Root<UserPermission> root, CriteriaQuery<UserPermission> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("type"), type);
	}
}