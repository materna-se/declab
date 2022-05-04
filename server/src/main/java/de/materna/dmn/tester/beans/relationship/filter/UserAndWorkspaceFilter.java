package de.materna.dmn.tester.beans.relationship.filter;

import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.relationship.Relationship;
import de.materna.dmn.tester.interfaces.filters.RelationshipFilter;

public class UserAndWorkspaceFilter implements RelationshipFilter {

	UUID userUuid, workspaceUuid;

	public UserAndWorkspaceFilter(UUID userUuid, UUID workspaceUuid) {
		this.userUuid = userUuid;
		this.workspaceUuid = workspaceUuid;
	}

	@Override
	public Predicate toPredicate(Root<Relationship> root, CriteriaQuery<Relationship> cq, CriteriaBuilder cb) {
		return cb.and(cb.equal(root.get("user"), userUuid), cb.equal(root.get("workspace"), workspaceUuid));
	}
}