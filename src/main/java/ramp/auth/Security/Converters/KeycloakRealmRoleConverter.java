package ramp.auth.Security.Converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class made to convert access token from keycloak and get the roles
 * (roles for a start might be more later)
 */
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * Denna metod är den som extraherar och konvertera access tokenen till roller som används inom metoden HttpsSecurity
     * @param jwt jwt version av access token
     * @return returnerar lista av alla Authoritys användaren har
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        //printAccessToken(jwt);
        final Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().get("resource_access");
        final Map<String, Object> rampAccess = (Map<String, Object>) resourceAccess.get("ramp");
        return ((List<String>) rampAccess.get("roles")).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Denna metod kan användas ör att skriva ut och kontrollera access tokenen.
     * @param jwt jwt version av access token
     */
    private void printAccessToken(Jwt jwt) {
        for (Map.Entry<String, Object> o : jwt.getClaims().entrySet()) {
            System.out.println(o.getKey() + " : " + o.getValue());
        }
    }
}
