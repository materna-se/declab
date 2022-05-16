package de.materna.dmn.tester.beans.userpermission.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.interfaces.filters.UserPermissionFilter;

public class UserAndLaboratoryFilter implements UserPermissionFilter {

	String laboratoryUuid, userUuid;

	public UserAndLaboratoryFilter(String userUuid, String laboratoryUuid) {
		this.userUuid = userUuid;
		this.laboratoryUuid = laboratoryUuid;
	}

	@Override
	public Predicate toPredicate(Root<UserPermission> root, CriteriaQuery<UserPermission> cq, CriteriaBuilder cb) {
		return cb.and(cb.equal(root.get("user"), userUuid), cb.equal(root.get("laboratory"), laboratoryUuid));
	}
}