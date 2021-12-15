package de.materna.dmn.tester.beans.repositories.filter.userfilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.registry.infomodel.EmailAddress;

import de.materna.dmn.tester.beans.User;
import de.materna.dmn.tester.beans.repositories.filter.UserFilter;

public class EmailFilter implements UserFilter {

	EmailAddress email;

	public EmailFilter(EmailAddress email) {
		this.email = email;
	}

	@Override
	public Predicate toPredicate(Root<User> root, CriteriaQuery<User> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("EMAIL"), email);
	}
}