package ru.karpov.NewsPublic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.restassured.RestAssured;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class KeycloakTestContainers {
    protected static KeycloakToken userTokens;

//    @Container
//    protected static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:21.0.2")
//            .withRealmImportFile("keycloack/realm-export.json");

    @Autowired
    private WebTestClient webTestClient;

//    @DynamicPropertySource
//    static void jwtValidationProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
//                () -> keycloak.getAuthServerUrl() + "/realms/SAT");
//    }

    @BeforeAll
    public static void setUp() {
        //keycloak.start();
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8084" + "/auth/realms/SAT/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        userTokens = authenticateWith("janedoe", "s3cr3t", webClient);
    }
    private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "NewsPublic")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }
//    @AfterAll
//    public static void tearDown() {
//        keycloak.stop();
//    }

//    protected String getJaneDoeBearer() {
//        try {
//            URI authorizationURI = new URIBuilder(keycloak.getAuthServerUrl() + "/realms/SAT/protocol/openid-connect/token").build();
//            WebClient webclient = WebClient.builder()
//                    .build();
//            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//            formData.put("grant_type", Collections.singletonList("password"));
//            formData.put("client_id", Collections.singletonList("NewsPublic"));
//            formData.put("username", Collections.singletonList("janedoe"));
//            formData.put("password", Collections.singletonList("s3cr3t"));
//
//            String result = webclient.post()
//                    .uri(authorizationURI)
//                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                    .body(BodyInserters.fromFormData(formData))
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            JacksonJsonParser jsonParser = new JacksonJsonParser();
//
//            return "Bearer " + jsonParser.parseMap(result)
//                    .get("access_token")
//                    .toString();
//        } catch (URISyntaxException e) {
//            System.out.println("Can't obtain an access token from Keycloak!" + e.toString());
//        }
//
//        return null;
//    }
    public static class KeycloakToken {

        public final String accessToken;

        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

    }
}