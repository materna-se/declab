package de.materna.dmn.tester.servlets;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.log4j.Logger;

import de.materna.dmn.tester.beans.Database;
import de.materna.dmn.tester.beans.User;
import de.materna.dmn.tester.beans.repositories.UserRepository;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.challenges.ChallengeServlet;
import de.materna.dmn.tester.servlets.exceptions.DefaultOptionsMethodExceptionMapper;
import de.materna.dmn.tester.servlets.exceptions.GeneralExceptionMapper;
import de.materna.dmn.tester.servlets.filters.CSRFFilter;
import de.materna.dmn.tester.servlets.filters.ReadAccessFilter;
import de.materna.dmn.tester.servlets.filters.WriteAccessFilter;
import de.materna.dmn.tester.servlets.input.InputServlet;
import de.materna.dmn.tester.servlets.model.ModelServlet;
import de.materna.dmn.tester.servlets.output.OutputServlet;
import de.materna.dmn.tester.servlets.playground.PlaygroundServlet;
import de.materna.dmn.tester.servlets.test.TestServlet;
import de.materna.dmn.tester.servlets.workspace.MetaWorkspaceServlet;
import de.materna.dmn.tester.servlets.workspace.WorkspaceServlet;

@ApplicationPath("/api")
public class MainApplication extends Application {
	private Set<Object> singletons = new HashSet<>();
	private Set<Class<?>> classes = new HashSet<>();
	private static final Logger log = Logger.getLogger(MainApplication.class);

	public MainApplication() throws IOException {
		// Before we initialize the endpoints, we'll initialize all workspaces.
		WorkspaceManager.getInstance().indexAll();

		singletons.add(new ModelServlet());
		singletons.add(new ChallengeServlet());
		singletons.add(new PlaygroundServlet());
		singletons.add(new InputServlet());
		singletons.add(new OutputServlet());
		singletons.add(new TestServlet());
		singletons.add(new MetaWorkspaceServlet());
		singletons.add(new WorkspaceServlet());

		classes.add(CSRFFilter.class);
		classes.add(ReadAccessFilter.class);
		classes.add(WriteAccessFilter.class);

		classes.add(DefaultOptionsMethodExceptionMapper.class);
		classes.add(GeneralExceptionMapper.class);
		
//		Database db = new Database();
//		db.connect();
/*		User newUser = new User();
		UserRepository ur = new UserRepository();
		ur.save(newUser);
		Optional<User> loaded = ur.findByUuid(0);
		boolean stop = true;*/
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}