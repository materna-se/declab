package de.materna.dmn.tester.interfaces.filters;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;

public interface SessionTokenFilter {
	public Predicate toPredicate(Root<SessionToken> root, CriteriaQuery<SessionToken> cq, CriteriaBuilder cb);
}