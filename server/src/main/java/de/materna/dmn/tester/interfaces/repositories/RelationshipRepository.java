package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;
import java.util.UUID;

import de.materna.dmn.tester.beans.relationship.Relationship;
import de.materna.dmn.tester.enums.RelationshipType;

public interface RelationshipRepository {

	List<Relationship> findAll();

	Relationship findByUuid(Long id);

	List<Relationship> findByUser(UUID userUuid);

	List<Relationship> findByLaboratory(UUID laboratoryUuid);

	List<Relationship> findByWorkspace(UUID workspaceUuid);

	List<Relationship> findByType(RelationshipType type);

	void put(Relationship relationship);

	void delete(Relationship relationship);
}
