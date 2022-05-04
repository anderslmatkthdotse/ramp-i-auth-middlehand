package ramp.auth.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import ramp.auth.BO.Organisation;
import ramp.auth.BO.User;
import ramp.auth.Rest.DTO.OrgDTO;
import ramp.auth.Rest.DTO.RoleDTO;
import ramp.auth.Rest.DTO.UserDTO;
import ramp.auth.Rest.OutGoing.Interface.AdminUserOutGoingInterface;
import ramp.auth.Rest.OutGoing.Interface.UserOutGoingInterface;
import ramp.auth.Security.Converters.UserConverter;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

//    @Autowired
//    private UserConverter userConverter;

    @Qualifier("userOutGoing")
    @Autowired
    private UserOutGoingInterface outGoing;

    //TODO tänka ut hur man ska göra med alla admin funktioner
    @Autowired
    private AdminUserOutGoingInterface adminOutGoing;

    public List<UserDTO> getAllUsersByGroup() {
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
        if(admin.isAdmin())
            return adminOutGoing.getUsers();
        return outGoing.getAllUsersByGroups(admin);
    }

    public List<UserDTO> getUsersByUsername(String username){
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
        if(admin.isAdmin())
            return adminOutGoing.getUsersByUsername(admin,username);
        return outGoing.getUsersByUsername(admin,username);
    }

    public UserDTO addNewUser(UserDTO newUser) {
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
//      UserDTO user = null;
      if(admin.isAdmin())
        return adminOutGoing.addNewUser(admin,newUser);
      return outGoing.addNewUser(admin, newUser);

/*        if(admin.isAdmin())
            adminOutGoing.addNewUser(admin,newUser);
        return outGoing.addNewUser(admin, newUser);
*/
        // Ska vara för att dem inte är med i gruppen, vet ej vad för HttpStatus det borde vara.
    }

    public HttpStatus addUserToGroup(UserDTO userDTO, String path) {
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
        if(admin.isAdmin())
            adminOutGoing.addUserToGroup(admin,userDTO,path);
        return outGoing.addUserToGroup(admin, userDTO, path);
    }

    public HttpStatus removeUserFromGroup(UserDTO userDTO, String path) {
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
        if(admin.isAdmin())
            return adminOutGoing.removeUserFromGroup(admin,userDTO,path);
        return outGoing.removeUserFromGroup(admin, userDTO, path);
    }

    public HttpStatus setUserPassword(UserDTO userDTO) {
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
        if(admin.isAdmin())
            return adminOutGoing.updateUserPassword(admin,userDTO);
        return outGoing.updateUserPassword(admin, userDTO);

    }

    public List<RoleDTO> getUserRoles(UserDTO userDTO){
        return outGoing.getUserRoles(userDTO);
    }

    public List<RoleDTO> getAvailableRoles(UserDTO userDTO) {
        return outGoing.getAvailableRoles(userDTO);
    }

    public List<RoleDTO> getRampClientRoles(){
        return outGoing.getRampClientRoles();
    }

    public HttpStatus addUserToRole(RoleDTO[] roleDTOS, String userId) {
        return outGoing.addUserToClientRole(roleDTOS, userId);
    }

    public HttpStatus removeUserFromRole(RoleDTO[] roleDTOS, String userId) {
        return outGoing.removeUserFromRole(roleDTOS, userId);
    }

    public HttpStatus deleteUser(UserDTO userDTO){
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
        if(admin.isAdmin())
            return adminOutGoing.deleteUser(admin,userDTO);
        return outGoing.deleteUser(admin,userDTO);
    }

    //@formatter:off
    private User convertFromDTO(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getCreatedTimestamp(), userDTO.getUsername(), userDTO.isEnabled(),
                userDTO.isEmailVerified(), userDTO.getFirstName(), userDTO.getLastName(), convertFromDTOList(userDTO.getGroups()));
    }

    private List<Organisation> convertFromDTOList(List<OrgDTO> orgDTOList){
        List<Organisation> organisationList = new ArrayList<>();
        for (OrgDTO org : orgDTOList) {
            organisationList.add(new Organisation(org.getId(),org.getName(),org.getPath()));
        }
        return organisationList;
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getCreatedTimestamp(), user.getUsername(), user.isEnabled(),
                user.isEmailVerified(), user.getFirstName(), user.getLastName(), convertToDTO(user.getGroups()));
    }
    private List<OrgDTO> convertToDTO(List<Organisation> organisationList){
        List<OrgDTO> orgDTOList = new ArrayList<>();
        for (Organisation org : organisationList ) {
            orgDTOList.add(new OrgDTO(org.getId(),org.getName(),org.getPath()));
        }
        return orgDTOList;
    }
    //@formatter:on
}
