package ramp.auth.Rest.DTO;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KeyCloakGroup {

  private String id;
  private String name;
  private String path;

  private List<KeyCloakGroup> subGroups;

  public KeyCloakGroup(OrgDTO orgDTO) {
    id = orgDTO.getId();
    name = orgDTO.getName();
    path = orgDTO.getPath();
    subGroups = new ArrayList<>();
    for (OrgDTO org: orgDTO.getSubGroups()) {
      subGroups.add(new KeyCloakGroup(org));
    }
  }
}
