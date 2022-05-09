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
import javax.xml.registry.JAXRException;

import org.mindrot.jbcrypt.BCrypt;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.laboratory.LaboratoryHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.UserHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.userpermission.UserPermission;
import de.materna.dmn.tester.beans.userpermission.UserPermissionHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.beans.workspace.WorkspaceHibernateH2RepositoryImpl;
import de.materna.dmn.tester.enums.UserPermissionType;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.repositories.LaboratoryRepository;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;
import de.materna.dmn.tester.interfaces.repositories.UserPermissionRepository;
import de.materna.dmn.tester.interfaces.repositories.UserRepository;
import de.materna.dmn.tester.interfaces.repositories.WorkspaceRepository;
import de.materna.dmn.tester.servlets.exceptions.authorization.MissingRightsException;
import de.materna.dmn.tester.servlets.exceptions.database.LaboratoryNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.UserNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.UserPermissionNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.registration.EmailInUseException;
import de.materna.dmn.tester.servlets.exceptions.registration.UsernameInUseException;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.CreateLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.DeleteLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.ReadLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.UpdateLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.LoginRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.RegisterRequest;
import de.materna.dmn.tester.servlets.portal.dto.workspace.CreateWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.workspace.DeleteWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.workspace.ReadWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.workspace.UpdateWorkspaceRequest;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/portal")
public class PortalServlet {
	private final UserRepository userRepository = new UserHibernateH2RepositoryImpl();
	private final SessionTokenRepository sessionTokenRepository = new SessionTokenHibernateH2RepositoryImpl();
	private final LaboratoryRepository laboratoryRepository = new LaboratoryHibernateH2RepositoryImpl();
	private final WorkspaceRepository workspaceRepository = new WorkspaceHibernateH2RepositoryImpl();
	private final UserPermissionRepository userPermissionRepository = new UserPermissionHibernateH2RepositoryImpl();

