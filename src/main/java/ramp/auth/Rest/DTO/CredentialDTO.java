package ramp.auth.Rest.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Representation of users credentials in the system")
public class CredentialDTO {

    private int createdDate;
    private String credentialData;
    private String id;

    private int priority;
    private String secretData;
    @ApiModelProperty(value = "Bool of the password is temporary (only working once) or lasting")
    private boolean temporary;

    private String type;
    private String userLabel;
    @ApiModelProperty(value = "The new password to send")
    private String value;
}
