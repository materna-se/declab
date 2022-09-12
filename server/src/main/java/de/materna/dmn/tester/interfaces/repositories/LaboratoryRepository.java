package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.servlets.exceptions.database.LaboratoryNotFoundException;

public interface LaboratoryRepository {

	List<Laboratory> getAll();

	Laboratory getByUuid(String laboratoryUuid) throws LaboratoryNotFoundException;

	List<Laboratory> getByName(String name);

	List<Laboratory> getByVisability(VisabilityType visability);

	List<Laboratory> getByUser(String ownerUuid);

	Laboratory put(Laboratory laboratory) throws LaboratoryNotFoundException;

	Laboratory create(String name, String description, VisabilityType visability) throws LaboratoryNotFoundException;

	boolean delete(Laboratory laboratory);

}