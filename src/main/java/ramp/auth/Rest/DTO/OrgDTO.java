package ramp.auth.Rest.DTO;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Representation of organisations in the system")
public class OrgDTO {

    private String id;
    private String name;

    private String path;

    private List<OrgDTO> subGroups;

    public OrgDTO(String id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgDTO orgDTO = (OrgDTO) o;
        return getId().equals(orgDTO.getId());
    }

  public boolean equals(String sName) {
      if (name.compareTo(sName) != 0)
        return false;
      else
        return true;
  }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
