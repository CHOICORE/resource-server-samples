package me.choicore.demo.resourceserver.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceApi {

    @GetMapping("/resource")
    public ResponseEntity<?> getMessages(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(STR."Hello, World! \{jwt.getSubject()}");
    }
}
