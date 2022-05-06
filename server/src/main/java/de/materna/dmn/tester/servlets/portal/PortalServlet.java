package de.materna.dmn.tester.servlets.portal;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mindrot.jbcrypt.BCrypt;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.laboratory.LaboratoryHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.relationship.Relationship;
import de.materna.dmn.tester.beans.relationship.RelationshipHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.UserHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.beans.workspace.WorkspaceHibernateH2RepositoryImpl;
import de.materna.dmn.tester.enums.RelationshipType;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.repositories.LaboratoryRepository;
import de.materna.dmn.tester.interfaces.repositories.RelationshipRepository;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;
import de.materna.dmn.tester.interfaces.repositories.UserRepository;
import de.materna.dmn.tester.interfaces.repositories.WorkspaceRepository;
import de.materna.dmn.tester.servlets.exceptions.authorization.MissingRightsException;
import de.materna.dmn.tester.servlets.exceptions.database.RelationshipNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.UserNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.registration.EmailInUseException;
import de.materna.dmn.tester.servlets.exceptions.registration.UsernameInUseException;
import de.materna.dmn.tester.servlets.portal.dto.CreateLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.CreateWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.DeleteLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.DeleteWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.LoginRequest;
import de.materna.dmn.tester.servlets.portal.dto.RegisterRequest;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/portal")
public class PortalServlet {
	private final UserRepository userRepository = new UserHibernateH2RepositoryImpl();
	private final SessionTokenRepository sessionTokenRepository = new SessionTokenHibernateH2RepositoryImpl();
	private final LaboratoryRepository laboratoryRepository = new LaboratoryHibernateH2RepositoryImpl();
	private final WorkspaceRepository workspaceRepository = new WorkspaceHibernateH2RepositoryImpl();
	private final RelationshipRepository relationshipRepository = new RelationshipHibernateH2RepositoryImpl();

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String body) {
		final LoginRequest loginRequest = (LoginRequest) SerializationHelper.getInstance().toClass(body,
				LoginRequest.class);
		final User user = userRepository.findByUsername(loginRequest.getUsername());
		if (user != null && user.getPassword().equals(BCrypt.hashpw(loginRequest.getPassword(), user.getSalt()))) {
			final SessionToken sessionToken = new SessionToken(user);
			sessionTokenRepository.put(sessionToken);
			return Response.status(Response.Status.OK)
					.entity(SerializationHelper.getInstance().toJSON(sessionToken.getUuid())).build();
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

	@POST
	@Path("/laboratory/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createLaboratory(String body) throws UserNotFoundException, SessionTokenNotFoundException {
		final CreateLaboratoryRequest createLaboratoryRequest = (CreateLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, CreateLaboratoryRequest.class);

		final UUID sessionTokenUuid = createLaboratoryRequest.getSessionTokenUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final User owner = userRepository.findByUuid(sessionToken.getUserUuid());

		if (owner == null) {
			throw new UserNotFoundException("User not found by session token : " + sessionToken.getUserUuid());
		}

		final String name = createLaboratoryRequest.getName();
		final String description = createLaboratoryRequest.getDescription();
		final VisabilityType visability = createLaboratoryRequest.getVisability();

		final Laboratory newLaboratory = laboratoryRepository.create(name, description, visability);
		return newLaboratory != null
				? Response.status(Response.Status.CREATED)
						.entity(SerializationHelper.getInstance().toJSON(newLaboratory.getUuid())).build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/laboratory/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteLaboratory(String body)
			throws SessionTokenNotFoundException, RelationshipNotFoundException, MissingRightsException {
		final DeleteLaboratoryRequest deleteLaboratoryRequest = (DeleteLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, DeleteLaboratoryRequest.class);

		final UUID sessionTokenUuid = deleteLaboratoryRequest.getSessionTokenUuid();
		final UUID laboratoryUuid = deleteLaboratoryRequest.getLaboratoryUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final UUID userUuid = sessionToken.getUserUuid();
		final List<Relationship> relationships = relationshipRepository.findByLaboratory(laboratoryUuid).stream()
				.filter(relationship -> relationship.getType() == RelationshipType.OWNER
						|| relationship.getType() == RelationshipType.ADMINISTRATOR)
				.collect(Collectors.toList());

		if (relationships.size() == 0) {
			throw new RelationshipNotFoundException("No user is owner or administrator : " + laboratoryUuid);
		}

		final Relationship userRelationship = relationships.stream()
				.filter(relationship -> relationship.getUser() == userUuid).findAny().orElse(null);

		if ((userRelationship != null) && (userRelationship.getType() != RelationshipType.OWNER
				&& userRelationship.getType() != RelationshipType.ADMINISTRATOR)) {
			throw new MissingRightsException(
					"Missing rights (owner or administrator status needed) : " + laboratoryUuid);
		}

		final Laboratory laboratory = laboratoryRepository.findByUuid(laboratoryUuid);
		return laboratoryRepository.delete(laboratory) ? Response.status(Response.Status.OK).build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/workspace/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createWorkspace(String body) throws UserNotFoundException, SessionTokenNotFoundException {
		final CreateWorkspaceRequest createWorkspaceRequest = (CreateWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, CreateWorkspaceRequest.class);

		final UUID sessionTokenUuid = createWorkspaceRequest.getSessionTokenUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final User owner = userRepository.findByUuid(sessionToken.getUserUuid());

		if (owner == null) {
			throw new UserNotFoundException("User not found by session token : " + sessionToken.getUserUuid());
		}

		final String name = createWorkspaceRequest.getName();
		final String description = createWorkspaceRequest.getDescription();
		final VisabilityType visability = createWorkspaceRequest.getVisability();

		final Workspace newWorkspace = workspaceRepository.create(name, description, visability);
		return newWorkspace != null
				? Response.status(Response.Status.CREATED)
						.entity(SerializationHelper.getInstance().toJSON(newWorkspace.getUuid())).build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/workspace/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteWorkspace(String body)
			throws SessionTokenNotFoundException, RelationshipNotFoundException, MissingRightsException {
		final DeleteWorkspaceRequest deleteWorkspaceRequest = (DeleteWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, DeleteWorkspaceRequest.class);

		final UUID sessionTokenUuid = deleteWorkspaceRequest.getSessionTokenUuid();
		final UUID workspaceUuid = deleteWorkspaceRequest.getWorkspaceUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final UUID userUuid = sessionToken.getUserUuid();
		final List<Relationship> relationships = relationshipRepository.findByWorkspace(workspaceUuid).stream()
				.filter(relationship -> relationship.getType() == RelationshipType.OWNER
						|| relationship.getType() == RelationshipType.ADMINISTRATOR)
				.collect(Collectors.toList());

		if (relationships.size() == 0) {
			throw new RelationshipNotFoundException(
					"No user or laboratory is owner or administrator : " + workspaceUuid);
		}

		final Relationship userRelationship = relationships.stream()
				.filter(relationship -> relationship.getUser() == userUuid).findAny().orElse(null);

		if (userRelationship != null) {
			if (userRelationship.getType() != RelationshipType.OWNER
					&& userRelationship.getType() != RelationshipType.ADMINISTRATOR) {
				throw new MissingRightsException(
						"Missing rights (owner or administrator status needed) : " + workspaceUuid);
			}
		} else {
			final List<Laboratory> laboratories = relationships.stream()
					.filter(relationship -> relationship.getLaboratory() != null)
					.map(relationship -> laboratoryRepository.findByUuid(relationship.getLaboratory()))
					.collect(Collectors.toList());
			final Laboratory laboratory = laboratories.stream()
					.filter(lab -> relationshipRepository.findByUserAndLaboratory(userUuid, lab.getUuid())
							.getType() == RelationshipType.OWNER
							|| relationshipRepository.findByUserAndLaboratory(userUuid, lab.getUuid())
									.getType() == RelationshipType.ADMINISTRATOR)
					.findAny().orElse(null);
			if (laboratory == null) {
				throw new MissingRightsException(
						"Missing rights (owner or administrator status needed) : " + workspaceUuid);
			}
		}

		final Workspace workspace = workspaceRepository.findByUuid(workspaceUuid);
		return workspaceRepository.delete(workspace) ? Response.status(Response.Status.OK).build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}
}