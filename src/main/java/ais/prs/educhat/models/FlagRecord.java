package ais.prs.educhat.models;

import java.time.Instant;

import ais.prs.educhat.models.enums.FlagCategory;

public class FlagRecord {
    
    private Long id;
    private String senderId;
    private String receiverId;
    private String messageId;
    private String originalText;
    private String rephraseSuggested;
    private FlagCategory flagCategory;
    private Instant flaggedAt;

    // Default constructor
    public FlagRecord() {}

    // Flag Record creation constructor (ID auto-incremental)
    public FlagRecord(String senderId, String receiverId, String messageId, String originalText, String LLMRephrase, FlagCategory flagCategory) {
        // this.id = flagIDBuilder();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageId = messageId;
        this.originalText = originalText;
        this.rephraseSuggested = LLMRephrase;
        this.flagCategory = flagCategory;
        this.flaggedAt = Instant.now();
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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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

    public FlagCategory getFlagCategory() {
        return flagCategory;
    }

    public void setFlagCategory(FlagCategory flagCategory) {
        this.flagCategory = flagCategory;
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
                ", receiverId='" + receiverId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", originalText='" + originalText + '\'' +
                ", rephraseSuggested='" + rephraseSuggested + '\'' +
                ", flagCategory=" + flagCategory +
                ", flaggedAt=" + flaggedAt +
                '}';
    }
}
