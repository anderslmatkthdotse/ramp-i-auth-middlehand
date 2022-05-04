package ramp.auth.BO;

import lombok.*;
import ramp.auth.Rest.DTO.OrgDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Organisation {

    private String id;
    private String name;

    private String path;

    private List<Organisation> subGroups;

    public Organisation(String id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }
}
