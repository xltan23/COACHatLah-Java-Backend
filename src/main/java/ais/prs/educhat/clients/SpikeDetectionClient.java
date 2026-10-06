package ais.prs.educhat.clients;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ais.prs.educhat.dto.SpikeDetectionRequestDto;
import ais.prs.educhat.dto.SpikeDetectionResponseDto;

@Component
public class SpikeDetectionClient {

    private static final String SPIKES_PATH = "/spikes";

    @Value("${spike.service.url:http://localhost:8001}")
    private String baseUrl;

    private final RestClient restClient;

    public SpikeDetectionClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    // Returns the detected spikes, or null if the call fails / response can't be parsed.
    public SpikeDetectionResponseDto detect(SpikeDetectionRequestDto request) {
        try {
            return restClient.post()
                    .uri(baseUrl + SPIKES_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(SpikeDetectionResponseDto.class);
        } catch (Exception e) {
            System.err.println("Spike detection call failed: " + e.getMessage());
            return null;
        }
    }
}
