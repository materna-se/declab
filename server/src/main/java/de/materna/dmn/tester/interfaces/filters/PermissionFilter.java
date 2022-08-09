package de.materna.dmn.tester.interfaces.filters;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.permission.Permission;

public interface PermissionFilter {
	public Predicate toPredicate(Root<Permission> root, CriteriaQuery<Permission> cq, CriteriaBuilder cb);
}
