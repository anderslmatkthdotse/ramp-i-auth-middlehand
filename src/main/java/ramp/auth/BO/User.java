package ramp.auth.BO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class User {

    private String id;
    private long createdTimestamp;
    private String username;

    private boolean enabled;
    private boolean emailVerified;

    private String firstName;
    private String lastName;

    private List<Organisation> groups;

}
