package de.materna.dmn.tester.beans.relationship.filter;

import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.relationship.Relationship;
import de.materna.dmn.tester.interfaces.filters.RelationshipFilter;

public class UserAndLaboratoryFilter implements RelationshipFilter {

	UUID laboratoryUuid, userUuid;

	public UserAndLaboratoryFilter(UUID userUuid, UUID laboratoryFilter) {
		this.userUuid = userUuid;
		this.laboratoryUuid = laboratoryFilter;
	}

	@Override
	public Predicate toPredicate(Root<Relationship> root, CriteriaQuery<Relationship> cq, CriteriaBuilder cb) {
		return cb.and(cb.equal(root.get("user"), userUuid), cb.equal(root.get("laboratory"), laboratoryUuid));
	}
}