package de.materna.dmn.tester.beans.user.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.UserFilter;

public class EmailFilter implements UserFilter {

	String email;

	public EmailFilter(String email) {
		this.email = email;
	}

	@Override
	public Predicate toPredicate(Root<User> root, CriteriaQuery<User> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("email"), email);
	}
}