package ais.prs.educhat.clients;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ais.prs.educhat.dto.ModerationRequest;
import ais.prs.educhat.dto.ModerationResponse;

@Component
public class ModerationClient {

    private static final String SCORE_PATH = "/score";

    @Value("${moderation.service.url:http://localhost:8000}")
    private String baseUrl;

    private final RestClient restClient;

    public ModerationClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    // Returns the moderation scores for the text, or null if the call fails / response can't be parsed.
    // Callers must treat null as "scores unavailable" rather than propagating an error.
    public ModerationResponse score(String text) {
        try {
            return restClient.post()
                    .uri(baseUrl + SCORE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ModerationRequest(text))
                    .retrieve()
                    .body(ModerationResponse.class);
        } catch (Exception e) {
            System.err.println("Moderation call failed: " + e.getMessage());
            return null;
        }
    }
}
