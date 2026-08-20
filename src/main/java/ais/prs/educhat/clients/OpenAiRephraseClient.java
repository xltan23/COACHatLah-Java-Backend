package ais.prs.educhat.clients;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ais.prs.educhat.models.enums.FlagCategory;
import tools.jackson.databind.JsonNode;

@Component
public class OpenAiRephraseClient {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${openai.api.model:gpt-4.1-mini}")
    private String model;

    private final RestClient restClient;

    public OpenAiRephraseClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(8));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    // Returns a rephrased version of the message, or null if the call fails / response can't be parsed.
    // Callers must treat null as "no suggestion available" rather than propagating an error.
    public String rephrase(String originalMessage, FlagCategory flagCategory) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", buildSystemPrompt(flagCategory)),
                        Map.of("role", "user", "content", originalMessage)
                ),
                "temperature", 0.3,
                "max_tokens", 200
        );

        try {
            JsonNode response = restClient.post()
                    .uri(baseUrl + CHAT_COMPLETIONS_PATH)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);
            // OpenAI comprise numerous structure, extract specifically choices[0] -> message -> content
            // Returns null if final value cannot be found
            String content = response
                    .path("choices").path(0)
                    .path("message").path("content")
                    .asString(null);

            return content != null ? content.strip() : null;
        } catch (Exception e) {
            System.err.println("OpenAI rephrase call failed: " + e.getMessage());
            return null;
        }
    }

    private String buildSystemPrompt(FlagCategory flagCategory) {
        return """
                You are a message-rewriting assistant for a school chat platform.
                The following student message was flagged as %s.
                Rewrite it so it expresses the same underlying point without the harmful language or tone.
                Keep it brief and in the student's own voice. Reply with ONLY the rewritten message, no explanation, no quotes.
                """.formatted(flagCategory.name().toLowerCase().replace('_', ' '));
    }
}
