package de.materna.dmn.tester.beans.repositories.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.Laboratory;

public interface LaboratoryFilter {
	public Predicate toPredicate(Root<Laboratory> root, CriteriaQuery<Laboratory> cq, CriteriaBuilder cb);
}
