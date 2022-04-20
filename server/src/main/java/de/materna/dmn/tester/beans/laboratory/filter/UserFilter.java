package de.materna.dmn.tester.beans.laboratory.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.laboratory.LaboratoryFilter;

public class UserFilter implements LaboratoryFilter {

	String owner;

	public UserFilter(String ownerUuid) {
		this.owner = ownerUuid;
	}

	@Override
	public Predicate toPredicate(Root<Laboratory> root, CriteriaQuery<Laboratory> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("user"), owner);
	}
}