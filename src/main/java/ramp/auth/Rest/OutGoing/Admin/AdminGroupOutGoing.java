package ramp.auth.Rest.OutGoing.Admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ramp.auth.Rest.DTO.OrgDTO;
import ramp.auth.Rest.OutGoing.Interface.AdminGroupOutGoingInterface;
import ramp.auth.Rest.OutGoing.OkHttp;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AdminGroupOutGoing implements AdminGroupOutGoingInterface {

    /**
     * Denna kan ändras inom application.properties för att ändra inom alla api metoder
     */
    @Value("${auth.server.root.url}")
    private String authServerRootUrl;
    @Value("${auth.server.realmAdmin.url}")
    private String adminUrlPath;

    @Autowired
    private OkHttp okHttp;

    public HttpStatus addTopLvlGroup(OrgDTO orgDTO) {
        System.out.println("Tjo");
        try {
            Response response = okHttp.sendOkHttpMessageWithObject("POST", authServerRootUrl + adminUrlPath + "/groups", orgDTO);
            assert response.body() != null;
            response.body().close();
            return HttpStatus.valueOf(response.code());
        } catch (IOException e) {
            return HttpStatus.BAD_REQUEST;
        }
    }
/*
  public HttpStatus addGroup(OrgDTO orgDTO, String id) {
    System.out.println("Tjo group");
    try {
      Response response = okHttp.sendOkHttpMessageWithObject("POST", authServerRootUrl + adminUrlPath + "/groups", orgDTO);
      assert response.body() != null;
      response.body().close();

      String url = authServerRootUrl + adminUrlPath + "/groups/" + parentId + "/children";
      Response response2 = okHttp.sendOkHttpMessageWithObject("POST", url;

      return HttpStatus.valueOf(response.code());
    } catch (IOException e) {
      return HttpStatus.BAD_REQUEST;
    }
  }
*/
    @Override
    public OrgDTO addSubGroup(AccessTokenUser admin, OrgDTO childOrg, String parentId) {
        try {
            String url = authServerRootUrl + adminUrlPath + "/groups/" + parentId + "/children";
            Response response = okHttp.sendOkHttpMessageWithObject("POST", url, childOrg);
            if (response.isSuccessful()) {
                Gson gson = new GsonBuilder().setLenient().create();
                return gson.fromJson(response.body().string(), OrgDTO.class);

            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

  public List<OrgDTO> getAllClientOrgs() {
    try {
      Response response = okHttp.sendGetOkHttp(authServerRootUrl + adminUrlPath + "/groups");
      if (response.isSuccessful()) {
        Gson gson = new GsonBuilder().setLenient().create();
        System.out.println("ORgs:"+gson.toString());
        return Arrays.asList(gson.fromJson(response.body().string(), OrgDTO[].class));
      }
      return null;
    } catch (IOException e) {
      return null;
    }
  }

    @Override
    public HttpStatus removeSubGroup(AccessTokenUser admin, String orgId) {
        try {
            String url = authServerRootUrl + adminUrlPath + "/groups/" + orgId;
            Response response = okHttp.sendOkHttpMessageBasedOnParameter("DELETE", url);
            assert response.body() != null;
            response.body().close();
            return HttpStatus.valueOf(response.code());
        } catch (IOException e) {
            return HttpStatus.BAD_REQUEST;
        }
    }


    @Override
    public List<OrgDTO> getAllOrgs(String userId) {
        return getAllClientOrgs();
    }

    @Override
    public OrgDTO getOrgById(AccessTokenUser admin, String id) {
        return null;
    }


}
