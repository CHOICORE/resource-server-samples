package me.choicore.demo.authorizationserver.configurer;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import me.choicore.demo.authorizationserver.jose.Jwks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwksConfigurer {

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        return (jwkSelector, _) -> jwkSelector.select(jwkSet());
    }

    @Bean
    public JWKSet jwkSet() {
        RSAKey rsaKey = Jwks.generateRsa();
        return new JWKSet(rsaKey);
    }
}
