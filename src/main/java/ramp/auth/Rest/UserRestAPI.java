package ramp.auth.Rest;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ramp.auth.Controller.UserController;
import ramp.auth.Rest.DTO.RoleDTO;
import ramp.auth.Rest.DTO.UserDTO;
import ramp.auth.Rest.OutGoing.Admin.AdminUserOutGoing;
import ramp.auth.Rest.OutGoing.SuperUser.UserOutGoing;
import ramp.auth.Security.Converters.UserConverter;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Api(tags = {"Användar API"},description = "Alla API funktioner som gäller hantering av användare")
public class UserRestAPI {

    @Autowired
    private UserController controller;

//    @GetMapping("/getUsers")
//    public ResponseEntity<?> getUsers() {
//        List<UserDTO> userDTOList = controller.getUsers();
//        if (userDTOList.isEmpty())
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
//    }

    @GetMapping("/getAllUsers")
    @ApiOperation(value = "Get all users based on the callers role and organisational belonging")
    public ResponseEntity<List<UserDTO>> getAllUsersByGroup() {
        List<UserDTO> userDTOS = controller.getAllUsersByGroup();
        if (userDTOS != null)
            return new ResponseEntity<>(controller.getAllUsersByGroup(), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getUsersByUsername")
    @ApiOperation(value = "Get all users based on the callers role and organisational belonging " +
            "which also matches the inputted username")
    public ResponseEntity<List<UserDTO>> getUsersByUsername(@RequestParam String username) {
        return new ResponseEntity<>(controller.getUsersByUsername(username), HttpStatus.OK);
    }

    //TODO lägga till användare, endast i adminend egna grupper och liknade.
    @PostMapping("/newUser")
    @ApiOperation(value = "Add new user, can only add users to organisations which also the admin belongs to")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
      System.out.println("NewUser");
        UserDTO returnedUser = controller.addNewUser(userDTO);

        if (returnedUser != null) {
            return new ResponseEntity<>(returnedUser, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/addUserToGroup")
    @ApiOperation(value = "Add user to new organisation, can only add users to organisations admin also belongs to")
    public ResponseEntity<HttpStatus> addUserToGroup(@RequestBody UserDTO userDTO, @RequestParam String orgPath) {
        return new ResponseEntity<>(controller.addUserToGroup(userDTO, orgPath));
    }

    @DeleteMapping("/removeUserFromGroup")
    @ApiOperation(value = "Remove user from organisation, users can only be removed from organisation the admin also belongs to")
    public ResponseEntity<HttpStatus> removeUserFromGroup(@RequestBody UserDTO userDTO, @RequestParam String orgPath) {
        return new ResponseEntity<>(controller.removeUserFromGroup(userDTO, orgPath));
    }

    //TODO PUT /{realm}/users/{id}/reset-password
    @PutMapping("/reset-password")
    @ApiOperation(value = "Reset the users password")
    public ResponseEntity<HttpStatus> resetUserPassword(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(controller.setUserPassword(userDTO));
    }

    @PostMapping("/getUserRoles")
    @ApiOperation(value = "Get all roles belonging to the user")
    public ResponseEntity<List<RoleDTO>> getUserRoles(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(controller.getUserRoles(userDTO), HttpStatus.OK);
    }

    @GetMapping("/getRoles")
    @ApiOperation(value = "Get all roles which exist in the system and can be placed on users")
    public ResponseEntity<List<RoleDTO>> getRoles(){
        return new ResponseEntity<>(controller.getRampClientRoles(), HttpStatus.OK);
    }

    // TODO måste testa hur denna kan fungera på olika sätt
    @PostMapping("/getAvailableRoles")
    @ApiOperation(value = "Get all roles which is available to the user")
    public ResponseEntity<List<RoleDTO>> getAvailableRoles(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(controller.getAvailableRoles(userDTO), HttpStatus.OK);
    }

    @PostMapping("/addUserToRole")
    @ApiOperation(value = "Add the user to role / roles")
    public ResponseEntity<HttpStatus> addUserToRole(@RequestBody RoleDTO[] roleDTOS, @RequestParam String userId) {
        return new ResponseEntity<>(controller.addUserToRole(roleDTOS, userId));
    }

    @DeleteMapping("/removeUserFromRole")
    @ApiOperation(value = "Remove user from specified role / roles")
    public ResponseEntity<HttpStatus> removeUserFromRole(@RequestBody RoleDTO[] roleDTOS, @RequestParam String userId) {
        return new ResponseEntity<>(controller.removeUserFromRole(roleDTOS, userId));
    }

    @DeleteMapping("/deleteUser")
    @ApiOperation(value = "Not implemented yet")
    public ResponseEntity<?> deleteUser(@RequestBody UserDTO userDTO){
        //return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        return new ResponseEntity<>(controller.deleteUser(userDTO), HttpStatus.OK);
    }

    //TODO se till att ta bort är till för att populera keycloak, lägg till även för grupper / organisationer
    @Autowired
    private AdminUserOutGoing userOutGoing;

    @PostMapping("/populateKeyCloak")
    public ResponseEntity<?> populateDB() throws IOException {
        XmlMapper mapper = new XmlMapper();
        File file = new File("src/main/resources/users.xml");

        UserDTO[] userDTO = mapper.readValue(file, UserDTO[].class);
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());

        int i = 0;
        for (UserDTO user : userDTO) {
            user.setUsername(user.getUsername() + i++);
            System.out.println("UserRestApi " + userOutGoing.addNewUser(admin,user));
            //userOutGoing.addNewUser(admin, user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
