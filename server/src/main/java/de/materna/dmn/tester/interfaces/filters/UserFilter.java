package de.materna.dmn.tester.interfaces.filters;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.user.User;

public interface UserFilter {
	public Predicate toPredicate(Root<User> root, CriteriaQuery<User> cq, CriteriaBuilder cb);
}
