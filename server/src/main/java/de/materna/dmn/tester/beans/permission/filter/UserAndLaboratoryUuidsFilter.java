package de.materna.dmn.tester.beans.permission.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.permission.Permission;
import de.materna.dmn.tester.interfaces.filters.PermissionFilter;

public class UserAndLaboratoryUuidsFilter implements PermissionFilter {

	String laboratoryUuid, userUuid;

	public UserAndLaboratoryUuidsFilter(String userUuid, String laboratoryUuid) {
		this.userUuid = userUuid;
		this.laboratoryUuid = laboratoryUuid;
	}

	@Override
	public Predicate toPredicate(Root<Permission> root, CriteriaQuery<Permission> cq, CriteriaBuilder cb) {
		return cb.and(cb.equal(root.get("userUuid"), userUuid), cb.equal(root.get("laboratoryUuid"), laboratoryUuid));
	}
}