package ramp.auth.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ramp.auth.Security.Converters.KeycloakRealmRoleConverter;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class JWTSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Main security function, defines converter and roles that's needed for endpoints.
     *
     * @param http object all the security config is in
     * @throws Exception ?
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        String[] swagger = {
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/v2/api-docs",
                "/webjars/**",

                "/v3/api-docs",
                "/swagger-ui/**"
        };

        http
                .cors()
                .and()
                //@formatter:off
                .authorizeRequests(/*authz -> authz*/)

                    .antMatchers(swagger).permitAll()

                    .antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority("ramp_superUser", "ramp_admin")
                    .antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority("ramp_superUser", "ramp_admin")
                    .antMatchers(HttpMethod.POST, "admin/**/admin").hasAuthority("ramp_admin")

                    .anyRequest().authenticated()
                .and()
                    .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        //@formatter:on
    }

    /**
     * Used to be able to translate JWT to find out authentication and roles within the JWT
     *
     * @return the jwtConverter that translate the JWT
     */
    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return jwtConverter;
    }

    /**
     * Used to check the signed JWT if it comes from a valid source
     *
     * @return jwtDecoder with the public key
     * @throws NoSuchAlgorithmException if the string value if not a valid algo
     * @throws InvalidKeySpecException  if the public key is not a valid public key
     */
    @Bean
    JwtDecoder jwtDecoder() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String keyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi7QClCThkbgIuOObfwnU5pV1WsXYO8tW5V0FW6/IwJizMU+Bvuqswqn0Yjn7bD4pVZ59dm1v0aRgmWjVkiB5jQ9WWWdgQUGufyeNW3mpJg+svpQkECtuYd+jjSa4uAMfIV+NKfC5GnrYRiw8wI0781baYnonChibSDUkq4+Zcqdxs/nLlHYfo880siUHG5cAhjzAwgNrnmN7k9K2WcAiYTRtPKNYLxTNpRpK/ESEqJ/2nwgNEbnmiA8wRUJOdkdvGzb5YmBFnrwk4hII2R52m79/dmhI7FyLno7eRNcLdOuT2L9QkjzkNAGUnKUuu2M2XZKYgpzsSGAUg1Jd9rXUcQIDAQAB";
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyString));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(keySpec);
//        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri()).build();
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(key).
                signatureAlgorithm(SignatureAlgorithm.RS256).build();
        //jwtDecoder.setClaimSetConverter(new KeycloakRealmRoleConverter());
        return jwtDecoder;
    }

    /**
     * Options linked to the frontend to match valid origins and operations
     *
     * @return cors config
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Change this to match your setup in production.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Credentials"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
