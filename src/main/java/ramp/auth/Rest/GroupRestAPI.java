package ramp.auth.Rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ramp.auth.Controller.GroupController;
import ramp.auth.Rest.DTO.OrgDTO;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/group")
@Api(tags = {"Organisations API"},description = "Alla API funktioner som gäller organisationer")
public class GroupRestAPI {

    @Autowired
    private GroupController controller;

    /*@GetMapping("/getGroups")
    public ResponseEntity<?> getGroups(){
        List<OrgDTO> orgDTOS = controller.getAllGroups();
        if(orgDTOS.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(orgDTOS,HttpStatus.OK);

    }*/

    @PostMapping("/addNewTopLvlOrg")
    @ApiOperation(value = "Add a new top level organisation, can only be done if the user have the right role (ramp_admin)")
//    public ResponseEntity<HttpStatus> addTopLvlOrg(@RequestBody OrgDTO orgDTO){
      public ResponseEntity<HttpStatus> addTopLvlOrg(@RequestParam String name){
        OrgDTO org = new OrgDTO();
        org.setName(name);
      return new ResponseEntity<>(controller.addTopLvlGroup(org), HttpStatus.OK);
//        return new ResponseEntity<>(controller.addTopLvlGroup(orgDTO), HttpStatus.OK);
    }
/*
  @PostMapping("/addOrg")
  @ApiOperation(value = "Add a new top level organisation, can only be done if the user have the right role (ramp_admin)")
//    public ResponseEntity<HttpStatus> addTopLvlOrg(@RequestBody OrgDTO orgDTO){
  public ResponseEntity<HttpStatus> addOrg(@RequestParam String name, @RequestParam String id){
    OrgDTO org = new OrgDTO();
    org.setName(name);
    return new ResponseEntity<>(controller.addSubGroup(org, id), HttpStatus.OK);
//        return new ResponseEntity<>(controller.addTopLvlGroup(orgDTO), HttpStatus.OK);
  }*/

    @GetMapping("/getOrganisations")
    @ApiOperation(value = "Get all organisation based on the admins roles and organisational belonging")
    public ResponseEntity<List<OrgDTO>> getOrganisations(){
        System.out.println("getOrganisation");
        List<OrgDTO> l = controller.getAllOrgsByToken();
        System.out.println("List"+l.size()+":"+l.toString());
        return new ResponseEntity<>(controller.getAllOrgsByToken(), HttpStatus.OK);
    }

    @GetMapping("/getOrganisationById")
    @ApiOperation(value = "Get organisation by the inputed id, but the admin still needs to have access to the organisation like" +
            "the usual getOrganisations")
    public ResponseEntity<OrgDTO> getOrganisationById(@RequestParam String orgId){
        return new ResponseEntity<>(controller.getOrgById(orgId), HttpStatus.OK);
    }

    /**
     * Används för att lägga till sub grupper på existerande grupper, bör endast kunna utföras av en admin inom parent gruppen
     *
     * @param parentId id string of the parent group
     * @param childOrg JSON object of the child
     * @return return httpstatus and copy of the created group if created
     * @throws IOException from sendPostOkHttp
     */
    //TODO denna ska kolla om adminedn är med i parent gruppen
    @PostMapping("/addSubGroup")
    @ApiOperation(value = "Add a new suborganisation to the organisation linked to the parentId, the admin needs to have the access to " +
            "the parentId to be able to do the method")
    public ResponseEntity<OrgDTO> addSubGroup(@RequestParam String parentId, @RequestBody OrgDTO childOrg) {
        System.out.println("Yo subbis0!");
        System.out.println(parentId);
        OrgDTO orgDTO = controller.addSubGroup(childOrg,parentId);
        if(orgDTO != null)
            return new ResponseEntity<>(orgDTO,HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/removeSubGroup")
    @ApiOperation("Remove sub organisation / organisation specifiked to the groupId, admin needs to have access to the organisation")
    public ResponseEntity<HttpStatus> removeSubGroup(@RequestParam String groupId){
        return new ResponseEntity<>(controller.removeSubGroup(groupId));
    }
}
