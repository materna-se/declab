package de.materna.dmn.tester.beans.sessiontoken.filter;

import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenFilter;

public class UserUuidFilter implements SessionTokenFilter {

	UUID userUuid;

	public UserUuidFilter(UUID userUuid) {
		this.userUuid = userUuid;
	}

	@Override
	public Predicate toPredicate(Root<SessionToken> root, CriteriaQuery<SessionToken> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("user"), userUuid);
	}
}