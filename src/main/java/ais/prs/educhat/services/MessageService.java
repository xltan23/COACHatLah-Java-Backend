package ais.prs.educhat.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ais.prs.educhat.models.Message;
import ais.prs.educhat.repositories.MessageRepository;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepo;

    private static final Duration MESSAGE_EXPIRATION = Duration.ofHours(48); // Set expiration to 48 hours

    public void sendMessage(String sender, String receiver, String content) {
        Message message = new Message(sender, receiver, content);
        String key = buildKey(sender, receiver);
        // Establish message check (Python API) 
        // If flagged, create FlagRecord object and update Profile flag_count
        // Proceed to call LLM to rephrase message and return suggested

        // If not flagged, proceed to send message
        messageRepo.saveMessage(key, message);
    }

    public List<Message> getConversation(String userID1, String userID2) {
        String key = buildKey(userID1, userID2);
        Instant cutoff = Instant.now().minus(MESSAGE_EXPIRATION);
        messageRepo.removeExpiredMessages(key, cutoff);
        return messageRepo.findConversation(key);
    }

    // Standardize key based on IDs of users in conversation
    private String buildKey(String sender, String receiver) {
        List<String> userIDs = new LinkedList<>();
        userIDs.add(sender);
        userIDs.add(receiver);
        Collections.sort(userIDs); // Sort to ensure consistent key regardless of sender/receiver order
        String key = "CHAT{" + userIDs.get(0) + ":" + userIDs.get(1) + "}"; // Unique key for the conversation

        return key;
    }
}
