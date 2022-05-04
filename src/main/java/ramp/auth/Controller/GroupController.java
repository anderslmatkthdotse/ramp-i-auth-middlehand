package ramp.auth.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import ramp.auth.Rest.DTO.OrgDTO;
import ramp.auth.Rest.OutGoing.Interface.AdminGroupOutGoingInterface;
import ramp.auth.Rest.OutGoing.Interface.AdminUserOutGoingInterface;
import ramp.auth.Rest.OutGoing.Interface.GroupOutGoingInterface;
import ramp.auth.Security.Converters.UserConverter;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.util.*;

@Controller
public class GroupController {

    @Qualifier("groupOutGoing")
    @Autowired
    private GroupOutGoingInterface outGoing;

    @Autowired
    private AdminGroupOutGoingInterface adminGroupOutGoing;

    public List<OrgDTO> getAllOrgsByToken(){
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
/*        if(admin.isAdmin())
            return adminGroupOutGoing.getAllClientOrgs();
        return outGoing.getAllOrgs(admin.getId());*/
      if(admin.isAdmin()) {
        List<OrgDTO>list =  adminGroupOutGoing.getAllClientOrgs();
        System.out.println("Orgs:"+list.toString());
        System.out.println("Orgs list:"+flattenList(list).toString());
        return flattenList(list);
      }
      return outGoing.getAllOrgs(admin.getId());
    }

    private List<OrgDTO> flattenList(List<OrgDTO> orgs) {
      List<OrgDTO> list  = new ArrayList<OrgDTO>();
      for (int i = 0; i < orgs.size();i++) {
        OrgDTO org = orgs.get(i);
        list.add(org);
        list.addAll(flattenList(org.getSubGroups()));
      }
      return list;
    }

  public List<OrgDTO> getAllOrgs(){
    UserConverter userConverter = new UserConverter();
    AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
/*        if(admin.isAdmin())
            return adminGroupOutGoing.getAllClientOrgs();
        return outGoing.getAllOrgs(admin.getId());*/
    if(admin.isAdmin()) {
      List<OrgDTO>list =  adminGroupOutGoing.getAllOrgs(admin.getId());
      System.out.println("Orgs:"+list.toString());
      return list;
    }
    return outGoing.getAllOrgs(admin.getId());
  }

    public OrgDTO getOrgById(String id){
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
        return outGoing.getOrgById(admin,id);
    }

    public HttpStatus addTopLvlGroup(OrgDTO orgDTO) {
        return adminGroupOutGoing.addTopLvlGroup(orgDTO);
    }

    public OrgDTO addSubGroup(OrgDTO orgDTO, String parentId) {
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());
        if(admin.isAdmin())
            return adminGroupOutGoing.addSubGroup(admin,orgDTO,parentId);
        return outGoing.addSubGroup(admin,orgDTO, parentId);
    }

    public HttpStatus removeSubGroup(String groupId){
        UserConverter userConverter = new UserConverter();
        AccessTokenUser admin = userConverter.convert(SecurityContextHolder.getContext());

        if(admin.isAdmin())
            return adminGroupOutGoing.removeSubGroup(admin,groupId);
        return outGoing.removeSubGroup(admin,groupId);
    }

}
