package de.materna.dmn.tester;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.laboratory.LaboratoryHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.permission.PermissionHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.UserHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.beans.workspace.WorkspaceHibernateH2RepositoryImpl;
import de.materna.dmn.tester.enums.PermissionType;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.servlets.workspace.WorkspaceServlet;

public abstract class Database {

	static UserHibernateH2RepositoryImpl userRepository = new UserHibernateH2RepositoryImpl();
	static PermissionHibernateH2RepositoryImpl permissionRepository = new PermissionHibernateH2RepositoryImpl();
	static LaboratoryHibernateH2RepositoryImpl laboratoryRepository = new LaboratoryHibernateH2RepositoryImpl();
	static WorkspaceHibernateH2RepositoryImpl workspaceRepository = new WorkspaceHibernateH2RepositoryImpl();

	private final static Logger log = LoggerFactory.getLogger(WorkspaceServlet.class);

	private final static Map<String, String> ADMIN = new HashMap<>() {
		private static final long serialVersionUID = 1L;
		{
			put("EMAIL", "administrator@declab.de");
			put("PASSWORD", "declab");
			put("USERNAME", "administrator");
		}
	};
	private final static Map<String, String> DEMO = new HashMap<>() {
		private static final long serialVersionUID = 1L;
		{
			put("EMAIL", "demo@declab.de");
			put("PASSWORD", "demo");
			put("USERNAME", "demo");
		}
	};
	private final static Map<String, String> MY_LAB = new HashMap<>() {
		private static final long serialVersionUID = 1L;
		{
			put("DESCRIPTION", "This is your laboratory alone. Only you can see it!");
			put("NAME", "My lab");
		}
	};
	private final static Map<String, String> PUBLIC_WORKSPACE = new HashMap<>() {
		private static final long serialVersionUID = 1L;
		{
			put("DESCRIPTION", "This workspace is visible for everybody.");
			put("NAME", "A public workspace");
		}
	};
	private final static Map<String, String> ELTERNGELD = new HashMap<>() {
		private static final long serialVersionUID = 1L;
		{
			put("DESCRIPTION", "Elterngeld DMN by Materna Information and Communications SE");
			put("NAME", "Elterngeld");
		}
	};

	public static void prepare() {
		try {
			Server.createWebServer("-webPort", "8081").start();
			log.info("H2 created!");

			// Administrator
			User admin = addUser(ADMIN.get("USERNAME"), ADMIN.get("EMAIL"), ADMIN.get("PASSWORD"), "Administrator",
					"Declab", true);
			if (admin != null) {
				Laboratory laboratory = addLaboratory(admin, MY_LAB.get("NAME"), MY_LAB.get("DESCRIPTION"),
						VisabilityType.PRIVATE);
				addWorkspace(admin, ELTERNGELD.get("NAME"), ELTERNGELD.get("DESCRIPTION"), VisabilityType.PROTECTED,
						laboratory.getUuid());
			}

			// Demo
			User demo = addUser(DEMO.get("USERNAME"), DEMO.get("EMAIL"), DEMO.get("PASSWORD"), "Demo", "Demo", false);
			if (demo != null) {
				Laboratory laboratory = addLaboratory(demo, MY_LAB.get("NAME"), MY_LAB.get("DESCRIPTION"),
						VisabilityType.PRIVATE);
				addWorkspace(demo, PUBLIC_WORKSPACE.get("NAME"), PUBLIC_WORKSPACE.get("DESCRIPTION"),
						VisabilityType.PUBLIC, laboratory.getUuid());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.info("H2 failed : " + e);
		}
	}

	private static Laboratory addLaboratory(User user, String name, String description, VisabilityType visability) {
		Laboratory laboratory = laboratoryRepository.create(name, description, visability);
		permissionRepository.create(user.getUuid(), laboratory.getUuid(), null, PermissionType.OWNER);
		return laboratory;
	}

	private static Workspace addWorkspace(User user, String name, String description, VisabilityType visability,
			String laboratoryUuid) {
		Workspace workspace = workspaceRepository.create(name, description, visability, laboratoryUuid);
		permissionRepository.create(null, laboratoryUuid, workspace.getUuid(), PermissionType.OWNER);
		return workspace;
	}

	private static User addUser(String username, String email, String password, String firstname, String lastname,
			boolean systemAdmin) {

		User user = userRepository.register(email, username, password, firstname, lastname);
		if (user != null) {
			user.setSystemAdmin(systemAdmin);
			log.info("H2: Saved user '" + username + "': " + user);
		} else {
			log.info("H2: User '" + username + "' already exists.");
		}
		return user;
	}
}