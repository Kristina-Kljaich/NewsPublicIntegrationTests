package ru.karpov.NewsPublic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import dasniko.testcontainers.keycloak.KeycloakContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class KeycloakTestContainers {

    protected static final KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("keycloack/realm-export.json");

    @BeforeAll
    public static void setUp() {
        keycloak.start();
    }
    @AfterAll
    public static void tearDown() {
        keycloak.stop();
    }
    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/SAT");
    }

    protected String getJaneDoeBearer() {
        try {
            URI authorizationURI = new URIBuilder(keycloak.getAuthServerUrl() + "/realms/SAT/protocol/openid-connect/token").build();
            WebClient webclient = WebClient.builder()
                    .build();
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.put("grant_type", Collections.singletonList("password"));
            formData.put("client_id", Collections.singletonList("baeldung-api"));
            formData.put("username", Collections.singletonList("jane.doe@baeldung.com"));
            formData.put("password", Collections.singletonList("s3cr3t"));

            String result = webclient.post()
                    .uri(authorizationURI)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JacksonJsonParser jsonParser = new JacksonJsonParser();

            return "Bearer " + jsonParser.parseMap(result)
                    .get("access_token")
                    .toString();
        } catch (URISyntaxException e) {
            System.out.println("Can't obtain an access token from Keycloak!" + e.toString());
        }

        return null;
    }
}