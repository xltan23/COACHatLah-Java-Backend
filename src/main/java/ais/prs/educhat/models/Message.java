package ais.prs.educhat.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonObject;

// Simple representation of a chat message (to be stored in Redis DB)
public class Message {
    
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
    private Instant timestamp;

    // Default constructor
    public Message() {}

    // Message constructor
    public Message(String senderId, String recipientId, String content) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID for the message
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = Instant.now(); // Set timestamp to current time upon message creation
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                // .add("id", id)
                .add("senderId", senderId)
                .add("recipientId", recipientId)
                .add("content", content)
                .add("timestamp", timestamp.toString())
                .build();
    }

    public static Message createMessage(JsonObject json) {
        Message message = new Message();
        message.setId(json.getString("id"));
        message.setSenderId(json.getString("senderId"));
        message.setRecipientId(json.getString("recipientId"));
        message.setContent(json.getString("content"));
        message.setTimestamp(Instant.parse(json.getString("timestamp")));
        return message;
    }
}
