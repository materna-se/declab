package de.materna.dmn.tester.beans.relationship;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface RelationshipFilter {
	public Predicate toPredicate(Root<Relationship> root, CriteriaQuery<Relationship> cq, CriteriaBuilder cb);
}
