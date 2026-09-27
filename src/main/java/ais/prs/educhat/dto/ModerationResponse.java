package ais.prs.educhat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Scores returned by the Python moderation service (POST /score), each a probability in [0, 1].
// "required" makes a missing key fail parsing instead of silently becoming 0.0 (which would look like a clean message).
@JsonIgnoreProperties(ignoreUnknown = true)
public record ModerationResponse(
    @JsonProperty(value = "toxic", required = true) float toxic,
    @JsonProperty(value = "inappropriate", required = true) float inappropriate,
    @JsonProperty(value = "cyberbullying", required = true) float cyberbullying
) {}
