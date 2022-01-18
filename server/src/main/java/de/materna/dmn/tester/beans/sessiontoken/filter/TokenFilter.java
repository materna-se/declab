package de.materna.dmn.tester.beans.sessiontoken.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenFilter;

public class TokenFilter implements SessionTokenFilter {

	String token;

	public TokenFilter(String token) {
		this.token = token;
	}

	@Override
	public Predicate toPredicate(Root<SessionToken> root, CriteriaQuery<SessionToken> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("token"), token);
	}
}