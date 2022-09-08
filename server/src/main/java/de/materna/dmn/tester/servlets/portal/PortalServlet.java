package de.materna.dmn.tester.servlets.portal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mindrot.jbcrypt.BCrypt;

import de.materna.dmn.tester.Database;
import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.laboratory.LaboratoryHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.permission.Permission;
import de.materna.dmn.tester.beans.permission.PermissionGroup;
import de.materna.dmn.tester.beans.permission.PermissionHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.rest.RestError;
import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.UserHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.beans.workspace.WorkspaceHibernateH2RepositoryImpl;
import de.materna.dmn.tester.enums.PermissionType;
import de.materna.dmn.tester.enums.RestErrorCode;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.repositories.LaboratoryRepository;
import de.materna.dmn.tester.interfaces.repositories.PermissionRepository;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;
import de.materna.dmn.tester.interfaces.repositories.UserRepository;
import de.materna.dmn.tester.interfaces.repositories.WorkspaceRepository;
import de.materna.dmn.tester.servlets.exceptions.authorization.MissingRightsException;
import de.materna.dmn.tester.servlets.exceptions.database.LaboratoryNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.PermissionNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.UserNotFoundException;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.CreateLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.DeleteLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.ReadLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.UpdateLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.sessionToken.ReadSessionTokenRequest;
import de.materna.dmn.tester.servlets.portal.dto.sessionToken.UpdateSessionTokenRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.ChangeEmailRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.ChangePasswordRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.ChangeSystemAdminStateRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.DeleteUserRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.LoginRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.ReadUserRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.RegisterRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.UpdateUserRequest;
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
	private final PermissionRepository permissionRepository = new PermissionHibernateH2RepositoryImpl();

	@POST
	@Path("/user/changeEmail")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeEmail(String body) throws SessionTokenNotFoundException {
		final ChangeEmailRequest changeEmailRequest = (ChangeEmailRequest) SerializationHelper.getInstance()
				.toClass(body, ChangeEmailRequest.class);

		final User userFound = userRepository.getByUuid(changeEmailRequest.getUserUuid());

		if (userFound == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		final SessionToken sessionToken = sessionTokenRepository.getByJwt(changeEmailRequest.getJwt());

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by JWT : " + changeEmailRequest.getJwt());
		}

		final User userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());

		if (userCurrent == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		if (userFound.getUuid() == userCurrent.getUuid() || userCurrent.isSystemAdmin()) {
			userFound.setEmail(changeEmailRequest.getEmail());
			final User userUpdated = userRepository.put(userFound);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userUpdated))
					.build();
		}

		return Response.status(Response.Status.FORBIDDEN).build();
	}

	@POST
	@Path("/user/changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(String body) throws SessionTokenNotFoundException {
		final ChangePasswordRequest changeEmailRequest = (ChangePasswordRequest) SerializationHelper.getInstance()
				.toClass(body, ChangePasswordRequest.class);

		final User userFound = userRepository.getByUuid(changeEmailRequest.getUserUuid());

		if (userFound == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		final SessionToken sessionToken = sessionTokenRepository.getByJwt(changeEmailRequest.getJwt());

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by JWT : " + changeEmailRequest.getJwt());
		}

		final User userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());

		if (userCurrent == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		if ((userFound.getUuid() == userCurrent.getUuid() || userCurrent.isSystemAdmin())
				&& BCrypt.checkpw(changeEmailRequest.getPasswordOld(), userFound.getPassword())) {
			userFound.setSalt(BCrypt.gensalt());
			userFound.setPassword(BCrypt.hashpw(changeEmailRequest.getPasswordNew(), userFound.getSalt()));
			final User userUpdated = userRepository.put(userFound);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userUpdated))
					.build();
		}
		return Response.status(Response.Status.FORBIDDEN).build();
	}

	@POST
	@Path("/user/changeSystemAdminState")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeSystemAdminState(String body) throws SessionTokenNotFoundException {
		final ChangeSystemAdminStateRequest changeSystemAdminStateRequest = (ChangeSystemAdminStateRequest) SerializationHelper
				.getInstance().toClass(body, ChangeSystemAdminStateRequest.class);

		User userFound;
		SessionToken sessionToken;
		try {
			userFound = userRepository.getByUuid(changeSystemAdminStateRequest.getUserUuid());
			sessionToken = sessionTokenRepository.getByJwt(changeSystemAdminStateRequest.getJwt());
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException(
					"SessionToken not found by JWT : " + changeSystemAdminStateRequest.getJwt());
		}

		final User userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());

		if (userCurrent == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		if (userCurrent.isSystemAdmin()) {
			userFound.setSystemAdmin(changeSystemAdminStateRequest.isSystemAdmin());
			final User userUpdated = userRepository.put(userFound);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userUpdated))
					.build();
		}
		return Response.status(Response.Status.FORBIDDEN).build();
	}

	@POST
	@Path("/user/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(String body) {
		final DeleteUserRequest deleteUserRequest = (DeleteUserRequest) SerializationHelper.getInstance().toClass(body,
				DeleteUserRequest.class);

		final User userFound = userRepository.getByUuid(deleteUserRequest.getUserUuid());

		if (userFound == null) {
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, deleteUserRequest.getUserUuid())).build();
		}

		final SessionToken sessionToken = sessionTokenRepository.getByJwt(deleteUserRequest.getJwt());

		if (sessionToken == null) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, deleteUserRequest.getJwt())).build();
		}

		final User userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());

		if (userCurrent == null) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, deleteUserRequest.getJwt())).build();
		}

		if (userFound.getUuid() == userCurrent.getUuid() || userCurrent.isSystemAdmin()) {
			if (userRepository.delete(userFound)) {
				final String userUuid = deleteUserRequest.getUserUuid();

				final List<Permission> allUserPermissions = permissionRepository.getByUserUuid(userUuid);
				for (final Permission userPermission : allUserPermissions) {
					permissionRepository.delete(userPermission);
				}

				final List<Workspace> allWorkspaces = workspaceRepository.getByUser(userUuid);
				for (final Workspace workspace : allWorkspaces) {
					workspaceRepository.delete(workspace);
				}

				final List<Laboratory> allLaboratories = laboratoryRepository.getByUser(userUuid);
				for (final Laboratory laboratory : allLaboratories) {
					laboratoryRepository.delete(laboratory);
				}
			}
			return Response.status(Response.Status.OK).build();
		}
		return Response.status(Response.Status.FORBIDDEN).entity(new RestError(RestErrorCode.MISSING_RIGHTS)).build();
	}

	@POST
	@Path("/user/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String body) {
		final LoginRequest loginRequest = (LoginRequest) SerializationHelper.getInstance().toClass(body,
				LoginRequest.class);
		final User user = userRepository.getByUsername(loginRequest.getUsername());

		if (user == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity(new RestError(RestErrorCode.USERNAME_NOT_FOUND))
					.build();
		}

		if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
			return Response.status(Response.Status.UNAUTHORIZED).entity(new RestError(RestErrorCode.PASSWORD_WRONG))
					.build();
		}

		final SessionToken sessionToken = new SessionToken(user);
		sessionTokenRepository.put(sessionToken);
		return Response.status(Response.Status.OK)
				.entity(SerializationHelper.getInstance().toJSON(sessionToken.getJwt())).build();
	}

	@POST
	@Path("/user/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readUser(String body) {
		final ReadUserRequest readUserRequest = (ReadUserRequest) SerializationHelper.getInstance().toClass(body,
				ReadUserRequest.class);

		final User userFound = userRepository.getByUuid(readUserRequest.getUserUuid());

		if (userFound == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		final SessionToken sessionToken = sessionTokenRepository.getByJwt(readUserRequest.getJwt());

		if (sessionToken != null) {
			final User userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());
			if (userFound.getUuid() == userCurrent.getUuid() || userCurrent.isSystemAdmin()) {
				return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userFound))
						.build();
			}
			return Response.status(Response.Status.FORBIDDEN).entity(new RestError(RestErrorCode.MISSING_RIGHTS))
					.build();
		}
		return Response.status(Response.Status.UNAUTHORIZED).entity(new RestError(RestErrorCode.NOT_LOGGED_IN)).build();
	}

	@POST
	@Path("/user/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(String body) {
		final RegisterRequest registerRequest = (RegisterRequest) SerializationHelper.getInstance().toClass(body,
				RegisterRequest.class);
		final String username = registerRequest.getUsername();
		final String email = registerRequest.getEmail();
		final String password = registerRequest.getPassword();

		if (userRepository.getByUsername(username) != null) {
			return Response.status(Response.Status.CONFLICT).entity(new RestError(RestErrorCode.USERNAME_TAKEN))
					.build();
		}

		if (userRepository.getByEmail(registerRequest.getEmail()) != null) {
			return Response.status(Response.Status.CONFLICT).entity(new RestError(RestErrorCode.EMAIL_TAKEN)).build();
		}

		final User newUser = userRepository.register(email, username, password);
		final Laboratory laboratory = laboratoryRepository.create(Database.MY_LAB.get("NAME"),
				Database.MY_LAB.get("DESCRIPTION"), VisabilityType.PRIVATE);
		permissionRepository.create(newUser.getUuid(), laboratory.getUuid(), null, PermissionType.OWNER);
		return newUser != null
				? Response.status(Response.Status.CREATED).entity(SerializationHelper.getInstance().toJSON(newUser))
						.build()
				: Response.status(Response.Status.NOT_MODIFIED).entity(new RestError(RestErrorCode.NOT_CREATED))
						.build();
	}

	@POST
	@Path("/user/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(String body) throws SessionTokenNotFoundException {
		final UpdateUserRequest updateUserRequest = (UpdateUserRequest) SerializationHelper.getInstance().toClass(body,
				UpdateUserRequest.class);

		final User userFound = userRepository.getByUuid(updateUserRequest.getUserUuid());

		if (userFound == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		final SessionToken sessionToken = sessionTokenRepository.getByJwt(updateUserRequest.getJwt());

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by JWT : " + updateUserRequest.getJwt());
		}

		final User userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());

		if (userFound.getUuid() == userCurrent.getUuid() || userCurrent.isSystemAdmin()) {
			userFound.setUsername(updateUserRequest.getUsername());
			userFound.setLastname(updateUserRequest.getLastname());
			userFound.setFirstname(updateUserRequest.getFirstname());
			final User userUpdated = userRepository.put(userFound);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userUpdated))
					.build();
		}

		return Response.status(Response.Status.FORBIDDEN).build();
	}

	@POST
	@Path("/sessiontoken/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readSessionToken(String body) throws SessionTokenNotFoundException {
		final ReadSessionTokenRequest readSessionTokenRequest = (ReadSessionTokenRequest) SerializationHelper
				.getInstance().toClass(body, ReadSessionTokenRequest.class);

		final SessionToken sessionTokenFound = sessionTokenRepository.getByJwt(readSessionTokenRequest.getJwt());

		if (sessionTokenFound == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sessionTokenFound))
				.build();

	}

	@POST
	@Path("/sessiontoken/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSessionToken(String body) throws SessionTokenNotFoundException {
		final UpdateSessionTokenRequest updateSessionTokenRequest = (UpdateSessionTokenRequest) SerializationHelper
				.getInstance().toClass(body, UpdateSessionTokenRequest.class);

		final SessionToken sessionTokenFound = sessionTokenRepository.getByJwt(updateSessionTokenRequest.getJwt());

		if (sessionTokenFound == null) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		final SessionToken sessionTokenUpdated = sessionTokenRepository.update(sessionTokenFound);

		if (sessionTokenUpdated == null) {
			return Response.status(Response.Status.NOT_MODIFIED).build();
		}

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sessionTokenUpdated))
				.build();
	}

	@POST
	@Path("/laboratory/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createLaboratory(String body) throws UserNotFoundException, SessionTokenNotFoundException {
		final CreateLaboratoryRequest createLaboratoryRequest = (CreateLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, CreateLaboratoryRequest.class);

		final SessionToken sessionToken = sessionTokenRepository.getByJwt(createLaboratoryRequest.getJwt());

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException(
					"SessionToken not found by JWT : " + createLaboratoryRequest.getJwt());
		}

		if (userRepository.getByUuid(sessionToken.getUserUuid()) == null) {
			throw new UserNotFoundException("User not found by session token : " + sessionToken.getUserUuid());
		}

		final Laboratory laboratoryNew = laboratoryRepository.create(createLaboratoryRequest.getName(),
				createLaboratoryRequest.getDescription(), createLaboratoryRequest.getVisability());
		return laboratoryNew != null
				? Response.status(Response.Status.CREATED)
						.entity(SerializationHelper.getInstance().toJSON(laboratoryNew)).build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/laboratory/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteLaboratory(String body) throws SessionTokenNotFoundException, MissingRightsException {
		final DeleteLaboratoryRequest deleteLaboratoryRequest = (DeleteLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, DeleteLaboratoryRequest.class);

		final Laboratory laboratory = laboratoryRepository.getByUuid(deleteLaboratoryRequest.getLaboratoryUuid());
		if (laboratory != null) {
			checkUserPermission(laboratory.getUuid(), deleteLaboratoryRequest.getJwt(), PermissionGroup.ADMINISTRATOR);

			if (laboratoryRepository.delete(laboratory)) {
				final List<Permission> allUserPermissions = permissionRepository
						.getByLaboratoryUuid(deleteLaboratoryRequest.getLaboratoryUuid());
				for (final Permission userPermission : allUserPermissions) {
					permissionRepository.delete(userPermission);
				}
				return Response.status(Response.Status.OK).build();
			}
		}
		return Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/laboratory/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readLaboratory(String body) throws SessionTokenNotFoundException, MissingRightsException {
		final ReadLaboratoryRequest readLaboratoryRequest = (ReadLaboratoryRequest) SerializationHelper.getInstance()
				.toClass(body, ReadLaboratoryRequest.class);

		if (readLaboratoryRequest.getLaboratoryUuid() == null) {
			final User userCurrent = userRepository.getByJwt(readLaboratoryRequest.getJwt());
			final List<Laboratory> laboratories = laboratoryRepository.getByUser(userCurrent.getUuid());

			return Response.status(Response.Status.FOUND).entity(SerializationHelper.getInstance().toJSON(laboratories))
					.build();
		}
		final Laboratory laboratory = laboratoryRepository.getByUuid(readLaboratoryRequest.getLaboratoryUuid());
		if (laboratory != null) {
			checkUserPermission(laboratory.getUuid(), readLaboratoryRequest.getJwt(), PermissionGroup.GUEST);
			final List<Laboratory> laboratories = new ArrayList<>();
			laboratories.add(laboratory);

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(laboratories))
					.build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@POST
	@Path("/laboratory/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateLaboratory(String body)
			throws SessionTokenNotFoundException, PermissionNotFoundException, MissingRightsException {
		final UpdateLaboratoryRequest updateLaboratoryRequest = (UpdateLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, UpdateLaboratoryRequest.class);

		final Laboratory laboratory = laboratoryRepository.getByUuid(updateLaboratoryRequest.getLaboratoryUuid());
		if (laboratory != null) {
			checkUserPermission(laboratory.getUuid(), updateLaboratoryRequest.getJwt(), PermissionGroup.CONTRIBUTOR);

			laboratory.setName(updateLaboratoryRequest.getName());
			laboratory.setDescription(updateLaboratoryRequest.getDescription());
			laboratory.setVisability(updateLaboratoryRequest.getVisability());
			final Laboratory laboratoryUpdated = laboratoryRepository.put(laboratory);
			if (laboratoryUpdated != null) {
				return Response.status(Response.Status.OK)
						.entity(SerializationHelper.getInstance().toJSON(laboratoryUpdated)).build();
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

		final SessionToken sessionToken = sessionTokenRepository.getByJwt(createWorkspaceRequest.getJwt());

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException(
					"SessionToken not found by JWT : " + createWorkspaceRequest.getJwt());
		}

		if (userRepository.getByUuid(sessionToken.getUserUuid()) == null) {
			throw new UserNotFoundException("User not found by session token : " + sessionToken.getUserUuid());
		}

		final String laboratoryUuid = createWorkspaceRequest.getLaboratoryUuid();
		if (laboratoryUuid != null) {
			if (laboratoryRepository.getByUuid(laboratoryUuid) == null) {
				throw new LaboratoryNotFoundException("Laboratory not found by UUID : " + laboratoryUuid);
			}

			final Workspace workspaceNew = workspaceRepository.create(createWorkspaceRequest.getName(),
					createWorkspaceRequest.getDescription(), createWorkspaceRequest.getVisability(), laboratoryUuid);

			if (workspaceNew != null) {
				return Response.status(Response.Status.CREATED)
						.entity(SerializationHelper.getInstance().toJSON(workspaceNew)).build();
			}
		}
		return Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/workspace/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteWorkspace(String body) throws SessionTokenNotFoundException, MissingRightsException {
		final DeleteWorkspaceRequest deleteWorkspaceRequest = (DeleteWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, DeleteWorkspaceRequest.class);

		final Workspace workspace = workspaceRepository.getByUuid(deleteWorkspaceRequest.getWorkspaceUuid());
		if (workspace != null) {
			try {
				checkUserPermission(workspace.getUuid(), deleteWorkspaceRequest.getJwt(),
						PermissionGroup.ADMINISTRATOR);
			} catch (SessionTokenNotFoundException | MissingRightsException e) {
				checkUserPermission(workspace.getLaboratoryUuid(), deleteWorkspaceRequest.getJwt(),
						PermissionGroup.ADMINISTRATOR);
			}

			if (workspaceRepository.delete(workspace)) {
				final List<Permission> allUserPermissions = permissionRepository
						.getByWorkspaceUuid(deleteWorkspaceRequest.getWorkspaceUuid());
				for (final Permission userPermission : allUserPermissions) {
					permissionRepository.delete(userPermission);
				}
				return Response.status(Response.Status.OK).build();
			}
		}
		return Response.status(Response.Status.NOT_MODIFIED).build();
	}

	@POST
	@Path("/workspace/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readWorkspace(String body) throws SessionTokenNotFoundException, MissingRightsException {
		final ReadWorkspaceRequest readWorkspaceRequest = (ReadWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, ReadWorkspaceRequest.class);

		if (readWorkspaceRequest.getWorkspaceUuid() == null) { // not only one single workspace
			final List<Workspace> workspaces = new ArrayList<>();
			final User userCurrent = userRepository.getByJwt(readWorkspaceRequest.getJwt());
			if (userCurrent != null) {
				final List<Laboratory> laboratories = laboratoryRepository.getByUser(userCurrent.getUuid());
				for (int lab = 0; lab < laboratories.size(); lab++) {
					final List<Permission> permissions = permissionRepository
							.getByLaboratoryUuid(laboratories.get(lab).getUuid());
					for (int permission = 0; permission < permissions.size(); permission++) {
						final String workspaceUuid = permissions.get(permission).getWorkspaceUuid();
						if (workspaceUuid != null) {
							final Workspace workspace = workspaceRepository.getByUuid(workspaceUuid);
							if (workspace != null) {
								workspaces.add(workspace);
							}
						}
					}
				}
			}
			return Response.status(Response.Status.FOUND).entity(SerializationHelper.getInstance().toJSON(workspaces))
					.build();
		}
		final Workspace workspace = workspaceRepository.getByUuid(readWorkspaceRequest.getWorkspaceUuid());
		if (workspace != null) {
			checkUserPermission(workspace.getUuid(), readWorkspaceRequest.getJwt(), PermissionGroup.GUEST);
			final List<Workspace> workspaces = new ArrayList<>();
			workspaces.add(workspace);

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspaces))
					.build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@POST
	@Path("/workspace/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateWorkspace(String body)
			throws SessionTokenNotFoundException, PermissionNotFoundException, MissingRightsException {
		final UpdateWorkspaceRequest updateWorkspaceRequest = (UpdateWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, UpdateWorkspaceRequest.class);

		final Workspace workspace = workspaceRepository.getByUuid(updateWorkspaceRequest.getWorkspaceUuid());
		if (workspace != null) {
			checkUserPermission(workspace.getUuid(), updateWorkspaceRequest.getJwt(), PermissionGroup.CONTRIBUTOR);

			workspace.setName(updateWorkspaceRequest.getName());
			workspace.setDescription(updateWorkspaceRequest.getDescription());
			workspace.setVisability(updateWorkspaceRequest.getVisability());
			final Workspace updatedWorkspace = workspaceRepository.put(workspace);
			if (updatedWorkspace != null) {
				return Response.status(Response.Status.OK)
						.entity(SerializationHelper.getInstance().toJSON(updatedWorkspace)).build();
			}
		}
		return Response.status(Response.Status.NOT_MODIFIED).build();
	}

	private void checkUserPermission(String targetUuid, String jwt, PermissionType[] userPermissionGroup)
			throws SessionTokenNotFoundException, MissingRightsException {
		final SessionToken sessionToken = sessionTokenRepository.getByJwt(jwt);

		if (sessionToken == null) {
			throw new SessionTokenNotFoundException("SessionToken not found by JWT : " + jwt);
		}

		final String userUuid = sessionToken.getUserUuid();
		if (!userRepository.getByUuid(userUuid).isSystemAdmin()) {
			final Permission userPermissionLaboratory = permissionRepository.getByUserAndLaboratoryUuids(userUuid,
					targetUuid);
			if (userPermissionLaboratory == null
					|| !Arrays.asList(userPermissionGroup).contains(userPermissionLaboratory.getType())) {
				final Permission userPermissionWorkspace = permissionRepository.getByUserAndWorkspaceUuids(userUuid,
						targetUuid);
				if (userPermissionWorkspace == null
						|| !Arrays.asList(userPermissionGroup).contains(userPermissionWorkspace.getType())) {
					throw new MissingRightsException("User is missing rights : " + targetUuid);
				}
			}
		}
	}
}