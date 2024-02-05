package me.choicore.demo.authorizationserver.configurer;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import me.choicore.demo.authorizationserver.authentication.NimbusJwkSetEndpointFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ApplicationBeanConfigurer {

    @Bean
    public NimbusJwkSetEndpointFilter nimbusJwkSetEndpointFilter(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwkSetEndpointFilter(jwkSource);
    }

}
