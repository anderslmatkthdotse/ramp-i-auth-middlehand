package ramp.auth.Security.Converters;

import net.minidev.json.JSONArray;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import ramp.auth.Security.KeyCloakMockUp.AccessTokenUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserConverter{

    public AccessTokenUser convert(SecurityContext context){
        Jwt jwt = (Jwt) context.getAuthentication().getPrincipal();

        AccessTokenUser user = new AccessTokenUser();
        user.setId(jwt.getClaim("sub"));
        user.setUsername(jwt.getClaim("preferred_username"));

        List<String> groups = new ArrayList<>();
        JSONArray json = jwt.getClaim("groups");
        for (Object o : json) {
            groups.add((String) o);
        }
        user.setGroups(groups);

        Map<String,Object> resource_access = jwt.getClaim("resource_access");
        Map<String,Object> ramp = (Map<String, Object>) resource_access.get("ramp");
        List<String> roles = (List<String>) ramp.get("roles");
        user.setRoles(roles);

        return user;
    }
}
