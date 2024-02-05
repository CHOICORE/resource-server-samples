package me.choicore.demo.authorizationserver.authentication;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.produce.JWSSignerFactory;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class AuthenticationApi {

    private final JWKSet jwkSet;

    public AuthenticationApi(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @PostMapping("/token")
    public ResponseEntity<?> issueToken() throws Exception {
        List<JWK> keys = jwkSet.getKeys();
        JWK jwk = keys.getFirst();
        String keyID = jwk.getKeyID();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID(keyID)
                .jwkURL(URI.create("http://localhost:9090/.well-known/jwks.json"))
                .x509CertSHA256Thumbprint(jwk.getX509CertSHA256Thumbprint())
                .build();

        JWSSignerFactory signerFactory = new DefaultJWSSignerFactory();

        JWSSigner jwsSigner = signerFactory.createJWSSigner(jwk);

        Instant issuedAt = Instant.now();
        JWTClaimsSet payload = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .notBeforeTime(Date.from(issuedAt))
                .audience("account")
                .issuer("http://localhost:9090")
                .subject("demo-user")
                .claim("scope", new String[]{"message.read", "message.write"})
                .expirationTime(Date.from(issuedAt.plus(1, ChronoUnit.HOURS)))
                .issueTime(Date.from(issuedAt))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, payload);
        signedJWT.sign(jwsSigner);
        String serialize = signedJWT.serialize();
        return ResponseEntity.ok(serialize);
    }
}
