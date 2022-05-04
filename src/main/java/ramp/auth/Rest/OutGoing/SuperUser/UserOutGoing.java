package ramp.auth.Rest.OutGoing.SuperUser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ramp.auth.Rest.DTO.*;
import ramp.auth.Rest.OutGoing.Interface.GroupOutGoingInterface;
import ramp.auth.Rest.OutGoing.Interface.UserOutGoingInterface;
import ramp.auth.Rest.OutGoing.OkHttp;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class UserOutGoing implements UserOutGoingInterface {

    /**
     * Denna kan ändras inom application.properties för att ändra inom alla api metoder
     */
    @Value("${auth.server.root.url}")
    private String authServerRootUrl;
    @Value("${auth.server.realmAdmin.url}")
    private String adminUrlPath;

    @Autowired
    private OkHttp okHttp;
    @Autowired
    private GroupOutGoingInterface groupOutGoing;

    public List<UserDTO> getAllUsersByGroups(AccessTokenUser admin) {
        try {
            List<OrgDTO> orgDTOLis = groupOutGoing.getAllOrgs(admin.getId());
            List<UserDTO> userDTOS = new ArrayList<>();
            Gson gson = new GsonBuilder().setLenient().create();
            for (OrgDTO org : orgDTOLis) {
                Response userResponse = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath + "/groups/" + org.getId() + "/members");
                List<UserDTO> foundDTOs = Arrays.asList(gson.fromJson(userResponse.body().string(), UserDTO[].class));
                userResponse.body().close();
                for (UserDTO user : foundDTOs) {
                    if (!userDTOS.contains(user)) {
                        Response orgs = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath +"/users/"+user.getId()+"/groups");
                        List<OrgDTO> orgDTOS = Arrays.asList(gson.fromJson(orgs.body().string(),OrgDTO[].class));
                        orgs.body().close();
                        user.setGroups(orgDTOS);
                        userDTOS.add(user);
                    }
                }
            }
            return userDTOS;
        }catch (IOException e){
            return null;
        }
    }

    @Override
    public List<UserDTO> getUsersByUsername(AccessTokenUser admin, String username) {
        try{
            List<OrgDTO> orgDTOLis = groupOutGoing.getAllOrgs(admin.getId());
            Gson gson = new GsonBuilder().setLenient().create();
            Response response = okHttp.sendGetOkHttp(authServerRootUrl+adminUrlPath+ "/users?username="+username);
            UserDTO[] userDTOS = gson.fromJson(response.body().string(),UserDTO[].class);
            response.body().close();

            List<UserDTO> returnList = new ArrayList<>();
            for (UserDTO user: userDTOS) {
                for (OrgDTO orgDTO: groupOutGoing.getAllOrgs(user.getId())) {
                    if(orgDTOLis.contains(orgDTO))
                        if(!returnList.contains(user)) {
                            Response orgs = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath +"/users/"+user.getId()+"/groups");
                            List<OrgDTO> orgDTOS = Arrays.asList(gson.fromJson(orgs.body().string(),OrgDTO[].class));
                            orgs.body().close();
                            user.setGroups(orgDTOS);
                            returnList.add(user);
                        }
                }
            }
            return returnList;

        }catch (IOException e){
            return null;
        }
    }

    //TODO få den att returnera den nya användaren via query med username exact
    public UserDTO addNewUser(AccessTokenUser admin,UserDTO userDTO) {
        for (OrgDTO org: userDTO.getGroups()) {
            if(!admin.getGroups().contains(org.getPath()))
                return null;
        }
        KeyCloakUser keyCloakUser = new KeyCloakUser(userDTO);
        try {
            Response response = okHttp.sendOkHttpMessageWithObject("POST",
                    authServerRootUrl + adminUrlPath + "/users", keyCloakUser);
            assert response.body() != null;
            response.body().close();
            System.out.println("addUser"+response.toString());
            System.out.println("addUserCode"+response.code());
            if(response.code() == 201) {

                Gson gson = new GsonBuilder().setLenient().create();
                Response responseUser = okHttp.sendGetOkHttp(
                        authServerRootUrl + adminUrlPath + "/users?exact=true&username="+userDTO.getUsername());
                assert responseUser.body() != null;
                UserDTO[] madeUser = gson.fromJson(responseUser.body().string(), UserDTO[].class);
                responseUser.body().close();

                if(userDTO.getRoleDTOS() != null) {
                    RoleDTO[] roleDTOS = new RoleDTO[userDTO.getRoleDTOS().size()];
                    for (int i = 0; i < userDTO.getRoleDTOS().size(); i++) {
                        roleDTOS[i] = userDTO.getRoleDTOS().get(i);
                    }
                    addUserToClientRole(roleDTOS, madeUser[0].getId());
                }
                return madeUser[0];
            }else {
                return null;
            }
        }catch (IOException e){
            return null;
        }
    }

    public HttpStatus updateUserPassword(AccessTokenUser admin, UserDTO userDTO) {
        try {
            List<OrgDTO> adminOrgList = groupOutGoing.getAllOrgs(admin.getId());
            List<OrgDTO> userOrgList = groupOutGoing.getAllOrgs(userDTO.getId());
            if (adminOrgList.isEmpty())
                return HttpStatus.NOT_FOUND;

            Response response;
            for (OrgDTO org : adminOrgList) {
                if (userOrgList.contains(org)) {
                    response = okHttp.sendOkHttpMessageWithObject("PUT", authServerRootUrl + adminUrlPath + "/users/" +
                            userDTO.getId() + "/reset-password", userDTO.getCredentials().get(0));
                    assert response.body() != null;
                    response.body().close();
                    return HttpStatus.valueOf(response.code());
                }
            }
            return HttpStatus.NOT_FOUND;
        }catch (IOException e){
            return HttpStatus.BAD_REQUEST;
        }
    }

    public List<RoleDTO> getUserRoles(UserDTO userDTO) {
        Gson gson = new GsonBuilder().setLenient().create();
        try {
            Response response = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath + "/users/" + userDTO.getId() +
                    "/role-mappings/clients/" + getClient().getId());
            List<RoleDTO> roleDTOList = Arrays.asList(gson.fromJson(response.body().string(), RoleDTO[].class));
            response.body().close();
            return roleDTOList;
        }catch (IOException e){
            return null;
        }
    }

    public List<RoleDTO> getAvailableRoles(UserDTO userDTO) {
        Gson gson = new GsonBuilder().setLenient().create();
        try {
            Response response = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath + "/users/" + userDTO.getId() +
                    "/role-mappings/clients/" + getClient().getId() + "/available");
            List<RoleDTO> roleDTOList = Arrays.asList(gson.fromJson(response.body().string(), RoleDTO[].class));
            response.body().close();
            return roleDTOList;
        }catch (IOException e){
            return null;
        }
    }

    @Override
    public List<RoleDTO> getRampClientRoles() {
        Gson gson = new GsonBuilder().setLenient().create();
        try {
            Response response = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath +
                    "/clients/"+getClient().getId()+"/roles");
            List<RoleDTO> roleDTOList = Arrays.asList(gson.fromJson(response.body().string(), RoleDTO[].class));
            response.body().close();
            return roleDTOList;
        }catch (IOException e){
            return null;
        }
    }

    public HttpStatus addUserToGroup(AccessTokenUser admin, UserDTO userDTO, String groupPath) {
        return handleGroup(admin,userDTO,groupPath,"PUT");
    }

    public HttpStatus removeUserFromGroup(AccessTokenUser admin, UserDTO userDTO, String groupPath) {
        return handleGroup(admin,userDTO,groupPath,"DELETE");
    }

    private HttpStatus handleGroup(AccessTokenUser admin, UserDTO userDTO, String groupPath,String operation) {
        try {
            List<OrgDTO> orgDTOList = groupOutGoing.getAllOrgs(admin.getId());
            if (orgDTOList.isEmpty())
                return HttpStatus.NOT_FOUND;

            Response response;
            for (OrgDTO org : orgDTOList) {
                if (org.getPath().equals(groupPath)) {
                    response = okHttp.sendOkHttpMessageBasedOnParameter(operation, authServerRootUrl + adminUrlPath + "/users/" +
                            userDTO.getId() + "/groups/" + org.getId());
                    assert response.body() != null;
                    response.body().close();
                    return HttpStatus.valueOf(response.code());
                }
            }
            return HttpStatus.NOT_FOUND;
        }catch (IOException e) {
            return HttpStatus.BAD_REQUEST;
        }
    }

    public HttpStatus addUserToClientRole(RoleDTO[] roleDTOS, String userId) {
        try{
            //TODO fundera på hur man ska göra med clientId 8bf98dd2-1429-40cf-a84f-6db8bd9585c0
            String url = authServerRootUrl+adminUrlPath+ "/users/"+ userId+"/role-mappings/clients/" + getClient().getId();
            Response response = okHttp.sendOkHttpMessageWithObject("POST",url,roleDTOS);
            response.body().close();
            return HttpStatus.valueOf(response.code());

        }catch (IOException e){
            return HttpStatus.BAD_REQUEST;
        }
    }
    public HttpStatus removeUserFromRole(RoleDTO[] roleDTOS, String userId) {
        try {
            //TODO fundera på hur man ska göra med clientId 8bf98dd2-1429-40cf-a84f-6db8bd9585c0
            String url = authServerRootUrl+adminUrlPath+ "/users/"+ userId+"/role-mappings/clients/" + getClient().getId();
            Response response = okHttp.sendOkHttpMessageWithObject("DELETE", url, roleDTOS);
            assert response.body() != null;
            response.body().close();
            return HttpStatus.valueOf(response.code());
        }catch (IOException e){
            return HttpStatus.BAD_REQUEST;
        }
    }

    @Override
    public HttpStatus deleteUser(AccessTokenUser admin, UserDTO userDTO) {
        try {
            List<OrgDTO> adminOrgList = groupOutGoing.getAllOrgs(admin.getId());
            List<OrgDTO> userOrgList = groupOutGoing.getAllOrgs(userDTO.getId());
            if (adminOrgList.isEmpty())
                return HttpStatus.NOT_FOUND;


            Response response;
            for (OrgDTO org : adminOrgList) {
                if (userOrgList.contains(org)) {
                    response = okHttp.sendOkHttpMessageBasedOnParameter(
                            "DELETE", authServerRootUrl + adminUrlPath + "/users/" + userDTO.getId());
                    response.body().close();

                    return HttpStatus.valueOf(response.code());
                }
            }
            return HttpStatus.NOT_FOUND;
        }catch (IOException e){
            return HttpStatus.BAD_REQUEST;
        }
    }


    private ClientDTO getClient() throws IOException {
        Gson gson = new Gson();

        Response response = okHttp.sendGetOkHttp(authServerRootUrl+adminUrlPath+"/clients?clientId=ramp");
        ClientDTO[] clientDTOS = gson.fromJson(response.body().string(),ClientDTO[].class);

        System.out.println(clientDTOS[0].toString());
        return clientDTOS[0];


    }

}
