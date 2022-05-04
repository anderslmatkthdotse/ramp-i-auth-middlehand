package ramp.auth.Rest.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Representation of the user object in the system")
public class UserDTO {

    @ApiModelProperty(value = "Auto generated id in the system ")
    private String id;
    private long createdTimestamp;
    private String username;

    private boolean enabled;
    private boolean emailVerified;

    private String firstName;
    private String lastName;

    @ApiModelProperty(value = "List of all the organisations the user is in," +
            " why groups is using other system in the backend")
    private List<OrgDTO> groups;

    @ApiModelProperty(value = "Representation of the credentials, wont be sending in GET request, " +
            "only used for POST newUser or PUT reset-password")
    private List<CredentialDTO> credentials;
    @ApiModelProperty(value = "Representation of all roles user have")
    private List<RoleDTO> roleDTOS;

    public UserDTO(String id, long createdTimestamp, String username, boolean enabled, boolean emailVerified,
                   String firstName, String lastName, List<OrgDTO> groups) {
        this.id = id;
        this.createdTimestamp = createdTimestamp;
        this.username = username;
        this.enabled = enabled;
        this.emailVerified = emailVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return getId().equals(userDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
