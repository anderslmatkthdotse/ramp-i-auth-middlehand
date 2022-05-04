package ramp.auth.Rest.OutGoing.Admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ramp.auth.Rest.DTO.KeyCloakUser;
import ramp.auth.Rest.DTO.OrgDTO;
import ramp.auth.Rest.DTO.RoleDTO;
import ramp.auth.Rest.DTO.UserDTO;
import ramp.auth.Rest.OutGoing.Interface.AdminUserOutGoingInterface;
import ramp.auth.Rest.OutGoing.OkHttp;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AdminUserOutGoing implements AdminUserOutGoingInterface {

    /**
     * Denna kan ändras inom application.properties för att ändra inom alla api metoder
     */
    @Value("${auth.server.root.url}")
    private String authServerRootUrl;
    @Value("${auth.server.realmAdmin.url}")
    private String adminUrlPath;

    @Autowired
    private OkHttp okHttp;

    public List<UserDTO> getUsers() {
        try {
            Response response = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath + "/users");
            if (response.isSuccessful()) {
                Gson gson = new GsonBuilder().setLenient().create();
                List<UserDTO> foundUsers =  Arrays.asList(gson.fromJson(response.body().string(), UserDTO[].class));
                List<UserDTO> returnList = new ArrayList<>();
                for (UserDTO user : foundUsers) {
                    if (!returnList.contains(user)) {
                        Response orgs = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath +"/users/"+user.getId()+"/groups");
                        List<OrgDTO> orgDTOS = Arrays.asList(gson.fromJson(orgs.body().string(),OrgDTO[].class));
                        orgs.body().close();
                        user.setGroups(orgDTOS);
                        returnList.add(user);
                    }
                }
                return returnList;
            }
            return null;
        }catch (IOException e){
            return null;
        }
    }

    @Override
    public List<UserDTO> getAllUsersByGroups(AccessTokenUser admin) {
        return null;
    }

    @Override
    public List<UserDTO> getUsersByUsername(AccessTokenUser admin, String username) {
        return null;
    }

    @Override
    public UserDTO addNewUser(AccessTokenUser admin, UserDTO userDTO) {
        System.out.println("OutGoing:addUser"+userDTO.toString());
        KeyCloakUser keyCloakUser = new KeyCloakUser(userDTO);
        try {
            Response response = okHttp.sendOkHttpMessageWithObject("POST",
                    authServerRootUrl + adminUrlPath + "/users", keyCloakUser);
            System.out.println("Efter Response");
            assert response.body() != null;
            response.body().close();
            System.out.println("Response code"+response.code());
            if(response.code() == 201) {
                Gson gson = new GsonBuilder().setLenient().create();
                Response responseUser = okHttp.sendGetOkHttp(
                        authServerRootUrl + adminUrlPath + "/users?exact=true&username="+userDTO.getUsername());
                System.out.println("efter");
                assert responseUser.body() != null;
              System.out.println("efter body");
                UserDTO[] madeUser = gson.fromJson(responseUser.body().string(), UserDTO[].class);
              System.out.println("efter fromJson"+madeUser.length);
                responseUser.body().close();

                if(userDTO.getRoleDTOS() != null) {
                    RoleDTO[] roleDTOS = new RoleDTO[userDTO.getRoleDTOS().size()];
                    for (int i = 0; i < userDTO.getRoleDTOS().size(); i++) {
                        roleDTOS[i] = userDTO.getRoleDTOS().get(i);
                    }
                    addUserToClientRole(roleDTOS, madeUser[0].getId());
                }
                System.out.println("before return"+madeUser[0].toString());
                return madeUser[0];
            }else {
                return null;
            }
        }catch (IOException e){
            System.out.println(e.toString());
            return null;
        }
    }

    @Override
    public HttpStatus updateUserPassword(AccessTokenUser admin, UserDTO userDTO) {
        return null;
    }

    @Override
    public List<RoleDTO> getUserRoles(UserDTO userDTO) {
        return null;
    }

    @Override
    public List<RoleDTO> getAvailableRoles(UserDTO userDTO) {
        return null;
    }

    @Override
    public List<RoleDTO> getRampClientRoles() {
        return null;
    }

    @Override
    public HttpStatus addUserToGroup(AccessTokenUser admin, UserDTO userDTO, String groupPath) {
        return null;
    }

    @Override
    public HttpStatus removeUserFromGroup(AccessTokenUser admin, UserDTO userDTO, String groupPath) {
        return null;
    }

    @Override
    public HttpStatus addUserToClientRole(RoleDTO[] roleDTOS, String userId) {
        return null;
    }

    @Override
    public HttpStatus removeUserFromRole(RoleDTO[] roleDTOS, String userId) {
        return null;
    }

    @Override
    public HttpStatus deleteUser(AccessTokenUser admin, UserDTO userDTO) {
        try {
            Response response = okHttp.sendOkHttpMessageBasedOnParameter(
                    "DELETE", authServerRootUrl + adminUrlPath + "/users/" + userDTO.getId());
            assert response.body() != null;
            response.body().close();
            return HttpStatus.valueOf(response.code());
        }catch (IOException e){
            return HttpStatus.NOT_FOUND;
        }
    }
}
