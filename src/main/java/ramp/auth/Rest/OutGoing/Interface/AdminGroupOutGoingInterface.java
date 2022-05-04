package ramp.auth.Rest.OutGoing.Interface;

import org.springframework.http.HttpStatus;
import ramp.auth.Rest.DTO.OrgDTO;

import java.util.List;

public interface AdminGroupOutGoingInterface extends GroupOutGoingInterface{

    HttpStatus addTopLvlGroup(OrgDTO orgDTO);

    List<OrgDTO> getAllClientOrgs();
}
