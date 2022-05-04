package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;
import java.util.UUID;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.enums.VisabilityType;

public interface LaboratoryRepository {

	List<Laboratory> findAll();

	Laboratory findByUuid(UUID laboratoryUuid);

	List<Laboratory> findByName(String name);

	List<Laboratory> findByVisability(VisabilityType visability);

	List<Laboratory> findByUser(UUID ownerUuid);

	Laboratory put(Laboratory laboratory);

	Laboratory create(String name, String description, VisabilityType visability);

	void delete(Laboratory laboratory);
}