	@POST
	@Path("/user/login")
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
	@Path("/user/register")
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
			throws SessionTokenNotFoundException, UserPermissionNotFoundException, MissingRightsException {
		final DeleteLaboratoryRequest deleteLaboratoryRequest = (DeleteLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, DeleteLaboratoryRequest.class);

		final UUID sessionTokenUuid = deleteLaboratoryRequest.getSessionTokenUuid();
		final UUID laboratoryUuid = deleteLaboratoryRequest.getLaboratoryUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final UUID userUuid = sessionToken.getUserUuid();
		final List<UserPermission> userPermissions = userPermissionRepository.findByLaboratory(laboratoryUuid).stream()
				.filter(userPermission -> userPermission.getType() == UserPermissionType.OWNER
						|| userPermission.getType() == UserPermissionType.ADMINISTRATOR)
				.collect(Collectors.toList());

		if (userPermissions.size() == 0) {
			throw new UserPermissionNotFoundException("No user is owner or administrator : " + laboratoryUuid);
		}

		final UserPermission userUserPermission = userPermissions.stream()
				.filter(userPermission -> userPermission.getUser() == userUuid).findAny().orElse(null);

		if (userUserPermission != null && userUserPermission.getType() != UserPermissionType.OWNER
				&& userUserPermission.getType() != UserPermissionType.ADMINISTRATOR) {
			throw new MissingRightsException(
					"Missing rights (owner or administrator status needed) : " + laboratoryUuid);
		}

		final Laboratory laboratory = laboratoryRepository.findByUuid(laboratoryUuid);

		if (laboratoryRepository.delete(laboratory)) {
			final List<UserPermission> allUserPermissions = userPermissionRepository.findByWorkspace(laboratoryUuid);
			for (final UserPermission rs : allUserPermissions) {
				userPermissionRepository.delete(rs);
			}
			return Response.status(Response.Status.OK).build();
		}

		return Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/laboratory/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readLaboratory(String body) throws SessionTokenNotFoundException, UserPermissionNotFoundException {
		final ReadLaboratoryRequest readLaboratoryRequest = (ReadLaboratoryRequest) SerializationHelper.getInstance()
				.toClass(body, ReadLaboratoryRequest.class);

		final UUID sessionTokenUuid = readLaboratoryRequest.getSessionTokenUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final UUID userUuid = sessionToken.getUserUuid();
		final UUID laboratoryUuid = readLaboratoryRequest.getLaboratoryUuid();
		final UserPermission userPermission = userPermissionRepository.findByUserAndLaboratory(userUuid,
				laboratoryUuid);

		if (userPermission == null) {
			throw new UserPermissionNotFoundException(
					"User is not in any relation with laboratory : " + laboratoryUuid);
		}

		final Laboratory laboratory = laboratoryRepository.findByUuid(laboratoryUuid);
		return laboratory != null
				? Response.status(Response.Status.FOUND).entity(SerializationHelper.getInstance().toJSON(laboratory))
						.build()
				: Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	@Path("/laboratory/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateLaboratoryDescription(String body) throws SessionTokenNotFoundException,
			UserPermissionNotFoundException, MissingRightsException, JAXRException {
		final UpdateLaboratoryRequest updateLaboratoryRequest = (UpdateLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, UpdateLaboratoryRequest.class);

		final UUID sessionTokenUuid = updateLaboratoryRequest.getSessionTokenUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final UUID userUuid = sessionToken.getUserUuid();
		final UUID laboratoryUuid = updateLaboratoryRequest.getLaboratoryUuid();
		final UserPermission userPermission = userPermissionRepository.findByUserAndLaboratory(userUuid,
				laboratoryUuid);

		if (userPermission == null) {
			throw new UserPermissionNotFoundException(
					"User is not in any relation with laboratory : " + laboratoryUuid);
		}

		if (userPermission.getType() == UserPermissionType.GUEST) {
			throw new MissingRightsException("Missing rights (user is only guest) : " + laboratoryUuid);
		}

		final Laboratory laboratory = laboratoryRepository.findByUuid(laboratoryUuid);
		if (laboratory != null) {
			laboratory.setName(updateLaboratoryRequest.getName());
			laboratory.setDescription(updateLaboratoryRequest.getDescription());
			laboratory.setVisability(updateLaboratoryRequest.getVisability());
			final Laboratory updatedLaboratory = laboratoryRepository.put(laboratory);
			if (updatedLaboratory != null) {
				return Response.status(Response.Status.OK)
						.entity(SerializationHelper.getInstance().toJSON(updatedLaboratory)).build();
			}
		}
		return Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/workspace/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createWorkspace(String body)
			throws UserNotFoundException, SessionTokenNotFoundException, LaboratoryNotFoundException {
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

		final UUID laboratoryUuid = createWorkspaceRequest.getLaboratoryUuid();
		final Laboratory laboratory = laboratoryRepository.findByUuid(laboratoryUuid);

		if (laboratory == null) {
			throw new LaboratoryNotFoundException("Laboratory not found by UUID : " + laboratoryUuid);
		}

		final String name = createWorkspaceRequest.getName();
		final String description = createWorkspaceRequest.getDescription();
		final VisabilityType visability = createWorkspaceRequest.getVisability();

		final Workspace newWorkspace = workspaceRepository.create(name, description, visability);

		if (newWorkspace != null) {
			laboratory.getWorkspaces().add(newWorkspace);
			laboratoryRepository.put(laboratory);
			return Response.status(Response.Status.CREATED)
					.entity(SerializationHelper.getInstance().toJSON(newWorkspace.getUuid())).build();
		}
		return Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/workspace/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteWorkspace(String body)
			throws SessionTokenNotFoundException, UserPermissionNotFoundException, MissingRightsException {
		final DeleteWorkspaceRequest deleteWorkspaceRequest = (DeleteWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, DeleteWorkspaceRequest.class);

		final UUID sessionTokenUuid = deleteWorkspaceRequest.getSessionTokenUuid();
		final UUID workspaceUuid = deleteWorkspaceRequest.getWorkspaceUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final UUID userUuid = sessionToken.getUserUuid();
		final List<UserPermission> workspaceUserPermissions = userPermissionRepository.findByWorkspace(workspaceUuid)
				.stream().filter(userPermission -> userPermission.getType() == UserPermissionType.OWNER
						|| userPermission.getType() == UserPermissionType.ADMINISTRATOR)
				.collect(Collectors.toList());

		if (workspaceUserPermissions.size() == 0) {
			throw new UserPermissionNotFoundException(
					"No user or laboratory is owner or administrator : " + workspaceUuid);
		}

		final UserPermission userUserPermission = workspaceUserPermissions.stream()
				.filter(userPermission -> userPermission.getUser() == userUuid).findAny().orElse(null);

		if (userUserPermission != null) {
			if (userUserPermission.getType() != UserPermissionType.OWNER
					&& userUserPermission.getType() != UserPermissionType.ADMINISTRATOR) {
				throw new MissingRightsException(
						"Missing rights (owner or administrator status needed) : " + workspaceUuid);
			}
		} else {
			final List<Laboratory> laboratories = workspaceUserPermissions.stream()
					.filter(userPermission -> userPermission.getLaboratory() != null)
					.map(userPermission -> laboratoryRepository.findByUuid(userPermission.getLaboratory()))
					.collect(Collectors.toList());
			final Laboratory laboratory = laboratories.stream()
					.filter(lab -> userPermissionRepository.findByUserAndLaboratory(userUuid, lab.getUuid())
							.getType() == UserPermissionType.OWNER
							|| userPermissionRepository.findByUserAndLaboratory(userUuid, lab.getUuid())
									.getType() == UserPermissionType.ADMINISTRATOR)
					.findAny().orElse(null);
			if (laboratory == null) {
				throw new MissingRightsException(
						"Missing rights (owner or administrator status needed) : " + workspaceUuid);
			}
		}

		final Workspace workspace = workspaceRepository.findByUuid(workspaceUuid);
		if (workspaceRepository.delete(workspace)) {
			final List<UserPermission> allUserPermissions = userPermissionRepository.findByWorkspace(workspaceUuid);
			for (final UserPermission userPermission : allUserPermissions) {
				userPermissionRepository.delete(userPermission);
			}
			return Response.status(Response.Status.OK).build();
		}

		return Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/laboratory/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readWorkspace(String body) throws SessionTokenNotFoundException, UserPermissionNotFoundException {
		final ReadWorkspaceRequest readWorkspaceRequest = (ReadWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, ReadWorkspaceRequest.class);

		final UUID sessionTokenUuid = readWorkspaceRequest.getSessionTokenUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final UUID userUuid = sessionToken.getUserUuid();
		final UUID workspaceUuid = readWorkspaceRequest.getWorkspaceUuid();
		final UserPermission userPermission = userPermissionRepository.findByUserAndWorkspace(userUuid, workspaceUuid);

		if (userPermission == null) {
			throw new UserPermissionNotFoundException("User is not in any relation with workspace : " + workspaceUuid);
		}

		final Workspace workspace = workspaceRepository.findByUuid(workspaceUuid);
		return workspace != null
				? Response.status(Response.Status.FOUND).entity(SerializationHelper.getInstance().toJSON(workspace))
						.build()
				: Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	@Path("/workspace/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateWorkspace(String body) throws SessionTokenNotFoundException, UserPermissionNotFoundException,
			MissingRightsException, JAXRException {
		final UpdateWorkspaceRequest updateWorkspaceRequest = (UpdateWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, UpdateWorkspaceRequest.class);

		final UUID sessionTokenUuid = updateWorkspaceRequest.getSessionTokenUuid();
		final SessionToken sessionToken = sessionTokenRepository.findByUuid(sessionTokenUuid);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by UUID : " + sessionTokenUuid);
		}

		final UUID userUuid = sessionToken.getUserUuid();
		final UUID workspaceUuid = updateWorkspaceRequest.getWorkspaceUuid();
		final UserPermission userPermission = userPermissionRepository.findByUserAndWorkspace(userUuid, workspaceUuid);

		if (userPermission == null) {
			throw new UserPermissionNotFoundException("User is not in any relation with workspace : " + workspaceUuid);
		}

		if (userPermission.getType() == UserPermissionType.GUEST) {
			throw new MissingRightsException("Missing rights (user is only guest) : " + workspaceUuid);
		}

		final Workspace workspace = workspaceRepository.findByUuid(workspaceUuid);
		if (workspace != null) {
			workspace.setName(updateWorkspaceRequest.getName());
			workspace.setDescription(updateWorkspaceRequest.getDescription());
			workspace.setVisability(updateWorkspaceRequest.getVisability());
			final Workspace updatedworkspace = workspaceRepository.put(workspace);
			if (updatedworkspace != null) {
				return Response.status(Response.Status.OK)
						.entity(SerializationHelper.getInstance().toJSON(updatedworkspace)).build();
			}
		}
		return Response.status(Response.Status.NOT_MODIFIED).build();
	}
}