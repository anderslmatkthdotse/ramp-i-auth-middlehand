package ramp.auth.Rest.OutGoing.Interface;

import org.springframework.http.HttpStatus;
import ramp.auth.Rest.DTO.OrgDTO;
import ramp.auth.Rest.DTO.RoleDTO;
import ramp.auth.Rest.DTO.UserDTO;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.io.IOException;
import java.util.List;

public interface UserOutGoingInterface {

    List<UserDTO> getAllUsersByGroups(AccessTokenUser admin);

    List<UserDTO> getUsersByUsername(AccessTokenUser admin,String username);

    UserDTO addNewUser(AccessTokenUser admin, UserDTO userDTO);

    HttpStatus updateUserPassword(AccessTokenUser admin, UserDTO userDTO);

    List<RoleDTO> getUserRoles(UserDTO userDTO);

    List<RoleDTO> getAvailableRoles(UserDTO userDTO);

    List<RoleDTO> getRampClientRoles();

    HttpStatus addUserToGroup(AccessTokenUser admin, UserDTO userDTO, String groupPath);

    HttpStatus removeUserFromGroup(AccessTokenUser admin, UserDTO userDTO, String groupPath);

    HttpStatus addUserToClientRole(RoleDTO[] roleDTOS, String userId);

    HttpStatus removeUserFromRole(RoleDTO[] roleDTOS, String userId);

    HttpStatus deleteUser(AccessTokenUser admin, UserDTO userDTO);


}
