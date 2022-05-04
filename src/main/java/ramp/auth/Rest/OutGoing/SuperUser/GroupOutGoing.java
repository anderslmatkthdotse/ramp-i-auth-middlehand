package ramp.auth.Rest.OutGoing.SuperUser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ramp.auth.Rest.DTO.OrgDTO;
import ramp.auth.Rest.OutGoing.Interface.GroupOutGoingInterface;
import ramp.auth.Rest.OutGoing.OkHttp;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GroupOutGoing implements GroupOutGoingInterface {

    /**
     * Denna kan ändras inom application.properties för att ändra inom alla api metoder
     */
    @Value("${auth.server.root.url}")
    private String authServerRootUrl;
    @Value("${auth.server.realmAdmin.url}")
    private String adminUrlPath;

    @Autowired
    private OkHttp okHttp;

    public List<OrgDTO> getAllOrgs(String userId){
        try {
            Response allGroupsResponse = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath + "/users/" + userId + "/groups");
            if (allGroupsResponse.isSuccessful()) {
                Gson gson = new GsonBuilder().setLenient().create();
                List<OrgDTO> orgDTOS = Arrays.asList(gson.fromJson(allGroupsResponse.body().string(), OrgDTO[].class));
                allGroupsResponse.body().close();
                return orgDTOS;
            }
            return new ArrayList<>();
        }catch (IOException e){
            return null;
        }
    }

    public OrgDTO addSubGroup(AccessTokenUser admin,OrgDTO childOrg, String parentId) {
        try {
            OrgDTO parent = getOrgById(parentId);
            List<OrgDTO> adminOrgs = getAllOrgs(admin.getId());
            for (OrgDTO org: adminOrgs) {
                if(parent.getPath().equals(org.getPath())){

                    String url = authServerRootUrl + adminUrlPath + "/groups/" + parentId + "/children";
                    Response response = okHttp.sendOkHttpMessageWithObject("POST", url, childOrg);
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().setLenient().create();
                        OrgDTO orgDTO = gson.fromJson(response.body().string(), OrgDTO.class);
                        url = authServerRootUrl + adminUrlPath + "/users/" + admin.getId() + "/groups/" + orgDTO.getId();
                        response = okHttp.sendOkHttpMessageBasedOnParameter("PUT",url);

                        assert response.body() != null;
                        response.body().close();
                        if(response.code() == 204)
                            return orgDTO;
                        else {
                            url = authServerRootUrl + adminUrlPath + "/groups/" + orgDTO.getId();
                            okHttp.sendOkHttpMessageBasedOnParameter("DELETE",url).body().close();
                            return null;
                        }
                    }
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public HttpStatus removeSubGroup(AccessTokenUser admin, String orgId ){
        try{
            List<OrgDTO> adminGroups = getAllOrgs(admin.getId());
            for (OrgDTO orgDTO: adminGroups) {
                if(orgDTO.getId().equals(orgId)){
                    String url = authServerRootUrl + adminUrlPath + "/groups/" + orgId;
                    Response response = okHttp.sendOkHttpMessageBasedOnParameter("DELETE",url);
                    assert response.body() != null;
                    response.body().close();
                    return HttpStatus.valueOf(response.code());
                }
            }
            return HttpStatus.BAD_REQUEST;
        }catch (IOException e){
            return HttpStatus.BAD_REQUEST;
        }
    }

    private OrgDTO getOrgById(String id) throws IOException {
        String url = authServerRootUrl+adminUrlPath+"/groups/"+id;
        Gson gson = new GsonBuilder().setLenient().create();
        return gson.fromJson(okHttp.sendGetOkHttp(url).body().string(),OrgDTO.class);
    }


    @Override
    //TODO ej implementerad
    public OrgDTO getOrgById(AccessTokenUser admin, String id) {
        return null;
    }
}
