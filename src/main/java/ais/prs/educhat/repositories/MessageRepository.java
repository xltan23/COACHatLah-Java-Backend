package ais.prs.educhat.repositories;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import ais.prs.educhat.models.Message;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Repository
public class MessageRepository {
    
    @Autowired
    @Qualifier("redislab")
    private RedisTemplate<String,String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // public void saveMessage(String key, String message) {
    //     System.out.println("Saving message for conversation between: " + key);
    //     ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
    //     valueOps.set(key, message);
    // }

    // public Optional<String> getMessage(String key) {
    //     System.out.println("Retrieving conversation for: " + key);
    //     ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
    //     String conversation = valueOps.get(key);
    //     if (null == conversation) {
    //         System.out.println("No conversation found for: " + key);
    //         return Optional.empty();
    //     } 
    //     return Optional.of(conversation);
    // }

    public void saveMessage(String key, Message message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            double score = message.getTimestamp().getEpochSecond(); // Use epoch seconds as the score for ordering & expiring
            redisTemplate.opsForZSet().add(key, json, score);
        } catch (JacksonException e) {
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }

    public List<Message> findConversation(String key) {
        // Return oldest message to newest message
        Set<String> rawMessages = redisTemplate.opsForZSet().range(key, 0, -1);
        return rawMessages.stream()
                          .map(this::deserializeMessage)
                          .collect(Collectors.toList());
    }

    public List<Message> getRecentContext(String key, int limit) {
        Set<String> lastMessages = redisTemplate.opsForZSet().reverseRange(key, 0, limit);
        List<Message> context = lastMessages.stream()
            .map(this::deserializeMessage)
            .collect(Collectors.toList());
        Collections.reverse(context);
        return context;
    } 

    public void removeExpiredMessages(String key, Instant cutoff) {
        // Removes messages older than the cutoff timestamp
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, cutoff.getEpochSecond());
    }

    private Message deserializeMessage(String json) {
        try {
            return objectMapper.readValue(json, Message.class);
        } catch (JacksonException e) {
            System.err.println("Error deserializing message: " + e.getMessage());
            return null;
        }
    }
}
