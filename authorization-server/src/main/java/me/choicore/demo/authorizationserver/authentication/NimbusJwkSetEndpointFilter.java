/*
 * Copyright 2020-2022 the original author or authors.
 * Modification and adaptation 2024 CHOICORE.
 * Original source: https://github.com/spring-projects/spring-authorization-server/blob/main/oauth2-authorization-server/src/main/java/org/springframework/security/oauth2/server/authorization/web/NimbusJwkSetEndpointFilter.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.choicore.demo.authorizationserver.authentication;

import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.Writer;

/**
 * A {@code Filter} that processes JWK Set requests.
 *
 * @author Joe Grandja
 * @see com.nimbusds.jose.jwk.source.JWKSource
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7517">JSON Web Key (JWK)</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7517#section-5">Section 5 JWK Set Format</a>
 * @since 0.0.1
 */
public final class NimbusJwkSetEndpointFilter extends OncePerRequestFilter {

    /**
     * The following code has been modified from the original.
     * Modification: DEFAULT_JWK_SET_ENDPOINT_URI changed
     * <p>
     * The default endpoint {@code URI} for JWK Set requests.
     */
    private static final String DEFAULT_JWK_SET_ENDPOINT_URI = "/.well-known/jwks.json";

    private final JWKSource<SecurityContext> jwkSource;
    private final JWKSelector jwkSelector;

    /**
     * Constructs a {@code NimbusJwkSetEndpointFilter} using the provided parameters.
     *
     * @param jwkSource the {@code com.nimbusds.jose.jwk.source.JWKSource}
     */
    public NimbusJwkSetEndpointFilter(JWKSource<SecurityContext> jwkSource) {
        this(jwkSource, DEFAULT_JWK_SET_ENDPOINT_URI);
    }


    /**
     * The following code has been modified from the original.
     * Modification: Removed requestMatcher as it was deemed unnecessary for the current implementation.
     * <p>
     * Constructs a {@code NimbusJwkSetEndpointFilter} using the provided parameters.
     *
     * @param jwkSource         the {@code com.nimbusds.jose.jwk.source.JWKSource}
     * @param jwkSetEndpointUri the endpoint {@code URI} for JWK Set requests
     */
    public NimbusJwkSetEndpointFilter(JWKSource<SecurityContext> jwkSource, String jwkSetEndpointUri) {
        Assert.notNull(jwkSource, "jwkSource cannot be null");
        Assert.hasText(jwkSetEndpointUri, "jwkSetEndpointUri cannot be empty");
        this.jwkSource = jwkSource;
        this.jwkSelector = new JWKSelector(new JWKMatcher.Builder().build());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (HttpMethod.GET != HttpMethod.valueOf(request.getMethod())
                && !request.getRequestURI().equals(DEFAULT_JWK_SET_ENDPOINT_URI)
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        final JWKSet jwkSet;
        try {
            jwkSet = new JWKSet(this.jwkSource.get(this.jwkSelector, null));
        } catch (Exception ex) {
            throw new IllegalStateException(STR."Failed to select the JWK(s) -> \{ex.getMessage()}", ex);
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (Writer writer = response.getWriter()) {
            writer.write(jwkSet.toString());
        }
    }
}