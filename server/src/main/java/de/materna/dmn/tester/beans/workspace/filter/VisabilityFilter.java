package de.materna.dmn.tester.beans.workspace.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.filters.WorkspaceFilter;

public class VisabilityFilter implements WorkspaceFilter {

	VisabilityType visabilityType;

	public VisabilityFilter(VisabilityType visabilityType) {
		this.visabilityType = visabilityType;
	}

	@Override
	public Predicate toPredicate(Root<Workspace> root, CriteriaQuery<Workspace> cq, CriteriaBuilder cb) {
		return cb.equal(root.get("visability"), visabilityType);
	}
}