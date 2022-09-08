package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.enums.VisabilityType;

public interface LaboratoryRepository {

	List<Laboratory> getAll();

	Laboratory getByUuid(String laboratoryUuid);

	List<Laboratory> getByName(String name);

	List<Laboratory> getByVisability(VisabilityType visability);

	List<Laboratory> getByUser(String ownerUuid);

	Laboratory put(Laboratory laboratory);

	Laboratory create(String name, String description, VisabilityType visability);

	boolean delete(Laboratory laboratory);

}