package ramp.auth.Rest.OutGoing.Interface;

import org.springframework.http.HttpStatus;
import ramp.auth.Rest.DTO.OrgDTO;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.util.List;

public interface GroupOutGoingInterface {

    List<OrgDTO> getAllOrgs(String userId);

    OrgDTO getOrgById(AccessTokenUser admin,String id);

    OrgDTO addSubGroup(AccessTokenUser admin, OrgDTO childOrg, String parentId);

    HttpStatus removeSubGroup(AccessTokenUser admin, String orgId );

}
