package de.materna.dmn.tester.beans.laboratory.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.filters.LaboratoryFilter;

public class VisabilityFilter implements LaboratoryFilter {

	VisabilityType visabilityType;

	public VisabilityFilter(VisabilityType visabilityType) {
		this.visabilityType = visabilityType;
	}

	@Override
	public Predicate toPredicate(Root<Laboratory> root, CriteriaQuery<Laboratory> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("visability"), visabilityType);
	}
}