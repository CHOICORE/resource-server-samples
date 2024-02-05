package me.choicore.demo.authorizationserver;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.produce.JWSSignerFactory;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import me.choicore.demo.authorizationserver.jose.Jwks;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtEncoderTests {

    @Test
    void t1() throws JOSEException {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        List<JWK> keys = jwkSet.getKeys();
        JWK jwk = keys.getFirst();
        String keyID = jwk.getKeyID();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
//                .type(JOSEObjectType.JWT)
                .keyID(keyID)
//                .jwkURL(URI.create("http://localhost:9090/.well-known/jwks.json"))
//                .x509CertSHA256Thumbprint(jwk.getX509CertSHA256Thumbprint())
                .build();
        JWSSignerFactory signerFactory = new DefaultJWSSignerFactory();

        JWSSigner jwsSigner = signerFactory.createJWSSigner(jwk);

        Instant issuedAt = Instant.now();
        JWTClaimsSet payload = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .notBeforeTime(Date.from(issuedAt))
                .audience("messaging-client")
                .issuer("http://localhost:9090")
                .subject("user1")
                .claim("scope", new String[]{"message.read", "message.write"})
                .expirationTime(Date.from(issuedAt.plus(1, ChronoUnit.HOURS)))
                .issueTime(Date.from(issuedAt))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, payload);
        signedJWT.sign(jwsSigner);

        String serialize = signedJWT.serialize();
        System.out.println(serialize);
    }
}
