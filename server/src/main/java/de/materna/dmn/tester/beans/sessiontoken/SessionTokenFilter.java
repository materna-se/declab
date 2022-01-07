package de.materna.dmn.tester.beans.sessiontoken;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface SessionTokenFilter {
	public Predicate toPredicate(Root<SessionToken> root, CriteriaQuery<SessionToken> cq, CriteriaBuilder cb);
}
