package com.lifeinventory.identity.infrastructure.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoogleAuthService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthService(
            @Value("${security.google.client-id}") String webClientId,
            @Value("${security.google.ios-client-id:}") String iosClientId,
            @Value("${security.google.android-client-id:}") String androidClientId) {

        List<String> clientIds = new ArrayList<>();
        clientIds.add(webClientId);
        if (iosClientId != null && !iosClientId.isEmpty()) {
            clientIds.add(iosClientId);
        }
        if (androidClientId != null && !androidClientId.isEmpty()) {
            clientIds.add(androidClientId);
        }

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(clientIds)
                .build();
    }

    public Mono<GoogleUserInfo> verifyIdToken(String idToken) {
        return Mono.fromCallable(() -> {
                    GoogleIdToken token = verifier.verify(idToken);
                    if (token == null) {
                        throw new IllegalArgumentException("Invalid Google ID token");
                    }

                    GoogleIdToken.Payload payload = token.getPayload();

                    return new GoogleUserInfo(
                            payload.getSubject(),
                            payload.getEmail(),
                            (String) payload.get("name"),
                            (String) payload.get("picture"),
                            payload.getEmailVerified()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Google token verification failed: {}", e.getMessage()));
    }

    public record GoogleUserInfo(
            String googleId,
            String email,
            String name,
            String pictureUrl,
            boolean emailVerified
    ) {}
}
