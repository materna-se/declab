package de.materna.dmn.tester.beans.userpermission.filter;

import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.interfaces.filters.UserPermissionFilter;

public class LaboratoryFilter implements UserPermissionFilter {

	UUID laboratoryUuid;

	public LaboratoryFilter(UUID userUuid) {
		this.laboratoryUuid = userUuid;
	}

	@Override
	public Predicate toPredicate(Root<UserPermission> root, CriteriaQuery<UserPermission> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("laboratory"), laboratoryUuid);
	}
}