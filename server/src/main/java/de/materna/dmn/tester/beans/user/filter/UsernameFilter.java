package de.materna.dmn.tester.beans.user.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.interfaces.filters.UserFilter;

public class UsernameFilter implements UserFilter {

	String username;

	public UsernameFilter(String username) {
		this.username = username;
	}

	@Override
	public Predicate toPredicate(Root<User> root, CriteriaQuery<User> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("username"), username);
	}
}