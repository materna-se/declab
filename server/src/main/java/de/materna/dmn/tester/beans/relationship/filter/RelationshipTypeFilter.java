package de.materna.dmn.tester.beans.relationship.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.relationship.Relationship;
import de.materna.dmn.tester.enums.RelationshipType;
import de.materna.dmn.tester.interfaces.filters.RelationshipFilter;

public class RelationshipTypeFilter implements RelationshipFilter {

	RelationshipType type;

	public RelationshipTypeFilter(RelationshipType type) {
		this.type = type;
	}

	@Override
	public Predicate toPredicate(Root<Relationship> root, CriteriaQuery<Relationship> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("type"), type);
	}
}