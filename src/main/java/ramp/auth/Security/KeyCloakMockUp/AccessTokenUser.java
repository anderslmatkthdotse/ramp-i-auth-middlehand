package ramp.auth.Security.KeyCloakMockUp;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccessTokenUser {

    private String id;
    private List<String> groups;

    private String username;

    private List<String> roles;

    // TODO döp om till något annat kommer inte på just nu
    public boolean isAdmin(){
        return roles.contains("ramp_admin");
    }

    public boolean checkIfAdminInGroups(List<String> groups){
        boolean groupIn = false;
        for (String userGroup: groups) {
            for (String adminGroup: this.groups) {
                if (adminGroup.equals(userGroup)) {
                    groupIn = true;
                    break;
                }
            }
            if(!groupIn)
                return false;
        }
        return true;
    }
}
