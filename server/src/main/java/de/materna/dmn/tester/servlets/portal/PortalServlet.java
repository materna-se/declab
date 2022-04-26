package de.materna.dmn.tester.servlets.portal;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mindrot.jbcrypt.BCrypt;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.laboratory.repository.LaboratoryHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.repository.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.repository.UserHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.beans.workspace.repository.WorkspaceHibernateH2RepositoryImpl;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;
import de.materna.dmn.tester.interfaces.repositories.UserRepository;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.UserNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.registration.EmailInUseException;
import de.materna.dmn.tester.servlets.exceptions.registration.UsernameInUseException;
import de.materna.dmn.tester.servlets.portal.dto.CreateLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.CreateWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.LoginRequest;
import de.materna.dmn.tester.servlets.portal.dto.RegisterRequest;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/portal")
public class PortalServlet {
	UserRepository userRepository = new UserHibernateH2RepositoryImpl();
	SessionTokenRepository sessionTokenRepository = new SessionTokenHibernateH2RepositoryImpl();

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String body) {
		final LoginRequest loginRequest = (LoginRequest) SerializationHelper.getInstance().toClass(body,
				LoginRequest.class);
		final User user = userRepository.findByUsername(loginRequest.getUsername());
		// -> user.getSalt()
		if (user != null && user.getPassword()
				.equals(BCrypt.hashpw(loginRequest.getPassword(), "$2a$10$uFCpSmJSWBm00LNHVOZD/O"))) {
			final String uuid = UUID.randomUUID().toString();

			// Save session token

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(uuid)).build();
		}
		return Response.status(Response.Status.UNAUTHORIZED).build();
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(String body) throws UsernameInUseException, EmailInUseException {
		final RegisterRequest registerRequest = (RegisterRequest) SerializationHelper.getInstance().toClass(body,
				RegisterRequest.class);
		final String username = registerRequest.getUsername();
		final String email = registerRequest.getEmail();
		final String password = registerRequest.getPassword();
		if (userRepository.findByUsername(username) != null) {
			throw new UsernameInUseException("The username is already in use : " + username);
		}
		if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
			throw new EmailInUseException("The email is already in use : " + email);
		}
		final User newUser = userRepository.register(email, username, password);
		return newUser != null
				? Response.status(Response.Status.CREATED).entity(SerializationHelper.getInstance().toJSON(newUser))
						.build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}

	public Response createLaboratory(String body) throws UserNotFoundException, SessionTokenNotFoundException {
		final CreateLaboratoryRequest createLaboratoryRequest = (CreateLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, CreateLaboratoryRequest.class);

		final UUID tokenUuid = createLaboratoryRequest.getSessionTokenUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(tokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by String : " + tokenUuid);
		}

		final User owner = userRepository.findByUuid(sessionToken.getUserUuid());

		if (owner == null) {
			throw new UserNotFoundException("User not found by uuid : " + sessionToken.getUserUuid());
		}

		final String name = createLaboratoryRequest.getName();
		final String description = createLaboratoryRequest.getDescription();
		final VisabilityType visability = createLaboratoryRequest.getVisability();

		final Laboratory newLaboratory = new LaboratoryHibernateH2RepositoryImpl().create(name, description,
				visability);
		return newLaboratory != null
				? Response.status(Response.Status.CREATED)
						.entity(SerializationHelper.getInstance().toJSON(newLaboratory)).build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}

	public Response createWorkspace(String body) throws UserNotFoundException, SessionTokenNotFoundException {
		final CreateWorkspaceRequest createWorkspaceRequest = (CreateWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, CreateWorkspaceRequest.class);

		final UUID tokenUuid = createWorkspaceRequest.getSessionTokenUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(tokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by String : " + tokenUuid);
		}

		final User owner = userRepository.findByUuid(sessionToken.getUserUuid());

		if (owner == null) {
			throw new UserNotFoundException("User not found by uuid : " + sessionToken.getUserUuid());
		}

		final String name = createWorkspaceRequest.getName();
		final String description = createWorkspaceRequest.getDescription();
		final VisabilityType visability = createWorkspaceRequest.getVisability();

		final Workspace newWorkspace = new WorkspaceHibernateH2RepositoryImpl().create(name, description, visability);
		return newWorkspace != null
				? Response.status(Response.Status.CREATED)
						.entity(SerializationHelper.getInstance().toJSON(newWorkspace)).build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}

}