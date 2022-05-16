package de.materna.dmn.tester.interfaces.repositories;

import java.util.List;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.enums.VisabilityType;

public interface LaboratoryRepository {

	List<Laboratory> findAll();

	Laboratory findByUuid(String laboratoryUuid);

	List<Laboratory> findByName(String name);

	List<Laboratory> findByVisability(VisabilityType visability);

	List<Laboratory> findByUser(String ownerUuid);

	Laboratory put(Laboratory laboratory);

	Laboratory create(String name, String description, VisabilityType visability);

	boolean delete(Laboratory laboratory);

}