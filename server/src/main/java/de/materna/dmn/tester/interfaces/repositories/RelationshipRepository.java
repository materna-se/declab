package de.materna.dmn.tester.interfaces.repositories;

import de.materna.dmn.tester.beans.relationship.Relationship;

public interface RelationshipRepository {

	public Relationship findByUuid(Long id);

	public void put(Relationship relationship);

	public void delete(Relationship relationship);
}
