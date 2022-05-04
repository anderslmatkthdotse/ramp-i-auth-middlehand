package ramp.auth.Rest.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Representation of roles in the system")
public class RoleDTO {

    @ApiModelProperty(value = "Bool if the role is a clientRole or a realmRole")
    private boolean clientRole;
    private boolean composite;

    // Lägga till composit rep https://www.keycloak.org/docs-api/12.0/rest-api/index.html#_rolerepresentation-composites om det behövs

    private String containerId;
    private String description;

    private String id;
    private String name;
}
