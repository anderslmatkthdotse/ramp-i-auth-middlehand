package ramp.auth.Rest.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KeyCloakUser {

    private String id;
    private long createdTimestamp;
    private String username;

    private boolean enabled;
    private boolean emailVerified;

    private String firstName;
    private String lastName;

    private List<String> groups;

    private List<CredentialDTO> credentials;

    public KeyCloakUser(UserDTO userDTO) {
        id = userDTO.getId();
        createdTimestamp = userDTO.getCreatedTimestamp();
        username = userDTO.getUsername();
        enabled = userDTO.isEnabled();
        emailVerified = userDTO.isEmailVerified();
        firstName = userDTO.getFirstName();
        lastName = userDTO.getLastName();
        groups = new ArrayList<>();
        for (OrgDTO org: userDTO.getGroups()) {
            groups.add(org.getPath());
        }
        credentials = userDTO.getCredentials();
    }
}
