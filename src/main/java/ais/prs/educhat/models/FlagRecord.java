package ais.prs.educhat.models;

import java.io.StringReader;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import ais.prs.educhat.models.enums.FlagCategory;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

public class FlagRecord {

    private Long id;
    private String senderId;
    private String recipientId;
    private String messageId;
    private String originalText;
    private String rephraseSuggested;
    private List<FlagCategory> flagCategories;
    // Per-category scores (0-1) from the moderation service, e.g. {"toxic":0.9,...}; null for records flagged before scores were stored
    private JsonObject confidenceScores;
    private Instant flaggedAt;

    // Default constructor
    public FlagRecord() {}

    // Flag Record creation constructor (ID auto-incremental)
    public FlagRecord(String senderId, String recipientId, String messageId, String originalText,
                        String rephraseSuggested, List<FlagCategory> flagCategories, JsonObject confidenceScores) {
        // this.id = flagIDBuilder();
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.messageId = messageId;
        this.originalText = originalText;
        this.rephraseSuggested = rephraseSuggested;
        this.flagCategories = flagCategories;
        this.confidenceScores = confidenceScores;
        this.flaggedAt = Instant.now();
    }

    // Flag Record constructor (For retrieval)
    public FlagRecord(Long id, String senderId, String recipientId, String messageId,
                       String originalText, String rephraseSuggested, List<FlagCategory> flagCategories,
                       JsonObject confidenceScores, Instant flaggedAt) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.messageId = messageId;
        this.originalText = originalText;
        this.rephraseSuggested = rephraseSuggested;
        this.flagCategories = flagCategories;
        this.confidenceScores = confidenceScores;
        this.flaggedAt = flaggedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getRephraseSuggested() {
        return rephraseSuggested;
    }

    public void setRephraseSuggested(String rephraseSuggested) {
        this.rephraseSuggested = rephraseSuggested;
    }

    public List<FlagCategory> getFlagCategories() {
        return flagCategories;
    }

    public void setFlagCategories(List<FlagCategory> flagCategories) {
        this.flagCategories = flagCategories;
    }

    public JsonObject getConfidenceScores() {
        return confidenceScores;
    }

    public void setConfidenceScores(JsonObject confidenceScores) {
        this.confidenceScores = confidenceScores;
    }

    public Instant getFlaggedAt() {
        return flaggedAt;
    }

    public void setFlaggedAt(Instant flaggedAt) {
        this.flaggedAt = flaggedAt;
    }

    @Override
    public String toString() {
        return "FlagRecord{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", originalText='" + originalText + '\'' +
                ", rephraseSuggested='" + rephraseSuggested + '\'' +
                ", flagCategories=" + flagCategories +
                ", confidenceScores=" + confidenceScores +
                ", flaggedAt=" + flaggedAt +
                '}';
    }

    public JsonObject toJson() {
        JsonArrayBuilder categories = Json.createArrayBuilder();
        for (FlagCategory category : flagCategories) {
            categories.add(category.name());
        }

        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("id", id)
            .add("senderId", senderId)
            .add("recipientId", recipientId)
            .add("messageId", messageId)
            .add("originalText", originalText)
            .add("rephraseSuggested", rephraseSuggested)
            .add("flagCategories", categories);

        if (confidenceScores != null) {
            builder.add("confidenceScores", confidenceScores);
        } else {
            builder.addNull("confidenceScores");
        }

        JsonObject flagRecord = builder.add("flaggedAt", flaggedAt.toString()).build();

        return flagRecord;
    }

    // Craete FlagRecord object from SQL row set
    public static FlagRecord createFlagRecord(SqlRowSet srs) {
        // List of the categories that are TRUE
        List<FlagCategory> categories = new LinkedList<>();
        if (srs.getBoolean("toxic")) {
            categories.add(FlagCategory.TOXIC);
        }
        if (srs.getBoolean("inappropriate")) {
            categories.add(FlagCategory.INAPPROPRIATE);
        }
        if (srs.getBoolean("cyberbullying")) {
            categories.add(FlagCategory.CYBERBULLYING);
        }

        // SQL NULL (records flagged before scores were stored) -> null
        JsonObject scores = null;
        String scoresJson = srs.getString("confidence_scores");
        if (scoresJson != null) {
            try (
                StringReader sr = new StringReader(scoresJson);
                JsonReader reader = Json.createReader(sr);
            ) {
                scores = reader.readObject();
            }
        }

        return new FlagRecord(
            srs.getLong("id"),
            srs.getString("sender_id"),
            srs.getString("recipient_id"),
            srs.getString("message_id"),
            srs.getString("original_text"),
            srs.getString("rephrase_suggested"),
            categories,
            scores,
            srs.getTimestamp("flagged_at").toInstant()
        );
    }
}
